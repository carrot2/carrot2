
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

import java.io.IOException;
import java.io.InputStream;


/**
 * The process loader is responsible for interpreting a stream of bytes and
 * turning it into an instance of {@link LoadedProcess}.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface ProcessLoader {
    /**
     * Loads a local process and any associated data from a data stream.
     *
     * @param dataStream The data stream to load from.
     *
     * @return Returns the factory and its associated data as {@link
     *         LoadedComponentFactory} object.
     *
     * @throws IOException If an i/o error occurs.
     * @throws InstantiationException If creating of the process failed for
     *         some reason.
     */
    public LoadedProcess load(InputStream dataStream)
        throws IOException, InstantiationException;
}
