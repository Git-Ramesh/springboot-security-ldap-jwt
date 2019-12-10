package com.rs.app.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rs.app.util.JwtTokenUtil;

public class TokenFilter extends OncePerRequestFilter {
	private JwtTokenUtil jwtTokenUtil;

	public TokenFilter(JwtTokenUtil jwtTokenUtil) {
		System.out.println("JWTAuthFilter: 0-param constr");
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("doFilter..");

		String token = jwtTokenUtil.resolveToken(request);
		System.out.println("token: " + token);
		try {
			if (token != null && jwtTokenUtil.validateToken(token)) {
				Authentication auth = (token != null) ? jwtTokenUtil.getAuthentication(token) : null;
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (Exception e) {
			response.sendError(403, e.getMessage());
		}
		filterChain.doFilter(request, response);
		System.out.println("After doFilter");
	}

}
