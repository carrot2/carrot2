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
package com.carrotsearch.jsondoclet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import jdk.javadoc.doclet.Doclet;

abstract class OptionImpl<T> implements Doclet.Option {
  private final List<String> names;
  private final String description;
  private final int argCount;
  private final String argDescription;
  private final Function<String, T> argConvert;

  public static class SingleArgumentOption<E> extends OptionImpl<E> {
    private E value;

    SingleArgumentOption(
        String description,
        List<String> optionNames,
        String argDescription,
        Function<String, E> argConvert) {
      super(description, optionNames, 1, argDescription, argConvert);
    }

    @Override
    protected boolean process0(String option, List<E> args) {
      if (args.size() != 1) {
        return false;
      } else {
        value = args.iterator().next();
        return true;
      }
    }

    public boolean hasValue() {
      return getValue() != null;
    }

    public E getValue() {
      return value;
    }
  }

  private OptionImpl(
      String description,
      List<String> optionNames,
      int argCount,
      String argDescription,
      Function<String, T> argConvert) {
    if (optionNames.isEmpty()) throw new IllegalArgumentException();
    if (description.isEmpty()) throw new IllegalArgumentException();
    if (argCount == 0 && argConvert != null) throw new IllegalArgumentException();
    if (argCount != 0 && argConvert == null) throw new IllegalArgumentException();
    this.names = optionNames;
    this.description = description;
    this.argDescription = argDescription;
    this.argCount = argCount;
    this.argConvert = argConvert;
  }

  public static SingleArgumentOption<Path> pathOption(
      String description, List<String> optionNames) {
    return new SingleArgumentOption<>(
        description, optionNames, "<path>", (value) -> Paths.get(value));
  }

  @Override
  public int getArgumentCount() {
    return argCount;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return names;
  }

  @Override
  public String getParameters() {
    return argDescription;
  }

  @Override
  public final boolean process(String option, List<String> arguments) {
    return process0(
        option,
        arguments.stream().limit(getArgumentCount()).map(argConvert).collect(Collectors.toList()));
  }

  protected abstract boolean process0(String option, List<T> args);
}
