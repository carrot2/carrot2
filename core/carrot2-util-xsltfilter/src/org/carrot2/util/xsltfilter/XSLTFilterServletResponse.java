
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

package org.carrot2.util.xsltfilter;

import java.io.*;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.carrot2.util.xslt.TemplatesPool;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A wrapper around a {@link HttpServletResponse} which attempts to detect the type of
 * output acquired from the servlet chain and apply a stylesheet to it if all conditions
 * mentioned in {@link XSLTFilter} are met.
 */
final class XSLTFilterServletResponse extends HttpServletResponseWrapper
{
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(XSLTFilterServletResponse.class);

    /**
     * If true, the stream will be passed verbatim to the next filter. This usually
     * happens when the output has a mime type different than <code>text/xml</code>.
     */
    private boolean passthrough;

    /**
     * The actual {@link HttpServletResponse}.
     */
    private HttpServletResponse origResponse = null;

    /**
     * The actual {@link HttpServletRequest}.
     */
    private HttpServletRequest origRequest;

    /**
     * The {@link ServletOutputStream} returned from {@link #getOutputStream()} or
     * <code>null</code>.
     */
    private ServletOutputStream stream = null;

    /**
     * The {@link PrintWriter} returned from {@link #getWriter()} or <code>null</code>.
     */
    private PrintWriter writer = null;

    /**
     * A pool of stylesheets used for XSLT processing.
     */
    private TemplatesPool transformers;

    /**
     * Servlet context for resolving local paths.
     */
    private ServletContext context;

    /**
     * Creates an XSLT filter servlet response for a single request, wrapping a given
     * {@link HttpServletResponse}.
     * 
     * @param response The original chain's {@link HttpServletResponse}.
     * @param request The original chain's {@link HttpServletRequest}.
     * @param transformers A pool of transformers to be used with this request.
     */
    public XSLTFilterServletResponse(HttpServletResponse response,
        HttpServletRequest request, ServletContext context, TemplatesPool transformers)
    {
        super(response);

        this.origResponse = response;
        this.transformers = transformers;
        this.origRequest = request;
        this.context = context;
    }

    /**
     * We override this method to detect XML data streams.
     */
    public void setContentType(String contentType)
    {
        // Check if XSLT processing has been suppressed for this request.
        final boolean processingSuppressed = processingSuppressed(origRequest);

        if (processingSuppressed)
        {
            // Processing is suppressed.
            log.debug("XSLT processing disabled for the request.");
        }

        if (!processingSuppressed
            && (contentType.startsWith("text/xml") || 
                contentType.startsWith("application/xml")))
        {
            /*
             * We have an XML data stream. Do not enforce the content type. If needed,
             * the XSLT stylesheet can override it via the xsl:output instruction.
             */
            origResponse.setContentType(contentType);
        }
        else
        {
            /*
             * The input is something we won't process anyway, so simply passthrough all
             * data directly to the output stream.
             */
            if (!processingSuppressed)
            {
                log.info("Content type is not text/xml or application/xml ("
                    + contentType + "), passthrough.");
            }

            origResponse.setContentType(contentType);
            passthrough = true;

            // If the output stream is already initialized, passthrough everything.
            if (stream != null && stream instanceof DeferredOutputStream)
            {
                try
                {
                    ((DeferredOutputStream) stream).passthrough(origResponse
                        .getOutputStream());
                }
                catch (IOException e)
                {
                    ((DeferredOutputStream) stream).setExceptionOnNext(e);
                }
            }
        }
    }

    /**
     * Return <code>true</code> if the original request contained XSLT suppressing key.
     * 
     * @see XSLTFilterConstants#NO_XSLT_PROCESSING
     */
    private boolean processingSuppressed(HttpServletRequest origRequest2)
    {
        return (origRequest.getAttribute(XSLTFilterConstants.NO_XSLT_PROCESSING) != null)
            | (origRequest.getParameter(XSLTFilterConstants.NO_XSLT_PROCESSING) != null);
    }

    /**
     * We do not delegate content length because it will most likely change.
     */
    public void setContentLength(final int length)
    {
        log.debug("Original content length (ignored): " + length);
    }

    /**
     * Flush the internal buffers. This only works if XSLT transformation is suppressed.
     */
    public void flushBuffer() throws IOException
    {
        if (stream != null) {
            this.stream.flush();
        }
    }

