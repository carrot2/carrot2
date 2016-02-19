
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

package org.carrot2.source.ambient;

import java.io.*;
import java.util.*;

import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.util.*;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.IResource;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * Handles data of test collections developed by Fondazione Ugo Bordoni.
 */
class FubTestCollection
{
    /**
     * The total number of Ambient topics.
     */
    int topicCount;

    /**
     * Documents by topic id.
     */
    final Map<Integer, List<Document>> documentsByTopicId;

    /**
     * Numbers of documents for each subtopic.
     */
    final Map<String, Integer> subtopicSizes;

    /**
     * Human-readable descriptions of topics.
     */
    final Map<String, String> subtopicLabels;

    public FubTestCollection(String basePath)
    {
        /** [topicId][resultIndex] = subopicId */
        final int [][] resultSubtopicIds = loadSubtopicMapping(new ClassResource(
            AmbientDocumentSource.class, basePath + "/STRel.txt"));

        documentsByTopicId = loadDocuments(new ClassResource(AmbientDocumentSource.class,
            basePath + "/results.txt"), resultSubtopicIds);
        subtopicSizes = prepareSubtopicSizes(resultSubtopicIds);

        subtopicLabels = loadSubtopicLabels(new ClassResource(
            AmbientDocumentSource.class, basePath + "/subTopics.txt"));
    }

    protected int getTopicCount()
    {
        return topicCount;
    }

    protected List<Document> getDocumentsForTopic(int topicId, int requestedResults,
        final int minTopicSize, final boolean includeDocumentsWithoutTopic)
        throws ProcessingException
    {
        // Filter the results
        final List<Document> documents = Lists.newArrayList(Collections2.filter(
            documentsByTopicId.get(topicId), new Predicate<Document>()
            {
                public boolean apply(Document document)
                {
                    // For now there is only one topic per document in Ambient
                    final String documentTopic = getTopic(document);
                    return subtopicSizes.get(documentTopic) >= minTopicSize
                        && (includeDocumentsWithoutTopic || !documentTopic.endsWith(".0"));
                }
            }));

        if (documents.size() >= requestedResults)
        {
            return documents.subList(0, requestedResults);
        }
        else
        {
            return documents;
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<Object> getTopicIds(final List<Document> documents)
    {
        final Set<Object> topicIds = Sets.newHashSet();
        for (Document document : documents)
        {
            topicIds.addAll((Collection<? extends Object>) document
                .<Object> getField(Document.PARTITIONS));
        }
        return topicIds;
    }

    @SuppressWarnings("unchecked")
    protected static String getTopic(Document document)
    {
        return ((List<String>) document.getField(Document.PARTITIONS)).get(0);
    }

    /**
     * Returns a human-readable label for a subtopic.
     */
    String getTopicLabel(String topicId)
    {
        return subtopicLabels.get(topicId);
    }

    /**
     * Loads human-readable labels for subtopics.
     */
    private static Map<String, String> loadSubtopicLabels(IResource subtopicLabelsResource)
    {
        final Map<String, String> labels = Maps.newHashMap();
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(subtopicLabelsResource
                .open(), "UTF-8"));

            String line = reader.readLine(); // discard first line
            while ((line = reader.readLine()) != null)
            {
                String [] split = line.split("\\t");
                if (split.length > 1)
                {
                    labels.put(split[0], split[1]);
                }
            }
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
        finally
        {
            if (reader != null)
            {
                CloseableUtils.close(reader);
            }
        }

        return labels;
    }

    /**
     * Prepares a map with subtopic sizes, keyed by subtopic string.
     */
    private static Map<String, Integer> prepareSubtopicSizes(int [][] resultSubtopicIds)
    {
        final Map<String, Integer> map = Maps.newHashMap();

        for (int topic = 1; topic < resultSubtopicIds.length; topic++)
        {
            for (int result = 1; result < resultSubtopicIds[topic].length; result++)
            {
                MapUtils.increment(map, buildTopicId(topic,
                    resultSubtopicIds[topic][result]));
            }
        }

        return map;
    }

    /**
     * Loads all Ambient documents.
     */
    private static Map<Integer, List<Document>> loadDocuments(IResource resultsResource,
        int [][] resultSubtopicIds)
    {
        final Map<Integer, List<Document>> documents = Maps.newHashMap();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(resultsResource.open(), "UTF-8"));

            String line = reader.readLine(); // discard first line
            while ((line = reader.readLine()) != null)
            {
                final String [] split = line.split("\\t");
                final String [] topicSplit = split[0].split("\\.");

                final int topicId = Integer.parseInt(topicSplit[0]);
                final int resultIndex = Integer.parseInt(topicSplit[1]);

                // Build document
                final Document document = new Document();
                document.setField(Document.CONTENT_URL, split[1]);
                document.setField(Document.TITLE, split[2]);
                if (split.length > 3)
                {
                    document.setField(Document.SUMMARY, split[3]);
                }
                document
                    .setField(
                        Document.PARTITIONS,
                        ImmutableList
                            .of(buildTopicId(
                                topicId,
                                resultSubtopicIds[topicId].length > resultIndex ? resultSubtopicIds[topicId][resultIndex]
                                    : 0)));

                // Add to list
                List<Document> topicList = documents.get(topicId);
                if (topicList == null)
                {
                    topicList = Lists.newArrayList();
                    documents.put(topicId, topicList);
                }
                topicList.add(document);
            }
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
        finally
        {
            CloseableUtils.close(reader);
        }

        final List<Document> allDocuments = Lists.newArrayList();
        for (List<Document> docList : documents.values()) {
            allDocuments.addAll(docList);
        }
        Document.assignDocumentIds(allDocuments);

        return documents;
    }

    private static String buildTopicId(final int topic, final int subtopic)
    {
        return topic + "." + subtopic;
    }

    /**
     * Loads topic mapping.
     */
    private int [][] loadSubtopicMapping(IResource resultsMappingResource)
    {
        final Map<Integer, Map<Integer, Integer>> topics = Maps.newHashMap();

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(resultsMappingResource
                .open(), "UTF-8"));

            reader.readLine(); // discard first line
            String line;
            while ((line = reader.readLine()) != null)
            {
                final String [] split = line.split("[\\t.]");

                final int topicId = Integer.parseInt(split[0]);
                final int subtopicId = Integer.parseInt(split[1]);
                final int resultId = Integer.parseInt(split[3]);

                Map<Integer, Integer> topicMap = topics.get(topicId);
                if (topicMap == null)
                {
                    topicMap = Maps.newHashMap();
                    topics.put(topicId, topicMap);
                }

                topicMap.put(resultId, subtopicId);
            }
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
        finally
        {
            if (reader != null)
            {
                CloseableUtils.close(reader);
            }
        }

        this.topicCount = topics.size();

        int [][] resultSubtopicIds = new int [topics.size() + 1] [];
        for (int topic = 1; topic < resultSubtopicIds.length; topic++)
        {
            final Map<Integer, Integer> results = topics.get(topic);

            resultSubtopicIds[topic] = new int [Collections.max(results.keySet()) + 1];
            for (int result = 1; result < resultSubtopicIds[topic].length; result++)
            {
                Integer subtopic = results.get(result);
                if (subtopic != null)
                {
                    resultSubtopicIds[topic][result] = subtopic;
                }
            }
        }
        return resultSubtopicIds;
    }
}
