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
package org.carrot2.language.snowball;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;

/**
 * This is the rev 502 of the Snowball SVN trunk, now located at <a target="_blank"
 * href="https://github.com/snowballstem/snowball/tree/e103b5c257383ee94a96e7fc58cab3c567bf079b">GitHub</a>,
 * but modified:
 *
 * <ul>
 *   <li>made abstract and introduced abstract method stem to avoid expensive reflection in filter
 *       class.
 *   <li>refactored StringBuffers to StringBuilder
 *   <li>uses char[] as buffer instead of StringBuffer/StringBuilder
 *   <li>eq_s,eq_s_b,insert,replace_s take CharSequence like eq_v and eq_v_b
 *   <li>use MethodHandles and fix <a target="_blank"
 *       href="http://article.gmane.org/gmane.comp.search.snowball/1139">method visibility bug</a>.
 * </ul>
 */
public final class Among {

  public Among(
      String s, int substring_i, int result, String methodname, MethodHandles.Lookup methodobject) {
    this.s_size = s.length();
    this.s = s.toCharArray();
    this.substring_i = substring_i;
    this.result = result;
    if (methodname.isEmpty()) {
      this.method = null;
    } else {
      final Class<? extends SnowballProgram> clazz =
          methodobject.lookupClass().asSubclass(SnowballProgram.class);
      try {
        this.method =
            methodobject
                .findVirtual(clazz, methodname, MethodType.methodType(boolean.class))
                .asType(MethodType.methodType(boolean.class, SnowballProgram.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new RuntimeException(
            String.format(
                Locale.ENGLISH,
                "Snowball program '%s' is broken, cannot access method: boolean %s()",
                clazz.getSimpleName(),
                methodname),
            e);
      }
    }
  }

  final int s_size; /* search string */
  final char[] s; /* search string */
  final int substring_i; /* index to longest matching substring */
  final int result; /* result of the lookup */

  // Make sure this is not accessible outside package for Java security reasons!
  final MethodHandle method; /* method to use if substring matches */
}
