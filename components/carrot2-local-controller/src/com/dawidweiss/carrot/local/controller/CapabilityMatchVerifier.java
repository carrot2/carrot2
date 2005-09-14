
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.local.controller;

import com.dawidweiss.carrot.core.local.LocalComponent;

import java.util.Iterator;
import java.util.Set;


/**
 * An implementation of capability match verifier. See {@link
 * com.dawidweiss.carrot.core.local.LocalComponent} class documentation for
 * details.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see com.dawidweiss.carrot.core.local.LocalComponent
 */
public final class CapabilityMatchVerifier {
    /**
     * No instantiation of this class, static utility  methods only.
     */
    private CapabilityMatchVerifier() {
    }

    /**
     * Check if <code>componentFrom</code> is compatible with
     * <code>compatibleTo</code>
     *
     * @param componentFrom The component to verify compatibility of.
     * @param componentTo The component to verify compatibility against.
     *
     * @return <code>true</code> if <code>componentFrom</code> is compatible
     *   with <code>componentTo</code>.
     */
    public static boolean isCompatible(LocalComponent componentFrom,
        LocalComponent componentTo) {
        Set available = componentFrom.getComponentCapabilities();
        Set required = componentTo.getRequiredPredecessorCapabilities();

        if (isCompatible(available, required) == false) {
            return false;
        }

        available = componentTo.getComponentCapabilities();
        required = componentFrom.getRequiredSuccessorCapabilities();

        if (isCompatible(available, required) == false) {
            return false;
        }

        return true;
    }

    /**
     * Check if the set of required capabilities is in available capabilities.
     * 
     * <p>
     * Classes in the required set are compatible if they represent subclasses
     * or subinterfaces of the classes represented in the available
     * capabilities set.
     * </p>
     *
     * @param available Available set of capabilities
     * @param required  Required set of capabilities.
     *
     * @return true if <code>available</code> fulfills <code>required</code>
     * false otherwise.
     */
    private static boolean isCompatible(Set available, Set required) {
        for (Iterator i = required.iterator(); i.hasNext();) {
            Object capability = i.next();

            if (capability instanceof java.lang.Class) {
                boolean found = false;

                for (Iterator j = available.iterator(); j.hasNext();) {
                    Object availableCapability = j.next();

                    if (availableCapability instanceof Class) {
                        if (((Class) capability).isAssignableFrom(
                                    (Class) availableCapability)) {
                            found = true;

                            break;
                        }
                    }
                }

                if (found == false) {
                    return false;
                }
            } else {
                // some generic capability?
                if (!available.contains(capability)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Creates a natural-language explanation of the nature of incompatibility
     * between two sets of capabilities.
     *
     * @param buf A string buffer to save the explanation to.
     * @param available Available set of capabilities
     * @param required  Required set of capabilities.
     */
    private static void explain(StringBuffer buf, Set available, Set required) {
        for (Iterator i = required.iterator(); i.hasNext();) {
            Object capability = i.next();

            if (capability instanceof java.lang.Class) {
                boolean found = false;

                for (Iterator j = available.iterator(); j.hasNext();) {
                    Object availableCapability = j.next();

                    if (availableCapability instanceof Class) {
                        if (((Class) capability).isAssignableFrom(
                                    (Class) availableCapability)) {
                            found = true;

                            break;
                        }
                    }
                }

                if (found == false) {
                    buf.append("\t<class> ");
                    buf.append(((Class) capability).getName());
                    buf.append("\n");
                }
            } else {
                // some generic capability?
                if (!available.contains(capability)) {
                    buf.append("\t<generic> ");
                    buf.append(capability.toString());
                    buf.append("\n");
                }
            }
        }
    }

    /**
     * @param componentFrom The component to verify compatibility of.
     * @param componentTo The component to verify compatibility against.
     *
     * @return A natural-language explanation of the nature of incompatibility
     *         between <code>componentFrom</code> and
     * <code>componentTo</code>.
     */
    public static String explain(LocalComponent componentFrom,
        LocalComponent componentTo) {
        StringBuffer buf = new StringBuffer();

        Set available = componentFrom.getComponentCapabilities();
        Set required = componentTo.getRequiredPredecessorCapabilities();

        if (isCompatible(available, required) == false) {
            buf.append("Component " + componentTo.getClass().getName() +
                " requires the following capabilities missing in component " +
                componentFrom.getClass().getName() + ":\n");
            explain(buf, available, required);
            buf.append("\n");
        }

        available = componentTo.getComponentCapabilities();
        required = componentFrom.getRequiredSuccessorCapabilities();

        if (isCompatible(available, required) == false) {
            buf.append("Component " + componentFrom.getClass().getName() +
                " requires the following capabilities missing in component " +
                componentTo.getClass().getName() + ":\n");
            explain(buf, available, required);
            buf.append("\n");
        }

        return buf.toString();
    }
}
