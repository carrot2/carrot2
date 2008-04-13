package org.carrot2.core.test.assertions;

import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;

/**
 * A number of FEST-style assertions for Carrot2 core classes. These are especially useful
 * as Carrot2 core classes by design don't override {@link Object#equals(Object)} and
 * {@link Object#hashCode()}.
 */
public class Carrot2CoreAssertions
{
    /**
     * No instantiation.
     */
    private Carrot2CoreAssertions()
    {
    }

    /**
     * Creates a {@link ClusterAssertion} object.
     * 
     * @param actual the actual cluster to make assertions on
     * @return the assertion object
     */
    public static ClusterAssertion assertThat(Cluster actual)
    {
        return new ClusterAssertion(actual);
    }

    /**
     * Creates a {@link ClusterListAssertion} object.
     * 
     * @param actual the actual cluster list to make assertions on
     * @return the assertion object
     */
    public static ClusterListAssertion assertThat(List<Cluster> actual)
    {
        return new ClusterListAssertion(actual);
    }

    /**
     * Creates a {@link DocumentAssertion} object.
     * 
     * @param actual the actual document to make assertions on
     * @return the assertion object
     */
    public static DocumentAssertion assertThat(Document actual)
    {
        return new DocumentAssertion(actual);
    }

    /**
     * Creates a {@link DocumentListAssertion} object.
     * 
     * @param actual the actual document list to make assertions on
     * @return the assertion object
     */
    public static DocumentListAssertion assertThat(List<Document> actual)
    {
        return new DocumentListAssertion(actual);
    }
}
