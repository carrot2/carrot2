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

public class LatvianLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public LatvianLanguageComponentsTest() throws IOException {
    super(
        LatvianLanguageComponents.NAME,
        new String[] {"apakšpus", "priekš"},
        new String[][] {
          {"lāča", "lāc"},
          {"akmeņiem", "akmen"},
        });
  }
}
