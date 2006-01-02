
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
package com.stachoodev.carrot.odp.lucene;

import org.apache.lucene.document.*;

import com.stachoodev.carrot.odp.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ExternalPageDocument
{
    /**
     * @param externalPage
     * @return
     */
    public static Document Document(ExternalPage externalPage)
    {
        Document document = new Document();

        // URL: store, don't index
        document.add(Field.UnIndexed("url",
            (externalPage.getUrl() != null ? externalPage.getUrl() : "")));

        // Title:
        document.add(Field.Text("title",
            (externalPage.getTitle() != null ? externalPage.getTitle() : "")));

        // Description:
        document.add(Field.Text("summary",
            (externalPage.getDescription() != null ? externalPage
                .getDescription() : "")));

        return document;
    }
}