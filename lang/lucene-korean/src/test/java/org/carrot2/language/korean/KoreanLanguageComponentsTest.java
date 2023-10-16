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
package org.carrot2.language.korean;

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

public class KoreanLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public KoreanLanguageComponentsTest() throws IOException {
    super(KoreanLanguageComponents.NAME, new String[] {}, new String[][] {});
  }

  @Test
  public void testTokenization() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "2018 평창 동계올림픽대회"))
        .containsExactly("2018", "평창", "동계", "올림픽", "대회");
  }
}
