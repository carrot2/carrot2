/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.client;

import java.net.HttpURLConnection;

public enum ErrorResponseType {
  BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST),
  UNHANDLED_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR);

  public final int httpStatusCode;

  ErrorResponseType(int code) {
    this.httpStatusCode = code;
  }
}
