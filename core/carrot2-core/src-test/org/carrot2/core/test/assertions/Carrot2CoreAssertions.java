
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
    public static ClusterListAssertion assertThatClusters(List<Cluster> actual)
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
    public static DocumentListAssertion assertThatDocuments(List<Document> actual)
    {
        return new DocumentListAssertion(actual);
    }
}
