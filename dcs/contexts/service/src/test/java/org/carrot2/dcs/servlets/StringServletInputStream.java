package org.carrot2.dcs.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

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
