package com.zipline.global.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.jwt.dto.TokenRequestDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenProvider {
	private static final String AUTHORITIES_KEY = "auth";
	private static final String BEARER_TYPE = "Bearer";

	@Value("${jwt.accessToken.expiration}")
	long ACCESS_TOKEN_EXPIRE_TIME;

	@Value("${jwt.refreshToken.expiration}")
	long REFRESH_TOKEN_EXPIRE_TIME;

	private final Key key;

	public TokenProvider(
		@Value("${jwt.secret}") String secretKey
	) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public TokenRequestDTO generateTokenDto(Authentication authentication, Long uid) {

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = new Date().getTime();

		Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
		String accessToken = Jwts.builder()
			.setSubject(uid.toString())
			.claim(AUTHORITIES_KEY, authorities)
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
		String refreshToken = Jwts.builder()
			.setSubject(uid.toString())
			.setExpiration(refreshTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return TokenRequestDTO.builder()
			.uid(uid)
			.grantType(BEARER_TYPE)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);   //token 내부 정보 읽어옴

		if (claims.get(AUTHORITIES_KEY) == null) {
			throw new AuthException(AuthErrorCode.MISSING_AUTHORITY);
		}

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		UserDetails principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(key)
				.parseClaimsJws(token);

			return !claims.getBody().getExpiration().before(new Date());

		} catch (SignatureException e) {
			log.error("JWT_SIGNATURE_FAIL: {}", e.getMessage());
			throw new AuthException(AuthErrorCode.JWT_SIGNATURE_FAIL);
		} catch (MalformedJwtException e) {
			log.error("JWT_Decode FAIL: {}", e.getMessage());
			throw new AuthException(AuthErrorCode.JWT_DECODE_FAIL);
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token: {}", e.getMessage());
			throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
		} catch (IllegalArgumentException e) {
			log.error("UNAUTHORIZED_CLIENT : {}", e.getMessage());
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_CLIENT);
		}
	}

	public String getUserIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return claims.getSubject();
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key).build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public Date getExpiration(String accessToken) {
		Claims claims = parseClaims(accessToken);
		Date expiration = claims.getExpiration();
		return expiration;
	}

}