package com.citas.medicas.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.citas.medicas.security.UsuarioAutenticado;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider;

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring(7);

		try {
			Claims claims = tokenProvider.validarToken(token);
			String correo = claims.getSubject();
			String rol = claims.get("role", String.class);
			Long idUsuario = null;
			Number userIdClaim = claims.get("userId", Number.class);
			if (userIdClaim != null) {
				idUsuario = userIdClaim.longValue();
			}

			List<GrantedAuthority> authorities = rol != null && !rol.isBlank()
					? List.of(new SimpleGrantedAuthority("ROLE_" + rol))
					: List.of();

			UsuarioAutenticado principal = new UsuarioAutenticado(
					correo, idUsuario, rol, authorities);

			var authentication = new UsernamePasswordAuthenticationToken(
					principal, null, authorities);
			authentication.setDetails(
					new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (JwtException | IllegalArgumentException e) {
			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Token inválido o expirado\"}");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
