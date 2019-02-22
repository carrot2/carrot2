package org.carrot2.language;

import com.carrotsearch.hppc.ObjectHashSet;
import org.carrot2.language.snowball.EnglishStemmer;
import org.carrot2.util.MutableCharArray;
import org.carrot2.util.ClassRelativeResourceLoader;
import org.carrot2.util.ResourceLookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 */
public class EnglishLanguageComponentsFactory implements LanguageComponentsFactory {
  public static final String NAME = "English";

  // Immutable and thread-safe once loaded, so reuse.
  private final LexicalDataImpl lexicalData;

  public EnglishLanguageComponentsFactory() throws IOException {
    this(new ClassRelativeResourceLoader(EnglishLanguageComponentsFactory.class));
  }

  public EnglishLanguageComponentsFactory(ResourceLookup resourceLoader) throws IOException {
    String stopwordsResource = "english.stopwords.utf8";
    String stoplabelsResource = "english.stoplabels.utf8";

    HashSet<String> stopwords = new HashSet<>();
    try (InputStream is = resourceLoader.open(stopwordsResource);
         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      readLines(reader)
        .forEach(word -> stopwords.add(word.toLowerCase(Locale.ROOT)));
    }

    List<Pattern> stoplabels;
    try (InputStream is = resourceLoader.open(stoplabelsResource);
         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      stoplabels = compile(readLines(reader));
    }

    lexicalData = new LexicalDataImpl(stopwords, union(stoplabels), true);
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public Stemmer createStemmer() {
    return new SnowballStemmerAdapter(new EnglishStemmer());
  }

  @Override
  public Tokenizer createTokenizer() {
    return new ExtendedWhitespaceTokenizer();
  }

  @Override
  public LexicalData createLexicalResources() {
    return lexicalData;
  }

  /**
   * Loads words from a given resource (UTF-8, one word per line, #-starting lines are
   * considered comments).
   */
  public static HashSet<String> readLines(BufferedReader reader) throws IOException {
    final HashSet<String> words = new HashSet<>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (!line.startsWith("#") && !line.isEmpty()) {
        words.add(line);
      }
    }
    return words;
  }

  private static List<Pattern> compile(Set<String> patterns) {
    ArrayList<Pattern> compiled = new ArrayList<>();
    for (String p : patterns) {
      compiled.add(Pattern.compile(p));
    }
    return compiled;
  }

  /**
   * Combines a number of patterns into a single pattern with a union
   * of all of them. With automata-based pattern engines, this should
   * be faster and memory-friendly.
   */
  private static Pattern union(List<Pattern> patterns) {
    final StringBuilder union = new StringBuilder();
    if (patterns.size() > 0) {
      union.append("(");
      for (int i = 0; i < patterns.size(); i++) {
        if (i > 0) union.append(")|(");
        union.append(patterns.get(i).toString());
      }
      union.append(")");
      return Pattern.compile(union.toString());
    } else {
      return null;
    }
  }
}
