
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

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.*;

import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Test cases for the {@link ByFieldClusteringAlgorithm}
 */
public class ByFieldClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<ByFieldClusteringAlgorithm>
{
    @Override
    public Class<ByFieldClusteringAlgorithm> getComponentClass()
    {
        return ByFieldClusteringAlgorithm.class;
    }

    private static final String FIELD = "field";

    @Test
    public void testScalarField()
    {
        final Document d11 = documentWithField("1");
        final Document d12 = documentWithField("1");
        final Document d21 = documentWithField("2");
        final Document d22 = documentWithField("2");
        final Document dn = documentWithField(null);
        final List<Document> documents = Lists.newArrayList(d11, d12, d21, d22, dn);

        check(documents, Lists.<Cluster> newArrayList(new Cluster("1", d11, d12),
            new Cluster("2", d21, d22)), dn);
    }

    @Test
    public void testCollectionField()
    {
        final Document d11 = documentWithField(Lists.newArrayList("11", "12"));
        final Document d12 = documentWithField(Lists.newArrayList("11"));
        final Document d21 = documentWithField(Lists.newArrayList("21", "22"));
        final Document d22 = documentWithField(Lists.newArrayList("23"));
        final Document dn = documentWithField(null);
        final List<Document> documents = Lists.newArrayList(d11, d12, d21, d22, dn);

        check(documents, Lists.<Cluster> newArrayList(new Cluster("11", d11, d12),
            new Cluster("12", d11), new Cluster("21", d21), new Cluster("22", d21),
            new Cluster("23", d22)), dn);
    }

    private void check(final List<Document> documents,
        final List<Cluster> expectedClusters, Document... unclustered)
    {
        processingAttributes.put(AttributeUtils.getKey(ByFieldClusteringAlgorithm.class,
            "fieldName"), FIELD);
        final List<Cluster> clusters = cluster(documents).getClusters();
        if (unclustered.length > 0)
        {
            final Cluster otherTopics = new Cluster("Other Topics", unclustered);
            otherTopics.setOtherTopics(true);
            expectedClusters.add(otherTopics);
        }
        assertThatClusters(clusters).isEquivalentTo(expectedClusters);
    }

    private Document documentWithField(Object fieldValue)
    {
        final Document document = new Document();
        document.setField(FIELD, fieldValue);
        return document;
    }
}
