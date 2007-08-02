package org.carrot2.util.xsltfilter;

/**
 * A callback listener for providing information about the content type
 * and encoding of the output.
 * 
 * @author Dawid Weiss
 */
interface ContentTypeListener {
    public void setContentType(String contentType, String encoding);
}
