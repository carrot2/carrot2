
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.tools.odp.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.carrot2.input.odp.ExternalPage;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ExternalPageDocument
{
    /**
     * @param externalPage
     */
    public static Document Document(ExternalPage externalPage)
    {
        Document document = new Document();

        // URL: store, don't index
        document.add(
                new Field("url", (externalPage.getUrl() != null ? externalPage.getUrl() : ""),
                        Field.Store.YES, Field.Index.UN_TOKENIZED));

        // Title:
        document.add(
                new Field("title", (externalPage.getTitle() != null ? externalPage.getTitle() : ""),
                        Field.Store.YES, Field.Index.TOKENIZED));

        // Description:
        document.add(
                new Field("summary", (externalPage.getDescription() != null ? externalPage.getDescription() : ""),
                        Field.Store.YES, Field.Index.TOKENIZED));

        return document;
    }
}