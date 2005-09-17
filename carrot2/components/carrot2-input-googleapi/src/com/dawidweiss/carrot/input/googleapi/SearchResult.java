package com.dawidweiss.carrot.input.googleapi;

import com.google.soap.search.GoogleSearchResultElement;

public class SearchResult {
	final GoogleSearchResultElement[] results;
	final int totalEstimated;
	final int keyExpectedSize;

	public SearchResult(GoogleSearchResultElement[] results, int totalEstimated, int expectedResultSize) {
		this.results = results;
		this.totalEstimated = totalEstimated;
		this.keyExpectedSize = expectedResultSize;
	}
}
