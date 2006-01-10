
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.carrot.odp.index;

import java.io.*;
import java.util.*;

import com.stachoodev.carrot.odp.*;

import junit.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class CatidPrimaryTopicIndexBuilderTest extends TestCase
{
    /** The index builder under tests */
    private CatidPrimaryTopicIndexBuilder indexBuilder;

    /** Temporary directory for index data */
    private File temporaryDirectory;

    private TopicSerializer serializer;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        temporaryDirectory = TempUtils.createTemporaryDirectory("cpibtest");
        indexBuilder = new CatidPrimaryTopicIndexBuilder();
        serializer = ODPIndex.getTopicSerializer();
        serializer.initialize(temporaryDirectory.getAbsolutePath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        serializer.dispose();
        TempUtils.deleteDirectory(temporaryDirectory);
        super.tearDown();
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void testEmptyInput() throws IOException, ClassNotFoundException
    {
        String odpInput = "<?xml version='1.0' encoding='UTF-8' ?>"
            + "    <RDF xmlns:r='http://www.w3.org/TR/RDF/'"
            + "         xmlns:d='http://purl.org/dc/elements/1.0/'"
            + "         xmlns='http://dmoz.org/rdf'>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes());

        PrimaryTopicIndex expectedIndex = new SimplePrimaryTopicIndex(
            new ArrayList());

        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            serializer, null);

        assertEquals("Equal indices", expectedIndex, index);
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void testDataStorageOneEmptyTopic() throws IOException,
        ClassNotFoundException
    {
        String odpInput = "<?xml version='1.0' encoding='UTF-8' ?>"
            + "    <RDF xmlns:r='http://www.w3.org/TR/RDF/'"
            + "         xmlns:d='http://purl.org/dc/elements/1.0/'"
            + "         xmlns='http://dmoz.org/rdf'>"
            + "      <Topic r:id='Top/World/Polska/Komputery'>"
            + "        <catid>32460</catid>" + "      </Topic>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            serializer, null);

        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid(32460);

        assertTrue("Empty topic not indexed",
            index.getLocation(32460) == null);
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void testDataStorageMoreNonEmptyTopics() throws IOException,
        ClassNotFoundException
    {
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
            + "      <ExternalPage about='http://www.gust.org.pl/'>"
            + "        <d:Title>GUST</d:Title>"
            + "        <d:Description>Grupa użytkowników systemu Tex.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery</topic>"
            + "      </ExternalPage>"
            + "      <Topic r:id='Top/World/Polska/Komputery2'>"
            + "        <catid>32461</catid>"
            + "      </Topic>"
            + "      <ExternalPage about='http://www.agh.edu.pl/ogonki/'>"
            + "        <d:Title>Polska Strona Ogonkowa</d:Title>"
            + "        <d:Description>Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery2</topic>"
            + "      </ExternalPage>"
            + "      <ExternalPage about='http://www.gust.org.pl/'>"
            + "        <d:Title>GUST</d:Title>"
            + "        <d:Description>Grupa użytkowników systemu Tex.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery2</topic>"
            + "      </ExternalPage>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            serializer, null);

        MutableExternalPage mutableExternalPage;
        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid(32460);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Polska Strona Ogonkowa");
        mutableExternalPage
            .setDescription("Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        topic01.addExternalPage(mutableExternalPage);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("GUST");
        mutableExternalPage.setDescription("Grupa użytkowników systemu Tex.");
        topic01.addExternalPage(mutableExternalPage);

        MutableTopic topic02 = new MutableTopic("Top/World/Polska/Komputery2");
        topic02.setCatid(32461);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Polska Strona Ogonkowa");
        mutableExternalPage
            .setDescription("Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        topic02.addExternalPage(mutableExternalPage);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("GUST");
        mutableExternalPage.setDescription("Grupa użytkowników systemu Tex.");
        topic02.addExternalPage(mutableExternalPage);

        Topic deserializedTopic01 = serializer.deserialize(index
            .getLocation(32460));
        assertEquals("Topic 1 deserialized", topic01, deserializedTopic01);

        Topic deserializedTopic02 = serializer.deserialize(index
            .getLocation(32461));
        assertEquals("Topic 2 deserialized", topic02, deserializedTopic02);
    }
}