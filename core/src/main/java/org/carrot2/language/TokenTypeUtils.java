/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2025, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

/** Utility methods for working with {@link Tokenizer} attributes. */
public final class TokenTypeUtils {
  private TokenTypeUtils() {
    // no instances.
  }

  /** Mask the given raw token type and leave just the token type bits. */
  public static int maskType(int rawType) {
    return rawType & Tokenizer.TYPE_MASK;
  }

  /**
   * Returns <code>true</code> if the given type has {@link Tokenizer#TF_SEPARATOR_DOCUMENT} set.
   */
  public static boolean isDocumentSeparator(int type) {
    return (type & Tokenizer.TF_SEPARATOR_DOCUMENT) != 0;
  }

  /** Returns <code>true</code> if the given type has {@link Tokenizer#TF_SEPARATOR_FIELD} set. */
  public static boolean isFieldSeparator(int type) {
    return (type & Tokenizer.TF_SEPARATOR_FIELD) != 0;
  }

  /** Returns <code>true</code> if the given type has {@link Tokenizer#TF_TERMINATOR} set. */
  public static boolean isTerminator(int type) {
    return (type & Tokenizer.TF_TERMINATOR) != 0;
  }

  /** Return <code>true</code> if {@link Tokenizer#TF_COMMON_WORD} is set. */
  public static boolean isCommon(int flag) {
    return (flag & Tokenizer.TF_COMMON_WORD) != 0;
  }

  /** Return <code>true</code> if {@link Tokenizer#TF_QUERY_WORD} is set. */
  public static boolean isInQuery(int flag) {
    return (flag & Tokenizer.TF_QUERY_WORD) != 0;
  }
}
