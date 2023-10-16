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
package org.carrot2.language.extras;

import java.io.IOException;

public class EstonianLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public EstonianLanguageComponentsTest() throws IOException {
    super(
        EstonianLanguageComponents.NAME,
        new String[] {"midagi", "kõik"},
        new String[][] {{"inimesed", /* no specific stemmer */ null}});
  }
}
