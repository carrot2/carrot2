package org.carrot2.dcs.it;

import java.io.Closeable;
import java.net.URI;

public interface DcsService extends Closeable {
  URI getAddress();
}
