package com.rs.app.ldap.repository;

import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextCallback;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapEntryIdentification;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControlExtractor;
import org.springframework.security.ldap.ppolicy.PasswordPolicyResponseControl;
import org.springframework.stereotype.Component;

import com.rs.app.model.LdapUser;

@Component
public class LdapUserRepository {
	@Autowired
	private LdapTemplate ldapTemplate;
	@Autowired
	private ContextSource contextSource;

	public String getUserDn(String username) {
		return "uid=" + username + ",ou=people,dc=radiantsage,dc=com";
	}

	public String getUserBase(String username) {
		return "uid=" + username + ",ou=people";
	}

	public boolean authenticate(String username, String password) {
		boolean isAuthenticated = false;
		// Working with Controls
//		DirContext context = contextSource.getContext(getUserDn(username), password);
//		LdapContext ldapContext = (LdapContext) context;
//		Control[] ctrls = null;
//		try {
//			ctrls = ldapContext.getResponseControls();
//		}
//		catch (javax.naming.NamingException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Controls...");
//		if(ctrls != null) {
//			for(Control ctrl: ctrls) {
//				System.out.println(ctrl.getID());
//			}
//
//		}
		// Authenticate
//		AuthenticatedLdapEntryContextMapper<DirContextOperations> mapper = (ctx, ldapEntryIdentification) -> {
//			DirContextOperations dirContextOperations = null;
//			try {
//				System.out.println("relativeName: " + ldapEntryIdentification.getRelativeName());
//				dirContextOperations = (DirContextOperations) ctx.lookup(ldapEntryIdentification.getRelativeName());
//			} catch (NamingException ne) {
//				ne.printStackTrace();
//			}
//			return dirContextOperations;
//		};
//		DirContextOperations dco = ldapTemplate.authenticate(LdapQueryBuilder.query().where("uid").is(username),
//				password, mapper);
//		isAuthenticated = (dco != null) && (dco.getStringAttribute("uid").equals(username));
		isAuthenticated = ldapTemplate.authenticate(getUserBase(username), "(uid=*)", password,
				new AuthenticatedLdapEntryContextCallback() {

					@Override
					public void executeWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
						LdapContext ldapContext = (LdapContext) ctx;
						try {
							Control[] ctrls = ldapContext.getResponseControls();
							if (ctrls != null) {
								for (Control ctrl : ctrls) {
									System.out.println("ControlID: " + ctrl.getID());
								}
							}
						} catch (NamingException e) {
							e.printStackTrace();
						}
					}

				}, new AuthenticationErrorCallback() {

					@Override
					public void execute(Exception e) {
						e.printStackTrace();
					}

				});
		return isAuthenticated;
	}

	public LdapUser getLdapUser(String username) {
		List<LdapUser> ldapUsers = ldapTemplate.search(
				LdapQueryBuilder.query().where("objectClass").is("organizationalPerson").and("uid").is(username),
				new LdapUserMapper());
		return ldapUsers.get(0);
	}

	private class LdapUserMapper implements AttributesMapper<LdapUser> {
		@Override
		public LdapUser mapFromAttributes(Attributes attributes) throws NamingException {
			LdapUser ldapUser = new LdapUser();
			ldapUser.setUsername(attributes.get("uid").get(0).toString());
			ldapUser.setFirstname(attributes.get("givenName").get(0).toString());
			ldapUser.setLastname(attributes.get("sn").get(0).toString());
//			 ldapUser.setPassword(attributes.get("userPassword").get(0).toString());
			ldapUser.setEmail(attributes.get("mail").get(0).toString());
			return ldapUser;
		}
	}
}
