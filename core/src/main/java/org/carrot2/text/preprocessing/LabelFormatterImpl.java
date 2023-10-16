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
package org.carrot2.text.preprocessing;

import org.carrot2.util.CharArrayUtils;

public class LabelFormatterImpl implements LabelFormatter {
  private final String delimiter;

  public LabelFormatterImpl(String termDelimiter) {
    this.delimiter = termDelimiter;
  }

  public String format(char[][] image, boolean[] stopWord) {
    final StringBuilder label = new StringBuilder();
    for (int i = 0; i < image.length; i++) {
      if (i > 0) label.append(delimiter);
      append(label, image[i], stopWord[i]);
    }
    return label.toString();
  }

  private static void append(final StringBuilder label, final char[] image, boolean stopword) {
    if (CharArrayUtils.hasCapitalizedLetters(image)) {
      label.append(image);
    } else if (label.length() == 0 || !stopword) {
      label.append(CharArrayUtils.toCapitalizedCopy(image));
    } else {
      label.append(CharArrayUtils.toLowerCaseCopy(image));
    }
  }
}
