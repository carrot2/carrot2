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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrObjectArray;

/**
 * Attribute providing lexical data overrides for {@link LanguageComponents}.
 *
 * @see #override(LanguageComponents)
 * @since 4.1.0
 */
public class Dictionaries extends AttrComposite {
  /**
   * Additional stop word dictionaries (words that should be discarded in the input).
   *
   * @see WordFilter#ignoreWord
   */
  public AttrObjectArray<WordFilterAttr> wordFilters =
      attributes.register(
          "words",
          AttrObjectArray.builder(WordFilterAttr.class, WordListFilter::new)
              .label("Common word filtering dictionaries.")
              .defaultValue(Collections.emptyList()));

  /**
   * Additional label filters.
   *
   * @see LabelFilter#ignoreLabel
   */
  public AttrObjectArray<LabelFilterAttr> labelFilters =
      attributes.register(
          "labels",
          AttrObjectArray.builder(LabelFilterAttr.class, RegExpLabelFilter::new)
              .label("Cluster label filtering dictionaries.")
              .defaultValue(Collections.emptyList()));

  /**
   * Override components of the existing {@link LanguageComponents} instance with this object's
   * dictionaries.
   */
  public LanguageComponents override(LanguageComponents languageComponents) {
    List<WordFilter> wordFilters;
    {
      List<WordFilterAttr> wordFilterAttrs = this.wordFilters.get();
      if (wordFilterAttrs == null) {
        wordFilterAttrs = Collections.emptyList();
      }
      wordFilters = wordFilterAttrs.stream().map(WordFilterAttr::get).collect(Collectors.toList());
    }

    List<LabelFilter> labelFilters;
    {
      List<LabelFilterAttr> labelFilterAttrs = this.labelFilters.get();
      if (labelFilterAttrs == null) {
        labelFilterAttrs = Collections.emptyList();
      }
      labelFilters =
          labelFilterAttrs.stream().map(LabelFilterAttr::get).collect(Collectors.toList());
    }

    LexicalData lexicalData = new LexicalDataImpl2(wordFilters, labelFilters);
    return languageComponents.override(
        LexicalData.class, (previous) -> () -> new ChainedLexicalData(lexicalData, previous.get()));
  }
}
