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

import com.zipline.dto.TokenRequestDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
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

	public TokenRequestDto generateTokenDto(Authentication authentication, Long uid) {

		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date().getTime());

		Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
		Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
		String accessToken = Jwts.builder()
			.setSubject(uid.toString())
			.claim(AUTHORITIES_KEY, authorities)
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		String refreshToken = Jwts.builder()
			.setSubject(uid.toString())
			.setExpiration(refreshTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();

		return TokenRequestDto.builder()
			.uid(uid)
			.grantType(BEARER_TYPE)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);   //token 내부 정보 읽어옴

		if (claims.get(AUTHORITIES_KEY) == null) {
			throw new JwtException(ErrorCode.MISSING_AUTHORITY.getMessage());
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
			log.info("SignatureException");
			throw new JwtException(ErrorCode.JWT_SIGNATURE_FAIL.getMessage());
		} catch (MalformedJwtException e) {
			log.info("MalformedJwtException");
			throw new JwtException(ErrorCode.JWT_DECODE_FAIL.getMessage());
		} catch (ExpiredJwtException e) {
			log.info("ExpiredJwtException");
			throw new JwtException(ErrorCode.EXPIRED_TOKEN.getMessage());
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException");
			throw new JwtException(ErrorCode.UNAUTHORIZED_CLIENT.getMessage());
		}
	}

	public String getUserIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return claims.getSubject();
	}

	public Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key).build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}
}