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
package org.carrot2.text.suffixtree;

/** A {@link Sequence} wrapper for any {@link CharSequence}. */
public final class CharacterSequence implements Sequence {
  public final CharSequence value;

  public CharacterSequence(CharSequence chs) {
    this.value = chs;
  }

  public int size() {
    return value.length();
  }

  public int objectAt(int i) {
    return value.charAt(i);
  }
}
