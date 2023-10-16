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
package org.carrot2.attrs;

import java.util.function.Consumer;

public final class Constraint<T> {
  private final Consumer<T> consumer;
  private final String description;

  private Constraint(String description, Consumer<T> consumer) {
    this.consumer = consumer;
    this.description = description;
  }

  public void accept(T t) {
    consumer.accept(t);
  }

  public static <T> Constraint<T> named(String description, Consumer<T> consumer) {
    return new Constraint<>(description, consumer);
  }

  public String description() {
    return description;
  }
}
