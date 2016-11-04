
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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class NewsResponse extends BingResponse {
  @JsonProperty
  public String readLink;

  @JsonProperty
  public long totalEstimatedMatches;

  @JsonProperty
  public List<NewsArticle> value;

  @JsonIgnoreProperties(ignoreUnknown = true)  
  static class NewsArticle {
    @JsonProperty String name;
    @JsonProperty String url;
    @JsonProperty String description;
    @JsonProperty String datePublished;
    @JsonProperty String category;

    @JsonProperty List<Organization> provider;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Organization {
      @JsonProperty String name;
    }

    @JsonProperty List<NewsArticle> clusteredArticles;
    
    @JsonProperty Image image;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Image {
      @JsonProperty String contentUrl;
      @JsonProperty Thumbnail thumbnail;

      @JsonIgnoreProperties(ignoreUnknown = true)
      static class Thumbnail {
        @JsonProperty String contentUrl;
        @JsonProperty int width;
        @JsonProperty int height;
      }
    }    
  }
}