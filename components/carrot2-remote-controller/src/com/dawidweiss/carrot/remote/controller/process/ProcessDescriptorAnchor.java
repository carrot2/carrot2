
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
package com.dawidweiss.carrot.remote.controller.process;


import java.io.*;


interface ProcessDescriptorAnchor
{
    /**
     * Indicates whether process descriptor is up-to-date since last openStream() call.
     */
    public boolean isUpToDate();


    /**
     * Returns a stream to process descriptor data and resets the up-to-date flag.
     */
    public InputStream openStream();
}
