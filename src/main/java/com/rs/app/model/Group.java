package com.rs.app.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entry(objectClasses = { "groupOfUniqueNames", "top" }, base = "ou=groups")
@Data
public class Group implements Serializable {
	private static final long serialVersionUID = -2092527852983079452L;
	@Id
	@JsonIgnore
	private Name dn;
	@Attribute(name = "cn")
	private String groupname;
	@Attribute(name = "uniqueMember")
	private Set<Name> uniqueMember = new HashSet<>();

	public void addMember(Name newMember) {
		uniqueMember.add(newMember);
	}

	public void removeMember(Name member) {
		uniqueMember.remove(member);
	}

}
