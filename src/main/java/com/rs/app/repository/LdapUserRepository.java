package com.rs.app.repository;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;

import com.rs.app.model.LdapUser;

@Component
public class LdapUserRepository {
	@Autowired
	private LdapTemplate ldapTemplate;

	public String getUserDn(String username) {
		return "uid=" + username + ",ou=people,dc=radiantsage,dc=com";
	}

	public boolean authenticate(String username, String password) {
		boolean isAuthenticated = false;
		AuthenticatedLdapEntryContextMapper<DirContextOperations> mapper = (ctx, ldapEntryIdentification) -> {
			DirContextOperations dirContextOperations = null;
			try {
				System.out.println("relativeName: " + ldapEntryIdentification.getRelativeName());
				dirContextOperations = (DirContextOperations) ctx.lookup(ldapEntryIdentification.getRelativeName());
			} catch (NamingException ne) {
				ne.printStackTrace();
			}

			return dirContextOperations;
		};
		DirContextOperations dco = ldapTemplate.authenticate(LdapQueryBuilder.query().where("uid").is(username),
				password, mapper);
		isAuthenticated = (dco != null) && (dco.getStringAttribute("uid").equals(username));

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
			ldapUser.setPassword(attributes.get("userPassword").get(0).toString());
			ldapUser.setEmail(attributes.get("mail").get(0).toString());
			return ldapUser;
		}
	}
}
