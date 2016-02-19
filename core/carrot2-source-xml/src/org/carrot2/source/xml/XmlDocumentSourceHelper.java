
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

package org.carrot2.source.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.StreamUtils;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.xslt.NopURIResolver;
import org.carrot2.util.xslt.TemplatesPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Exposes the common functionality a {@link IDocumentSource} based on XML/XSLT is likely
 * to need. This helper does note expose any attributes, so that different implementations
 * can decide which attributes they expose.
 */
@Bindable(prefix = "XmlDocumentSourceHelper")
public class XmlDocumentSourceHelper
{
    /**
     * Data transfer timeout. Specifies the data transfer timeout, in seconds. A timeout value of 
     * zero is interpreted as an infinite timeout.  
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 0, max = 5 * 60)
    @Label("Data transfer timeout")
    @Level(AttributeLevel.ADVANCED)
    @Group(SimpleSearchEngine.SERVICE)
    public int timeout = 8;

    /** Precompiled XSLT templates. */
    private final TemplatesPool pool;

    /**
     * URI resolver. Does nothing.
     */
    private final static URIResolver uriResolver = new NopURIResolver();
    
    private final static Logger log = LoggerFactory.getLogger(XmlDocumentSourceHelper.class);

    /**
     *
     */
    public XmlDocumentSourceHelper()
    {
        try
        {
            // No template caching.
            this.pool = new TemplatesPool(false);
            this.pool.tFactory.setURIResolver(uriResolver);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a {@link ProcessingResult} from the provided {@link InputStream}, applying
     * XSLT transform if specified. The provided {@link InputStream} will be closed.
     */
    public ProcessingResult loadProcessingResult(InputStream xml, Templates stylesheet,
        Map<String, String> xsltParameters) throws Exception
    {
        InputStream carrot2XmlStream = null;
        try
        {
            carrot2XmlStream = getCarrot2XmlStream(xml, stylesheet, xsltParameters);
            return ProcessingResult.deserialize(carrot2XmlStream);
        }
        finally
        {
            CloseableUtils.close(carrot2XmlStream, xml);
        }
    }

    /**
     * Returns a Carrot2 XML stream, applying an XSLT transformation if the stylesheet is
     * provided.
     */
    private InputStream getCarrot2XmlStream(InputStream xmlInputStream,
        Templates stylesheet, Map<String, String> xsltParameters)
        throws TransformerConfigurationException, IOException, TransformerException
    {
        // Perform transformation if stylesheet found.
        InputStream carrot2XmlInputStream;
        if (stylesheet != null)
        {
            byte [] debugInput = null;
            try
            {
                // Initialize transformer
                final Transformer transformer = pool.newTransformer(stylesheet);
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Set XSLT parameters, if any
                if (xsltParameters != null)
                {
                    for (Map.Entry<String, String> entry : xsltParameters.entrySet())
                    {
                        transformer.setParameter(entry.getKey(), entry.getValue());
                    }
                }

                if (log.isDebugEnabled())
                {
                    debugInput = StreamUtils.readFullyAndClose(xmlInputStream);
                    xmlInputStream = new ByteArrayInputStream(debugInput);
                }

                // Perform transformation
                transformer.transform(new StreamSource(xmlInputStream), new StreamResult(
                    outputStream));
                carrot2XmlInputStream = new ByteArrayInputStream(
                    outputStream.toByteArray());
            }
            catch (TransformerException e)
            {
                if (debugInput != null)
                {
                    log.debug("Transformer input: " + new String(debugInput, "UTF-8"));
                }
                throw e;
            }
            finally
            {
                CloseableUtils.close(xmlInputStream);
            }
        }
        else
        {
            carrot2XmlInputStream = xmlInputStream;
        }

        return carrot2XmlInputStream;
    }

    /**
     * Loads the XSLT stylesheet from the provided {@link IResource}.
     */
    public Templates loadXslt(IResource xslt)
    {
        InputStream is = null;
        try
        {
            is = xslt.open();
            return pool.compileTemplate(is);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (SAXException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            CloseableUtils.close(is);
        }
    }

}
