package com.dawidweiss.carrot.input.yahoo;

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

    public YahooSearchResult(String url, String title, String summary, String clickurl) {
        this.url = url;
        this.title = title;
        this.summary = summary;
        this.clickurl = clickurl;
    }
}
