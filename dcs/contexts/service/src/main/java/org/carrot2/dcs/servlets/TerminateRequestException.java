/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.servlets;

import org.carrot2.dcs.model.ErrorResponseType;

@SuppressWarnings("serial")
public class TerminateRequestException extends Exception {
  public final ErrorResponseType type;

  TerminateRequestException(ErrorResponseType type, String message, Throwable cause) {
    super(message, cause);
    this.type = type;
  }

  TerminateRequestException(ErrorResponseType type, String message) {
    super(message);
    this.type = type;
  }
}
