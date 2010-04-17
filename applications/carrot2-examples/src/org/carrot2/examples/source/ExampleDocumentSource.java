
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.source;

import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
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
    @Attribute(key = AttributeNames.QUERY)
    public String query;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @IntRange(min = 1, max = 1000)
    public int results = 20;

    /**
     * Documents produced by this document source. The documents are returned in an output
     * attribute with key equal to {@link AttributeNames#DOCUMENTS},
     */
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    @Internal
    public List<Document> documents;

    /**
     * Modulo to fetch the documents with. This dummy input attribute is just to show how
     * custom input attributes can be implemented.
     */
    @Processing
    @Input
    @Attribute
    public int modulo = 1;

    /**
     * Another dummy attribute. This one shows that if the attribute is not a primitive
     * type for the implementation), {@link ImplementingClasses} constraint must be added to specify
     * which assignable types are allowed as values for the attribute. To allow all
     * assignable values, specify empty {@link ImplementingClasses#classes()} and
     * {@link ImplementingClasses#strict()} equal to <code>false</code>.
     */
    @Processing
    @Input
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    public Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

    @Override
    public void process() throws ProcessingException
    {
        // The input attributes will have already been bound at this point

        // Create a place holder for the results
        this.documents = new ArrayList<Document>();

        // Fetch results.
        final List<Document> inputDocuments = 
            new ArrayList<Document>(SampleDocumentData.DOCUMENTS_DATA_MINING);
        int resultsToPush = Math.min(inputDocuments.size(), this.results);
        for (int i = 0; i < resultsToPush; i++)
        {
            if (i % this.modulo == 0)
            {
                final Document originalDocument = inputDocuments.get(i);

                // For the sake of example we just copy the original document fields
                final Document document = new Document();
                document.setField(Document.TITLE, originalDocument
                    .getField(Document.TITLE));
                document.setField(Document.SUMMARY, "");
                document.setField(Document.CONTENT_URL, originalDocument
                    .getField(Document.CONTENT_URL));
                documents.add(document);
            }
        }

        // We've assigned and populated the documents field and we're done, Carrot2 core
        // will take care of the rest.
    }

    public static void main(String [] args)
    {
        final Controller controller = ControllerFactory.createSimple();
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(AttributeUtils.getKey(ExampleDocumentSource.class, "modulo"), 2);
        params.put(AttributeUtils.getKey(ExampleDocumentSource.class, "analyzer"),
            new WhitespaceAnalyzer());

        final ProcessingResult result = controller.process(params,
            ExampleDocumentSource.class, LingoClusteringAlgorithm.class);

        ConsoleFormatter.displayResults(result);
    }
}
