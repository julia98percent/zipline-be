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
				.requestMatchers("/api/users/login", "/api/users/signup", "/api/users/me",
					"/api/users/reissue")
				.permitAll()
				.requestMatchers(new RegexRequestMatcher("/api/surveys/\\d+$", "GET"),
					new RegexRequestMatcher("/api/surveys/\\d+/submit$", "POST"))
				.permitAll()
				.requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs",
					"/api-docs/**",
					"/v3/api-docs/**")
				.permitAll()
				.requestMatchers("/api/admin/**")
				.hasRole("ADMIN")
				.requestMatchers("/api/**")
				.hasRole("AGENT")
				.anyRequest()
				.authenticated()
			)
			.addFilterBefore(new JwtFilter(jwtTokenProvider, redisTemplate),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}