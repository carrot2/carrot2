/*
 * ZipFileTopicSerializerTest.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp;

import java.io.*;

import junit.framework.*;

/**
 * @author stachoo
 */
public class ZipFileTopicSerializerTest extends TestCase
{
    /** A temporary file */
    private File temporaryFile;

    /** The TopicSerializer under tests */
    private TopicSerializer topicSerializer;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        temporaryFile = File.createTempFile("ctstest", null);
        topicSerializer = new ZipFileTopicSerializer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        temporaryFile.delete();
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     *  
     */
    public void testEmptyTopic() throws IOException, ClassNotFoundException
    {
        Topic topic = new MutableTopic("Top/World");

        topicSerializer.serialize(topic, temporaryFile.getAbsolutePath());
        Topic deserializeTopic = topicSerializer.deserialize(temporaryFile
            .getAbsolutePath());
        assertEquals("Equal after deserialization", topic, deserializeTopic);
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     *  
     */
    public void testNonEmptyTopic() throws IOException, ClassNotFoundException
    {
        MutableTopic topic = new MutableTopic("Top/World");
        topic.setCatid("124");
        MutableExternalPage mutableExternalPage;

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Title01");
        mutableExternalPage.setDescription("Description01");
        topic.addExternalPage(mutableExternalPage);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Title02");
        mutableExternalPage.setDescription("Description02");
        topic.addExternalPage(mutableExternalPage);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Title03");
        mutableExternalPage.setDescription("Description03");
        topic.addExternalPage(mutableExternalPage);

        topicSerializer.serialize(topic, temporaryFile.getAbsolutePath());
        Topic deserializeTopic = topicSerializer.deserialize(temporaryFile
            .getAbsolutePath());
        assertEquals("Equal after deserialization", topic, deserializeTopic);
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     *  
     */
    public void testUTF8Data() throws IOException, ClassNotFoundException
    {
        MutableTopic topic = new MutableTopic("Top/World");
        topic.setCatid("34");
        MutableExternalPage mutableExternalPage;

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Zażółć gęślą jaźń");
        mutableExternalPage.setDescription("ŻAŻÓŁĆ GĘŚLĄ JAŹŃ");
        topic.addExternalPage(mutableExternalPage);

        topicSerializer.serialize(topic, temporaryFile.getAbsolutePath());
        Topic deserializeTopic = topicSerializer.deserialize(temporaryFile
            .getAbsolutePath());
        assertEquals("Equal after deserialization", topic, deserializeTopic);
    }
}