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
package org.carrot2.util;

import java.util.Arrays;

/**
 * Implements {@link CharSequence} over a mutable <code>char[]</code> buffer.
 *
 * <p>This class implements proper content-based {@link #hashCode()} and {@link #equals(Object)}
 * against other {@link MutableCharArray} objects, assuming the underlying character buffers does
 * not change. In case the buffers is changed, the resulting behavior is unpredictable.
 */
public final class MutableCharArray implements CharSequence, Cloneable {
  private static final char[] EMPTY = new char[0];

  /**
   * Internal buffer with character data. The buffer may be smaller or larger than the actual
   * sequence represented by this object.
   */
  private char[] buffer;

  /** Sequence length in {@link #buffer}, counting from {@link #start}. */
  private int length;

  /** Sequence start in {@link #buffer}. */
  private int start;

  /** Hash code (lazily calculated). */
  private int hash;

  /** Creates an empty {@link MutableCharArray}. */
  public MutableCharArray() {
    reset(EMPTY);
  }

  /**
   * Creates a {@link MutableCharArray} from another {@link CharSequence}, creates a new buffer to
   * store characters.
   */
  public MutableCharArray(CharSequence seq) {
    reset(seq);
  }

  /**
   * Resets the internal buffer to use the provided argument.
   *
   * @see #reset(char[])
   */
  public MutableCharArray(char[] buffer) {
    reset(buffer, 0, buffer.length);
  }

  /**
   * Resets the internal buffer to use the provided argument.
   *
   * @see #reset(char[])
   */
  public MutableCharArray(char[] buffer, int start, int length) {
    reset(buffer, start, length);
  }

  /**
   * Resets internal buffers in this object to represent another character sequence. See class
   * header notes for side-effects on {@link #equals(Object)} and {@link #hashCode()}.
   */
  public void reset(CharSequence seq) {
    if (buffer == null || buffer.length < seq.length()) {
      buffer = new char[seq.length()];
    }
    this.length = seq.length();
    this.start = 0;

    for (int i = 0; i < length; i++) {
      buffer[i] = seq.charAt(i);
    }

    this.hash = hashCode(buffer, start, length);
  }

  /**
   * Resets internal buffers in this object to point to another character buffer. See class header
   * notes for side-effects on {@link #equals(Object)} and {@link #hashCode()}.
   */
  public void reset(char[] buffer) {
    reset(buffer, 0, buffer.length);
  }

  /**
   * Resets internal buffers in this object to point to another character buffer. See class header
   * notes for side-effects on {@link #equals(Object)} and {@link #hashCode()}.
   */
  public void reset(char[] buffer, int start, int length) {
    this.length = length;
    this.start = start;
    this.buffer = buffer;

    this.hash = hashCode(buffer, start, length);
  }

  /** Resets internal buffers to an empty string. */
  public void reset() {
    reset(EMPTY);
  }

  /**
   * @return Returns the internal buffer <i>currently</i> used to store the content of this char
   *     sequence.
   */
  public char[] getBuffer() {
    return this.buffer;
  }

  /**
   * @return the offset at which the data <i>currently</i> starts in the buffer.
   * @see #getBuffer()
   * @see #length
   */
  public int getStart() {
    return this.start;
  }

  /** */
  public final char charAt(final int index) {
    if (index < 0 || index >= length) {
      throw new IndexOutOfBoundsException();
    }

    return buffer[start + index];
  }

  /** */
  public int length() {
    return length;
  }

  /** */
  public MutableCharArray subSequence(int start, int end) {
    return new MutableCharArray(buffer, start, end - start);
  }

  /*
   *
   */
  @Override
  public String toString() {
    return new String(buffer, start, length);
  }

  /** See comments in the header of this class. */
  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object other) {
    return other == this || getClass().isInstance(other) && equals(getClass().cast(other));
  }

  public final boolean equals(MutableCharArray other) {
    return this.hash == other.hash
        && this.length == other.length
        && Arrays.equals(
            this.buffer,
            this.start,
            this.start + this.length,
            other.buffer,
            other.start,
            other.start + other.length);
  }

  /**
   * Calculates a hash code for a given portion of the character buffer. The returned value is
   * identical to what be returned from {@link #hashCode()} if a wrapper {@link MutableCharArray}
   * were created.
   */
  public static final int hashCode(final char[] buffer, int start, int length) {
    int h = 0;
    for (int i = length - 1; i >= 0; i--) {
      h = 31 * h + buffer[start++];
    }

    return h;
  }

  /*
   *
   */
  public MutableCharArray clone() {
    if (this.length == 0) return new MutableCharArray();

    final char[] cloned = new char[length];
    System.arraycopy(buffer, start, cloned, 0, length);
    return new MutableCharArray(cloned);
  }

  public char[] toArray() {
    return Arrays.copyOfRange(getBuffer(), getStart(), length());
  }
}
