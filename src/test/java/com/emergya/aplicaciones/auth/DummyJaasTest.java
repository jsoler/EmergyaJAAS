package com.emergya.aplicaciones.auth;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.junit.Test;

public class DummyJaasTest {

	@Test
	public void testLogin() {
		  boolean loginSuccess = false;
	        Subject subject = null;

	        try {
	            ConsoleCallbackHandler cbh = new ConsoleCallbackHandler();

	            LoginContext lc = new LoginContext("test-realm", cbh);

	            try {
	                lc.login();
	                loginSuccess = true;

	                subject = lc.getSubject();

	                Iterator it = subject.getPrincipals().iterator();
	                while (it.hasNext()) 
	                    System.out.println("Authenticated: " + it.next().toString());

	                it = subject.getPublicCredentials(Properties.class).iterator();
	                while (it.hasNext()) 
	                    ((Properties)it.next()).list(System.out);

	                lc.logout();
	            } catch (LoginException lex) {
	                System.out.println(lex.getClass().getName() + ": " + lex.getMessage());
	            }

	        } catch (Exception ex) {
	            System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
	        }

	        System.exit(0);
	}

}
