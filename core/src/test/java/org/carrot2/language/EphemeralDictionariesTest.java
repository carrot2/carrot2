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
package org.carrot2.language;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.Attrs;
import org.junit.Test;

public class EphemeralDictionariesTest extends TestBase {
  @Test
  public void testImplementationsRoundTrip() {
    EphemeralDictionaries dictionaries = new EphemeralDictionaries();

    DefaultDictionaryImpl filter1 = new DefaultDictionaryImpl();
    filter1.exact.set("word-image1", "word-image2", "word-image3");
    dictionaries.wordFilters.set(List.of(filter1));

    DefaultDictionaryImpl filter2 = new DefaultDictionaryImpl();
    filter2.exact.set("label-image1", "label-image2");
    filter2.regexp.set("label-regex1", "label-regex2");
    DefaultDictionaryImpl filter3 = new DefaultDictionaryImpl();
    filter3.exact.set("label-image3");
    dictionaries.labelFilters.set(List.of(filter2, filter3));

    // Serialize to JSON.
    String asJson = Attrs.toJson(dictionaries, AliasMapper.SPI_DEFAULTS);
    System.out.println(asJson);

    // Round trip to/from map.
    EphemeralDictionaries repopulated =
        Attrs.populate(new EphemeralDictionaries(), Attrs.extract(dictionaries));
    Assertions.assertThat(Attrs.toJson(repopulated, AliasMapper.SPI_DEFAULTS)).isEqualTo(asJson);
  }

  @Test
  public void testDefaultFilterAttrImpl() {
    DefaultDictionaryImpl filter = new DefaultDictionaryImpl();
    filter.exact.set("word1", "word2");
    filter.regexp.set("foo.+");

    LabelFilter labelFilter = filter.compileLabelFilter();
    Assertions.assertThat(labelFilter.ignoreLabel("word1")).isTrue();
    Assertions.assertThat(labelFilter.ignoreLabel("word2")).isTrue();
    Assertions.assertThat(labelFilter.ignoreLabel("word3")).isFalse();
    Assertions.assertThat(labelFilter.ignoreLabel("foobar")).isTrue();
    Assertions.assertThat(labelFilter.ignoreLabel("prefix-foobar")).isFalse();
  }
}
