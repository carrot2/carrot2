package org.carrot2.source.microsoft.v5;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON responses, Bing API V5.
 * 
 * @see "https://msdn.microsoft.com/en-us/library/dn760794.aspx#response"
 */
@JsonTypeInfo(
    defaultImpl = UnstructuredResponse.class,
    use = JsonTypeInfo.Id.NAME,
    property = "_type")
@JsonSubTypes({
  @JsonSubTypes.Type(name = "unstructured", value = UnstructuredResponse.class),
  @JsonSubTypes.Type(name = "ErrorResponse", value = ErrorResponse.class),
  @JsonSubTypes.Type(name = "SearchResponse", value = SearchResponse.class)})
public abstract class BingResponse {
  private final static ObjectMapper mapper;
  static {
    mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mapper.enable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
    mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    mapper.getFactory().enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    mapper.getFactory().enable(JsonParser.Feature.ALLOW_COMMENTS);
    mapper.getFactory().enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES); 
  }
  
  public static BingResponse parse(InputStream is) throws IOException {
    return mapper.readValue(is, BingResponse.class);
  }
}

class UnstructuredResponse extends BingResponse {
  @JsonProperty(required = true)
  public int statusCode;

  @JsonProperty
  public String message;
}

class ErrorResponse extends BingResponse {
  @JsonProperty
  public List<Error> errors;
  
  static class Error {
    /**
     * The error code that identifies the error.
     * 
     * <pre>
     * 200 The call succeeded.
     * 400 One of the query parameters is missing or not valid. For details, see ErrorResponse.
     * 401 The subscription key is missing or is not valid.
     * 403 The user is authenticated (for example, used a valid subscription key) but they don’t have permission to the 
     *     requested resource. Bing may also return this status if the caller exceeded their queries per month quota.
     * 410 The request used HTTP instead of the HTTPS protocol. HTTPS is the only supported protocol. 
     * 429 The caller exceeded their queries per second quota.
     * </pre>
     */
    @JsonProperty
    public String code;

    /**
     * A description of the error.
     */
    @JsonProperty
    public String message;

    /**
     * The query parameter in the request that caused the error.
     */
    @JsonProperty
    public String parameter;

    /**
     * The query parameter's value that was not valid.
     */
    @JsonProperty
    public String value;
  }
}

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
  
  @JsonProperty
  public Images images;

  @JsonIgnoreProperties(ignoreUnknown = true)  
  static class Images {
    @JsonProperty String id;
    @JsonProperty String readLink;
    @JsonProperty String webSearchUrl;
    @JsonProperty boolean isFamilyFriendly;

    @JsonProperty
    public List<Result> value;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Result {
      @JsonProperty String name;
      @JsonProperty String webSearchUrl;
      @JsonProperty String thumbnailUrl;
      @JsonProperty String datePublished;
      @JsonProperty String contentUrl;
      @JsonProperty String hostPageUrl;
      @JsonProperty String encodingFormat;
      @JsonProperty String hostPageDisplayUrl;
      @JsonProperty int width;
      @JsonProperty int height;
      // Some properties omitted.
    }
  }
  
  @JsonProperty
  public News news;
  
  @JsonIgnoreProperties(ignoreUnknown = true)  
  static class News {
    @JsonProperty String id;
    @JsonProperty String readLink;

    @JsonProperty
    public List<Result> value;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Result {
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
}