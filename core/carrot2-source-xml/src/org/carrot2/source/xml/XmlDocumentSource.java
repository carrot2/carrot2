package org.carrot2.source.xml;

import java.io.InputStream;
import java.net.ConnectException;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Fetches documents from XML files and stream.
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
    @Init
    @Processing
    @Attribute
    @Required
    private Resource resource;

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

    /*
     * 
     */
    @Override
    public void process() throws ProcessingException
    {
        InputStream inputStream = null;
        try
        {
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

            // Deserialize the XML stream
            final ProcessingResult processingResult = ProcessingResult
                .deserialize(inputStream);

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
            CloseableUtils.close(inputStream);
        }
    }
}
