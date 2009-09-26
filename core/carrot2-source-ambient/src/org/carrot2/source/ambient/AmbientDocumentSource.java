/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.ambient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.ClassResource;
import org.carrot2.util.resource.IResource;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

/**
 * Serves documents from the Ambient test set. Ambient (AMBIgous ENTries) is a data set
 * designed for evaluating subtopic information retrieval. It consists of 44 topics, each
 * with a set of subtopics and a list of 100 ranked documents. For more information,
 * please see <a href="http://credo.fub.it/ambient/">Ambient home page</a>.
 */
@Bindable(prefix = "AmbientDocumentSource")
public class AmbientDocumentSource extends ProcessingComponentBase implements
    IDocumentSource
{
    /**
     * The number of results per one Ambient topic.
     */
    public static final int RESULTS_PER_TOPIC = 100;

    /**
     * The total number of Ambient topics.
     */
    public static final int TOPIC_COUNT = 44;

    /**
     * Resource with Ambient's subtopic to result mapping.
     */
    private final static IResource RESULTS_MAPPING_RESOURCE = new ClassResource(
        AmbientDocumentSource.class, "/ambient/STRel.txt");

    /**
     * Resource with Ambient's results texts.
     */
    private final static IResource RESULTS_RESOURCE = new ClassResource(
        AmbientDocumentSource.class, "/ambient/results.txt");

    /**
     * Resource with Ambient's subtopic labels.
     */
    private final static IResource SUBTOPIC_LABELS_RESOURCE = new ClassResource(
        AmbientDocumentSource.class, "/ambient/subTopics.txt");

    /**
     * Subtopic ids by topic id.
     */
    static final Map<Integer, Set<Integer>> subtopicIdsByTopicId;

    /**
     * Documents by topic id.
     */
    static final Map<Integer, List<Document>> documentsByTopicId;

    /**
     * Numbers of documents for each subtopic.
     */
    private static final Map<String, Integer> subtopicSizes;

    /**
     * Human-readable descriptions of topics.
     */
    private static final Map<String, String> subtopicLabels;

    static
    {
        /** [topicId][resultIndex] = subopicId */
        int [][] resultSubtopicIds = loadSubtopicMapping();
        documentsByTopicId = loadDocuments(resultSubtopicIds);
        subtopicSizes = prepareSubtopicSizes(resultSubtopicIds);
        subtopicIdsByTopicId = prepareSubtopicIdsByTopicId(resultSubtopicIds);
        subtopicLabels = loadSubtopicLabels();
    }

    /**
     * Ambient Topic. The Ambient Topic to load documents from.
     * 
     * @group Topic ID
     * @level Basic
     */
    @Input
    @Processing
    @Attribute
    @Required
    public AmbientTopic topic = AmbientTopic.AIDA;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS)
    @IntRange(min = 1, max = RESULTS_PER_TOPIC)
    public int results = 100;

    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL)
    public long resultsTotal = RESULTS_PER_TOPIC;

    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS)
    @Internal
    public List<Document> documents;

    /**
     * Ambient topics and subtopics covered in the output documents. The set is computed
     * for the output {@link #documents} and it may vary for the same main topic based
     * e.g. on the requested number of {@link #results} or {@link #minTopicSize}.
     */
    @Processing
    @Output
    @Attribute
    public Set<Object> topicIds;

    @Processing
    @Output
    @Attribute(key = AttributeNames.QUERY)
    public String query;

    /**
     * Minimum topic size. Documents belonging to a topic with fewer documents than
     * minimum topic size will not be returned.
     * 
     * @level Medium
     * @group Filtering
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 1)
    public int minTopicSize = 1;

    /**
     * Include documents without topics.
     * 
     * @level Medium
     * @group Filtering
     */
    public boolean includeDocumentsWithoutTopic = false;

    /**
     * All available Ambient topics.
     */
    public static enum AmbientTopic
    {
        AIDA(1, "Aida"), B_52(2, "B-52"), BEAGLE(3, "Beagle"), BRONX(4, "Bronx"), CAIN(5,
            "Cain"), CAMEL(6, "Camel"), CORAL_SEA(7, "Coral Sea"), CUBE(8, "Cube"), EOS(
            9, "Eos"), EXCALIBUR(10, "Excalibur"), FAHRENHEIT(11, "Fahrenheit"), GLOBE(
            12, "Globe"), HORNET(13, "Hornet"), INDIGO(14, "Indigo"), IWO_JIMA(15,
            "Iwo Jima"), JAGUAR(16, "Jaguar"), LA_PLATA(17, "La Plata"), LABYRINTH(18,
            "Labyrinth"), LANDAU(19, "Landau"), LIFE_ON_MARS(20, "Life on Mars"), LOCUST(
            21, "Locust"), MAGIC_MOUNTAIN(22, "Magic Mountain"), MATADOR(23, "Matador"), METAMORPHOSIS(
            24, "Metamorphosis"), MINOTAUR(25, "Minotaur"), MIRA(26, "Mira"), MIRAGE(27,
            "Mirage"), MONTE_CARLO(28, "Monte Carlo"), OPPENHEIM(29, "Oppenheim"), OUT_OF_CONTROL(
            30, "Out of Control"), PELICAN(31, "Pelican"), PURPLE_HAZE(32, "Purple Haze"), RAAM(
            33, "Raam"), RHEA(34, "Rhea"), SCORPION(35, "Scorpion"), THE_LITTLE_MERMAID(
            36, "The Little Mermaid"), TORTUGA(37, "Tortuga"), URANIA(38, "Urania"), WINK(
            39, "Wink"), XANADU(40, "Xanadu"), ZEBRA(41, "Zebra"), ZENITH(42, "Zenith"), ZODIAC(
            43, "Zodiac"), ZOMBIE(44, "Zombie");

        private int topicId;
        private String query;

        private AmbientTopic(int topicId, String query)
        {
            this.topicId = topicId;
            this.query = query;
        }

        public int getTopicId()
        {
            return topicId;
        }

        @Override
        public String toString()
        {
            return query;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process() throws ProcessingException
    {
        query = topic.query;

        // Filter the results
        documents = Lists.newArrayList(Collections2.filter(documentsByTopicId.get(topic
            .getTopicId()), new Predicate<Document>()
        {
            public boolean apply(Document document)
            {
                // For now there is only one topic per document in Ambient
                final String documentTopic = getAmbientTopic(document);
                return subtopicSizes.get(documentTopic) >= minTopicSize
                    && (includeDocumentsWithoutTopic || !documentTopic.endsWith(".0"));
            }
        }));

        if (documents.size() >= results)
        {
            documents = documents.subList(0, results);
        }

        topicIds = Sets.newHashSet();
        for (Document document : documents)
        {
            topicIds.addAll((Collection<? extends Object>) document
                .<Object> getField(Document.PARTITIONS));
        }
    }

    @SuppressWarnings("unchecked")
    static String getAmbientTopic(Document document)
    {
        return ((List<String>) document.getField(Document.PARTITIONS)).get(0);
    }

    /**
     * Returns a human-readable label for a subtopic.
     */
    public static String getTopicLabel(String topicId)
    {
        return subtopicLabels.get(topicId);
    }

    /**
     * Loads human-readable labels for subtopics.
     */
    private static Map<String, String> loadSubtopicLabels()
    {
        final Map<String, String> labels = Maps.newHashMap();
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(SUBTOPIC_LABELS_RESOURCE
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
            for (int result = 0; result < resultSubtopicIds[topic].length; result++)
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
    private static Map<Integer, List<Document>> loadDocuments(int [][] resultSubtopicIds)
    {
        final Map<Integer, List<Document>> documents = Maps.newHashMap();

        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new InputStreamReader(RESULTS_RESOURCE.open(),
                "UTF-8"));

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
                document.setField(Document.PARTITIONS, ImmutableList.of(buildTopicId(
                    topicId, resultSubtopicIds[topicId][resultIndex])));

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
            if (reader != null)
            {
                CloseableUtils.close(reader);
            }
        }

        return documents;
    }

    private static String buildTopicId(final int topic, final int subtopic)
    {
        return topic + "." + subtopic;
    }

    /**
     * Loads topic mapping.
     */
    private static int [][] loadSubtopicMapping()
    {
        int [][] resultSubtopicIds = new int [TOPIC_COUNT + 1] [RESULTS_PER_TOPIC + 1];

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(RESULTS_MAPPING_RESOURCE
                .open(), "UTF-8"));

            reader.readLine(); // discard first line
            String line;
            while ((line = reader.readLine()) != null)
            {
                final String [] split = line.split("[\\t.]");

                final int topicId = Integer.parseInt(split[0]);
                final int subtopicId = Integer.parseInt(split[1]);
                final int resultId = Integer.parseInt(split[3]);

                resultSubtopicIds[topicId][resultId] = subtopicId;
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

        return resultSubtopicIds;
    }

    private static Map<Integer, Set<Integer>> prepareSubtopicIdsByTopicId(
        int [][] resultSubtopicIds)
    {
        final Map<Integer, Set<Integer>> topicSubtopicIds = Maps.newHashMap();

        for (int topicId = 1; topicId < resultSubtopicIds.length; topicId++)
        {
            final int [] topicResultSubtopicIds = resultSubtopicIds[topicId];
            final Set<Integer> subtopicIds = Sets.newLinkedHashSet();
            for (int resultIndex = 1; resultIndex < topicResultSubtopicIds.length; resultIndex++)
            {
                subtopicIds.add(topicResultSubtopicIds[resultIndex]);
            }
            topicSubtopicIds.put(topicId, subtopicIds);
        }

        return topicSubtopicIds;
    }
}
