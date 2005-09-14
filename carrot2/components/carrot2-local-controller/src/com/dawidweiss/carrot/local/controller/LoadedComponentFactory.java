
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

import com.dawidweiss.carrot.core.local.LocalComponentFactory;


/**
 * An information-holder class for {@link LocalComponentFactory} objects
 * instantiated by {@link ComponentFactoryLoader} objects.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LoadedComponentFactory {
    /**
     * The factory instance.
     */
    private LocalComponentFactory factory;

    /**
     * Identifier string of the factory.
     */
    private String id;

    /**
     * Size of the pool for objects created by this factory
     */
    private int poolSize;

    /**
     * Creates a new LoadedComponentFactory object.
     *
     * @param id The identifier to use when adding this factory to a
     *        controller.
     * @param factory An initialized {@link LocalComponentFactory} object.
     * @param poolSize Size of the pool for objects created by this factory.
     */
    public LoadedComponentFactory(String id, LocalComponentFactory factory,
        int poolSize) {
        this.id = id;
        this.factory = factory;
        this.poolSize = poolSize;
    }

    /**
     * @return Returns the factory.
     */
    public LocalComponentFactory getFactory() {
        return factory;
    }

    /**
     * @return Returns the id for this factory.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Returns the maximum pool size for objects created by this
     *         factory.
     */
    public int getPoolSize() {
        return poolSize;
    }
}
