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
package com.stachoodev.carrot.local.benchmark.report;

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;

/**
 * Converts {@link com.dawidweiss.carrot.core.local.clustering.RawDocument}s
 * into XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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
            rawDocumentElement.addAttribute("id", rawDocument.getId().toString());
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

        if (rawDocument.getProperty(LingoWeb.PROPERTY_CLUSTER_MEMBER_SCORE) != null)
        {
            rawDocumentElement.addElement("member-score").addText(
                StringUtils.toString((Double) rawDocument
                    .getProperty(LingoWeb.PROPERTY_CLUSTER_MEMBER_SCORE), "#.##"));
        }

        if (rawDocument.getProperty(RawDocumentsProducer.PROPERTY_CATID) != null)
        {
            rawDocumentElement.addElement("catid").addText(
                (String) rawDocument
                    .getProperty(RawDocumentsProducer.PROPERTY_CATID));
        }

        return rawDocumentElement;
    }
}