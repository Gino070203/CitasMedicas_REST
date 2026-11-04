package com.citas.medicas.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.citas.medicas.security.OAuth2JwtSuccessHandler;
import com.citas.medicas.security.OAuth2UsuarioService;
import com.citas.medicas.security.jwt.JwtAccessDeniedHandler;
import com.citas.medicas.security.jwt.JwtAuthenticationEntryPoint;
import com.citas.medicas.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final OAuth2UsuarioService oAuth2UsuarioService;
	private final OAuth2JwtSuccessHandler oAuth2JwtSuccessHandler;

	@Value("${app.cors.allowed-origins:http://localhost:4200}")
	private String allowedOrigins;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
						  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
						  JwtAccessDeniedHandler jwtAccessDeniedHandler,
						  OAuth2UsuarioService oAuth2UsuarioService,
						  OAuth2JwtSuccessHandler oAuth2JwtSuccessHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
		this.oAuth2UsuarioService = oAuth2UsuarioService;
		this.oAuth2JwtSuccessHandler = oAuth2JwtSuccessHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/api/auth/**", "/error").permitAll()
					.requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
					.requestMatchers("/api/admin/**").hasRole("ADMIN")
					.requestMatchers("/api/medico/**").hasRole("MEDICO")
					.requestMatchers("/api/paciente/**").hasRole("PACIENTE")
					.requestMatchers("/api/usuario/**").authenticated()
					.anyRequest().authenticated())
			.oauth2Login(oauth2 -> oauth2
					.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UsuarioService))
					.successHandler(oAuth2JwtSuccessHandler))
			.exceptionHandling(ex -> ex
					.authenticationEntryPoint(jwtAuthenticationEntryPoint)
					.accessDeniedHandler(jwtAccessDeniedHandler))
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(origin -> !origin.isEmpty())
				.toList());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
