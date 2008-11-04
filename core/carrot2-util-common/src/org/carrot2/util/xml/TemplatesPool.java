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

package org.carrot2.util.xml;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

/**
 * A pool of precompiled XSLT stylesheets ({@link Template}s). Caching can be disabled
 * via constructor parameter or via setting a system property:
 * 
 * <pre>
 * template.caching
 * </pre>
 * 
 * to <code>false</code>.
 */
public final class TemplatesPool
{
    private final static Logger logger = Logger.getLogger(TemplatesPool.class); 
    
    /**
     * Global system property disabling template caching. This property can also be set at
     * runtime (after the pool is initialized).
     */
    public static String TEMPLATE_CACHING_PROPERTY = "template.caching";

    /**
     * A map of precompiled stylesheets ({@link Templates} objects).
     */
    private volatile HashMap<String, Templates> stylesheets = new HashMap<String, Templates>();

    /**
     * If <code>true</code> the templates will not be cached until the application shuts
     * down. This speeds up the application, but may be annoying, especially during
     * development.
     */
    private final boolean templateCaching;

    /**
     * {@link SAXTransformerFactory} capable of producing SAX-based transformers.
     */
    public final SAXTransformerFactory tFactory;

    /**
     * Creates a {@link TemplatesPool} with caching enabled.
     */
    public TemplatesPool() throws Exception
    {
        this(true);
    }

    /**
     * Check for required facilities. If not available, an exception will be thrown.
     */
    public TemplatesPool(boolean templateCaching) throws Exception
    {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final String processorClass = tFactory.getClass().getName();
        logger.info("XSLT transformer factory: " + processorClass);
        if (processorClass.indexOf(".xsltc.") >= 0)
        {
            logger.warn("XSLTC processor is used. Expect memory leaks when redeploying Web applications.");
        }

        if (!tFactory.getFeature(SAXSource.FEATURE)
            || !tFactory.getFeature(SAXResult.FEATURE))
        {
            throw new Exception(
                "Required source types not supported by the transformer factory.");
        }

        if (!tFactory.getFeature(SAXResult.FEATURE)
            || !tFactory.getFeature(StreamResult.FEATURE))
        {
            throw new Exception(
                "Required result types not supported by the transformer factory.");
        }

        if (!(tFactory instanceof SAXTransformerFactory))
        {
            throw new Exception(
                "TransformerFactory not an instance of SAXTransformerFactory: "
                    + tFactory.getClass().getName());
        }

        this.tFactory = ((SAXTransformerFactory) tFactory);
        this.tFactory.setErrorListener(new StylesheetErrorListener());
        this.templateCaching = templateCaching;
    }

    /**
     * @return returns the identity transformer handler.
     */
    public TransformerHandler getIdentityTransformerHandler()
        throws TransformerConfigurationException
    {
        return tFactory.newTransformerHandler();
    }

    /**
     * Retrieves a previously stored template, if available.
     */
    public Templates getTemplate(String key)
    {
        if (!isCaching())
        {
            return null;
        }

        return stylesheets.get(key);
    }

    /**
     * Add a new template to the pool. Addition is quite costly as it replaces the
     * internal {@link #stylesheets} {@link HashMap}.
     */
    public void addTemplate(String key, Templates template)
    {
        if (!isCaching())
        {
            return;
        }

        /*
         * Copy-on-write.
         */
        synchronized (this)
        {
            final HashMap<String, Templates> newMap = new HashMap<String, Templates>(
                this.stylesheets);
            newMap.put(key, template);
            this.stylesheets = newMap;
        }
    }

    /**
     * @return <code>true</code> if template caching is enabled.
     */
    private boolean isCaching()
    {
        /*
         * Global override takes precedence.
         */
        final String global = System.getProperty(TEMPLATE_CACHING_PROPERTY);
        if (global != null)
        {
            return Boolean.parseBoolean(global);
        }

        return templateCaching;
    }

    /**
     * Compile a {@link Template} from a given system identifier. The template is not
     * added to the pool, a manual call to {@link #addTemplate(String, Templates)} is
     * required.
     */
    public Templates compileTemplate(String systemId) throws SAXException
    {
        final StreamSource source = new StreamSource(systemId);
        try
        {
            return tFactory.newTemplates(source);
        }
        catch (Exception e)
        {
            throw new SAXException("Could not compile stylesheet: " + systemId, e);
        }
    }

    /**
     * Compile a {@link Template} from a given stream. The template is not added to the
     * pool automatically.
     */
    public Templates compileTemplate(InputStream stream) throws SAXException
    {
        final StreamSource source = new StreamSource(stream);
        try
        {
            return tFactory.newTemplates(source);
        }
        catch (Exception e)
        {
            throw new SAXException("Could not compile stylesheet.", e);
        }
    }

    /**
     * Return a new {@link TransformerHandler} based on a given precompiled
     * {@link Template}. The handler {@link Transformer}'s {@link ErrorListener} is set
     * to {@link TransformerErrorListener} to raise exceptions and give proper warnings.
     */
    public TransformerHandler newTransformerHandler(Templates template)
        throws TransformerConfigurationException
    {
        final TransformerHandler handler = this.tFactory.newTransformerHandler(template);

        /*
         * We want to raise transformer exceptions on <xml:message terminate="true">, so
         * we add a custom listener. Also, various XSLT processors react in different ways
         * to transformation errors -- some of them report error as recoverable, some of
         * them report error as unrecoverable.
         */
        handler.getTransformer().setErrorListener(new TransformerErrorListener());
        return handler;
    }

    /**
     * Return a new {@link Transformer}.
     * 
     * @see #newTransformerHandler(Templates)
     */
    public Transformer newTransformer(Templates t)
        throws TransformerConfigurationException
    {
        return newTransformerHandler(t).getTransformer();
    }
}