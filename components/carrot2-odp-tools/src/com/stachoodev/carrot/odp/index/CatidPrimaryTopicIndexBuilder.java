/*
 * CatidPrimaryTopicIndexBuilder.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp.index;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.stachoodev.carrot.odp.*;

/**
 * Builds a {@link CatidPrimaryTopicIndexBuilder}based on the ODP Topic's
 * <code>catid</code> attribute. Indices created by this class are instances
 * of {@link CatidPrimaryTopicIndexBuilder}.
 * 
 * Content files (one file contains one topic along with all its external pages)
 * are laid out in a hierarchical structure of file system directories
 * corresponding to topic 'paths' in the original ODP structure, e.g.
 * Top/World/Poland/Komputery. The maximum depth of the file system directory
 * structure can be specified beyond which all topics will be saved in a flat
 * list of files (file name is the topics <code>catid</code>). As ODP topic
 * 'paths' can contain problematic UTF8 characters, each element of the path is
 * mapped to an integer number.
 * 
 * This index builder is <b>not </b> thread-safe.
 * 
 * @author stachoo
 */
public class CatidPrimaryTopicIndexBuilder extends DefaultHandler implements
    PrimaryTopicIndexBuilder, ObservableTopicIndexBuilder
{
    /** Maximum depth of file system directory structure */
    private int maxDepth;
    private static final int DEFAULT_MAX_DEPTH = 6;

    /** Topic serializer */
    private TopicSerializer topicSerializer;

    /** Location in which the index data is to be stored */
    private String dataLocationPath;

    /** Listeners */
    private List topicIndexBuilderListeners;

    /** Currently processed ODP category */
    private MutableTopic currentTopic;

    /** Currently processed ODP reference */
    private MutableExternalPage currentExternalPage;

    /** Collects contents of text elements */
    private StringBuffer stringBuffer;

    /** Entries of this index */
    private List indexEntries;

    /**
     * Mapping between ODP 'path' elements (keys) and filesystem directory names
     * (values)
     */
    private Map pathElements;

    /** The maximum path element code */
    private int maxPathElementCode;

    /** A helper list for splitting ODP paths */
    private List splitList;

    /**
     * Creates a new CatidPrimaryTopicIndexBuilder.
     * 
     * @param dataLocationPath location in which the index data is to be stored.
     */
    public CatidPrimaryTopicIndexBuilder()
    {
        this.maxDepth = DEFAULT_MAX_DEPTH;
        this.topicSerializer = ODPIndex.getTopicSerializer();
        this.topicIndexBuilderListeners = new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.PrimaryTopicIndexBuilder#create(java.io.InputStream)
     */
    public PrimaryTopicIndex create(InputStream inputStream,
        String indexDataLocation) throws IOException
    {
        this.dataLocationPath = indexDataLocation;
        indexEntries = new ArrayList();
        splitList = new ArrayList();

        pathElements = new HashMap();
        maxPathElementCode = 0;

        // Initialize SAX
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            SAXParser parser = factory.newSAXParser();
            parser.parse(inputStream, this);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Cannot initialize SAXParser", e);
        }
        catch (SAXException e)
        {
            System.err.println("SAX parser exception: " + e.getMessage());
        }

        // Sort the entries according to the id and convert to a
        // PrimaryTopicIndex
        Collections.sort(indexEntries);
        return new SimplePrimaryTopicIndex(indexEntries);
    }

    /**
     * @param topic
     * @throws IOException
     */
    private void index(Topic topic) throws IOException
    {
        String id = topic.getId();

        // Build the file system path
        StringBuffer path = new StringBuffer();
        split(id, '/', splitList);

        int i = 0;
        for (Iterator iter = splitList.iterator(); iter.hasNext()
            && i < maxDepth; i++)
        {
            String element = (String) iter.next();
            String pathElement;
            if (pathElements.containsKey(element))
            {
                pathElement = (String) pathElements.get(element);
            }
            else
            {
                pathElement = Integer.toString(maxPathElementCode++);
                pathElements.put(element, pathElement);
            }

            path.append(pathElement);
            path.append(System.getProperty("file.separator"));
        }

        // Add file name
        path.append(topic.getCatid());

        // Serialize the topic
        topicSerializer.serialize(topic, new File(dataLocationPath, path
            .toString()).getAbsolutePath());

        // Add to index entries
        indexEntries.add(new SimplePrimaryTopicIndex.IndexEntry(topic
            .getCatid(), path.toString()));

        // Fire the event
        fireTopicIndexed();
    }

    /**
     * @param string
     * @param delimiter
     * @param substrings
     */
    private void split(String string, char delimiter, List substrings)
    {
        substrings.clear();

        int i = 0;
        int j = string.indexOf(delimiter);

        while (j >= 0)
        {
            substrings.add(string.substring(i, j));
            i = j + 1;
            j = string.indexOf(delimiter, i);
        }

        substrings.add(string.substring(i));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char [] ch, int start, int length)
        throws SAXException
    {
        if (stringBuffer != null)
        {
            stringBuffer.append(ch, start, length);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) throws SAXException
    {
        // Just in case localName is null
        String elementName = localName;
        if (elementName.equals(""))
        {
            elementName = qName;
        }

        // Topic element
        if (elementName.equals("Topic"))
        {
            // Index previous topic if there is any
            if (currentTopic != null)
            {
                try
                {
                    // Add currentTopic to the index
                    index(currentTopic);
                }
                catch (IOException e)
                {
                    new SAXException("Can't serialize topic", e);
                }
            }

            String id = attributes.getValue("r:id");
            if (id == null)
            {
                currentTopic = null;
                throw new SAXException("WARNING: no id found for a topic.");
            }

            currentTopic = new MutableTopic(id);
            return;
        }

        // If we were unable to parse the Topic element - don't bother to parse
        // its content and the following ExternalPages.
        if (currentTopic == null)
        {
            return;
        }

        // Topic/catid element
        if (elementName.equals("catid"))
        {
            stringBuffer = new StringBuffer();
            return;
        }

        // ExternalPage element
        if (elementName.equals("ExternalPage"))
        {
            currentExternalPage = new MutableExternalPage();
            return;
        }

        // ExternalPage/Title element
        if (elementName.equals("d:Title"))
        {
            stringBuffer = new StringBuffer();
            return;
        }

        // ExternalPage/Description element
        if (elementName.equals("d:Description"))
        {
            stringBuffer = new StringBuffer();
            return;
        }

        // We don't parse the ExternalPage/topic element as we make an
        // assumption that all ExternalPages following a Topic element belong
        // to that topic.
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
        // Don't bother doing anything if there is no valid Topic
        if (currentTopic == null)
        {
            return;
        }

        // Just in case localName is null
        String elementName = localName;
        if (elementName.equals(""))
        {
            elementName = qName;
        }

        // Topic/catid element
        if (elementName.equals("catid"))
        {
            // Current stringBuffer is the contents of catid
            currentTopic.setCatid(stringBuffer.toString());
            return;
        }

        // ExternalPage element
        if (elementName.equals("ExternalPage"))
        {
            // Add currentExternalPage to currentTopic
            currentTopic.addExternalPage(currentExternalPage);
            return;
        }

        // ExternalPage/Title element
        if (elementName.equals("d:Title"))
        {
            // Current stringBuffer is the contents of Title
            currentExternalPage.setTitle(stringBuffer.toString());
            return;
        }

        // ExternalPage/Description element
        if (elementName.equals("d:Description"))
        {
            // Current stringBuffer is the contents of Description
            currentExternalPage.setDescription(stringBuffer.toString());
            return;
        }

        // We don't parse the ExternalPage/topic element as we make an
        // assumption that all ExternalPages following a Topic element belong
        // to that topic.
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
        // Serialize the last topic
        if (currentTopic != null)
        {
            try
            {
                // Add currentTopic to the index
                index(currentTopic);
            }
            catch (IOException e)
            {
                new SAXException("Can't serialize topic", e);
            }
        }
    }

    /**
     * Returns the maximum depth of file system directory structure.
     * 
     * @return
     */
    public int getMaxDepth()
    {
        return maxDepth;
    }

    /**
     * Sets the maximum depth of file system directory structure.
     * 
     * @param maxDepth
     */
    public void setMaxDepth(int maxDepth)
    {
        this.maxDepth = maxDepth;
    }

    /**
     * Sets this CatidPrimaryTopicIndexBuilder's <code>topicSerializer</code>.
     * 
     * @param topicSerializer
     */
    public void setTopicSerializer(TopicSerializer topicSerializer)
    {
        this.topicSerializer = topicSerializer;
    }

    /* (non-Javadoc)
     * @see com.stachoodev.carrot.odp.index.ObservableTopicIndexBuilder#addTopicIndexBuilderListener(com.stachoodev.carrot.odp.index.TopicIndexBuilderListener)
     */
    public void addTopicIndexBuilderListener(TopicIndexBuilderListener listener)
    {
        topicIndexBuilderListeners.add(listener);
    }

    /* (non-Javadoc)
     * @see com.stachoodev.carrot.odp.index.ObservableTopicIndexBuilder#removeTopicIndexBuilderListener(com.stachoodev.carrot.odp.index.TopicIndexBuilderListener)
     */
    public void removeTopicIndexBuilderListener(
        TopicIndexBuilderListener listener)
    {
        topicIndexBuilderListeners.remove(listener);
    }

    /**
     * Fires the {@link TopicIndexBuilderListener#topicIndexed()}method on all
     * registered listeners.
     */
    protected void fireTopicIndexed()
    {
        for (Iterator iter = topicIndexBuilderListeners.iterator(); iter
            .hasNext();)
        {
            TopicIndexBuilderListener listener = (TopicIndexBuilderListener) iter
                .next();
            listener.topicIndexed();
        }
    }
}