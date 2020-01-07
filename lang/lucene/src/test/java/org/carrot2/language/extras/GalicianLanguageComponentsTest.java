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
package org.carrot2.language.extras;

public class GalicianLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public GalicianLanguageComponentsTest() {
    super(
        GalicianLanguageComponents.NAME,
        new String[] {"vosos", "miñas"},
        new String[][] {
          {"correspondente", "correspond"},
          {"corresponderá", "correspond"},
        });
  }
}
