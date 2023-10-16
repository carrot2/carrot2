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
package org.carrot2.language.chinese;

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

public class TraditionalChineseLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public TraditionalChineseLanguageComponentsTest() throws IOException {
    super(
        TraditionalChineseLanguageComponents.NAME, new String[] {"除此之外", "不特"}, new String[][] {});
  }

  @Test
  public void testTokenization() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "东亚货币贬值")).containsExactly("东亚", "货币", "贬值");

    Assertions.assertThat(tokenize(tokenizer, "test 东亚货币贬值 English"))
        .containsExactly("test", "东亚", "货币", "贬值", "English");
  }

  @Test
  public void testPunctuationTokens() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "东亚货币贬值。周小燕老师，您辛苦了！"))
        .containsExactly("东亚", "货币", "贬值", "周", "小", "燕", "老师", "您", "辛苦", "了");
  }

  @Test
  public void testNumericTokens() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "湖南１１个部门")).containsExactly("湖南", "１１", "个", "部门");
  }
}
