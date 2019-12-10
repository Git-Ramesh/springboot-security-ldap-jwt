package com.rs.app.util;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.rs.app.model.LdapUser;
import com.rs.app.model.LdapUserDetails;
import com.rs.app.repository.LdapUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

	@Value("${security.jwt.token.secret-key:123456}")
	private String secretKey;
	@Value("${security.jwt.token.expire-time:3600000}")
	private long validityInMilliseconds = 3600000;
	@Autowired
	private LdapUserRepository ldapUserRepository;

	@PostConstruct
	protected void init() {
		// secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String username, List<String> roles) {
		System.out.println("Creating token..");
		Claims claims = Jwts.claims().setSubject(username);
		claims.put("auth", roles);
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		return Jwts.builder().setClaims(claims).setExpiration(validity).signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public Authentication getAuthentication(String token) {
		System.out.println("Getting the authentication from token");
		LdapUser ldapUser = ldapUserRepository.getLdapUser(getUsername(token));
		LdapUserDetails ldapUserDetails = new LdapUserDetails(ldapUser);
		return new UsernamePasswordAuthenticationToken(ldapUserDetails, "", ldapUserDetails.getAuthorities());
	}

	public String getUsername(String token) {
		System.out.println("Getting the username from token..");
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		System.out.println("Checking Bearer token from req header..");
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	public boolean validateToken(String token) {
		System.out.println("Validating the token..");
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			System.out.println("Token valid..");
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new RuntimeException("Expired or invalid JWT token");
		}
	}
}
