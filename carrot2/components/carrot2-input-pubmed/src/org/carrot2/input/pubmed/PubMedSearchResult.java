
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

package org.carrot2.input.pubmed;

/**
 * Represents one PubMed search result.
 * 
 * @author Stanislaw Osinski
 */
final class PubMedSearchResult
{

    public final String id;
    public final String title;
    public final String summary;
    public final String url;


    public PubMedSearchResult(String id, String title, String summary,
            String url)
    {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.url = url;
    }
}
