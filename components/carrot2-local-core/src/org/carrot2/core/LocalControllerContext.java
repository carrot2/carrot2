
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

/**
 * An interface providing access to several utility methods of a local
 * controller (local components container object). The methods exposed by this
 * interface may be used  to verify compatibility and availability of
 * components required by instances of a {@link LocalProcess} objects.
 *
 * @author Dawid Weiss
 *
 * @see LocalProcess#initialize(LocalControllerContext)
 */
public interface LocalControllerContext {
    /**
     * @param key A <code>String</code> identifier of a component factory.
     *
     * @return Returns <code>true</code> if a component factory of the provided
     *         id is available.
     */
    public boolean isComponentFactoryAvailable(String key);

    /**
     * Returns the class of a component of the specified id. The returned class
     * reference can be used to verify if the provided component identifier is
     * associated with some expected class.
     *
     * @param key A <code>String</code> identifier of a component factory.
     *
     * @return Returns a {@link java.lang.Class} instance acquired by a call to
     *         {@link java.lang.Object#getClass()} on an instance of a
     *         component acquired from the factory identified by the provided
     *         <code>key</code>.
     *
     * @throws MissingComponentException Thrown if <code>key</code> is  not
     *         associated with any factory.
     */
    public Class getComponentClass(String key) throws MissingComponentException;

    /**
     * Checks compatibility of two ordered components.
     * <code>keyComponentFrom</code> is the factory id of the first component
     * and <code>keyComponentFrom</code> is the factory id of the successor
     * component.
     * 
     * <p>
     * See {@link LocalComponent documentation of the
     * <code>LocalComponent</code> class} for details concerning capabilities.
     * </p>
     *
     * @param keyComponentFrom The factory id of the first component.
     * @param keyComponentTo The factory id of the successor component.
     *
     * @return Returns <code>true</code> if and only if the two components are
     *         available and compatible in terms of the compatibility
     *         definition provided in the documentation of the {@link
     *         LocalComponent} interface.
     *
     * @throws MissingComponentException Thrown if there is no factory for
     *         either <code>keyComponentFrom</code> or
     *         <code>keyComponentTo</code>.
     */
    public boolean isComponentSequenceCompatible(String keyComponentFrom, 
            String keyComponentTo) throws MissingComponentException;

    /**
     * Returns a human-explanation of the reason of incompatibility between two
     * ordered components.
     * 
     * <p>
     * The returned value is undefined if the two components are compatible; a
     * call to {@link #isComponentSequenceCompatible(String, String)} should
     * always proceed this method to ensure the two components are indeed
     * incompatible.
     * </p>
     *
     * @param from The factory id of the first component.
     * @param to The factory id of the successor component.
     *
     * @return Returns a <code>java.lang.String</code> with human
     *         understandable explanation of the reason why the two components
     *         are incompatible.
     *
     * @throws MissingComponentException Thrown if there is no factory for
     *         either <code>keyComponentFrom</code> or
     *         <code>keyComponentTo</code>.
     */
    public String explainIncompatibility(String from, String to)
        throws MissingComponentException;
}
