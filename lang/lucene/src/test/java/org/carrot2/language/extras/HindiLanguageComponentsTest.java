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

public class HindiLanguageComponentsTest extends AbstractLanguageComponentsTest {
  public HindiLanguageComponentsTest() throws IOException {
    super(
        HindiLanguageComponents.NAME,
        new String[] {"जितना"},
        new String[][] {
          {"खाना", "खा"},
          {"हिन्दी", "हिंद"},
        });
  }

  @Test
  public void testTokens() throws IOException {
    Tokenizer tokenizer = components.get(Tokenizer.class);

    Assertions.assertThat(tokenize(tokenizer, "डाटा को कई जगह पर foobar"))
        .containsExactly("डाटा", "को", "कई", "जगह", "पर", "foobar");

    Assertions.assertThat(tokenize(tokenizer, "रिडनडेंसी कहलाता है । डाटा माइनिंग"))
        .containsExactly("रिडनडेंसी", "कहलाता", "है", "।", "डाटा", "माइनिंग");
  }
}
