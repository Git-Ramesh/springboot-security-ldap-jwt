package com.rs.app.repository;

import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapUtils;

import com.rs.app.model.Group;

public class GroupRepoImpl implements GroupRepoExtension, BaseLdapNameAware {
//	private static final LdapName ADMIN_USER = LdapUtils.newLdapName("uid=ramesh,ou=people,dc=radiantsage,dc=com");
//	private LdapTemplate ldapTemplate;
	private LdapName baseLdapPath;
//
//	@Override
//	public List<String> getAllGroupNames() {
//		LdapQuery query = LdapQueryBuilder.query().attributes("cn").where("objectclass").is("groupOfNames");
//
//		return ldapTemplate.search(query, new AttributesMapper<String>() {
//			@Override
//			public String mapFromAttributes(Attributes attributes) throws javax.naming.NamingException {
//				return attributes.get("cn").get(0).toString();
//			}
//		});
//	}
//
//	@Override
//	public void create(Group group) {
//		// A groupOfNames cannot be empty - add a system entry to all new groups.
//		group.getUniqueMember().add(LdapUtils.prepend(ADMIN_USER, baseLdapPath));
//		ldapTemplate.create(group);
//	}
//
	@Override
	public void setBaseLdapPath(LdapName baseLdapPath) {
		this.baseLdapPath = baseLdapPath;
	}

}
