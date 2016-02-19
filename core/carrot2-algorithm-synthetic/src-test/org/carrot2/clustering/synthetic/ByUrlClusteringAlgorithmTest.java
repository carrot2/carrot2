
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
import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Test cases for the {@link ByUrlClusteringAlgorithm}.
 */
public class ByUrlClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<ByUrlClusteringAlgorithm>
{
    @Override
    public Class<ByUrlClusteringAlgorithm> getComponentClass()
    {
        return ByUrlClusteringAlgorithm.class;
    }

    @Test
    public void testUrlParsing()
    {
        final Collection<Document> docs = DocumentWithUrlsFactory.INSTANCE
            .generate(new String []
            {
                "cos.pl", "http://cos.pl/cos", "cos.pl/cos", "http://", null
            });

        final ByUrlClusteringAlgorithm instance = new ByUrlClusteringAlgorithm();

        final String [][] actualUrlParts = instance.buildUrlParts(docs
            .toArray(new Document [docs.size()]));
        final String [][] expectedUrlParts = new String [] []
        {
            {
                "pl", "cos"
            },
            {
                "pl", "cos"
            },
            {
                "pl", "cos"
            }, null, null
        };

        assertArrayEquals("Url parts equality", expectedUrlParts, actualUrlParts);
    }

    @Test
    public void testOneUrl()
    {
        final List<Document> docs = DocumentWithUrlsFactory.INSTANCE
            .generate(new String []
            {
                "cos.pl", "http://cos.pl/cos", "cos.pl/cos"
            });

        final List<Cluster> expectedFacets = Lists.newArrayList(new Cluster("cos.pl",
            docs.get(0), docs.get(1), docs.get(2)));

        final ArrayList<Cluster> actual = Lists.newArrayList(cluster(docs).getClusters());
        assertThatClusters(actual).isEquivalentTo(expectedFacets);
    }

    @Test
    public void testStopPartsStripping()
    {
        final List<Document> docs = DocumentWithUrlsFactory.INSTANCE
            .generate(new String []
            {
                "www.cos.pl", "http://cos.pl/cos", "cos.pl/cos"
            });

        final List<Cluster> expectedFacets = Lists.newArrayList(new Cluster("cos.pl",
            docs.get(0), docs.get(1), docs.get(2)));

        assertThatClusters(cluster(docs).getClusters()).isEquivalentTo(expectedFacets);
    }

    @Test
    public void testOneUrlWithTwoSuburls()
    {
        final List<Document> docs = DocumentWithUrlsFactory.INSTANCE
            .generate(new String []
            {
                "mail.cos.pl", "http://cos.pl/cos", "cos.pl/cos", "mail.cos.pl"
            });

        final List<Cluster> expectedFacets = Lists.newArrayList();
        final Cluster facet11 = new Cluster("mail.cos.pl", docs.get(0), docs.get(3));
        final Cluster facet12 = new Cluster("Other Sites", docs.get(1), docs.get(2))
            .setOtherTopics(true);
        final Cluster facet1 = new Cluster("cos.pl").addSubclusters(facet11, facet12);
        expectedFacets.add(facet1);

        assertThatClusters(cluster(docs).getClusters()).isEquivalentTo(expectedFacets);
    }

    @Test
    public void testSorting()
    {
        final List<Document> docs = DocumentWithUrlsFactory.INSTANCE
            .generate(new String []
            {
                "cos.pl", "http://cos.pl/cos", "cos.com/cos", "cos.com", "cos.pl"
            });

        final List<Cluster> expectedFacets = Lists.newArrayList(new Cluster("cos.pl",
            docs.get(0), docs.get(1), docs.get(4)), new Cluster("cos.com", docs.get(2),
            docs.get(3)));

        assertThatClusters(cluster(docs).getClusters()).isEquivalentTo(expectedFacets);
    }
}
