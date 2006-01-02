
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.local.controller;

import com.dawidweiss.carrot.core.local.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A stub implementation of an output component.
 */
public class StubOutputComponent extends StubComponent
    implements LocalOutputComponent {
    /** a buffer for collecting pseudo-output of the process execution. */
    private StringBuffer output = new StringBuffer();

    /** a map of persistent properties for this component */
    private Map properties = new HashMap();

    /** a map of required context attributes */
    private Map requiredContextAttrs = new HashMap();

    /* */
    public StubOutputComponent(String id, Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities) {
        super(id, componentCapabilities, predecessorCapabilities,
            successorCapabilities, "o", "An output component stub.");
    }

    /* */
    public StubOutputComponent(Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities) {
        this("o", componentCapabilities, predecessorCapabilities,
            successorCapabilities);
    }

    /* */
    public StubOutputComponent() {
        this("o", Collections.EMPTY_SET, Collections.EMPTY_SET,
            Collections.EMPTY_SET);
    }

    /**
     * Sets a required request context attribute. If the attribute is not
     * present in request context (and not equal to <code>equals</code>
     * argument),  a runtime exception is thrown.
     * 
     * <p>
     * This method is for required for some tests.
     * </p>
     */
    public void setRequiredContextAttribute(String name, Object equals) {
        requiredContextAttrs.put(name, equals);
    }

    /* */
    public void setNext(LocalComponent next) {
        throw new RuntimeException("No setNext on output component.");
    }

    /* */
    public void pushOutput(String id, String message) {
        output.append((id + ":" + message + ","));
    }

    /* */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException {
        super.startProcessing(requestContext);

        Map requestParams = requestContext.getRequestParameters();

        for (Iterator i = requiredContextAttrs.keySet().iterator();
                i.hasNext();) {
            String key = (String) i.next();

            if (!requestParams.containsKey(key)) {
                throw new RuntimeException(
                    "Request context parameter missing: " + key);
            }

            if (!requiredContextAttrs.get(key).equals(requestParams.get(key))) {
                throw new RuntimeException(
                    "Request context parameter with a different value to what was expected: " +
                    requestParams.get(key));
            }
        }
    }

    /* */
    public Object getResult() {
        return output;
    }

    /* */
    public void setProperty(String key, String value) {
        this.properties.put(key, value);
    }

    /* */
    public String getProperty(String key) {
        return (String) properties.get(key);
    }
}
