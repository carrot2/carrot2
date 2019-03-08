package org.carrot2.dcs.servlets;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public abstract class ServletTest {
  @Mock
  protected HttpServletRequest request;

  @Mock
  protected HttpServletResponse response;

  @Mock
  protected ServletConfig config;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  protected StringServletInputStream requestStream(String resource) {
    return new StringServletInputStream(getClass().getResourceAsStream(resource));
  }

  protected String response(String resource) {
    try (InputStream is = getClass().getResourceAsStream(resource)) {
      if (is == null) {
        throw new RuntimeException("Resource not found: " + resource);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte [] buf = new byte [1024];
      for (int len; (len = is.read(buf)) > 0;) {
        baos.write(buf, 0, len);
      }
      return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
