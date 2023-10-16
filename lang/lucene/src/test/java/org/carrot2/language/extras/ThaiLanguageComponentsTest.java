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
import org.assertj.core.api.Assertions;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

public class ThaiLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public ThaiLanguageComponentsTest() throws IOException {
    super(ThaiLanguageComponents.NAME, new String[] {"เนื่องจาก", "เดียวกัน"}, new String[][] {});
  }

  @Test
  public void testTokenization() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "การที่ได้ต้องแสดงว่างานดี"))
        .containsExactly("การ", "ที่", "ได้", "ต้อง", "แสดง", "ว่า", "งาน", "ดี");

    Assertions.assertThat(tokenize(tokenizer, "ประโยคว่า The quick brown"))
        .containsExactly("ประโยค", "ว่า", "The", "quick", "brown");
  }
}
