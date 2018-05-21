
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2018, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.velocity;

import org.apache.commons.lang3.StringEscapeUtils;

public class EscaperUtils {
  public String html(String in) {
    return StringEscapeUtils.escapeHtml4(in);
  }
}
