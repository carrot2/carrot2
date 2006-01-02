
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.local.remoteadapters;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;


class ClusterElementWrapper
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

    public ClusterElementWrapper(Element node)
    {
        this.node = node;
    }

    public List getClusterDescription()
    {
        Element title = node.element("title");
        if (title == null)
        {
            return NO_TITLE;
        }

        List phrases = title.elements("phrase");
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
        List groups = node.elements("group");
        if (groups.size() == 0)
        {
            return null;
        }

        List clusters = new LinkedList();

        for (Iterator i = groups.iterator(); i.hasNext();)
        {
            Element group = (Element) i.next();
            clusters.add(new ClusterElementWrapper(group));
        }

        return clusters;
    }


    /**
     * @see com.dawidweiss.carrot.adapters.localfilter.Cluster#getHits()
     */
    public List getDocuments()
    {
        List groups = node.elements("document");

        if (groups.size() == 0)
        {
            return null;
        }

        List docs = new LinkedList();

        for (Iterator i = groups.iterator(); i.hasNext();)
        {
            Element doc = (Element) i.next();

            String value = doc.attributeValue("refid");
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
