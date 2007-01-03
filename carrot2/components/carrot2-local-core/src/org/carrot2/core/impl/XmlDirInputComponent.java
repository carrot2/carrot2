
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.impl;

import java.io.*;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;

/**
 * Opens XML files in a local filesystem.
 * 
 * This component expects that the {@link #XML_DIR} parameter contains 
 * the name of the data file to be loaded. Tha name must be relative to the local filesystem
 * directory path provided in the constructor or in the {@link #XML_DIR} parameter.
 * 
 * @author Stanislaw Osinski
 */
public class XmlDirInputComponent extends XmlStreamInputComponent
{
    /**
     * A path to a folder in a local filesystem containing 
     * XML input files. The query is concatenated with the folder location.
     */
    public static final String XML_DIR = "input:directory";
    
    /** */
    private File inputDir;

    /** */
    private final File defaultInputDir;

    public XmlDirInputComponent()
    {
        this.defaultInputDir = null;
    }

    public XmlDirInputComponent(File inputDir)
    {
        if (inputDir == null)
        {
            throw new IllegalArgumentException("inputDir must not be null");
        }

        if (!inputDir.isDirectory())
        {
            throw new IllegalArgumentException("inputDir must be a directory");
        }

        this.defaultInputDir = inputDir;
    }

    protected InputStream getInputXML(RequestContext requestContext) throws ProcessingException {
        InputStream inputXML = (InputStream) requestContext.getRequestParameters().get(XML_STREAM);
        if (inputXML != null)
        {
            return inputXML;
        }
        
        // Get source path from the request context
        inputDir = (File) requestContext.getRequestParameters().get(XML_DIR);
        if (inputDir == null)
        {
            inputDir = defaultInputDir;
        }

        if (inputDir == null)
        {
            throw new ProcessingException(
                XML_DIR + " parameter of type java.io.File must be set");
        }

        if (!inputDir.isDirectory())
        {
            throw new ProcessingException(
                "File provided in the " + XML_DIR + " parameter must be a directory");
        }

        final File inputFile = new File(inputDir, getQuery());
        try {
            return new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            throw new ProcessingException("File not found or cannot be read: "
                    + inputFile.getAbsolutePath());
        }
    }
}
