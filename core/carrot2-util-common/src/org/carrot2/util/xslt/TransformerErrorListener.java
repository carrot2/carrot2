
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.xslt;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;

/**
 * An {@link ErrorListener} that reacts to errors when transforming (applying) a
 * stylesheet.
 */
public final class TransformerErrorListener implements ErrorListener
{
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(TransformerErrorListener.class);

    /**
     * We store the exception internally as a workaround to xalan, which reports
     * {@link TransformerException} as {@link RuntimeException} (wrapped).
     */
    public TransformerException exception;

    /*
     * 
     */
    public void warning(TransformerException e) throws TransformerException
    {
        logger.warn("Warning (recoverable): " + e.getMessage());
    }

    /*
     * 
     */
    public void error(TransformerException e) throws TransformerException
    {
        logger.warn("Error (recoverable): " + e.getMessage());
    }

    /**
     * Unrecoverable errors cause an exception to be rethrown.
     */
    public void fatalError(TransformerException e) throws TransformerException
    {
        logger.error("Fatal error: " + e.getMessage());

        this.exception = e;
        throw e;
    }
}
