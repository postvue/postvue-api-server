package com.postvue.feelogserver.core.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.postvue.feelogserver.global.constant.HeaderConst;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		String token = getToken(bearerToken);

		if (token != null) {
			jwtTokenProvider.validateToken(token);
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	public String getToken(String bearerTokenHeader) {
		if (bearerTokenHeader == null || !bearerTokenHeader.startsWith(HeaderConst.TOKEN_HEADER_PREFIX)) {
			return null;
		}
		return bearerTokenHeader.substring(HeaderConst.TOKEN_HEADER_PREFIX.length());
	}
}
