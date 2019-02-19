
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.language;

/**
 *
 */
public class EnglishLanguageComponentsFactoryTest extends LanguageComponentsFactoryTestBase {
  @Override
  protected LanguageComponents getComponents() {
    return LanguageComponents.get(EnglishLanguageComponentsFactory.NAME);
  }

  protected String[][] getStemmingTestData() {
    return new String[][] {
        {"pulps", "pulp"},
        {"driving", "drive"},
        {"king's", "king"},
        {"mining", "mine"}
    };
  }

  protected String[] getCommonWordsTestData() {
    return new String[] {
        "and", "or", "to", "from"
    };
  }
}
