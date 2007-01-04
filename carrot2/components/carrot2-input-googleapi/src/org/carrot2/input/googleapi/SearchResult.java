
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

package org.carrot2.input.googleapi;

import com.google.soap.search.GoogleSearchResultElement;

public class SearchResult {
	final int at;
	final GoogleSearchResultElement[] results;
	final int totalEstimated;

	final Throwable error;

	public SearchResult(GoogleSearchResultElement[] results, int at, int totalEstimated) {
		this.results = results;
		this.totalEstimated = totalEstimated;
		this.at = at;
		error = null;
	}
	
	public SearchResult(Throwable t) {
		this.error = t;
		this.results = null;
		this.totalEstimated = -1;
		this.at = -1;
	}	
}
