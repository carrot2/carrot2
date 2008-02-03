/**
 * 
 */
package org.carrot2.clustering.synthetic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.test.ClusteringAlgorithmTest;
import org.junit.Test;

/**
 *
 */
public class ByUrlClusteringAlgorithmTest extends
    ClusteringAlgorithmTest<ByUrlClusteringAlgorithm>
{
    @Override
    public Class<ByUrlClusteringAlgorithm> getComponentClass()
    {
        return ByUrlClusteringAlgorithm.class;
    }

    @Test
    public void testUrlParsing()
    {
        Collection<Document> docs = DocumentWithUrlsFactory.INSTANCE
            .generate(new String []
            {
                "cos.pl", "http://cos.pl/cos", "cos.pl/cos", "http://", null
            });

        ByUrlClusteringAlgorithm instance = new ByUrlClusteringAlgorithm();

        String [][] actualUrlParts = instance.buildUrlParts(docs
            .toArray(new Document [docs.size()]));
        String [][] expectedUrlParts = new String [] []
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
        List<Document> docs = DocumentWithUrlsFactory.INSTANCE.generate(new String []
        {
            "cos.pl", "http://cos.pl/cos", "cos.pl/cos"
        });

        List<Cluster> expectedFacets = Arrays.asList(new Cluster []
        {
            new Cluster("cos.pl", docs.get(0), docs.get(1), docs.get(2))
        });

        assertEquals(expectedFacets, cluster(docs));
    }

    @Test
    public void testStopPartsStripping()
    {
        List<Document> docs = DocumentWithUrlsFactory.INSTANCE.generate(new String []
        {
            "www.cos.pl", "http://cos.pl/cos", "cos.pl/cos"
        });

        List<Cluster> expectedFacets = Arrays.asList(new Cluster []
        {
            new Cluster("cos.pl", docs.get(0), docs.get(1), docs.get(2))
        });

        assertEquals(expectedFacets, cluster(docs));
    }

    @Test
    public void testOneUrlWithTwoSuburls()
    {
        List<Document> docs = DocumentWithUrlsFactory.INSTANCE.generate(new String []
        {
            "mail.cos.pl", "http://cos.pl/cos", "cos.pl/cos", "mail.cos.pl"
        });

        List<Cluster> expectedFacets = new ArrayList<Cluster>();
        Cluster facet11 = new Cluster("mail.cos.pl", docs.get(0), docs.get(3));
        Cluster facet12 = new Cluster("Other Sites", docs.get(1), docs.get(2))
            .setAttribute(Cluster.OTHER_TOPICS, Boolean.TRUE);
        Cluster facet1 = new Cluster("cos.pl").addSubclusters(facet11, facet12);
        expectedFacets.add(facet1);

        assertEquals(expectedFacets, cluster(docs));
    }

    @Test
    public void testSorting()
    {
        List<Document> docs = DocumentWithUrlsFactory.INSTANCE.generate(new String []
        {
            "cos.pl", "http://cos.pl/cos", "cos.com/cos", "cos.com", "cos.pl"
        });

        List<Cluster> expectedFacets = Arrays.asList(new Cluster []
        {
            new Cluster("cos.pl", docs.get(0), docs.get(1), docs.get(4)),
            new Cluster("cos.com", docs.get(2), docs.get(3))
        });

        assertEquals(expectedFacets, cluster(docs));
    }
}
