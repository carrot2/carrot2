
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
 * A baseline implementation of a {@link LocalInputComponent}.  It mainly
 * contains correct implementations of passing the required method calls to a
 * successor component, as specified in {@link LocalComponent} documentation.
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
public abstract class LocalInputComponentBase extends LocalComponentBase
    implements LocalInputComponent {
    /**
     * <code>true</code> indicates that the processing has started. This flag
     * is used to lock the value of {@link next} field.
     */
    private boolean processingStarted;

    /**
     * A reference to the successor component. This reference is valid only
     * during request processing.
     */
    protected LocalComponent next;

    /**
     * The default implementation sets the {@link #next} field and verifies the
     * contract in {@link LocalFilterComponent#setNext(LocalComponent)}
     */
    public void setNext(LocalComponent next) {
        if ((this.next != null) && processingStarted) {
            throw new IllegalStateException(
                "Successor must not be set after the processing has started.");
        }

        this.next = next;
    }

    /**
     * Validate the preconditions of a component before processing of a query
     * starts. This method is invoked from the {@link
     * #startProcessing(RequestContext)} method just before the chained call
     * to a subsequent component.
     *
     * @throws ProcessingException Thrown if one or more preconditions make it
     *         impossible for the component to start query processing.
     */
    protected void validate() throws ProcessingException {
    }

    /**
     * Validate preconditions of the base component's implementation and invoke
     * the protected {@link #validate()} method.
     *
     * @throws ProcessingException Thrown if one or more preconditions make it
     *         impossible for the component to start query processing.
     */
    private void internalValidate() throws ProcessingException {
        // these precondition checks left in the body of this method
        // just in case a subclass overrides validate() and "forgets"
        // to invoke super.validate().
        if (this.next == null) {
            throw new IllegalStateException(
                "Input components must have successors.");
        }

        if (this.processingStarted == true) {
            throw new IllegalStateException("Processing already started.");
        }

        validate();
    }

    /**
     * The default implementation invokes protected {@link #validate()} method
     * first to verify preconditions. Then it invokes
     * <code>startProcessing(requestContext)</code> method on the successor
     * component.
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException {
        internalValidate();

        this.processingStarted = true;
        this.next.startProcessing(requestContext);
    }

    /**
     * The default implementation invokes  <code>endProcessing()</code> method
     * on the successor component.
     */
    public void endProcessing() throws ProcessingException {
        if (this.next == null) {
            throw new IllegalStateException("Filters must have successors.");
        }

        if (this.processingStarted == false) {
            throw new IllegalStateException("Processing not started.");
        }

        this.next.endProcessing();
    }

    /**
     * The default implementation invokes
     * <code>processingErrorOccurred()</code> method on the successor
     * component.
     */
    public void processingErrorOccurred() {
        // in this case there may be no next component, because
        // something has gone wrong at startup.
        if (this.next != null) {
            this.next.processingErrorOccurred();
        }
    }

    /**
     * The default implementation invokes  <code>flushResources()</code> method
     * on the successor component.
     */
    public void flushResources() {
        try {
            if (this.next != null) {
                this.next.flushResources();
            }
        } finally {
            this.next = null;
            this.processingStarted = false;
        }
    }
}
