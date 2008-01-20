
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.aggregator;

import java.util.*;

import org.carrot2.core.clustering.RawDocument;

/**
 * A simple search results merger that takes into account only the URL of the
 * document and simple (literal) content comparisons.
 * 
 * @author Stanislaw Osinski
 */
public class SimpleResultsMerger implements ResultsMerger
{
    private static final String PROPERTY_AGGREGATOR_SOURCE_RANKS = "aggr-src-weight";

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.input.aggregator.ResultsMerger#mergeResults(java.util.Map)
     */
    public List mergeResults(Map resultSets, AggregatorInput [] inputs)
    {
        // Put the results into a flat list and store the source id into
        // each document
        List results = flattenResultSets(resultSets, inputs);

        // Build similarity matrix
        boolean [][] similarity = new boolean [results.size()] [results.size()];
        for (int i = 1; i < results.size(); i++)
        {
            for (int j = 0; j < i; j++)
            {
                similarity[i][j] = computeSimilarity((RawDocument) results
                    .get(i), (RawDocument) results.get(j));
                similarity[j][i] = similarity[i][j];
            }
        }

        List mergedDocuments = mergeDocuments(results, similarity);
        sortDocuments(mergedDocuments);

        return mergedDocuments;
    }

    private void sortDocuments(List results)
    {
        Collections.sort(results, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                RawDocument d1 = (RawDocument) o1;
                RawDocument d2 = (RawDocument) o2;

                int [] r1 = (int []) d1
                    .getProperty(PROPERTY_AGGREGATOR_SOURCE_RANKS);
                int [] r2 = (int []) d2
                    .getProperty(PROPERTY_AGGREGATOR_SOURCE_RANKS);

                // Prefer documents with more sources
                if (r1.length != r2.length)
                {
                    return r2.length - r1.length;
                }

                int rank1 = 0;
                int rank2 = 0;
                for (int i = 0; i < r2.length; i++)
                {
                    rank1 += r1[i];
                    rank2 += r2[i];
                }

                return rank2 - rank1;
            }
        });
    }

    private List mergeDocuments(List results, boolean [][] similarity)
    {
        List mergedDocuments = new ArrayList();

        // Find coherent sub-graphs using breadth-first search
        Set nodesQueued = new HashSet();
        for (int i = 0; i < results.size(); i++)
        {
            Integer nodeIndex = new Integer(i);
            if (!nodesQueued.contains(nodeIndex))
            {
                List nodeQueue = new LinkedList();
                nodeQueue.add(nodeIndex);
                nodesQueued.add(nodeIndex);
                List documentGroup = new ArrayList();

                while (!nodeQueue.isEmpty())
                {
                    // Get a node from the queue
                    Integer node = (Integer) nodeQueue.get(0);
                    nodeQueue.remove(0);

                    // Add to the current sub-graph (document group)
                    documentGroup.add(node);

                    // Add all its non-checked neighbours to the queue
                    for (int j = 0; j < results.size(); j++)
                    {
                        if (similarity[node.intValue()][j])
                        {
                            Integer nodeIndex2 = new Integer(j);
                            if (!nodesQueued.contains(nodeIndex2))
                            {
                                nodeQueue.add(nodeIndex2);
                                nodesQueued.add(nodeIndex2);
                            }
                        }
                    }
                }

                mergedDocuments
                    .add(createMergedDocument(documentGroup, results));
            }
        }
        return mergedDocuments;
    }

    private Object createMergedDocument(List documentGroup, List results)
    {
        RawDocument preferredDocument = (RawDocument) results
            .get(((Integer) documentGroup.get(0)).intValue());
        if (documentGroup.size() == 1)
        {
            return preferredDocument;
        }

        int index = 0;
        String [] sources = new String [documentGroup.size()];
        int [] ranks = new int [documentGroup.size()];
        int contentLength = -1;

        for (Iterator it = documentGroup.iterator(); it.hasNext();)
        {
            int documentIndex = ((Integer) it.next()).intValue();
            RawDocument document = (RawDocument) results.get(documentIndex);
            sources[index] = ((String []) document
                .getProperty(RawDocument.PROPERTY_SOURCES))[0];
            ranks[index] = ((int []) document
                .getProperty(PROPERTY_AGGREGATOR_SOURCE_RANKS))[0];
            index++;

            int documentLength = 0;
            if (document.getTitle() != null)
            {
                documentLength += document.getTitle().length() * 3;
            }

            if (document.getSnippet() != null)
            {
                documentLength += document.getSnippet().length();
            }

            if (documentLength > contentLength)
            {
                preferredDocument = document;
                contentLength = documentLength;
            }
        }

        // Each document appears in a document group only once, so we
        // can safely reuse the preferred document to form the result
        preferredDocument.setProperty(RawDocument.PROPERTY_SOURCES, sources);
        preferredDocument.setProperty(PROPERTY_AGGREGATOR_SOURCE_RANKS, ranks);
        Arrays.sort(sources);

        return preferredDocument;
    }

    private boolean computeSimilarity(RawDocument documentA,
        RawDocument documentB)
    {
        String sourceA = ((String []) documentA
            .getProperty(RawDocument.PROPERTY_SOURCES))[0];
        String sourceB = ((String []) documentB
            .getProperty(RawDocument.PROPERTY_SOURCES))[0];

        // Don't check for duplicates among documents from the same source
        if (sourceA != null && sourceA.equals(sourceB))
        {
            return false;
        }

        // Very unlikely that titles and snippets are equal, but in case...
        if (documentA.getTitle() != null
            && documentA.getTitle().equals(documentB.getTitle())
            && documentA.getSnippet() != null
            && documentA.getSnippet().equals(documentB.getSnippet()))
        {
            return true;
        }

        return documentA.getUrl() != null
            && documentA.getUrl().equals(documentB.getUrl());
    }

    private List flattenResultSets(Map resultSets, AggregatorInput [] inputs)
    {
        // Determine the maximum length of the individual results list
        int maxRank = 0;
        for (Iterator iter = resultSets.values().iterator(); iter.hasNext();)
        {
            List list = (List) iter.next();
            maxRank = Math.max(maxRank, list.size());
        }

        List results = new ArrayList();

        for (int i = 0; i < inputs.length; i++)
        {
            List individualResults = (List) resultSets.get(inputs[i].inputId);
            if (individualResults != null)
            {
                int rank = 0;
                for (Iterator iter = individualResults.iterator(); iter
                    .hasNext(); rank++)
                {
                    RawDocument document = (RawDocument) iter.next();
                    document.setProperty(RawDocument.PROPERTY_SOURCES,
                        new String []
                        {
                            inputs[i].inputId
                        });
                    document.setProperty(PROPERTY_AGGREGATOR_SOURCE_RANKS,
                        new int []
                        {
                            (maxRank - rank) * inputs.length + inputs.length
                                - i
                        });
                    results.add(document);

                }
            }
        }

        return results;
    }

}
