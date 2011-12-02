package com.emergya.aplicaciones.auth;

/* Security & JAAS imports */
import java.security.Principal;

/**
 * <p>
 * Basic implementation of the Principal class. By implementing our own
 * Principal for our application, we can more easily add and remove
 * instances of our principals in the authenticated Subject during the
 * login and logout process.
 *
 */

public class DummyPrincipal implements Principal, java.io.Serializable {

    private String name;

    /**
     * Create a <code>DummyPrincipal</code> with no
     * user name.
     *
     */
    public DummyPrincipal() {
        name = "";
    }

    /**
     * Create a <code>DummyPrincipal</code> using a
     * <code>String</code> representation of the
     * user name.
     *
     * <p>
     *
     * @param name the user identification number (UID) for this user.
     *
     */
    public DummyPrincipal(String newName) {
        name = newName;
    }

    /**
     * Compares the specified Object with this
     * <code>DummyPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>DummyPrincipal</code> and the two
     * DummyPrincipals have the same name.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *		<code>DummyPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *		<code>DummyPrincipal</code>.
     */
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (this == o)
            return true;
 
        if (o instanceof DummyPrincipal) {
            if (((DummyPrincipal) o).getName().equals(name))
                return true;
            else
                return false;
        }
        else 
            return false;
    }

    /**
     * Return a hash code for this <code>DummyPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>DummyPrincipal</code>.
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Return a string representation of this
     * <code>DummyPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this
     *		<code>DummyPrincipal</code>.
     */
    public String toString() {
        return name;
    }

    /**
     * Return the user name for this
     * <code>DummyPrincipal</code>.
     *
     * <p>
     *
     * @return the user name for this
     *		<code>DummyPrincipal</code>
     */
    public String getName() {
        return name;
    }
}

