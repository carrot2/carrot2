/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.mixer;

import java.io.*;
import java.util.*;

import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.tools.*;

import junit.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class CatidTopicMixerTest extends TestCase
{
    /** Temporary directory for index data */
    private File temporaryDirectory;

    /** The TopicMixed under tests */
    private TopicMixer topicMixer;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        // Create a temporary index
        temporaryDirectory = TempUtils.createTemporaryDirectory("ctmtest");

        String odpInput = "<?xml version='1.0' encoding='UTF-8' ?>"
            + "    <RDF xmlns:r='http://www.w3.org/TR/RDF/'"
            + "         xmlns:d='http://purl.org/dc/elements/1.0/'"
            + "         xmlns='http://dmoz.org/rdf'>"
            + "      <Topic r:id='Top/World/Polska/Komputery'>"
            + "        <catid>32460</catid>"
            + "      </Topic>"
            + "      <ExternalPage about='http://www.agh.edu.pl/ogonki/'>"
            + "        <d:Title>Polska Strona Ogonkowa</d:Title>"
            + "        <d:Description>Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery</topic>"
            + "      </ExternalPage>"
            + "      <Topic r:id='Top/World/Polska/Komputery2'>"
            + "        <catid>32461</catid>"
            + "      </Topic>"
            + "      <Topic r:id='Top/World/Polska/Komputery3'>"
            + "        <catid>32462</catid>"
            + "      </Topic>"
            + "      <ExternalPage about='http://www.gust.org.pl/'>"
            + "        <d:Title>GUST</d:Title>"
            + "        <d:Description>Grupa użytkowników systemu Tex.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery3</topic>"
            + "      </ExternalPage>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        ODPIndexer indxer = new ODPIndexer(temporaryDirectory.getPath());
        indxer.index(odpInputStream);

        ODPIndex.initialize(temporaryDirectory.getPath());

        // Create the TopicMixer
        topicMixer = new CatidTopicMixer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        ODPIndex.dispose();
        TempUtils.deleteDirectory(temporaryDirectory);
        super.tearDown();
    }

    /**
     *  
     */
    public void testEmptyCatidList()
    {
        List topics = topicMixer.mix("");

        assertEquals("Empty topics list", new ArrayList(), topics);
    }

    /**
     *  
     */
    public void testOneTopic()
    {
        MutableExternalPage mutableExternalPage;
        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid(32460);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Polska Strona Ogonkowa");
        mutableExternalPage
            .setDescription("Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        topic01.addExternalPage(mutableExternalPage);

        List expectedTopics = Arrays.asList(new Topic []
        { topic01 });

        List topics = topicMixer.mix("32460");

        assertEquals("Equal topic lists", expectedTopics, topics);
    }

    /**
     *  
     */
    public void testMoreTopics()
    {
        MutableExternalPage mutableExternalPage;
        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid(32460);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Polska Strona Ogonkowa");
        mutableExternalPage
            .setDescription("Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        topic01.addExternalPage(mutableExternalPage);

        MutableTopic topic02 = new MutableTopic("Top/World/Polska/Komputery3");
        topic02.setCatid(32462);
        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("GUST");
        mutableExternalPage
            .setDescription("Grupa użytkowników systemu Tex.");
        topic02.addExternalPage(mutableExternalPage);

        List expectedTopics = Arrays.asList(new Topic []
        { topic01, topic02 });

        List topics = topicMixer.mix("32460 32462");

        assertEquals("Equal topic lists", expectedTopics, topics);
    }

    /**
     *  
     */
    public void testAbsentTopics()
    {
        MutableExternalPage mutableExternalPage;
        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid(32460);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Polska Strona Ogonkowa");
        mutableExternalPage
            .setDescription("Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        topic01.addExternalPage(mutableExternalPage);

        List expectedTopics = Arrays.asList(new Topic []
        { topic01 });

        List topics = topicMixer.mix("5 32460 48");

        assertEquals("Equal topic lists", expectedTopics, topics);
    }
}