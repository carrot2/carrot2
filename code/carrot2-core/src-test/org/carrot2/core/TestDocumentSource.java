package org.carrot2.core;

import java.util.Arrays;
import java.util.Iterator;

import org.carrot2.core.parameters.Bindable;
import org.carrot2.core.parameters.Parameter;
import org.carrot2.core.parameters.BindingPolicy;

@Bindable
public class TestDocumentSource implements DocumentSource
{
    @Parameter(policy = BindingPolicy.RUNTIME)
    int numDocs;

    @Parameter(policy = BindingPolicy.RUNTIME)
    String query;
    

    public Iterator<Document> getDocuments()
    {
        final Document [] result = new Document [numDocs];
        for (int i = 0; i < numDocs; i++)
        {
            result[i] = new Document();
        }
        return Arrays.asList(result).iterator();
    }
}
