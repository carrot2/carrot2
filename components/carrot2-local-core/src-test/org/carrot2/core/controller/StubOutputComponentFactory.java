
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

import java.util.*;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;


/**
 * A stub implementation of a component factory that produces output
 * components
 */
public class StubOutputComponentFactory implements LocalComponentFactory {
    /* */
    private List createdInstances = new ArrayList();

    /* */
    public LocalComponent getInstance() {
        LocalComponent instance = new StubOutputComponent(Collections.EMPTY_SET,
                Collections.EMPTY_SET, Collections.EMPTY_SET);

        createdInstances.add(instance);

        return instance;
    }

    /* */
    public List getCreatedInstances() {
        return createdInstances;
    }
}
