
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

package org.carrot2.tools.odp.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.carrot2.input.odp.*;
import org.carrot2.util.PropertyHelper;
import org.carrot2.util.PropertyProvider;
import org.dom4j.io.SAXReader;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class ODPAbstractSaxHandler extends DefaultHandler implements
    PropertyProvider, ObservableTopicIndexBuilder
{
    /** Stores properties of this indexer */
    protected PropertyHelper propertyHelper;

    /** Listeners */
    protected List topicIndexBuilderListeners;

    /** Currently processed ODP category */
    protected MutableTopic currentTopic;

    /** Currently processed ODP reference */
    protected MutableExternalPage currentExternalPage;

    /** Collects contents of text elements */
    protected StringBuffer stringBuffer;

    /**
     *  
     */
    public ODPAbstractSaxHandler()
    {
        this.topicIndexBuilderListeners = new ArrayList();
        this.propertyHelper = new PropertyHelper();
    }

    /**
     * @param topic
     * @throws IOException
     */
    protected abstract void index(Topic topic) throws IOException;
    
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
            currentExternalPage.setUrl(attributes.getValue("about"));
            return;
        }

        // ExternalPage/Title element
        if (elementName.equals("Title"))
        {
            stringBuffer = new StringBuffer();
            return;
        }

        // ExternalPage/Description element
        if (elementName.equals("Description"))
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
            currentTopic.setCatid(Integer.parseInt(stringBuffer.toString()));
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
        if (elementName.equals("Title"))
        {
            // Current stringBuffer is the contents of Title
            currentExternalPage.setTitle(stringBuffer.toString());
            return;
        }

        // ExternalPage/Description element
        if (elementName.equals("Description"))
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
     * @param listener
     */
    public void addTopicIndexBuilderListener(TopicIndexBuilderListener listener)
    {
        topicIndexBuilderListeners.add(listener);
    }

    /**
     * @param listener
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

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getDoubleProperty(java.lang.String,
     *      double)
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getIntProperty(java.lang.String,
     *      int)
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return propertyHelper.getProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setProperty(java.lang.String,
     *      java.lang.Object)
     */
    public Object setProperty(String propertyName, Object property)
    {
        return propertyHelper.setProperty(propertyName, property);
    }

    /**
     * @param inputStream
     * @throws IOException
     */
    protected void initalizeParser(InputStream inputStream) throws IOException
    {
        currentTopic = null;
        currentExternalPage = null;
        stringBuffer = null;
    
        // Initialize SAX
        try
        {
            XMLReader xmlReader = new SAXReader().getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.parse(new InputSource(inputStream));
        }
        catch (SAXException e)
        {
            System.err.println("SAX parser exception: " + e.getMessage());
        }
    }
}