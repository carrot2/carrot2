package com.dawidweiss.carrot.local.remoteadapters;

import java.util.*;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;


public class ClusterJdomElementWrapper
    implements RawCluster
{
    /**
     * Lazily initialize hash map of
     * object's properties.
     */
    private HashMap properties;

    private static final List NO_TITLE 
            = Collections.EMPTY_LIST;

    private final Element node;

    public ClusterJdomElementWrapper(Element node)
    {
        this.node = node;
    }

    public List getClusterDescription()
    {
        Element title = node.getChild("title");

        if (title == null)
        {
            return NO_TITLE;
        }

        List phrases = title.getChildren("phrase");

        String [] titlePhrases = new String[phrases.size()];
        int j = 0;

        for (Iterator i = phrases.iterator(); i.hasNext(); j++)
        {
            titlePhrases[j] = ((Element) i.next()).getTextTrim();
        }

        return Arrays.asList( titlePhrases );
    }


    public List getSubclusters()
    {
        List groups = node.getChildren("group");

        if (groups.size() == 0)
        {
            return null;
        }

        List clusters = new LinkedList();

        for (Iterator i = groups.iterator(); i.hasNext();)
        {
            Element group = (Element) i.next();
            clusters.add(new ClusterJdomElementWrapper(group));
        }

        return clusters;
    }


    /**
     * @see com.dawidweiss.carrot.adapters.localfilter.Cluster#getHits()
     */
    public List getDocuments()
    {
        List groups = node.getChildren("document");

        if (groups.size() == 0)
        {
            return null;
        }

        List docs = new LinkedList();

        for (Iterator i = groups.iterator(); i.hasNext();)
        {
            Element doc = (Element) i.next();

            String value = doc.getAttributeValue("refid");

            if (value == null)
            {
                throw new RuntimeException("Missing 'refid' attribute in the clustered XML.");
            }

            docs.add(value);
        }

        return docs;
    }
    
    /**
     * Returns the value of a named property.
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawCluster#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName) {
        // the synchronized block should be before 'properties != null'
        // for 100% correct concurrent solution... but it seems unlikely
        // that pointer access would cause us much trouble here.
        if (properties != null) {
            synchronized (this) {
                Object value = properties.get(propertyName);
                if (value != null)
                    return value;
            }
        }
        return null;
    }

    /**
     * Sets the value of a named property.
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocument#setProperty(java.lang.String)
     */
    public Object setProperty(String propertyName, Object value) {
        synchronized (this) {
            if (properties == null) {
                properties = new HashMap();
            }
            return properties.put(propertyName, value);
        }
    }
} 
