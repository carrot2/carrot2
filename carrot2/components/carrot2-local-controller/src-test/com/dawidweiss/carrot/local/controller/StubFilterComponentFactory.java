
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
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;

import java.util.*;


/**
 * A stub implementation of a component factory that produces
 * local filters.
 */
public class StubFilterComponentFactory extends LocalComponentFactoryBase {
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
