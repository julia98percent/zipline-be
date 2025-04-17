package com.zipline.global.jwt;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import net.minidev.json.JSONObject;

import com.zipline.global.common.response.ApiResponse;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer";

	public final TokenProvider tokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request
		, HttpServletResponse response
		, FilterChain filterChain) throws IOException, ServletException {
		//1. Request Header에서 토큰 꺼냄
		String jwt = resolveToken(request);

		//2. validateToken으로 토큰 유효성 검사
		try {
			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				String blacklistKey = "blacklist:" + jwt;
				Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);

				if (isBlacklisted) {
					setResponse(response, ErrorCode.EXPIRED_TOKEN);
					return;
				}
				Authentication authentication = tokenProvider.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);
		} catch (JwtException ex) {
			String message = ex.getMessage();
			if (ErrorCode.UNAUTHORIZED_CLIENT.getMessage().equals(message))
				setResponse(response, ErrorCode.UNAUTHORIZED_CLIENT);
			else if (ErrorCode.JWT_SIGNATURE_FAIL.getMessage().equals(message))
				setResponse(response, ErrorCode.JWT_SIGNATURE_FAIL);
			else if (ErrorCode.EXPIRED_TOKEN.getMessage().equals(message))
				setResponse(response, ErrorCode.EXPIRED_TOKEN);
			else if (ErrorCode.JWT_DECODE_FAIL.getMessage().equals(message))
				setResponse(response, ErrorCode.JWT_DECODE_FAIL);
			else
				setResponse(response, ErrorCode.FORBIDDEN_CLIENT);
		}
	}

	private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws RuntimeException, IOException {
		JSONObject apiResponse = ApiResponse.jsonOf(errorCode);
		String json = apiResponse.toJSONString();
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(errorCode.getHttpStatus().value());
		response.getWriter().write(json);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			String token = bearerToken.substring(BEARER_PREFIX.length()).trim();
			return token;
		}
		return null;
	}
}
