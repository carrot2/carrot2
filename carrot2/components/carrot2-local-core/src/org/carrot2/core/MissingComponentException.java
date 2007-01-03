
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

/**
 * An exception thrown if an operation has been attempted on a component
 * identifier that is not associated with any factory.
 *
 * @author Dawid Weiss
 *
 * @see LocalComponentFactory
 * @see LocalComponent
 */
public class MissingComponentException extends Exception {

    private final String componentId;
    
    /**
     * Creates a new exception object with  an associated message explaining
     * the cause of the error.
     *
     * @param componentId The identifier of a missing component.
     */
    public MissingComponentException(String componentId) {
        super("Component with this identifier not found: " + componentId);
        
        this.componentId = componentId;
    }
    
    /**
     * Returns the identifier of a missing component.
     */
    public String getMissingComponentId() {
        return this.componentId;
    }
}
