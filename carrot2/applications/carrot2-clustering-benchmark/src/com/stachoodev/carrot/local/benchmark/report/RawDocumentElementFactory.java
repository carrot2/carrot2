/*
 * RawDocumentElementFactory.java
 * 
 * Created on 2004-06-30
 */
package com.stachoodev.carrot.local.benchmark.report;

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;

/**
 * @author stachoo
 */
public class RawDocumentElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(org.dom4j.Document,
     *      java.lang.Object)
     */
    public Element createElement(Object object)
    {
        RawDocument rawDocument = (RawDocument) object;
        Element rawDocumentElement = DocumentHelper
            .createElement("raw-document");

        if (rawDocument.getId() != null)
        {
            rawDocumentElement.addAttribute("id", (String) rawDocument.getId());
        }

        if (rawDocument.getTitle() != null)
        {
            rawDocumentElement.addElement("title").addText(
                rawDocument.getTitle());
        }

        if (rawDocument.getSnippet() != null)
        {
            rawDocumentElement.addElement("snippet").addText(
                rawDocument.getSnippet());
        }

        if (rawDocument.getScore() != -1)
        {
            rawDocumentElement.addElement("score").addText(
                Float.toString(rawDocument.getScore()));
        }

        if (rawDocument.getProperty(RawDocument.PROPERTY_LANGUAGE) != null)
        {
            rawDocumentElement.addElement("lang")
                .addText(
                    (String) rawDocument
                        .getProperty(RawDocument.PROPERTY_LANGUAGE));
        }

        if (rawDocument.getProperty(RawDocument.PROPERTY_URL) != null)
        {
            rawDocumentElement.addElement("url").addText(
                (String) rawDocument.getProperty(RawDocument.PROPERTY_URL));
        }

        if (rawDocument.getProperty(Lingo.PROPERTY_CLUSTER_MEMBER_SCORE) != null)
        {
            rawDocumentElement.addElement("member-score").addText(
                StringUtils.toString((Double) rawDocument
                    .getProperty(Lingo.PROPERTY_CLUSTER_MEMBER_SCORE), "#.##"));
        }

        return rawDocumentElement;
    }
}