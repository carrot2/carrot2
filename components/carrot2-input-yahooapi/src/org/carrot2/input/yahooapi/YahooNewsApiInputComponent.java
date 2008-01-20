
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

package org.carrot2.input.yahooapi;


/**
 * An input component for 
 * <a href="http://developer.yahoo.com/search/news/V1/newsSearch.html">Yahoo! News API</a>. 
 * We love these guys.
 */
public class YahooNewsApiInputComponent extends YahooApiInputComponent {
    /**
     * Create an input component with the default service descriptor.
     */
    public YahooNewsApiInputComponent() {
        super("resource/yahoo-news.xml");
    }
}
