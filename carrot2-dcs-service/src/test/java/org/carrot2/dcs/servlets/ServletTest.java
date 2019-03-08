package org.carrot2.dcs.servlets;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
}
