
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

package org.carrot2.clustering.synthetic;

import java.util.*;

import org.carrot2.core.Document;
import org.carrot2.core.test.TestDocumentFactory;

/**
 * A {@link TestDocumentFactory} that generates documents with the provided URLs.
 */
public class DocumentWithUrlsFactory extends TestDocumentFactory
{
    /**
     * Static instance to be used instead of the constructor.
     */
    public final static DocumentWithUrlsFactory INSTANCE = new DocumentWithUrlsFactory();

    /**
     * Private constructor, use the {@link #INSTANCE} instead.
     */
    private DocumentWithUrlsFactory()
    {
        super(DEFAULT_GENERATORS, DEFAULT_FIELDS);
    }

    /**
     * Generates documents with the provided URLs. The number of generated documents is
     * equal to the number of the <code>urls</code> provided.
     *
     * @param urls URLs for the documents
     * @return documents with provided URLs
     */
    public List<Document> generate(final String [] urls)
    {
        final Map<String, IDataGenerator<?>> customGenerators = new HashMap<String, IDataGenerator<?>>();
        customGenerators.put(Document.CONTENT_URL, new IDataGenerator<String>()
        {
            public String generate(int sequentialNumber)
            {
                return urls[sequentialNumber % urls.length];
            }
        });

        return generate(urls.length, DEFAULT_FIELDS, customGenerators);
    }
}
