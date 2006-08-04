
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
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
 * Stub implementation of a local filter component for testing.
 */
public class StubFilterComponent extends StubComponent
    implements LocalFilterComponent {
    /* */
    private String id = "f";

    /* */
    public StubFilterComponent(String id, Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities) {
        super(id, componentCapabilities, predecessorCapabilities,
            successorCapabilities, "f", "A filter stub.");
    }

    /* */
    public StubFilterComponent(Set componentCapabilities,
        Set predecessorCapabilities, Set successorCapabilities) {
        this("f", componentCapabilities, predecessorCapabilities,
            successorCapabilities);
    }

    /* */
    public StubFilterComponent() {
        this("f", Collections.EMPTY_SET, Collections.EMPTY_SET,
            Collections.EMPTY_SET);
    }
}
