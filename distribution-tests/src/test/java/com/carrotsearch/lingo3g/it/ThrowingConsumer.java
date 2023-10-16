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
package com.carrotsearch.lingo3g.it;

@FunctionalInterface
public interface ThrowingConsumer<T> {
  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   */
  void accept(T t) throws Exception;
}
