package org.carrot2.core;

import java.util.Arrays;
import java.util.Iterator;

import org.carrot2.core.parameters.Bindable;
import org.carrot2.core.parameters.Binding;
import org.carrot2.core.parameters.BindingPolicy;
import org.carrot2.core.parameters.Constraint;
import org.carrot2.core.parameters.HasConstraint;

@Bindable
public class TestDocumentSource implements DocumentSource
{
    @Binding(policy = BindingPolicy.RUNTIME)
    int numDocs;

    @Binding(policy = BindingPolicy.RUNTIME)
    @HasConstraint(TestConstraint.class)
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
