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

package org.carrot2.util.xsltfilter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.xml.transform.*;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.log4j.Logger;
import org.carrot2.util.xml.TemplatesPool;
import org.carrot2.util.xml.TransformerErrorListener;
import org.xml.sax.*;

/**
 * A SAX handler that detects <code>xml-stylesheet</code> directive and delegates SAX
 * events to a declared transformer.
 */
final class TransformingDocumentHandler implements ContentHandler
{
    private static final Logger log = Logger.getLogger(TransformingDocumentHandler.class);

    /**
     * A map of XSLT output methods and their corresponding MIME content types.
     */
    private final static HashMap<String, String> methodMapping;
    static
    {
        methodMapping = new HashMap<String, String>();
        methodMapping.put("xml", "application/xml");
        methodMapping.put("html", "text/html");
        methodMapping.put("text", "text/plain");
    }

    /**
     * A regular expression for extracting <code>xml-stylesheet</code>'s
     * <code>type</code> pseudo-attribute.
     */
    private final Pattern typePattern = Pattern.compile(
        "(type[ \t]*=[ \\t]*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE);

    /**
     * A regular expression for extracting <code>xml-stylesheet</code>'s
     * <code>href</code> pseudo-attribute.
     */
    private final Pattern hrefPattern = Pattern.compile(
        "(href[ \\t]*=[ \\t]*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE);

    /**
     * Base application URL for resolving stylesheet URIs (scheme, host and port). For
     * example:
     * 
     * <pre>
     * http://localhost:8080
     * </pre>
     */
    private String baseApplicationURL;

    /**
     * The default handler used when no <code>xml-stylesheet</code> directive is
     * specified in the XML stream.
     */
    private TransformerHandler defaultHandler;

    /**
     * The actual content handler (transformer) used for processing the input.
     */
    private TransformerHandler contentHandler;

    /**
     * Servlet context for resolving local paths.
     */
    private ServletContext context;

    /**
     * A result sink where the transformation output should be redirected.
     */
    private Result result;

    /**
     * Transformer error listener.
     */
    private TransformerErrorListener transformerErrorListener = new TransformerErrorListener();

    /**
     * Locator instance used by this handler is also shared with the transformation
     * handler.
     */
    private Locator locator;

    /**
     * A pool of precompiled stylesheets.
     */
    private TemplatesPool pool;

    /**
     * 
     */
    private IContentTypeListener contentTypeListener;

    /**
     * A set of stylesheet parameters, copied from the request context when the
     * transformation begins.
     */
    private final Map<String, Object> stylesheetParams;

    /**
     * Creates a SAX handler with the given base application URL and context path. The
     * base URL is needed to resolve host-relative stylesheet URIs. Application context
     * path is used to initialize local streams instead of requesting the stylesheet via
     * HTTP.
     */
    public TransformingDocumentHandler(String baseApplicationURL, ServletContext context,
        Map<String, Object> stylesheetParams, TemplatesPool pool)
    {
        this.baseApplicationURL = baseApplicationURL;
        this.context = context;
        this.pool = pool;
        this.stylesheetParams = stylesheetParams;
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void startDocument() throws SAXException
    {
        // Empty. We don't know the actual content handler yet.
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void characters(final char [] ch, final int start, final int length)
        throws SAXException
    {
        initContentHandler();
        contentHandler.characters(ch, start, length);
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void endDocument() throws SAXException
    {
        initContentHandler();
        try
        {
            contentHandler.endDocument();
        }
        catch (RuntimeException t)
        {
            final TransformerException transformerException = transformerErrorListener.exception;
            if (transformerException != null)
            {
                final Throwable cause = transformerException.getCause();
                if (cause != null && cause instanceof Exception)
                {
                    throw new SAXException("XSLT transformation error.",
                        (Exception) cause);
                }
                else
                {
                    throw new SAXException("XSLT transformation error.",
                        transformerException);
                }
            }
        }
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        initContentHandler();
        contentHandler.endElement(namespaceURI, localName, qName);
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void endPrefixMapping(String prefix) throws SAXException
    {
        initContentHandler();
        contentHandler.endPrefixMapping(prefix);
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void ignorableWhitespace(char [] ch, int start, int length)
        throws SAXException
    {
        /*
         * Pass ignorable whitespace if we have a content handler. Before content handler
         * initialization simply ignore these calls. We could queue SAX events until
         * content handler is available, but would it make any sense?
         */
        if (contentHandler != null)
        {
            contentHandler.ignorableWhitespace(ch, start, length);
        }
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        this.initContentHandler();
        contentHandler.startPrefixMapping(prefix, uri);
    }

    /**
     * {@link ContentHandler} implementation. Detect processing instructions and see if we
     * have <code>xml-stylesheet</code> anywhere.
     */
    public void processingInstruction(String target, String data) throws SAXException
    {
        if (contentHandler == null)
        {
            inspectProcessingInstruction(this, target, data);
        }

        initContentHandler();
        contentHandler.processingInstruction(target, data);
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void skippedEntity(String name) throws SAXException
    {
        this.initContentHandler();
        contentHandler.skippedEntity(name);
    }

    /**
     * {@link ContentHandler} implementation.
     */
    public void startElement(String namespaceURI, String localName, String qName,
        Attributes atts) throws SAXException
    {
        this.initContentHandler();
        contentHandler.startElement(namespaceURI, localName, qName, atts);
    }

    /**
     * Replaces the default transformer handler with the given one.
     */
    private void setTransformerHandler(TransformerHandler fallbackHandler)
        throws SAXException
    {
        if (contentHandler != null)
        {
            throw new SAXException(
                "Some input has been already processed. Cannot change the handler anymore. "
                    + "Place xml-stylesheet "
                    + "directive immediately at the top of the XML file.");
        }

        final Transformer transformer = fallbackHandler.getTransformer();
        /*
         * Pass any stylesheet parameters to the transformer.
         */
        if (stylesheetParams != null)
        {
            for (Iterator<Map.Entry<String, Object>> i = stylesheetParams.entrySet()
                .iterator(); i.hasNext();)
            {
                final Map.Entry<String, Object> entry = i.next();
                transformer.setParameter((String) entry.getKey(), entry.getValue());
            }
        }

        this.defaultHandler = fallbackHandler;
    }

    /**
     * Sets a {@link IContentTypeListener} for this transformation.
     */
    public final void setContentTypeListener(IContentTypeListener l)
    {
        this.contentTypeListener = l;
    }

    /**
     * This method should be invoked to cleanup after processing is done.
     */
    public final void cleanup()
    {
        if (this.defaultHandler != null)
        {
            /*
             * Reset the default handler's transformer.
             */
            this.defaultHandler.getTransformer().reset();
        }
    }

    /**
     * Sets the result sink for the xslt transformation.
     */
    public void setTransformationResult(Result result)
    {
        this.result = result;
    }

    /**
     * Inspect a processing instruction looking for <code>xml-stylesheet</code>
     * directives. If found, update the
     * {@link TransformingDocumentHandler#setTransformerHandler(TransformerHandler)}
     * appropriately.
     */
    public void inspectProcessingInstruction(TransformingDocumentHandler handler,
        String target, String data) throws SAXException
    {
        if (!target.equals("xml-stylesheet"))
        {
            return;
        }

        /*
         * Break up pseudo-attributes and look for content-type
         */
        final Matcher typeMatcher = typePattern.matcher(data);
        if (!typeMatcher.find())
        {
            log
                .warn("xml-stylesheet directive with no type attribute (should be text/xsl).");
            return;
        }
        final String type = typeMatcher.group(2);
        if (!"text/xsl".equals(type))
        {
            log
                .warn("xml-stylesheet directive with incorrect type (should be text/xsl): "
                    + type);
            return;
        }

        final Matcher hrefMatcher = hrefPattern.matcher(data);
        if (!hrefMatcher.find())
        {
            log.warn("xml-stylesheet directive with no 'href' pseudo-attribute.");
            return;
        }

        /*
         * Resolve the stylesheet URI.
         */
        final String unresolvedURI = hrefMatcher.group(2);
        final String resolvedURI = resolveTemplateURL(unresolvedURI);
        if (resolvedURI == null)
        {
            throw new SAXException(
                "Stylesheet not recognized (try webapp-relative path starting with '@/'): "
                    + unresolvedURI);
        }

        /*
         * Check the pool for precompiled cached Templates
         */
        Templates template;
        try
        {
            template = pool.getTemplate(resolvedURI);
            if (template == null)
            {
                template = pool.compileTemplate(resolvedURI);
                pool.addTemplate(resolvedURI, template);
            }

            // Find out about the content type and encoding.
            if (contentTypeListener != null)
            {
                final Properties outputProps = template.getOutputProperties();
                final String encoding;

                String contentType = null;
                if (outputProps.containsKey(OutputKeys.MEDIA_TYPE))
                {
                    contentType = outputProps.getProperty(OutputKeys.MEDIA_TYPE);
                }
                else if (outputProps.containsKey(OutputKeys.METHOD))
                {
                    final String method = outputProps.getProperty(OutputKeys.METHOD);
                    contentType = (String) methodMapping.get(method);
                }

                if (contentType == null)
                {
                    // Default content type.
                    contentType = (String) methodMapping.get("xml");
                }

                if (outputProps.containsKey(OutputKeys.ENCODING))
                {
                    encoding = outputProps.getProperty(OutputKeys.ENCODING);
                }
                else
                {
                    encoding = "UTF-8";
                }
                contentTypeListener.setContentType(contentType, encoding);
            }

            final TransformerHandler tHandler = pool.newTransformerHandler(template);
            tHandler.getTransformer().setErrorListener(transformerErrorListener);
            handler.setTransformerHandler(tHandler);
        }
        catch (TransformerConfigurationException e)
        {
            log.error("Transformer configuration exception.", e);
        }
    }

    /**
     * Attempt to resolve the template URI. <code>null</code> is returned if this method
     * fails to find a resource matching the given path.
     */
    private String resolveTemplateURL(String path) throws SAXException
    {
        String templatesURL = null;

        if (path.startsWith("@/"))
        {
            /*
             * The stylesheet is in the application context which is unknown to the XML
             * source. Expand to the current application context and replace with a local
             * resource URL (should be safe; I assume XMLs come from the same source as
             * the application).
             */
            try
            {
                final String resourcePath = path.substring(1);
                final URL resource = context.getResource(resourcePath);
                if (resource == null)
                {
                    throw new SAXException("Context-relative stylesheet does not exist: "
                        + resourcePath);
                }

                templatesURL = resource.toExternalForm();
                log.debug("Context-path relative (expanded) xml-stylesheet URL: " + path
                    + " resolved as: " + templatesURL);
            }
            catch (MalformedURLException e)
            {
                // Will never happen, but just in case.
                throw new RuntimeException();
            }
        }
        else if (path.startsWith("/"))
        {
            /*
             * Try host-relative URL. Simply concatenate with the context URI.
             */
            templatesURL = this.baseApplicationURL + path;
            log.debug("Context-relative xml-stylesheet URL: " + path + " resolved as: "
                + templatesURL);
        }
        else
        {
            // Try absolute URL.
            try
            {
                templatesURL = new URL(path).toExternalForm();
                log.debug("Absolute xml-stylesheet URL: " + templatesURL);
            }
            catch (MalformedURLException e)
            {
                // Ignore, malformed URL.
            }
        }

        return templatesURL;
    }

    /**
     * Initializes the content handler because content is about to be sent to the result.
     * If no content handler is available, throws an exception.
     */
    private final void initContentHandler() throws SAXException
    {
        if (contentHandler == null)
        {
            if (defaultHandler == null)
            {
                log.info("Stylesheet not specified, using identity handler.");
                try
                {
                    this.defaultHandler = this.pool.getIdentityTransformerHandler();
                }
                catch (TransformerConfigurationException e)
                {
                    throw new RuntimeException("Could not create identity handler.");
                }
                if (contentTypeListener != null)
                {
                    contentTypeListener.setContentType((String) methodMapping.get("xml"),
                        null);
                }
            }

            log.debug("XSLT transformation using handler: "
                + defaultHandler.getClass().getName());

            this.contentHandler = defaultHandler;
            this.contentHandler.setResult(result);
            this.contentHandler.startDocument();
            if (locator != null)
            {
                this.contentHandler.setDocumentLocator(locator);
            }
        }

        if (transformerErrorListener.exception != null)
        {
            throw new SAXException("XSLT transformation error.",
                transformerErrorListener.exception);
        }
    }
}