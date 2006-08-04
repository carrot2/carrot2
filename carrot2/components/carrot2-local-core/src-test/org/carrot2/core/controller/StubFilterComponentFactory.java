
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

import java.util.*;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;


/**
 * A stub implementation of a component factory that produces
 * local filters.
 */
public class StubFilterComponentFactory implements LocalComponentFactory {
    /* */
    private Set successor;

    /* */
    private Set predecessor;

    /* */
    private Set component;

    /* */
    private List createdInstances = new ArrayList();

    /* */
    private String id;

    /* */
    public StubFilterComponentFactory(String id, Set component,
        Set predecessor, Set successor) {
        this.component = component;
        this.predecessor = predecessor;
        this.successor = successor;
        this.id = id;
    }

    /* */
    public StubFilterComponentFactory(String id) {
        this(id, Collections.EMPTY_SET, Collections.EMPTY_SET,
            Collections.EMPTY_SET);
    }

    /* */
    public StubFilterComponentFactory() {
        this("f");
    }

    /* */
    public LocalComponent getInstance() {
        LocalComponent instance = 
            new StubFilterComponent(id, component, predecessor, successor);

        createdInstances.add(instance);

        return instance;
    }

    /* */
    public List getCreatedInstances() {
        return createdInstances;
    }
}
