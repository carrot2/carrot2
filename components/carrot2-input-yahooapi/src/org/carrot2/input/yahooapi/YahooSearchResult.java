
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.yahooapi;

/**
 * Yahoo search result reference.
 * 
 * @author Dawid Weiss
 */
final class YahooSearchResult {

    final String url;
    final String title;
    final String summary;
    final String clickurl;

    final String newsSource;
    final String newsSourceUrl;

    public YahooSearchResult(String url, String title, String summary, String clickurl, 
        String newsSource, String newsSourceUrl)
    {
        this.url = url;
        this.title = title;
        this.summary = summary;
        this.clickurl = clickurl;
        this.newsSource = newsSource;
        this.newsSourceUrl = newsSourceUrl;
    }

    public YahooSearchResult(String url, String title, String summary, String clickurl)
    {
        this(url, title, summary, clickurl, null, null);
    }
}
