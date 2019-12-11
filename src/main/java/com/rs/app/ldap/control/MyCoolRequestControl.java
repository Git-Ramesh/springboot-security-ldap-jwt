package com.rs.app.ldap.control;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;

import org.springframework.ldap.control.AbstractRequestControlDirContextProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyCoolRequestControl extends AbstractRequestControlDirContextProcessor {

	@Override
	public void postProcess(DirContext ctx) throws NamingException {
		System.out.println("***************** postProcess ****************");
	}

	@Override
	public Control createRequestControl() {
		System.out.println("***************** createRequestControl ****************");
		return null;
	}

}
