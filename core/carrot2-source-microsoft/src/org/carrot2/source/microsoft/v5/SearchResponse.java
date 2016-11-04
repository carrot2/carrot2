
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
class SearchResponse extends BingResponse {
  @JsonProperty
  public WebPages webPages;

  @JsonIgnoreProperties(ignoreUnknown = true)  
  static class WebPages {
    @JsonProperty
    public String webSearchUrl;
    
    @JsonProperty
    public long totalEstimatedMatches;
    
    @JsonProperty
    public List<Result> value;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Result {
      @JsonProperty String id;
      @JsonProperty String name;
      @JsonProperty String url;
      @JsonProperty String displayUrl;
      @JsonProperty String snippet;
      @JsonProperty String dateLastCrawled;

      // Omit: deepLinks
    }
  }
}