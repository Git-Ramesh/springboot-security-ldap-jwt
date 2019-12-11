package com.rs.app.config;

import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rs.app.filter.CorsFilter;
import com.rs.app.filter.TokenFilter;
import com.rs.app.util.JwtTokenUtil;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public LdapContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl("ldap://localhost:1389");
		contextSource.setBase("dc=radiantsage,dc=com");
//		contextSource.setUserDn("cn=Directory Manager");
//		contextSource.setPassword("123456");
		return contextSource;
	}
//
//	@Bean
//	public LdapTemplate ldapTemplate() {
//		return new LdapTemplate(contextSource());
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/", "/resources/**", "/static/**", "/public/**", "/h2-console/**", " /*.html",
				"/**/*.html", "/**/*.js", "/**/*.gif", "/**/*.ico", "/swagger-ui/**", "/swagger-resources/**",
				"/api-docs/**", "/user/**");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
				.addFilterBefore(new TokenFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().anyRequest().authenticated();
	}

}
