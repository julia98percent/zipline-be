package com.zipline.global.jwt;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;

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
	private final JwtExceptionHandler jwtExceptionHandler;
	private final TokenProvider tokenProvider;
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
					jwtExceptionHandler.handle(response, AuthErrorCode.EXPIRED_TOKEN);
					return;
				}
				Authentication authentication = tokenProvider.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);
		} catch (AuthException e) {
			jwtExceptionHandler.handle(response, e.getErrorCode());
		}
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
