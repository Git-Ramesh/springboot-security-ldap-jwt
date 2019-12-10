package com.rs.app.model;

import java.io.Serializable;

import org.springframework.security.core.userdetails.UserDetails;

public class LdapUserDetails extends LdapUser implements UserDetails, Serializable {

	private static final long serialVersionUID = 8592532799308917111L;

	public LdapUserDetails(LdapUser ldapUser) {
		super(ldapUser);
	}
}
