package com.rs.app.ldap;

import java.util.Collection;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import com.rs.app.model.LdapUser;
import com.rs.app.model.LdapUserDetails;

public class UserDetailsContextMapperImpl implements UserDetailsContextMapper {

	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {
		LdapUser ldapUser = new LdapUser();
		ldapUser.setUsername(ctx.getStringAttribute("uid"));
		ldapUser.setFirstname(ctx.getStringAttribute("givenName"));
		ldapUser.setLastname(ctx.getStringAttribute("sn"));
		ldapUser.setEmail(ctx.getStringAttribute("mail"));
		ldapUser.setAuthorities(authorities);
		return new LdapUserDetails(ldapUser);
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		LdapUserDetails ldapUserDetails = (LdapUserDetails) user;
		ctx.setAttributeValue("uid", ldapUserDetails.getUsername());
		ctx.setAttributeValue("givenName", ldapUserDetails.getFirstname());
		ctx.setAttributeValue("sn", ldapUserDetails.getLastname());
		ctx.setAttributeValue("mail", ldapUserDetails.getEmail());
		ctx.setAttributeValue("userPassword", ldapUserDetails.getPassword());
	}

}
