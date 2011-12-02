package com.emergya.aplicaciones.auth;

/* Java imports */
import java.io.*;
import java.util.*;
import java.sql.*;

/* Security & JAAS imports */
import java.security.*;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;

/**
 * <p>
 * DummyLoginModule is a LoginModule that authenticates
 * a given username/password credential.
 * 
 * 
 *  
 */

public class DummyLoginModule implements LoginModule {

    // initial state
    CallbackHandler callbackHandler;
    Subject  subject;
    Map      sharedState;
    Map      options;

    // temporary state
    Vector   tempCredentials;
    Vector   tempPrincipals;

    // the authentication status
    boolean  success;

    // configurable options
    boolean  debug;

    /**
     * <p>Creates a login module that can authenticate against
     * a .
     */
    public DummyLoginModule() {
        tempCredentials = new Vector();
        tempPrincipals  = new Vector();
        success = false;
        debug   = false;
    }

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *			with the end user (prompting for usernames and
     *			passwords, for example). <p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *			<code>Configuration</code> for this particular
     *			<code>LoginModule</code>.
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map sharedState, Map options) {

        // save the initial state
        this.callbackHandler = callbackHandler;
        this.subject     = subject;
        this.sharedState = sharedState;
        this.options     = options;

        // initialize any configured options
        if (options.containsKey("debug"))
            debug = "true".equalsIgnoreCase((String)options.get("debug"));


        if (debug) {
            System.out.println("\t\t[DummyLoginModule] initialize");
        }
    }

    /**
     * <p> Verify the password
     *
     * @return true always, since this <code>LoginModule</code>
     *      should not be ignored.
     *
     * @exception FailedLoginException if the authentication fails. <p>
     *
     * @exception LoginException if this <code>LoginModule</code>
     *      is unable to perform the authentication.
     */
    public boolean login() throws LoginException {

        if (debug)
            System.out.println("\t\t[DummyLoginModule] login");

        if (callbackHandler == null)
            throw new LoginException("Error: no CallbackHandler available " +
                    "to garner authentication information from the user");

        try {
            // Setup default callback handlers.
            Callback[] callbacks = new Callback[] {
                new NameCallback("Username: "),
                new PasswordCallback("Password: ", false)
            };

            callbackHandler.handle(callbacks);

            String username = ((NameCallback)callbacks[0]).getName();
            String password = new String(((PasswordCallback)callbacks[1]).getPassword());

            ((PasswordCallback)callbacks[1]).clearPassword();

            
            success = dummyValidate(username, password);

            callbacks[0] = null;
            callbacks[1] = null;

            if (!success)
                throw new LoginException("Authentication failed: Password does not match");

            return(true);
        } catch (LoginException ex) {
            throw ex;
        } catch (Exception ex) {
            success = false;
            throw new LoginException(ex.getMessage());
        }
    }


	/**
     * Abstract method to commit the authentication process (phase 2).
     *
     * <p> This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> method), then this method associates a
     * <code>RdbmsPrincipal</code>
     * with the <code>Subject</code> located in the
     * <code>LoginModule</code>.  If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the commit fails
     *
     * @return true if this LoginModule's own login and commit
     *      attempts succeeded, or false otherwise.
     */
    public boolean commit() throws LoginException {

        if (debug)
            System.out.println("\t\t[DummyLoginModule] commit");

        if (success) {

            if (subject.isReadOnly()) {
                throw new LoginException ("Subject is Readonly");
            }

            try {
                Iterator it = tempPrincipals.iterator();
                
                if (debug) {
                    while (it.hasNext())
                        System.out.println("\t\t[DummyLoginModule] Principal: " + it.next().toString());
                }

                subject.getPrincipals().addAll(tempPrincipals);
                subject.getPublicCredentials().addAll(tempCredentials);

                tempPrincipals.clear();
                tempCredentials.clear();

                if(callbackHandler instanceof PassiveCallbackHandler)
                    ((PassiveCallbackHandler)callbackHandler).clearPassword();

                return(true);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
                throw new LoginException(ex.getMessage());
            }
        } else {
            tempPrincipals.clear();
            tempCredentials.clear();
            return(true);
        }
    }

    /**
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> and <code>commit</code> methods),
     * then this method cleans up any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the abort fails.
     *
     * @return false if this LoginModule's own login and/or commit attempts
     *     failed, and true otherwise.
     */
    public boolean abort() throws javax.security.auth.login.LoginException {

        if (debug)
            System.out.println("\t\t[DummyLoginModule] abort");

        // Clean out state
        success = false;

        tempPrincipals.clear();
        tempCredentials.clear();

        if (callbackHandler instanceof PassiveCallbackHandler)
            ((PassiveCallbackHandler)callbackHandler).clearPassword();

        logout();

        return(true);
    }

    /**
     * Logout a user.
     *
     * <p> This method removes the Principals
     * that were added by the <code>commit</code> method.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *		should not be ignored.
     */
    public boolean logout() throws javax.security.auth.login.LoginException {

        if (debug)
            System.out.println("\t\t[DummyLoginModule] logout");

        tempPrincipals.clear();
        tempCredentials.clear();

        if (callbackHandler instanceof PassiveCallbackHandler)
            ((PassiveCallbackHandler)callbackHandler).clearPassword();

        // remove the principals the login module added
        Iterator it = subject.getPrincipals(DummyPrincipal.class).iterator();
        while (it.hasNext()) {
            DummyPrincipal p = (DummyPrincipal)it.next();
            if(debug)
                System.out.println("\t\t[DummyLoginModule] removing principal "+p.toString());
            subject.getPrincipals().remove(p);
        }

        // remove the credentials the login module added
        it = subject.getPublicCredentials(DummyCredential.class).iterator();
        while (it.hasNext()) {
            DummyCredential c = (DummyCredential)it.next();
            if(debug)
                System.out.println("\t\t[DummyLoginModule] removing credential "+c.toString());
            subject.getPrincipals().remove(c);
        }

        return(true);
    }

        
    
    private boolean dummyValidate(String username, String password) throws Exception{
    	
    	final String validUser ="test";
    	final String validPassword ="test";

    	
    	DummyPrincipal  p = null;
        DummyCredential c = null;
        boolean passwordMatch = false;
        
        if(username!=null && validUser.equals(username)){
	        	
	        passwordMatch = validPassword.equals(password);
	        if (passwordMatch) {
	            if (debug) 
	                System.out.println("\t\t[DummyLoginModule] passwords match!");
	
	            c = new DummyCredential();
	            // Credential = role de jaas 
	            c.setProperty("ROLE_USER", "true");
	            this.tempCredentials.add(c);
	            this.tempPrincipals.add(new DummyPrincipal(username));
	        } else {
	            if (debug)
	                System.out.println("\t\t[DummyLoginModule] passwords do NOT match!");
	        }
    	} else{
			throw new LoginException("User " + username + " not found");
    	}	
    	return passwordMatch;
	}

}

