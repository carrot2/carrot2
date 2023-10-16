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

public class JvmNameMapper implements ClassNameMapper {
  public static JvmNameMapper INSTANCE = new JvmNameMapper();

  private JvmNameMapper() {}

  public Object fromName(String className) {
    try {
      return Class.forName(className).getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String toName(Object object) {
    return object.getClass().getName();
  }
}
