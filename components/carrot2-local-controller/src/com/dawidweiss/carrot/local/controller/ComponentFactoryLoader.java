
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

package com.dawidweiss.carrot.local.controller;

import java.io.IOException;
import java.io.InputStream;

import com.dawidweiss.carrot.local.controller.loaders.ComponentInitializationException;


/**
 * The component loader is responsible for interpreting a stream of bytes and
 * turning it into an instance of {@link LoadedComponentFactory}.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface ComponentFactoryLoader {
    /**
     * Loads a component factory and associated data from a data stream.
     *
     * @param dataStream The data stream to load from.
     *
     * @return Returns the factory and its associated data as {@link
     *         LoadedComponentFactory} object.
     *
     * @throws IOException If an i/o error occurs.
     */
    public LoadedComponentFactory load(InputStream dataStream)
        throws IOException, ComponentInitializationException;
}
