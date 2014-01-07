
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.source;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.core.attribute.CommonAttributesDescriptor.Keys;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * An example {@link IDocumentSource} that accepts a list of {@link Document}s
 * and returns a filtered list ({@link #modulo}).  
 */
@Bindable
public class ModuloDocumentSource extends ProcessingComponentBase implements
    IDocumentSource
{
    /**
     * The query won't matter to us but we bind it anyway.
     */
    @Processing
    @Input
    @Attribute(key = CommonAttributesDescriptor.Keys.QUERY)
    public String query;

    /**
     * Maximum number of results to return.
     */
    @Processing
    @Input
    @Attribute(key = CommonAttributesDescriptor.Keys.RESULTS)
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

    /**
     * Documents accepted and returned by this document source. 
     * The documents are returned in an output 
     * attribute with key equal to {@link Keys#DOCUMENTS},
     */
    @Processing
    @Input
    @Output
    @Attribute(key = CommonAttributesDescriptor.Keys.DOCUMENTS)
    @Internal
    public List<Document> documents;

    /**
     * A non-primitive attribute do demonstrate the need for
     * {@link org.carrot2.util.attribute.constraint.ImplementingClasses} constraint. 
     * It must be added to specify
     * which assignable types are allowed as values for the attribute. To allow all
     * assignable values, specify empty 
     * {@link org.carrot2.util.attribute.constraint.ImplementingClasses#classes()} and
     * {@link org.carrot2.util.attribute.constraint.ImplementingClasses#strict()} equal to <code>false</code>.
     */
    @SuppressWarnings("deprecation")
    @Processing
    @Input
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    public Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

    /**
     * Processing routine.
     */
    @Override
    public void process() throws ProcessingException
    {
        // The input attributes will have already been bound at this point

        // Create a copy of the input list and filter.
        final List<Document> filtered = new ArrayList<Document>();
        for (int i = 0; i < documents.size() && filtered.size() < results ; i++)
        {
            if (i % this.modulo == 0)
            {
                final Document originalDocument = documents.get(i);

                // For the sake of example we just copy the original document fields.
                final Document document = new Document();
                document.setField(Document.TITLE, originalDocument.getTitle());
                document.setField(Document.SUMMARY, "");
                document.setField(Document.CONTENT_URL, originalDocument.getField(Document.CONTENT_URL));
                filtered.add(document);
            }
        }

        // We've assigned and populated the documents field and we're done. Write
        // the output list of documents to an output attribute.
        this.documents = filtered;
    }
}
