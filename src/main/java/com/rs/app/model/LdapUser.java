package com.rs.app.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LdapUser implements Serializable {
	private static final long serialVersionUID = -8493852315908152042L;
	private String username;
	private String firstname;
	private String lastname;
	private String password;
	private String email;
	private boolean enabled;
	private boolean accountNonExpired;
	private boolean credentialsNonExpired;
	private boolean accountNonLocked;
	private Collection<? extends GrantedAuthority> authorities = new HashSet<>();

	public LdapUser(LdapUser ldapUser) {
		this.username = ldapUser.getUsername();
		this.firstname = ldapUser.getFirstname();
		this.lastname = ldapUser.getLastname();
		this.password = ldapUser.getPassword();
		this.email = ldapUser.getEmail();
		this.enabled = ldapUser.isEnabled();
		this.accountNonExpired = ldapUser.isAccountNonExpired();
		this.credentialsNonExpired = ldapUser.isCredentialsNonExpired();
		this.accountNonLocked = ldapUser.isAccountNonLocked();
		this.authorities = ldapUser.getAuthorities();
	}
}
