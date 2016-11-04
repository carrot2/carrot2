
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

import java.io.IOException;
import java.io.InputStream;

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
  @JsonSubTypes.Type(name = "SearchResponse", value = SearchResponse.class),
  @JsonSubTypes.Type(name = "News", value = NewsResponse.class)})
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