/*
 * ZipFilesTopicSerializerTest.java
 * 
 * Created on 2004-06-27
 */
package com.stachoodev.carrot.odp;

import java.io.*;

import junit.framework.*;

/**
 * @author stachoo
 */
public class ZipFilesTopicSerializerTest extends TestCase
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
        temporaryFile.delete();
        topicSerializer = new ZipFilesTopicSerializer();
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

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void testModuloStorage() throws IOException, ClassNotFoundException
    {
        int code = 124;
        MutableTopic topic01 = new MutableTopic("Top/World");
        topic01.setCatid(Integer.toString(code));
        MutableExternalPage mutableExternalPage;

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Title01");
        mutableExternalPage.setDescription("Description01");
        topic01.addExternalPage(mutableExternalPage);

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Title02");
        mutableExternalPage.setDescription("Description02");
        topic01.addExternalPage(mutableExternalPage);

        MutableTopic topic02 = new MutableTopic("Top/World");
        topic02.setCatid(Integer
            .toString(code + ZipFilesTopicSerializer.modulo));

        mutableExternalPage = new MutableExternalPage();
        mutableExternalPage.setTitle("Title03");
        mutableExternalPage.setDescription("Description03");
        topic02.addExternalPage(mutableExternalPage);

        String path = System.getProperty("java.io.tmpdir")
            + System.getProperty("file.separator");
        topicSerializer.serialize(topic01, path + topic01.getCatid());
        topicSerializer.serialize(topic02, path + topic02.getCatid());

        Topic deserializedTopic01 = topicSerializer.deserialize(path
            + topic01.getCatid());
        Topic deserializedTopic02 = topicSerializer.deserialize(path
            + topic02.getCatid());

        assertEquals("Deserialized topic01", topic01, deserializedTopic01);
        assertEquals("Deserialized topic02", topic02, deserializedTopic02);

        new File(path + Integer.toString(code % ZipFilesTopicSerializer.modulo))
            .delete();
    }
}