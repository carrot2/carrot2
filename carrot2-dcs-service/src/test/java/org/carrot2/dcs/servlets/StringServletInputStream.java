package org.carrot2.dcs.servlets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class StringServletInputStream extends ServletInputStream {
  private final InputStream delegate;

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