    /**
     * Return the byte output stream for this response. This is either the original stream
     * or a buffered stream.
     * 
     * @exception IllegalStateException Thrown when character stream has been already
     *                initialized ({@link #getWriter()}).
     */
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (writer != null)
        {
            throw new IllegalStateException(
                "Character stream has been already initialized. Use streams consequently.");
        }

        if (stream != null)
        {
            return stream;
        }

        if (passthrough)
        {
            stream = origResponse.getOutputStream();
        }
        else
        {
            stream = new DeferredOutputStream();
        }

        return stream;
    }

    /**
     * Return the character output stream for this response. This is either the original
     * stream or a buffered stream.
     * 
     * @exception IllegalStateException Thrown when byte stream has been already
     *                initialized ({@link #getOutputStream()}).
     */
    public PrintWriter getWriter() throws IOException
    {
        if (stream != null)
        {
            throw new IllegalStateException(
                "Byte stream has been already initialized. Use streams consequently.");
        }

        if (writer != null)
        {
            return writer;
        }

        if (passthrough)
        {
            writer = this.origResponse.getWriter();
            return writer;
        }

        /*
         * TODO: The character encoding should be extracted in {@link #setContentType()},
         * saved somewhere locally and used here. The response's character encoding may be
         * different (depends on the stylesheet).
         */
        final String charEnc = origResponse.getCharacterEncoding();

        this.stream = new DeferredOutputStream();
        if (charEnc != null)
        {
            writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
        }
        else
        {
            writer = new PrintWriter(stream);
        }

        return writer;
    }

    /**
     * This method must be invoked at the end of processing. The streams are closed and
     * their content is analyzed. Actual XSLT processing takes place here.
     */
    @SuppressWarnings("unchecked")
    void finishResponse() throws IOException
    {
        if (writer != null)
        {
            writer.close();
        }
        else
        {
            if (stream != null) stream.close();
        }

        /*
         * If we're not in passthrough mode, then we need to finalize XSLT transformation.
         */
        if (false == passthrough)
        {
            if (stream != null)
            {
                final byte [] bytes = ((DeferredOutputStream) stream).getBytes();
                final boolean processingSuppressed = (origRequest
                    .getAttribute(XSLTFilterConstants.NO_XSLT_PROCESSING) != null)
                    | (origRequest.getParameter(XSLTFilterConstants.NO_XSLT_PROCESSING) != null);

                if (processingSuppressed)
                {
                    // Just copy the buffered data to the output directly.
                    final OutputStream os = origResponse.getOutputStream();
                    os.write(bytes);
                    os.close();
                }
                else
                {
                    // Otherwise apply XSLT transformation to it.
                    try
                    {
                        processWithXslt(
                            bytes, 
                            (Map<String, Object>) origRequest.getAttribute(XSLTFilterConstants.XSLT_PARAMS_MAP),
                            origResponse);
                    }
                    catch (TransformerException e)
                    {
                        final Throwable t = unwrapCause(e);
                        if (t instanceof IOException)
                        {
                            throw (IOException) t;
                        }

                        filterError("Error applying stylesheet.", e);
                    }
                }
            }
        }
    }

    /**
     * Unwraps original throwable from the transformer/ SAX stack.
     */
    private Throwable unwrapCause(TransformerException e)
    {
        Throwable t;

        if (e.getException() != null)
        {
            t = e.getException();
        }
        else if (e.getCause() != null)
        {
            t = e.getCause();
        }
        else
        {
            return e;
        }

        do
        {
            if (t instanceof IOException)
            {
                // break early on IOException
                return t;
            }
            else if (t.getCause() != null)
            {
                t = t.getCause();
            }
            else if (t instanceof SAXException
                && ((SAXException) t).getException() != null)
            {
                t = ((SAXException) t).getException();
            }
            else
            {
                return t;
            }
        }
        while (true);
    }

    /**
     * Process the byte array (input XML) with the XSLT stylesheet and push the result to
     * the output stream.
     */
    private void processWithXslt(byte [] bytes,
        final Map<String, Object> stylesheetParams, final HttpServletResponse response)
        throws TransformerConfigurationException, TransformerException, IOException
    {
        final TransformingDocumentHandler docHandler;
        try
        {
            final XMLReader reader = XMLReaderFactory.createXMLReader();

            docHandler = new TransformingDocumentHandler(origRequest, context,
                stylesheetParams, transformers);

            docHandler.setContentTypeListener(new IContentTypeListener()
            {
                public void setContentType(String contentType, String encoding)
                {
                    if (encoding == null)
                    {
                        response.setContentType(contentType);
                    }
                    else
                    {
                        response.setContentType(contentType + "; charset=" + encoding);
                    }
                    try
                    {
                        docHandler.setTransformationResult(new StreamResult(response
                            .getOutputStream()));
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Could not open output stream.");
                    }
                }
            });

            reader.setContentHandler(docHandler);
            try
            {
                reader.parse(new InputSource(new ByteArrayInputStream(bytes)));
            }
            finally
            {
                docHandler.cleanup();
            }
        }
        catch (SAXException e)
        {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                char [] hex = "0123456789abcdef".toCharArray();
                for (int i = 0; i < bytes.length; i++) {
                    int c = bytes[i] & 0xff;
                    sb.append(hex[c >>> 4]);
                    sb.append(hex[c & 0xf]);
                }
                log.debug("Failed to parse the following input (hex-encoded): " 
                    + sb.toString(), e);
            }

            final Exception nested = e.getException();
            if (nested != null)
            {
                if (nested instanceof IOException)
                {
                    throw (IOException) nested;
                }
                else if (nested instanceof TransformerException)
                {
                    throw (TransformerException) nested;
                }
            }

            throw new TransformerException("Input parsing exception.", e);
        }
    }

    /**
     * Attempts to send an internal server error HTTP error, if possible. Otherwise simply
     * pushes the exception message to the output stream.
     * 
     * @param message Message to be printed to the logger and to the output stream.
     * @param t Exception that caused the error.
     */
    protected void filterError(String message, Throwable t)
    {
        log.error("XSLT filter error: " + message, t);
        if (false == origResponse.isCommitted())
        {
            // Reset the buffer and previous status code.
            origResponse.reset();
            origResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            origResponse.setContentType("text/html; charset=UTF-8");
        }

        // Response committed. Just push the error to the output stream.
        try
        {
            final OutputStream os = origResponse.getOutputStream();
            final PrintWriter osw = new PrintWriter(new OutputStreamWriter(os,
                "iso8859-1"));
            osw.write("<html><body><!-- " + XSLTFilterConstants.ERROR_TOKEN + " -->");
            osw.write("<h1 style=\"color: red; margin-top: 1em;\">");
            osw.write("Internal server exception");
            osw.write("</h1>");
            osw.write("<b>URI</b>: " + origRequest.getRequestURI() + "\n<br/><br/>");
            serializeException(osw, t);
            if (t instanceof ServletException
                && ((ServletException) t).getRootCause() != null)
            {
                osw.write("<br/><br/><h2>ServletException root cause:</h2>");
                serializeException(osw, ((ServletException) t).getRootCause());
            }
            osw.write("</body></html>");
            osw.flush();
        }
        catch (IOException e)
        {
            // Not much to do in such case (connection broken most likely).
            log.debug("Filter error could not be returned to client.");
        }
    }

    /**
     * Utility method to serialize an exception and its stack trace to simple HTML.
     */
    private final void serializeException(PrintWriter osw, Throwable t)
    {
        osw.write("<b>Exception</b>: " + t.toString() + "\n<br/><br/>");
        osw.write("<b>Stack trace:</b>");
        osw
            .write("<pre style=\"margin: 1px solid red; padding: 3px; font-family: sans-serif; font-size: small;\">");
        t.printStackTrace(osw);
        osw.write("</pre>");
    }

    /**
     * 
     */
    private void detectErrorResponse(int errorCode)
    {
        if (errorCode != HttpServletResponse.SC_ACCEPTED)
        {
            origRequest
                .setAttribute(XSLTFilterConstants.NO_XSLT_PROCESSING, Boolean.TRUE);
        }
    }

    /**
     * 
     */
    public void sendError(int errorCode) throws IOException
    {
        detectErrorResponse(errorCode);
        super.sendError(errorCode);
    }

    /**
     * 
     */
    public void sendError(int errorCode, String message) throws IOException
    {
        detectErrorResponse(errorCode);
        super.sendError(errorCode, message);
    }

    /**
     * 
     */
    public void setStatus(int statusCode)
    {
        detectErrorResponse(statusCode);
        super.setStatus(statusCode);
    }

    /**
     * 
     */
    public void setStatus(int statusCode, String message)
    {
        detectErrorResponse(statusCode);
        super.setStatus(statusCode, message);
    }
}
