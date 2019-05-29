/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.extras;

public class ArabicLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public ArabicLanguageComponentsTest() {
    super(
        ArabicLanguageComponents.NAME,
        new String[] {"باستثناء", "اكثر"},
        new String[][] {
          {"الحسن", "حسن"},
          {"والحسن", "حسن"},
          {"بالحسن", "حسن"},
          {"كالحسن", "حسن"},
          {"فالحسن", "حسن"},
          {"للاخر", "اخر"},
          {"وحسن", "حسن"},
          {"زوجها", "زوج"},
          {"ساهدان", "ساهد"},
          {"ساهدات", "ساهد"},
          {"ساهدون", "ساهد"},
          {"ساهدين", "ساهد"},
          {"ساهديه", "ساهد"},
          {"ساهدية", "ساهد"},
          {"ساهده", "ساهد"},
          {"ساهدة", "ساهد"},
          {"ساهدي", "ساهد"},
          {"وساهدون", "ساهد"},
          {"ساهدهات", "ساهد"},
        });
  }
}
