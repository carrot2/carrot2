/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

public class AcceptAllLexicalData implements LexicalData {
  @Override
  public boolean ignoreWord(CharSequence word) {
    return false;
  }

  @Override
  public boolean ignoreLabel(CharSequence labelCandidate) {
    return false;
  }
}
