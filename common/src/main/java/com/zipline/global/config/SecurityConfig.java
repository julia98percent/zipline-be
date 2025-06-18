package com.zipline.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import com.zipline.global.jwt.JwtExceptionHandler;
import com.zipline.global.jwt.JwtFilter;
import com.zipline.global.jwt.TokenProvider;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final TokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtExceptionHandler jwtExceptionHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
			.components(new Components()
				.addSecuritySchemes("BearerAuth",
					new SecurityScheme()
						.name("Authorization")
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
				));
	}

	@Bean
	public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.formLogin(form -> form.disable())
			.cors(cors -> cors.configure(http))
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/users/login", "/api/v1/users/signup", "/api/v1/users/info",
					"/api/v1/users/reissue", "/api/v1/users/find-id", "/api/v1/users/find-password",
					"/api/v1/users/reset-password", "/actuator/health", "/actuator/info")
				.permitAll()
				.requestMatchers(new RegexRequestMatcher("/api/v1/surveys/[A-Za-z0-9]+$", "GET"),
					new RegexRequestMatcher("/api/v1/surveys/[A-Za-z0-9]{26}/submit$$", "POST"))
				.permitAll()
				.requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs",
					"/api-docs/**",
					"/v3/api-docs/**")
				.permitAll()
				.requestMatchers("/api/admin/**", "/actuator/**")
				.hasRole("ADMIN")
				.requestMatchers("/api/**")
				.hasAnyRole("AGENT", "ADMIN")
				.anyRequest()
				.authenticated()
			)
			.addFilterBefore(new JwtFilter(jwtExceptionHandler, jwtTokenProvider, redisTemplate),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}