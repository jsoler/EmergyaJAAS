package com.emergya.aplicaciones.auth;

/* Java imports */
import java.io.*;
import java.util.*;

/* JAAS imports */
import java.security.*;
import javax.security.auth.*;
import javax.security.auth.login.*;


/**
 * <p>
 * JaasTest uses JAAS and our custom DummyLoginModule 
 * and ConsoleCallbackHandler. Prompts and reads from the 
 * command line console line for username/password and
 * then authenticates.
 * 
 */

public class JaasTest{

    public static void main(String[] args) {

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

