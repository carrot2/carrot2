
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

package com.dawidweiss.carrot.core.local;

/**
 * A baseline implementation of a {@link LocalOutputComponent} interface.
 * 
 * <p>
 * <b>If you override any method of this class, make sure to add a call to
 * super class's implementation.</b>
 * </p>
 * 
 * <p>
 * As a convenience method, this class also provides a protected {@link
 * #validate()} method, which is invoked from {@link
 * #startProcessing(RequestContext requestContext)} after all internal
 * precondition checks have been verified.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public abstract class LocalOutputComponentBase extends LocalComponentBase
    implements LocalOutputComponent {
    /**
     * Validate the preconditions of a component before processing of a query
     * starts. This method is invoked from the {@link
     * #startProcessing(RequestContext)} method.
     *
     * @throws ProcessingException Thrown if one or more preconditions make it
     *         impossible for the component to start query processing.
     */
    protected void validate() throws ProcessingException {
    }

    /**
     * The default implementation checks preconditions and invokes the
     * protected {@link #validate()} method.
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException {
        validate();
    }

    /**
     * Empty default implementation.
     */
    public void endProcessing() throws ProcessingException {
    }

    /**
     * Empty default implementation.
     */
    public void processingErrorOccurred() {
    }

    /**
     * Empty default implementation.
     */
    public void flushResources() {
    }
}
