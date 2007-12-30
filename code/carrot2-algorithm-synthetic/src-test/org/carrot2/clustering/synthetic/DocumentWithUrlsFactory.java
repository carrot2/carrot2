/**
 * 
 */
package org.carrot2.clustering.synthetic;

import java.util.*;

import org.carrot2.core.Document;
import org.carrot2.core.TestDocumentFactory;

/**
 *
 */
public class DocumentWithUrlsFactory extends TestDocumentFactory
{
    public final static DocumentWithUrlsFactory INSTANCE = new DocumentWithUrlsFactory();

    public DocumentWithUrlsFactory()
    {
        super(DEFAULT_GENERATORS, DEFAULT_FIELDS);
    }

    public List<Document> generate(final String [] urls)
    {
        Map<String, DataGenerator<?>> customGenerators = new HashMap<String, DataGenerator<?>>();
        customGenerators.put(Document.CONTENT_URL, new DataGenerator<String>()
        {
            @Override
            public String generate(int sequentialNumber)
            {
                return urls[sequentialNumber % urls.length];
            }
        });

        return generate(urls.length, DEFAULT_FIELDS, customGenerators);
    }
}
