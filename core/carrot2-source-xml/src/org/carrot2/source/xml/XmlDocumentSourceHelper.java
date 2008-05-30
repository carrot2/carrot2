package org.carrot2.source.xml;

import java.io.*;
import java.util.Map;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.carrot2.core.DocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.Resource;

/**
 * Exposes the common functionality a {@link DocumentSource} based on XML/XSLT is likely
 * to need. This helper does note expose any attributes, so that different implementations
 * can decide which attributes they expose.
 */
public class XmlDocumentSourceHelper
{
    /** Transformer factory */
    private final TransformerFactory transformerFactory;

    /**
     * URI resolver. Does nothing.
     */
    private final static URIResolver uriResolver = new URIResolver()
    {
        public Source resolve(String href, String base) throws TransformerException
        {
            return null;
        }
    };

    /**
     *
     */
    public XmlDocumentSourceHelper()
    {
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformerFactory.setURIResolver(uriResolver);
    }

    /**
     * Loads a {@link ProcessingResult} from the provided {@link InputStream}, applying
     * XSLT transform if specified. The provided {@link InputStream} will be closed.
     */
    public ProcessingResult loadProcessingResult(InputStream xml, Templates stylesheet,
        Map<String, String> xsltParameters) throws Exception
    {
        // Perform the transformation if stylesheet available
        Reader carrot2XmlReader = null;

        try
        {
            carrot2XmlReader = new InputStreamReader(getCarrot2XmlStream(xml, stylesheet,
                xsltParameters));

            // Deserialize the XML stream
            return ProcessingResult.deserialize(carrot2XmlReader);
        }
        finally
        {
            CloseableUtils.close(carrot2XmlReader);
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
        // Perform transformation if stylesheet found
        InputStream carrot2XmlInputStream;
        if (stylesheet != null)
        {
            try
            {
                // Initialize transformer
                final Transformer transformer = stylesheet.newTransformer();
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Set XSLT parameters, if any
                if (xsltParameters != null)
                {
                    for (Map.Entry<String, String> entry : xsltParameters.entrySet())
                    {
                        transformer.setParameter(entry.getKey(), entry.getValue());
                    }
                }

                // Perform transformation
                transformer.transform(new StreamSource(xmlInputStream), new StreamResult(
                    outputStream));
                carrot2XmlInputStream = new ByteArrayInputStream(outputStream
                    .toByteArray());
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
     * Loads the XSLT stylesheet from the provided {@link Resource}.
     */
    public Templates loadXslt(Resource xslt)
    {
        InputStream templateInputStream = null;
        try
        {
            templateInputStream = xslt.open();

            if (!transformerFactory.getFeature(SAXSource.FEATURE)
                || !transformerFactory.getFeature(SAXResult.FEATURE))
            {
                throw new RuntimeException(
                    "Required source types not supported by the Transformer Factory.");
            }

            if (!transformerFactory.getFeature(SAXResult.FEATURE)
                || !transformerFactory.getFeature(StreamResult.FEATURE))
            {
                throw new RuntimeException(
                    "Required result types not supported by the Transformer Factory.");
            }

            if (!(transformerFactory instanceof SAXTransformerFactory))
            {
                throw new RuntimeException(
                    "TransformerFactory not an instance of SAXTransformerFactory");
            }

            transformerFactory.setErrorListener(new ErrorListener()
            {
                public void warning(TransformerException exception)
                    throws TransformerException
                {
                    throw exception;
                }

                public void error(TransformerException exception)
                    throws TransformerException
                {
                    throw exception;
                }

                public void fatalError(TransformerException exception)
                    throws TransformerException
                {
                    throw exception;
                }
            });

            try
            {
                final Templates newTemplates = transformerFactory
                    .newTemplates(new StreamSource(templateInputStream));
                return newTemplates;
            }
            catch (TransformerConfigurationException e)
            {
                throw new RuntimeException("Could not compile stylesheet.", e);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load stylesheet", e);
        }
        finally
        {
            CloseableUtils.close(templateInputStream);
        }
    }

}
