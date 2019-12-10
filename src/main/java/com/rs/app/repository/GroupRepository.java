package com.rs.app.repository;

import java.util.List;

import javax.naming.Name;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.ldap.repository.Query;

import com.rs.app.model.Group;

public interface GroupRepository extends LdapRepository<Group>, GroupRepoExtension {
	@Query("&(objectClass=groupOfUniqueNames)(cn={0})")
	Group findByName(String groupName);

	@Query("(uniqueMember={0})")
	List<Group> findByMember(Name member);
}
