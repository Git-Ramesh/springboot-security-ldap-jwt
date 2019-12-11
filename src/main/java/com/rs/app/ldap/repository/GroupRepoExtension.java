package com.rs.app.ldap.repository;

import java.util.List;

import com.rs.app.model.Group;

public interface GroupRepoExtension {
	List<String> getAllGroupNames();
	void create(Group group);
}
