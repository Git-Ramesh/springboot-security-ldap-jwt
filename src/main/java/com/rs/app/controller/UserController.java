package com.rs.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rs.app.model.Group;
import com.rs.app.model.LdapUser;
import com.rs.app.model.LdapUserDetails;
import com.rs.app.repository.GroupRepository;
import com.rs.app.repository.LdapUserRepository;
import com.rs.app.util.JwtTokenUtil;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private LdapUserRepository ldapUserRepository;
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public String welcome() {
		return "Welcome to LDAP Authentication and Authorization";
	}

	@PostMapping
	public String getAuthUserToken(@RequestBody String loginInfo) {
		try {
			JSONObject login = new JSONObject(loginInfo);
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		return null;
	}

	@PostMapping("/login")
	public boolean login(@RequestBody String loginInfo) {
		boolean isAuth = false;
		try {
			JSONObject login = new JSONObject(loginInfo);
			isAuth = ldapUserRepository.authenticate(login.getString("username"), login.getString("password"));
			System.out.println(ldapUserRepository.getLdapUser(login.getString("username")));
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		return isAuth;
	}

	@PostMapping(value = "/authenticate", produces = "application/json")
	public String token(@RequestBody String loginInfo) throws IOException, InvalidNameException {
		String token = null;
		String result = loginInfo;

		System.out.println(result);
		JSONObject jsonUsernameAndPassword = new JSONObject(result);
		JSONObject resp = new JSONObject();

		String username = jsonUsernameAndPassword.getString("username");
		String password = jsonUsernameAndPassword.getString("password");
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (ldapUserRepository.authenticate(username, password)) {
			LdapUser ldapUser = ldapUserRepository.getLdapUser(username);
			String userDn = ldapUserRepository.getUserDn(ldapUser.getUsername());
			System.out.println("UserDn: " + userDn);
//			List<Group> groups = (List<Group>) groupRepository.findAll();
			System.out.println("Name: " + new LdapName(userDn));
			System.out.println("===By Groupname===");
			System.out.println(groupRepository.findByName("admin"));
			System.out.println("==============");
			List<Group> groups = groupRepository.findByMember(new LdapName(userDn));
			System.out.println("Groups: " + groups);

			for (Group g : groups) {
				if (g.getUniqueMember().contains(new LdapName(userDn))) {
					System.out.println(g.getGroupname());
					authorities.add(new SimpleGrantedAuthority("ROLE_" + g.getGroupname().toUpperCase()));
				}
			}
			System.out.println(authorities);
			ldapUser.setAuthorities(authorities);
			if (ldapUser != null) {
				LdapUserDetails ldapUserDetails = new LdapUserDetails(ldapUser);
				Authentication auth = new UsernamePasswordAuthenticationToken(ldapUserDetails,
						ldapUserDetails.getPassword(), ldapUser.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				if (auth.isAuthenticated()) {
					token = jwtTokenUtil.createToken(ldapUserDetails.getUsername(), ldapUserDetails.getAuthorities()
							.stream().map(authority -> authority.getAuthority()).collect(Collectors.toList()));
				}
			}
			resp.put("username", username);
			resp.put("authorities", ldapUser.getAuthorities().stream().map(authority -> authority.getAuthority())
					.collect(Collectors.toList()));
			resp.put("token", token);

			return resp.toString();
		} else {
			throw new RuntimeException("Username or password invalid");
		}
	}

	@GetMapping("/group/all")
	public List<Group> all() {
		return (List<Group>) groupRepository.findAll();
	}
}