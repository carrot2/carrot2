
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

import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * Serves documents from the Ambient test set. Ambient (AMBIgous ENTries) is a data set
 * designed for evaluating subtopic information retrieval. It consists of 44 topics, each
 * with a set of subtopics and a list of 100 ranked documents. For more information,
 * please see <a href="http://credo.fub.it/ambient/">Ambient home page</a>.
 */
@Bindable(prefix = "AmbientDocumentSource", inherit = CommonAttributes.class)
public class AmbientDocumentSource extends FubDocumentSource
{
    static final FubTestCollection DATA = new FubTestCollection("/ambient");

    static final int TOPIC_COUNT = 44;
    static final int MAX_RESULTS_PER_TOPIC = 100;

    /**
     * Ambient Topic. The Ambient Topic to load documents from.
     */
    @Input
    @Processing
    @Attribute
    @Required
    @Group(TOPIC_ID)
    @Level(AttributeLevel.BASIC)
    public AmbientTopic topic = AmbientTopic.AIDA;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS, inherit = true)
    @IntRange(min = 1, max = MAX_RESULTS_PER_TOPIC)
    public int results = 100;

    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL, inherit = true)
    public long resultsTotal = MAX_RESULTS_PER_TOPIC;

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
    public void process() throws ProcessingException
    {
        query = topic.query;
        processInternal(DATA, topic.getTopicId(), results);
    }

    public static String getTopicLabel(String topicId)
    {
        return DATA.getTopicLabel(topicId);
    }
}
