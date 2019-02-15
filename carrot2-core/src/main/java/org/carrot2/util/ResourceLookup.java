package org.carrot2.util;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLookup {
  InputStream open(String resource) throws IOException;
}
