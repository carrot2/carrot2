
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

package org.carrot2.core.controller;

import org.carrot2.core.*;

import java.util.Collections;
import java.util.Set;


/**
 * A stub implementation of an input component for tests.
 */
public class StubInputComponent extends StubComponent
    implements LocalInputComponent {
    /* */
    private String query;

    /* */
    private LocalControllerContext context;

    /* */
    public StubInputComponent(String id, Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities) {
        super(id, componentCapabilities, predecessorCapabilities,
            successorCapabilities, "i", "Input component stub.");
    }

    /* */
    public StubInputComponent(Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities) {
        this("i", componentCapabilities, predecessorCapabilities,
            successorCapabilities);
    }

    /* */
    public StubInputComponent() {
        this("i", Collections.EMPTY_SET, Collections.EMPTY_SET,
            Collections.EMPTY_SET);
    }

    /* */
    public void setQuery(String query) {
        this.query = query;
    }
}
