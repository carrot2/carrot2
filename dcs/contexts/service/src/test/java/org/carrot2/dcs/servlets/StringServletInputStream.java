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

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class StringServletInputStream extends ServletInputStream {
  private final InputStream delegate;

  StringServletInputStream(String utf8) {
    this(new ByteArrayInputStream(utf8.getBytes(StandardCharsets.UTF_8)));
  }

  StringServletInputStream(InputStream delegate) {
    this.delegate = Objects.requireNonNull(delegate);
  }

  @Override
  public boolean isFinished() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReady() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setReadListener(ReadListener readListener) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int read() throws IOException {
    return delegate.read();
  }
}
