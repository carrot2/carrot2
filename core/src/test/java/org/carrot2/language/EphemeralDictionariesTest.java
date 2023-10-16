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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.attrs.Attrs;
import org.carrot2.internal.nanojson.JsonObject;
import org.carrot2.internal.nanojson.JsonParser;
import org.carrot2.internal.nanojson.JsonParserException;
import org.junit.Test;

public class EphemeralDictionariesTest extends TestBase {
  @Test
  public void testImplementationsRoundTrip() throws JsonParserException {
    EphemeralDictionaries dictionaries = new EphemeralDictionaries();

    DefaultDictionaryImpl filter1 = new DefaultDictionaryImpl();
    filter1.exact.set("word-image1", "word-image2", "word-image3");
    dictionaries.wordFilters.set(List.of(filter1));
    DefaultDictionaryImpl filter2 = new DefaultDictionaryImpl();
    filter2.exact.set("label-image1", "label-image2");
    filter2.regexp.set("label-regex1", "label-regex2");
    DefaultDictionaryImpl filter3 = new DefaultDictionaryImpl();
    filter3.exact.set("label-image3");
    filter3.glob.set("foo bar", "foo *");
    dictionaries.labelFilters.set(List.of(filter2, filter3));

    // Serialize to JSON.
    String typedJson = Attrs.toJson(dictionaries);

    // Round trip to/from map.
    EphemeralDictionaries repopulated =
        Attrs.populate(new EphemeralDictionaries(), Attrs.extract(dictionaries));
    Assertions.assertThat(Attrs.toJson(repopulated)).isEqualTo(typedJson);

    // Round trip from json.
    JsonObject dict = JsonParser.object().from(typedJson);
    repopulated = Attrs.fromMap(EphemeralDictionaries.class, dict);
    Assertions.assertThat(Attrs.toJson(repopulated)).isEqualTo(typedJson);

    // Round trip from @type-less JSON.
    String typelessJson = typedJson.replaceAll("\"@type\"[^\n]+", "");
    dict = JsonParser.object().from(typelessJson);
    repopulated = Attrs.fromMap(EphemeralDictionaries.class, EphemeralDictionaries::new, dict);
    Assertions.assertThat(Attrs.toJson(repopulated)).isEqualTo(typedJson);
  }

  @Test
  public void testDefaultFilterAttrImpl() {
    DefaultDictionaryImpl filter = new DefaultDictionaryImpl();
    filter.exact.set("word1", "word2");
    filter.regexp.set("^foo.+");

    LabelFilter labelFilter = filter.compileLabelFilter();
    Assertions.assertThat(labelFilter.test("word1")).isFalse();
    Assertions.assertThat(labelFilter.test("word2")).isFalse();
    Assertions.assertThat(labelFilter.test("word3")).isTrue();
    Assertions.assertThat(labelFilter.test("foobar")).isFalse();
    Assertions.assertThat(labelFilter.test("prefix-foobar")).isTrue();
  }

  @Test
  public void testGlobs() {
    class Entry {
      List<String> patterns = new ArrayList<>();
      List<String> positive = new ArrayList<>();
      List<String> negative = new ArrayList<>();

      Entry(String... patterns) {
        this.patterns.addAll(Arrays.asList(patterns));
      }

      Entry positive(String... patterns) {
        this.positive.addAll(Arrays.asList(patterns));
        return this;
      }

      Entry negative(String... patterns) {
        this.negative.addAll(Arrays.asList(patterns));
        return this;
      }
    }

    List<Entry> entries =
        List.of(
            new Entry("more information")
                .positive("More information", "MORE INFORMATION")
                .negative(
                    "more informations",
                    "more informations",
                    "more information about",
                    "some more information"),
            new Entry("more information *")
                .positive("more information", "More information about", "More information about a")
                .negative("more informations", "more informations about", "some more information"),
            new Entry("* information *")
                .positive(
                    "information",
                    "more information",
                    "information about",
                    "a lot more information on")
                .negative("informations", "more informations about", "some more informations"),
            new Entry("\"Information\" *")
                .positive("Information", "Information about", "Information ABOUT")
                .negative("information", "information about", "Informations about"),
            new Entry("\"Programm*\"").positive("Programm*").negative("Programmer", "Programming"),
            new Entry("\\\"information\\\"")
                .positive("\"INFOrmation\"", "\"information\"")
                .negative("information", "\"information"));

    for (Entry e : entries) {
      DefaultDictionaryImpl filter = new DefaultDictionaryImpl();
      filter.glob.set(e.patterns.toArray(String[]::new));

      LabelFilter labelFilter = filter.compileLabelFilter();
      StopwordFilter swFilter = filter.compileStopwordFilter();

      for (String positiveExample : e.positive) {
        Assertions.assertThat(labelFilter.test(positiveExample))
            .as(e.patterns.toString() + " :: " + positiveExample)
            .isFalse();
        Assertions.assertThat(swFilter.test(positiveExample))
            .as(e.patterns.toString() + " :: " + positiveExample)
            .isFalse();
      }

      for (String negativeExample : e.negative) {
        Assertions.assertThat(labelFilter.test(negativeExample))
            .as(e.patterns.toString() + " :: " + negativeExample)
            .isTrue();
        Assertions.assertThat(swFilter.test(negativeExample))
            .as(e.patterns.toString() + " :: " + negativeExample)
            .isTrue();
      }
    }
  }
}
