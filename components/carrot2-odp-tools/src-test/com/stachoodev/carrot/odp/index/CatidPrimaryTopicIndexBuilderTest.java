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
        indexBuilder.setIntProperty(
            CatidPrimaryTopicIndexBuilder.PROPERTY_MAX_DEPTH, 4);
        indexBuilder.setTopicSerializer(new ZipFileTopicSerializer());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        TempUtils.deleteDirectory(temporaryDirectory);
        super.tearDown();
    }

    /**
     * @throws IOException
     */
    public void testEmptyInput() throws IOException
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
            temporaryDirectory.getPath());

        assertEquals("Equal indices", expectedIndex, index);
    }

    /**
     * @throws IOException
     */
    public void testOneEmptyTopic() throws IOException
    {
        String odpInput = "<?xml version='1.0' encoding='UTF-8' ?>"
            + "    <RDF xmlns:r='http://www.w3.org/TR/RDF/'"
            + "         xmlns:d='http://purl.org/dc/elements/1.0/'"
            + "         xmlns='http://dmoz.org/rdf'>"
            + "      <Topic r:id='Top/Arts'>" + "        <catid>2</catid>"
            + "      </Topic>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        PrimaryTopicIndex expectedIndex = createPrimaryTopicIndex(new String [] []
        {
        { "2", "0/1/2" } });
        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            temporaryDirectory.getPath());

        assertEquals("Equal indices", expectedIndex, index);
    }

    /**
     * @throws IOException
     */
    public void testOneNonEmptyTopic() throws IOException
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
            + "      </ExternalPage>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        PrimaryTopicIndex expectedIndex = createPrimaryTopicIndex(new String [] []
        {
        { "32460", "0/1/2/3/32460" } });
        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            temporaryDirectory.getPath());

        assertEquals("Equal indices", expectedIndex, index);
    }

    /**
     * @throws IOException
     */
    public void testMoreNonEmptyTopics() throws IOException
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

        PrimaryTopicIndex expectedIndex = createPrimaryTopicIndex(new String [] []
        {
        { "32460", "0/1/2/3/32460" },
        { "32461", "0/1/2/4/32461" } });
        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            temporaryDirectory.getPath());

        assertEquals("Equal indices", expectedIndex, index);
    }

    /**
     * @throws IOException
     */
    public void testMaxDirectoryDepth() throws IOException
    {
        String odpInput = "<?xml version='1.0' encoding='UTF-8' ?>"
            + "    <RDF xmlns:r='http://www.w3.org/TR/RDF/'"
            + "         xmlns:d='http://purl.org/dc/elements/1.0/'"
            + "         xmlns='http://dmoz.org/rdf'>"
            + "      <Topic r:id='Top/Arts'>" + "        <catid>1</catid>"
            + "      </Topic>" + "      <Topic r:id='Top/Arts/C1/C2'>"
            + "        <catid>2</catid>" + "      </Topic>"
            + "      <Topic r:id='Top/Arts/C1/C2/C3/C4/C5'>"
            + "        <catid>3</catid>" + "      </Topic>"
            + "      <Topic r:id='Top/Arts/C1/C2/C3/C4/C5/C6'>"
            + "        <catid>4</catid>" + "      </Topic>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        PrimaryTopicIndex expectedIndex = createPrimaryTopicIndex(new String [] []
        {
        { "1", "0/1/1" },
        { "2", "0/1/2/3/2" },
        { "3", "0/1/2/3/3" },
        { "4", "0/1/2/3/4" } });
        PrimaryTopicIndex index = indexBuilder.create(odpInputStream,
            temporaryDirectory.getPath());

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
            temporaryDirectory.getPath());

        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid("32460");
        TopicSerializer serializer = new ZipFileTopicSerializer();

        Topic deserializedTopic01 = serializer
            .deserialize(temporaryDirectory.getAbsolutePath()
                + System.getProperty("file.separator")
                + index.getLocation("32460"));
        assertEquals("Topic 1 deserialized", topic01, deserializedTopic01);
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
            temporaryDirectory.getPath());

        MutableExternalPage mutableExternalPage;
        MutableTopic topic01 = new MutableTopic("Top/World/Polska/Komputery");
        topic01.setCatid("32460");

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
        topic02.setCatid("32461");

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Polska Strona Ogonkowa");
        mutableExternalPage
            .setDescription("Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        topic02.addExternalPage(mutableExternalPage);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("GUST");
        mutableExternalPage.setDescription("Grupa użytkowników systemu Tex.");
        topic02.addExternalPage(mutableExternalPage);

        TopicSerializer serializer = new ZipFileTopicSerializer();

        Topic deserializedTopic01 = serializer
            .deserialize(temporaryDirectory.getAbsolutePath()
                + System.getProperty("file.separator")
                + index.getLocation("32460"));
        assertEquals("Topic 1 deserialized", topic01, deserializedTopic01);

        Topic deserializedTopic02 = serializer
            .deserialize(temporaryDirectory.getAbsolutePath()
                + System.getProperty("file.separator")
                + index.getLocation("32461"));
        assertEquals("Topic 2 deserialized", topic02, deserializedTopic02);
    }

    /**
     * @param entries
     * @return
     */
    private PrimaryTopicIndex createPrimaryTopicIndex(String [][] entries)
    {
        List indexEntries = new ArrayList();

        for (int i = 0; i < entries.length; i++)
        {
            indexEntries.add(new SimplePrimaryTopicIndex.IndexEntry(
                entries[i][0], entries[i][1].replace('/', System.getProperty(
                    "file.separator").charAt(0))));
        }

        PrimaryTopicIndex index = new SimplePrimaryTopicIndex(indexEntries);

        return index;
    }
}