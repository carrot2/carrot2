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
package org.carrot2.language;

import java.util.Collections;
import java.util.List;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrObjectArray;

/**
 * Ephemeral per-request overrides for the default {@link LanguageComponents} passed to the
 * algorithm.
 *
 * @see #override(LanguageComponents)
 * @since 4.1.0
 */
public class EphemeralDictionaries extends AttrComposite {
  /**
   * Additional stop word filtering dictionaries (supplying word filters that should be discarded
   * from the input).
   *
   * <p>One or more dictionaries can be supplied. The default implementation in {@link
   * DefaultDictionaryImpl} supports exact string matching and regular expression patterns.
   *
   * <p>REST-style example using the default implementation:
   *
   * <pre>{@code "wordFilters": [{
   *   "exact": ["word1", "word2"],
   *   "regexp": [
   *     "(?).+pattern1.+",
   *     "(?).+[0-9]{2}.+"
   *   ]
   * }]}</pre>
   *
   * @see DefaultDictionaryImpl
   * @see StopwordFilterDictionary
   * @see StopwordFilter
   */
  public AttrObjectArray<StopwordFilterDictionary> wordFilters =
      attributes.register(
          "wordFilters",
          AttrObjectArray.builder(StopwordFilterDictionary.class, DefaultDictionaryImpl::new)
              .label("Word filtering dictionaries.")
              .defaultValue(Collections.emptyList()));

  /**
   * Additional label filtering dictionaries (supplying cluster label filters that should be
   * discarded from the output).
   *
   * <p>One or more dictionaries can be supplied. The default implementation in {@link
   * DefaultDictionaryImpl} supports exact string matching and regular expression patterns.
   *
   * <p>REST-style example using the default implementation:
   *
   * <pre>{@code "labelFilters": [{
   *   "exact": ["Cluster Label 1", "Foo Bar"],
   *   "regexp": [
   *     "(?).+pattern1.+",
   *     "(?).+[0-9]{2}.+"
   *   ]
   * }]}</pre>
   *
   * @see DefaultDictionaryImpl
   * @see LabelFilterDictionary
   * @see LabelFilter
   */
  public AttrObjectArray<LabelFilterDictionary> labelFilters =
      attributes.register(
          "labelFilters",
          AttrObjectArray.builder(LabelFilterDictionary.class, DefaultDictionaryImpl::new)
              .label("Cluster label filtering dictionaries.")
              .defaultValue(Collections.emptyList()));

  /**
   * Override components of the existing {@link LanguageComponents} instance with this object's
   * dictionaries.
   */
  public LanguageComponents override(LanguageComponents languageComponents) {
    List<StopwordFilterDictionary> wordFilterAttrs = this.wordFilters.get();
    if (wordFilterAttrs != null && !wordFilterAttrs.isEmpty()) {
      StopwordFilter dictFilter =
          wordFilterAttrs.stream()
              .map(StopwordFilterDictionary::compileStopwordFilter)
              .reduce(StopwordFilter::and)
              .get();

      languageComponents =
          languageComponents.override(
              StopwordFilter.class,
              (previous) ->
                  () -> {
                    StopwordFilter previousFilter = previous.get();
                    if (previousFilter != null) {
                      return previousFilter.and(dictFilter);
                    } else {
                      return dictFilter;
                    }
                  });
    }

    List<LabelFilterDictionary> labelFilterAttrs = this.labelFilters.get();
    if (labelFilterAttrs != null && !labelFilterAttrs.isEmpty()) {
      LabelFilter dictFilter =
          labelFilterAttrs.stream()
              .map(LabelFilterDictionary::compileLabelFilter)
              .reduce(LabelFilter::and)
              .get();

      languageComponents =
          languageComponents.override(
              LabelFilter.class,
              (previous) ->
                  () -> {
                    LabelFilter previousFilter = previous.get();
                    if (previousFilter != null) {
                      return previousFilter.and(dictFilter);
                    } else {
                      return dictFilter;
                    }
                  });
    }

    return languageComponents;
  }
}
