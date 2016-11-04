
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

import com.fasterxml.jackson.annotation.JsonProperty;

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