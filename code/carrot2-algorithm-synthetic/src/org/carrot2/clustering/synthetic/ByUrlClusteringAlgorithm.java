/**
 * 
 */
package org.carrot2.clustering.synthetic;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.parameters.*;
import org.carrot2.util.ArrayUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 *
 */
@Bindable
public class ByUrlClusteringAlgorithm implements ClusteringAlgorithm
{
    @Attribute(key = "documents", bindingDirection = BindingDirection.IN)
    private Collection<Document> documents = Collections.<Document> emptyList();

    @SuppressWarnings("unused")
    @Attribute(key = "clusters", bindingDirection = BindingDirection.OUT)
    private Collection<Cluster> clusters = null;

    private static final Set<String> STOP_URL_PARTS;
    static
    {
        STOP_URL_PARTS = new HashSet<String>();
        STOP_URL_PARTS.add("www");
    }

    @Override
    public void performProcessing() throws ProcessingException
    {
        // Just in case we get a linked list, create an array of documents
        final Document [] documentArray = this.documents
            .toArray(new Document [this.documents.size()]);

        // Prepare an array of url parts
        String [][] urlParts = buildUrlParts(documentArray);

        // Recursively build the cluster structure
        List<Integer> documentIndexes = new ArrayList<Integer>(documentArray.length);
        for (int i = 0; i < documentArray.length; i++)
        {
            documentIndexes.add(new Integer(i));
        }
        this.clusters = createClusters(documentArray, documentIndexes, urlParts, 0, "");
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
    }

    @Override
    public void afterProcessing()
    {
    }

    @Override
    public void init() throws InitializationException
    {
    }

    @Override
    public void destroy()
    {
    }

    private List<Cluster> createClusters(Document [] documents,
        Collection<Integer> documentIndexes, String [][] urlParts, int level,
        String labelSuffix)
    {
        Multimap<String, Integer> urlPartToDocumentIndex = new HashMultimap<String, Integer>();
        for (Integer documentIndex : documentIndexes)
        {
            String [] urlPartsForDocument = urlParts[documentIndex.intValue()];
            if (urlPartsForDocument != null && urlPartsForDocument.length > level
                && !STOP_URL_PARTS.contains(urlPartsForDocument[level]))
            {
                urlPartToDocumentIndex.put(urlPartsForDocument[level], documentIndex);
            }
        }

        Set<Integer> documentsInClusters = new HashSet<Integer>();
        List<Cluster> clusters = new ArrayList<Cluster>();
        for (Iterator<String> it = urlPartToDocumentIndex.keySet().iterator(); it
            .hasNext();)
        {
            String urlPart = it.next();
            Collection<Integer> indexes = urlPartToDocumentIndex.get(urlPart);

            if (indexes.size() > 1)
            {
                Cluster cluster = new Cluster();
                String clusterLabel = urlPart
                    + (labelSuffix.length() > 0 ? "." + labelSuffix : "");

                List<Cluster> subclusters = createClusters(documents, indexes, urlParts,
                    level + 1, clusterLabel);
                if (subclusters.size() > 1)
                {
                    cluster.addSubclusters(subclusters);
                }
                else
                {
                    // only one subcluster -- move the label one level up
                    if (subclusters.size() == 1)
                    {
                        final Cluster subcluster = subclusters.get(0);
                        clusterLabel = subcluster.getPhrases().get(0);
                        cluster.addDocuments(subcluster.getDocuments());
                        cluster.addSubclusters(subcluster.getSubclusters());
                    }
                    else
                    {
                        for (Iterator<Integer> it2 = indexes.iterator(); it2.hasNext();)
                        {
                            Integer documentIndex = it2.next();
                            cluster.addDocuments(documents[documentIndex.intValue()]);
                        }
                    }
                }

                cluster.addPhrases(clusterLabel);
                clusters.add(cluster);
                documentsInClusters.addAll(indexes);
            }
        }

        // Sort clusters
        Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);

        // Add junk clusters
        Cluster otherUrls = new Cluster();
        otherUrls.addPhrases("Other Sites");
        otherUrls.setAttribute(Cluster.OTHER_TOPICS, false);

        for (Integer documentIndex : documentIndexes)
        {
            if (!documentsInClusters.contains(documentIndex))
            {
                otherUrls.addDocuments(documents[documentIndex]);
            }
        }

        if (!otherUrls.getDocuments().isEmpty())
        {
            clusters.add(otherUrls);
        }

        return clusters;
    }

    String [][] buildUrlParts(final Document [] documents)
    {
        final String [][] urlParts = new String [documents.length] [];
        for (int i = 0; i < documents.length; i++)
        {
            final String url = documents[i].getField(Document.CONTENT_URL);
            if (url == null)
            {
                continue;
            }

            int colonSlashSlashIndex = url.indexOf("://");
            if (colonSlashSlashIndex < 0)
            {
                colonSlashSlashIndex = 0;
            }
            else if (colonSlashSlashIndex + 3 >= url.length())
            {
                continue;
            }
            else
            {
                colonSlashSlashIndex += 3;
            }

            int slashIndex = url.indexOf('/', colonSlashSlashIndex + 3);
            if (slashIndex < 0)
            {
                slashIndex = url.length();
            }

            final String urlMainPart = url.substring(colonSlashSlashIndex, slashIndex)
                .toLowerCase();

            final String [] splitUrl = urlMainPart.split("\\.");
            ArrayUtils.reverse(splitUrl);
            urlParts[i] = splitUrl;
        }

        return urlParts;
    }
}
