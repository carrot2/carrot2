
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
package com.stachoodev.carrot.local.benchmark.report;

import org.dom4j.*;

import com.stachoodev.carrot.odp.*;

/**
 * Converts {@link com.stachoodev.carrot.odp.Topic}s into XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TopicElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        Topic topic = (Topic) object;
        Element topicElement = DocumentHelper.createElement("odp-topic");

        topicElement.addElement("id").addText(topic.getId());
        topicElement.addElement("catid").addText(
            Integer.toString(topic.getCatid()));
        topicElement.addElement("size").addText(
            Integer.toString(topic.getExternalPages().size()));

        return topicElement;
    }
}