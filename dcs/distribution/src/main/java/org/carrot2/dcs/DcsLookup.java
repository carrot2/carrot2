package org.carrot2.dcs;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(name = "dcs", category = StrLookup.CATEGORY)
public class DcsLookup extends AbstractLookup {
  private static volatile Map<String, String> props;

  @Override
  public String lookup(final LogEvent event, final String key) {
    if (props == null) {
      throw new RuntimeException("Not initialized?");
    }
    if (!props.containsKey(key)) {
      throw new RuntimeException("Missing key: " + key);
    }
    return props.get(key);
  }

  static final void setup(Map<String, String> properties) {
    props = new HashMap<>(properties);
  }
}
