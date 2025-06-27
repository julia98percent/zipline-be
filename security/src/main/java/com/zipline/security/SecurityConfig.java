package com.zipline.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

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
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/v1/users/login", "/api/v1/users/signup")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .formLogin(form -> form.disable())
        .logout(logout -> logout
            .logoutUrl("/api/v1/users/logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .logoutSuccessHandler((request, response, authentication) -> {
              response.setStatus(HttpServletResponse.SC_OK);
            })
        )
        .cors(cors -> cors.configure(http))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/users/login", "/api/v1/users/signup",
                "/api/v1/users/reissue", "/api/v1/users/find-id", "/api/v1/users/find-password",
                "/api/v1/users/reset-password", "/actuator/health", "/actuator/health/**", 
                "/actuator/info", "/actuator/prometheus", "/api/v1/users/csrf")
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
        );

    return http.build();
  }
}