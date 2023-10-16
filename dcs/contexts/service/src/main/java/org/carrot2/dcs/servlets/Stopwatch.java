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

import java.util.concurrent.TimeUnit;

final class Stopwatch {
  long startNanos;

  Stopwatch() {
    this.startNanos = now();
  }

  private long now() {
    return System.nanoTime();
  }

  public long elapsedMillis() {
    return TimeUnit.NANOSECONDS.toMillis(now() - startNanos);
  }
}
