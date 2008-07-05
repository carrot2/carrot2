package org.carrot2.webapp.test;

import java.util.List;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;

/**
 * A document "source" that generates documents with certain properties.
 */
@Bindable
public class TestDocumentSource extends ProcessingComponentBase implements DocumentSource
{
    /**
     * Number of results to fetch.
     * 
     * @label Results count
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    public int results = 100;

    /**
     * Search query to execute.
     * 
     * @label Query
     */
    @Processing
    @Input
    @Attribute(key = AttributeNames.QUERY)
    @Required
    public String query;

    /**
     * A collection of documents retrieved for the query.
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    @Override
    public void process() throws ProcessingException
    {
        documents = Lists.newArrayList();

        for (int i = 0; i < results; i++)
        {
            final Document document = new Document();
            document.addField(Document.TITLE, "Document " + (i + 1));
            document.addField(Document.SUMMARY, "Document " + (i + 1));
            document
                .addField(Document.CONTENT_URL, "http://document " + (i + 1) + ".com");
            
            documents.add(document);
        }
    }
}
