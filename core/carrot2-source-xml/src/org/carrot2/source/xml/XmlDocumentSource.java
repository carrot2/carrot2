package org.carrot2.source.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.resource.*;

import com.google.common.collect.Maps;

/**
 * Fetches documents from XML files and streams. For additional flexibility, an XSLT
 * stylesheet can be applied to the XML stream before it is deserialized into Carrot2
 * data.
 */
/**
 *
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
     * <li><code>query</code> will be replaced with the current query being processed. If
     * the query has not been provided, this attribute will be substituted with an empty
     * string.</li>
     * <li><code>results</code> will be replaced with the number of results requested. If
     * the number of results has not been provided, this attribute will be substituted
     * with an empty string.</li>
     * </ul>
     * 
     * @label XML Resource
     * @level Basic
     */
    @Input
    @Processing
    @Attribute
    @Required
    @ImplementingClasses(classes =
    {
        FileResource.class, ParameterizedUrlResource.class, URLResource.class
    }, strict = false)
    public Resource xml;

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
     * @level Medium
     */
    @Input
    @Init
    @Processing
    @Attribute
    @ImplementingClasses(classes =
    {
        FileResource.class, ParameterizedUrlResource.class, URLResource.class
    }, strict = false)
    public Resource xslt;

    /**
     * Parameters to be passed to the XSLT transformer.
     * 
     * @label XSLT parameters
     * @level Advanced
     */
    @Input
    @Init
    @Processing
    @Attribute
    public Map<String, String> xsltParameters = Maps.immutableMap();

    /**
     * Query to be used to fetch the documents (see {@link #xml}). After processing, the
     * query read from the XML data, if any.
     */
    @Input
    @Output
    @Processing
    @Attribute(key = AttributeNames.QUERY)
    public String query;

    /**
     * The number of {@link Document}s to read from the XML data. Set to <code>-1</code>
     * to load all {@link Document}s available in the XML data.
     */
    @Input
    @Processing
    @Attribute(key = AttributeNames.RESULTS)
    public int results = -1;

    /**
     * {@link Document}s read from the XML data.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    /**
     * The XSLT resource provided at init. If we want to allow specifying the XSLT both on
     * init and processing, and want to cache the XSLT template provided on init, we must
     * store this reference.
     */
    private Resource initXslt;

    /** A template defined at initialization time, can be null */
    private Templates instanceLevelXslt;

    /** A helper class that groups common functionality for XML/XSLT based data sources. */
    private final XmlDocumentSourceHelper xmlDocumentSourceHelper = new XmlDocumentSourceHelper();

    /**
     * Creates a new {@link XmlDocumentSource}.
     */
    public XmlDocumentSource()
    {
    }

    @Override
    public void init()
    {
        super.init();

        // Try to initialize the XSLT template, if provided in init attributes
        if (xslt != null)
        {
            initXslt = xslt;
            instanceLevelXslt = xmlDocumentSourceHelper.loadXslt(xslt);
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        try
        {
            final ProcessingResult processingResult = xmlDocumentSourceHelper
                .loadProcessingResult(openResource(xml), resolveStylesheet(),
                    xsltParameters);

            query = (String) processingResult.getAttributes().get(AttributeNames.QUERY);
            documents = processingResult.getDocuments();

            // Truncate to the requested number of documents if needed
            if (results != -1 && documents.size() > results)
            {
                documents = documents.subList(0, results);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Could not process query: " + e.getMessage(), e);
        }
    }

    /**
     *
     */
    private Templates resolveStylesheet()
    {
        // Resolve the stylesheet to use
        Templates stylesheet = instanceLevelXslt;
        if (xslt != null)
        {
            if (!ObjectUtils.equals(xslt, initXslt))
            {
                stylesheet = xmlDocumentSourceHelper.loadXslt(xslt);
            }
        }
        else
        {
            stylesheet = null;
        }
        return stylesheet;
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
