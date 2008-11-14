
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

import java.util.Map;

import org.carrot2.core.LocalComponentFactory;
import org.carrot2.util.PropertyProviderBase;


/**
 * An information-holder class for {@link LocalComponentFactory} objects
 * instantiated by {@link ComponentFactoryLoader} objects.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class LoadedComponentFactory extends PropertyProviderBase {
    /**
     * The factory instance.
     */
    private LocalComponentFactory factory;

    /**
     * Identifier string of the factory.
     */
    private String id;

    /**
     * Creates a new LoadedComponentFactory object.
     *
     * @param id The identifier to use when adding this factory to a
     *        controller.
     * @param factory An initialized {@link LocalComponentFactory} object.
     */
    public LoadedComponentFactory(String id, LocalComponentFactory factory) {
        this.id = id;
        this.factory = factory;
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
     * Expose all properties.
     */
    public Map getProperties() {
        return super.getProperties();
    }
}
