package com.dawidweiss.carrot.input.googleapi;

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
