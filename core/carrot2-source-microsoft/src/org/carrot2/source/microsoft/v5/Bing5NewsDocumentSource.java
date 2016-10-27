
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft.v5;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;

/**
 * A {@link IDocumentSource} fetching news search results from Bing, 
 * using Search API V5.
 * 
 * <p>Important: there are limits for free use of the above API (beyond which it is a
 * paid service).
 * 
 * @see "https://msdn.microsoft.com/en-us/library/mt711408.aspx"
 */
@Bindable(prefix = "Bing5NewsDocumentSource", inherit = CommonAttributes.class)
public class Bing5NewsDocumentSource extends Bing5DocumentSource
{
    /**
     * REST endpoint.
     */
    private final static String SERVICE_URL = "https://api.cognitive.microsoft.com/bing/v5.0/news/search";

    /**
     * Filter news by age.
     */
    @Processing
    @Input
    @Attribute
    @Label("Filter news by age")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.FILTERING)
    public Freshness freshness;

    public Bing5NewsDocumentSource() {
      super(METADATA, SERVICE_URL);
    }

    @Override
    protected void augmentSearchParameters(List<NameValuePair> params) {
      if (freshness != null) {
        params.add(new BasicNameValuePair("freshness", freshness.argName));
      }
    }

    @Override
    protected void handleResponse(BingResponse response, SearchEngineResponse ser) {
      NewsResponse newsResponse = (NewsResponse) response;
      ser.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, newsResponse.totalEstimatedMatches);

      if (newsResponse.value != null) {
        ArrayDeque<NewsResponse.NewsArticle> articles = new ArrayDeque<>(newsResponse.value);
        while (!articles.isEmpty()) {
          NewsResponse.NewsArticle r = articles.removeFirst();
          if (r.clusteredArticles != null) {
            articles.addAll(r.clusteredArticles);
          }

          Document doc = new Document(r.name, r.description, r.url);
          if (r.image != null && r.image.thumbnail != null) {
            doc.setField(Document.THUMBNAIL_URL, r.image.thumbnail.contentUrl);
          }
          if (r.provider != null) {
            ArrayList<String> sources = new ArrayList<>();
            for (NewsResponse.NewsArticle.Organization o : r.provider) {
              sources.add(o.name);
            }
            doc.setField(Document.SOURCES, sources);
          }

          ser.results.add(doc);
        }
      }
    }
}
