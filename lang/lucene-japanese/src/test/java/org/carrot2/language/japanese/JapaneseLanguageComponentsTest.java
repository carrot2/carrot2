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
package org.carrot2.language.japanese;

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

public class JapaneseLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public JapaneseLanguageComponentsTest() throws IOException {
    super(JapaneseLanguageComponents.NAME, new String[] {"に対する", "ものの"}, new String[][] {});
  }

  @Test
  public void testTokenization() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "シニアソフトウェアエンジニア"))
        .containsExactly("シニア", "ソフトウェア", "エンジニア");
  }
}
