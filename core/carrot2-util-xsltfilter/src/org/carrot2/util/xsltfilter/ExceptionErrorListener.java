package org.carrot2.util.xsltfilter;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * An {@link ErrorListener} that throws exceptions on warnings, errors and fatal errors.
 */
final class ExceptionErrorListener implements ErrorListener
{
    public void warning(TransformerException exception) throws TransformerException
    {
        throw exception;
    }

    public void error(TransformerException exception) throws TransformerException
    {
        throw exception;
    }

    public void fatalError(TransformerException exception) throws TransformerException
    {
        throw exception;
    }
}