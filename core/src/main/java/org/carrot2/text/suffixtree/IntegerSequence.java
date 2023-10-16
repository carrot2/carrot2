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

/** An {@link Sequence} wrapper for an array of integers. */
public final class IntegerSequence implements Sequence {
  private final int[] seq;
  private final int start;
  private final int length;

  public IntegerSequence(int[] seq) {
    this(seq, 0, seq.length);
  }

  public IntegerSequence(int[] seq, int start, int length) {
    this.seq = seq;
    this.start = start;
    this.length = length;
  }

  public int size() {
    return length;
  }

  public int objectAt(int i) {
    return seq[start + i];
  }
}
