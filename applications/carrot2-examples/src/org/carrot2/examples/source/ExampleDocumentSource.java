package org.carrot2.examples.source;

import java.util.*;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * This example shows how to implement a simple Carrot2 {@link IDocumentSource}.
 */
@Bindable
public class ExampleDocumentSource extends ProcessingComponentBase implements
    IDocumentSource
{
    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    public String query;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @IntRange(min = 1, max = 1000)
    public int results = 20;

    /**
     * Modulo to fetch the documents with. This dummy input attribute is just to show how
     * custom input attributes can be implemented.
     */
    @Processing
    @Input
    @Attribute
    public int modulo = 1;

    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    @Internal
    public List<Document> documents;

    @Override
    public void process() throws ProcessingException
    {
        // The input attributes will have already been bound at this point

        // Create a place holder for the results
        this.documents = new ArrayList<Document>();

        // Fetch results
        int resultsToPush = Math.min(ExampleUtils.documentContent.length, this.results);
        for (int i = 0; i < resultsToPush; i++)
        {
            if (i % this.modulo == 0)
            {
                Document document = new Document();
                document.addField(Document.TITLE, ExampleUtils.documentContent[i][0]);
                document.addField(Document.SUMMARY, "");
                document.addField(Document.CONTENT_URL,
                    ExampleUtils.documentContent[i][1]);
                documents.add(document);
            }
        }

        // We've assigned and populated the documents field and we're done, Carrot2 core
        // will take care of the rest.
    }

    public static void main(String [] args)
    {
        final IController controller = new SimpleController();
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(AttributeUtils.getKey(ExampleDocumentSource.class, "modulo"), 2);

        final ProcessingResult result = controller.process(params,
            ExampleDocumentSource.class, LingoClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
    }
}
