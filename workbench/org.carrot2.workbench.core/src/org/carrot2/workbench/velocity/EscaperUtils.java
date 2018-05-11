package org.carrot2.workbench.velocity;

import org.apache.commons.lang3.StringEscapeUtils;

public class EscaperUtils {
  public String html(String in) {
    return StringEscapeUtils.escapeHtml4(in);
  }
}
