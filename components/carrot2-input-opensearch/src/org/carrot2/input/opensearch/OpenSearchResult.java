
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

package org.carrot2.input.opensearch;

/**
 * Open search result reference.
 * 
 * @author Julien Nioche
 */
final class OpenSearchResult {
    final String url;
    final String title;
    final String summary;

    public OpenSearchResult(String url, String title, String summary) {
        this.url = url;
        this.title = title;
        this.summary = summary;
    }
}
