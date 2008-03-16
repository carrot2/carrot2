package org.carrot2.source.xml;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.Resource;
import org.carrot2.util.resource.ResourceUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Fetches documents from XML files and streams. For additional flexibility, an XSLT
 * stylesheet can be applied to the XML stream before it is deserialized into Carrot2
 * data.
 */
@Bindable
public class XmlDocumentSource extends ProcessingComponentBase implements DocumentSource
{
    /**
     * The resource to load XML data from. You can either create instances of
     * {@link Resource} implementations directly or use {@link ResourceUtils} to look up
     * {@link Resource} instances from a variety of locations.
     * <p>
     * One special {@link Resource} implementation you can use is
     * {@link ParameterizedUrlResource}. It allows you to specify attribute place holders
     * in the URL that will be replaced during runtime. The place holder format is
     * <code>${attribute}</code>. The following attributes will be resolved:
     * <ul>
     * <li><code>query</code> will be replaced with the current query being processed.
     * If the query has not been provided, this attribute will be substituted with an
     * empty string.</li>
     * <li><code>results</code> will be replaced with the number of results requested.
     * If the number of results has not been provided, this attribute will be substituted
     * with an empty string.</li>
     * </ul>
     * 
     * @label XML Resource
     */
    @Input
    @Processing
    @Attribute
    @Required
    private Resource xml;

    /**
     * The resource to load XSLT stylesheet from. The XSLT stylesheet is optional and is
     * useful when the source XML stream does not follow the Carrot2 format. The XSLT
     * transformation will be applied to the source XML stream, the transformed XML stream
     * will be deserialized into {@link Document}s.
     * <p>
     * The XSLT {@link Resource} can be provided both on initialization and processing
     * time. The stylesheet provided on initialization will be cached for the life time of
     * the component, while processing-time style sheets will be compiled every time
     * processing is requested and will override the initialization-time stylesheet.
     * <p>
     * To pass additional parameters to the XSLT transformer, use the
     * {@link #xsltParameters} attribute.
     * 
     * @label XSLT stylesheet
     */
    @Input
    @Init
    @Processing
    @Attribute
    private Resource xslt;

    /**
     * Parameters to be passed to the XSLT transformer.
     * 
     * @label XSLT parameters
     */
    @Input
    @Init
    @Processing
    @Attribute
    private Map<String, String> xsltParameters = Collections.<String, String> emptyMap();

    @Input
    @Output
    @Processing
    @Attribute(key = AttributeNames.QUERY)
    private String query;

    @Input
    @Processing
    @Attribute(key = AttributeNames.RESULTS)
    private int results = -1;

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    private Collection<Document> documents;

    /**
     * The XSLT resource provided at init. If we want to allow specifying the XSLT both on
     * init and processing, and want to cache the XSLT template provided on init, we must
     * store this reference.
     */
    private Resource initXslt;

    /** A template defined at initialization time, can be null */
    private Templates instanceLevelXslt;

    /** XSLT transformer factory. */
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
     * Creates a new {@link XmlDocumentSource}.
     */
    public XmlDocumentSource()
    {
        this.transformerFactory = TransformerFactory.newInstance();
        this.transformerFactory.setURIResolver(uriResolver);
    }

    @Override
    public void init()
    {
        super.init();

        // Try to initialize the XSLT template, if provided in init attributes
        if (xslt != null)
        {
            initXslt = xslt;
            instanceLevelXslt = loadXslt(xslt);
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        InputStream carrot2XmlInputStream = null;
        try
        {
            // Perform the transformation if stylesheet available
            carrot2XmlInputStream = getCarrot2XmlStream();

            // Deserialize the XML stream
            final ProcessingResult processingResult = ProcessingResult
                .deserialize(carrot2XmlInputStream);

            query = (String) processingResult.getAttributes().get(AttributeNames.QUERY);
            documents = processingResult.getDocuments();

            // Truncate to the requested number of documents if needed
            if (results != -1 && documents.size() > results)
            {
                List<Document> truncatedDocuments = Lists.newArrayList();

                int documentCount = results;
                for (Document document : documents)
                {
                    truncatedDocuments.add(document);
                    if (--documentCount <= 0)
                    {
                        break;
                    }
                }
                documents = truncatedDocuments;
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Could not process query: " + e.getMessage(), e);
        }
        finally
        {
            CloseableUtils.close(carrot2XmlInputStream);
        }
    }

    /**
     * Returns a Carrot2 XML stream, applying an XSLT transformation if the stylesheet is
     * provided.
     */
    private InputStream getCarrot2XmlStream() throws TransformerConfigurationException,
        IOException, TransformerException
    {
        // Resolve the stylesheet to use
        Templates stylesheet = instanceLevelXslt;
        if (xslt != null)
        {
            if (!ObjectUtils.equals(xslt, initXslt))
            {
                stylesheet = loadXslt(xslt);
            }
        }
        else
        {
            stylesheet = null;
        }

        // Perform transformation if stylesheet found
        InputStream carrot2XmlInputStream;
        if (stylesheet != null)
        {
            InputStream xmlInputStream = null;
            try
            {
                // Initialize transformer
                final Transformer transformer = stylesheet.newTransformer();
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Set XSLT parameters, if any
                for (Map.Entry<String, String> entry : xsltParameters.entrySet())
                {
                    transformer.setParameter(entry.getKey(), entry.getValue());
                }

                // Perform transformation
                xmlInputStream = openResource(xml);
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
            carrot2XmlInputStream = openResource(xml);
        }

        return carrot2XmlInputStream;
    }

    /**
     * Loads the XSLT stylesheet from the provided {@link Resource}.
     */
    private Templates loadXslt(Resource xslt)
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

    /**
     * Opens a {@link Resource}, also handles {@link ParameterizedUrlResource}s.
     */
    private InputStream openResource(Resource resource) throws IOException
    {
        InputStream inputStream;
        if (resource instanceof ParameterizedUrlResource)
        {
            // If we got a specialized implementation of the Resource interface,
            // perform substitution of known attributes
            Map<String, Object> attributes = Maps.newHashMap();

            attributes.put("query", (query != null ? query : ""));
            attributes.put("results", (results != -1 ? results : ""));

            inputStream = ((ParameterizedUrlResource) resource).open(attributes);
        }
        else
        {
            // Open the generic Resource instance
            inputStream = resource.open();
        }
        return inputStream;
    }
}
