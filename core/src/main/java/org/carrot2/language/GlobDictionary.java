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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.util.StringUtils;

/**
 * This dictionary implementation is a middle ground between the complexity of regular expressions
 * and sheer speed of plain text matching. It offers case sensitive and case insensitive matching,
 * as well as globs (wildcards matching any token sequence).
 */
public class GlobDictionary implements Predicate<CharSequence> {
  private static final List<WordPattern> EMPTY = new ArrayList<>();
  private final Function<String, String> tokenNormalization = defaultTokenNormalization();

  final Map<String, List<WordPattern>> tokenToPatterns;

  public GlobDictionary(Stream<WordPattern> patterns) {
    /*
     * For each pattern, create an inverted index containing:
     * normalized(token) -> patternList
     * so that we can quickly compute the list of candidate patterns that
     * can (but may not) match a given input.
     *
     * This is similar in nature to this:
     * http://swtch.com/~rsc/regexp/regexp4.html
     */
    tokenToPatterns = compile(patterns, tokenNormalization);
  }

  @Override
  public boolean test(CharSequence input) {
    String[] inputTerms = splitTerms(input);

    // normalized inputTerms
    String[] normalizedTerms = normalize(inputTerms);

    List<WordPattern> matches = match(inputTerms, normalizedTerms, (p) -> true);
    return matches != null && !matches.isEmpty();
  }

  public List<WordPattern> match(
      String[] inputTerms, String[] normalizedTerms, Predicate<WordPattern> earlyAbort) {
    // Already-checked terms and patterns, combined.
    List<WordPattern> passing = null;
    for (String normalizedToken : normalizedTerms) {
      List<WordPattern> patterns = tokenToPatterns.get(normalizedToken);
      if (patterns != null) {
        for (WordPattern pattern : patterns) {
          if (pattern.matches(inputTerms, normalizedTerms)) {
            if (passing == null) {
              passing = new ArrayList<>(2);
            }
            passing.add(pattern);

            if (earlyAbort.test(pattern)) {
              return passing;
            }
          }
        }
      }
    }

    // Use homogeneous return type.
    assert EMPTY.isEmpty();
    return passing == null ? EMPTY : passing;
  }

  public String[] normalize(String[] tokens) {
    var normalized = new String[tokens.length];
    for (int i = 0; i < tokens.length; i++) {
      normalized[i] = tokenNormalization.apply(tokens[i]);
    }
    return normalized;
  }

  private static final Pattern SPACES = Pattern.compile("\\ +");

  private String[] splitTerms(CharSequence input) {
    return SPACES.split(input);
  }

  @Override
  public String toString() {
    return "GlobDictionary: " + this.tokenToPatterns;
  }

  private static HashMap<String, List<WordPattern>> compile(
      Stream<WordPattern> patterns, Function<String, String> tokenNormalization) {
    HashMap<String, String> cache = new HashMap<>();
    Function<String, String> normalize =
        (s) -> {
          String normalized = tokenNormalization.apply(s);
          return cache.computeIfAbsent(normalized, (x) -> normalized);
        };

    // Remove invalid inputs and normalize tokens.
    patterns =
        patterns.map(
            (pattern) -> {
              // Handle invalid inputs.
              checkInvalid(pattern);

              // Rewrite the pattern so that tokens with NORMALIZED matching have already
              // normalized image.
              pattern.tokens.replaceAll(
                  (t) -> {
                    if (t.matchType == GlobDictionary.MatchType.NORMALIZED) {
                      return new Token(normalize.apply(t.image), t.matchType);
                    } else {
                      return t;
                    }
                  });

              return pattern;
            });

    // Sort patterns on input for hash consistency.
    patterns = patterns.sorted();

    // Create a simple inverted index from tokens to the patterns they occur in.
    HashMap<String, List<WordPattern>> keyToPatterns = new HashMap<>();
    patterns.forEach(
        (pattern) -> {
          for (Token t : pattern.tokens) {
            if (t.matchType.hasTokenImage()) {
              String key = normalize.apply(t.image);
              List<WordPattern> patternList =
                  keyToPatterns.computeIfAbsent(key, k -> new ArrayList<>());
              patternList.add(pattern);
            }
          }
        });
    return keyToPatterns;
  }

  private static void checkInvalid(WordPattern pattern) {
    if (pattern.tokens().isEmpty()) {
      throw new IllegalArgumentException("Empty pattern is not valid.");
    }

    if (pattern.tokens().stream().noneMatch(t -> t.matchType.hasTokenImage())) {
      throw new IllegalArgumentException("A wildcard-only pattern is not valid.");
    }
  }

  public static final class WordPattern implements Comparable<WordPattern> {
    private static final EnumSet<GlobDictionary.MatchType> FIXED_POSITION =
        EnumSet.of(
            GlobDictionary.MatchType.ANY,
            GlobDictionary.MatchType.NORMALIZED,
            GlobDictionary.MatchType.VERBATIM);

    enum MatchType {
      FIXED,
      TRAILING,
      FULL;
    }

    private final int concreteTokens;
    private final List<Token> tokens;

    private final BiPredicate<String[], String[]> matchTest;
    public MatchType matchType;

    public WordPattern(List<Token> tokens) {
      if (tokens.isEmpty()) {
        throw new RuntimeException("Empty patterns not allowed.");
      }

      this.concreteTokens =
          (int) tokens.stream().filter(t -> FIXED_POSITION.contains(t.matchType)).count();
      this.tokens = tokens;
      this.matchTest = determineMatchTest(tokens);
    }

    /** Determine if a quick-check is available for the pattern's token sequence. */
    private BiPredicate<String[], String[]> determineMatchTest(List<Token> tokens) {
      int tokenCount = tokens.size();

      // Concrete token sequence or token sequence with single-position wildcards only.
      if (concreteTokens == tokenCount) {
        this.matchType = MatchType.FIXED;
        return this::matchFixedSequence;
      }

      // If there is a non-zero trailing sequence of concrete tokens, check from the right side.
      int rightConcrete = countRightFixedTokens(tokens);
      if (rightConcrete > 0) {
        this.matchType = MatchType.TRAILING;
        return (verbatim, normalized) -> {
          if (verbatim.length < concreteTokens) {
            return false;
          }
          int tokMax = verbatim.length;
          int tokIdx = tokMax - rightConcrete;
          int patMax = tokenCount;
          int patIdx = patMax - rightConcrete;
          return matchSubrange(verbatim, normalized, tokIdx, tokMax, patIdx, patMax)
              && matchCheckFull(verbatim, normalized);
        };
      }

      // The input must have at least as many tokens as concrete token count.
      this.matchType = MatchType.FULL;
      return this::matchCheckFull;
    }

    private int countRightFixedTokens(List<Token> tokens) {
      int count = 0;
      for (int i = tokens.size(); --i >= 0; ) {
        if (!FIXED_POSITION.contains(tokens.get(i).matchType)) {
          break;
        }
        count++;
      }
      return count;
    }

    public List<Token> tokens() {
      return tokens;
    }

    @Override
    public String toString() {
      return tokens.toString();
    }

    @Override
    public int hashCode() {
      return tokens.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return getClass().isInstance(obj) && compareTo((WordPattern) obj) == 0;
    }

    @Override
    public int compareTo(WordPattern other) {
      int v;
      List<Token> t1 = this.tokens;
      List<Token> t2 = other.tokens;
      for (int i = 0, max = Math.min(t1.size(), t2.size()); i < max; i++) {
        v = t1.get(i).compareTo(t2.get(i));
        if (v != 0) {
          return v;
        }
      }

      return Integer.compare(t1.size(), t2.size());
    }

    public boolean matches(String[] verbatimTerms, String[] normalizedTerms) {
      return matchTest.test(verbatimTerms, normalizedTerms);
    }

    private boolean matchFixedSequence(String[] verbatimTerms, String[] normalizedTerms) {
      if (tokens.size() != verbatimTerms.length) {
        return false;
      }

      for (int i = 0; i < verbatimTerms.length; i++) {
        if (!tokenMatches(tokens.get(i), verbatimTerms[i], normalizedTerms[i])) {
          return false;
        }
      }

      return true;
    }

    private boolean matchCheckFull(String[] verbatimTerms, String[] normalizedTerms) {
      if (verbatimTerms.length < concreteTokens) {
        return false;
      }

      List<Token> patternTokens = tokens();
      assert patternTokens.size() >= 1;
      assert verbatimTerms.length == normalizedTerms.length;
      assert verbatimTerms.length >= 1;

      int tIndex = 0;
      int pIndex = 0;
      final int tMax = verbatimTerms.length;
      final int pMax = patternTokens.size();
      return matchSubrange(verbatimTerms, normalizedTerms, tIndex, tMax, pIndex, pMax);
    }

    private boolean matchSubrange(
        String[] verbatim, String[] normalized, int tokIdx, int tokMax, int patIdx, int patMax) {
      List<Token> patternTokens = tokens();
      while (true) {
        // If the pattern ended and the tokens ended, we have a match.
        if (patIdx == patMax) {
          return tokIdx == tokMax;
        }

        Token pToken = patternTokens.get(patIdx);
        switch (pToken.matchType) {
          case NORMALIZED:
            if (tokIdx == tokMax || !pToken.image.equals(normalized[tokIdx])) {
              return false;
            }
            patIdx++;
            tokIdx++;
            break;

          case VERBATIM:
            if (tokIdx == tokMax || !pToken.image.equals(verbatim[tokIdx])) {
              return false;
            }
            patIdx++;
            tokIdx++;
            break;

          case ANY:
            if (tokIdx == tokMax) {
              return false;
            }
            patIdx++;
            tokIdx++;
            break;

          case WILDCARD:
            if (patIdx + 1 == patMax) {
              // This is a trailing wildcard. The input matched, regardless
              // of any remaining tokens.
              return true;
            }

            // Reluctant match: seek for the next non-wildcard pattern's token.
            Token nextToken = patternTokens.get(++patIdx);
            assert nextToken.matchType != GlobDictionary.MatchType.WILDCARD;
            while (tokIdx < tokMax
                && !tokenMatches(nextToken, verbatim[tokIdx], normalized[tokIdx])) {
              tokIdx++;
            }

            if (tokIdx == tokMax) {
              // We didn't find the next matching token.
              return false;
            }

            // We know there's a match on the next token after a wildcard, so skip it immediately.
            patIdx++;
            tokIdx++;
            break;

          default:
            throw new RuntimeException();
        }
      }
    }

    private boolean tokenMatches(Token token, String verbatim, String normalized) {
      switch (token.matchType) {
        case ANY:
          return true;
        case NORMALIZED:
          return token.image.equals(normalized);
        case VERBATIM:
          return token.image.equals(verbatim);
        default:
          throw new AssertionError("Unexpected token type: " + token);
      }
    }
  }

  public static enum MatchType {
    /** Wildcard match (zero or more tokens). */
    WILDCARD,
    /** Vermatim token image match. */
    VERBATIM,
    /** Normalized token image match. */
    NORMALIZED,
    /** Any single token. */
    ANY;

    public boolean hasTokenImage() {
      return this == VERBATIM || this == NORMALIZED;
    }
  }

  public static final class Token implements Comparable<Token> {
    final MatchType matchType;
    final String image;

    public Token(String image, MatchType matchType) {
      this.matchType = matchType;
      this.image = image;
    }

    public int compareTo(Token other) {
      int v = this.image.compareTo(other.image);
      if (v == 0) {
        v = this.matchType.compareTo(other.matchType);
      }
      return v;
    }

    public String image() {
      return image;
    }

    public MatchType matchType() {
      return matchType;
    }

    @Override
    public String toString() {
      switch (matchType()) {
        case NORMALIZED:
          return image();
        case VERBATIM:
          return "'" + image() + "'";
        case WILDCARD:
          assert image().equals("*");
          return "*";
        case ANY:
          assert image().equals("?");
          return "?";
      }
      throw new RuntimeException();
    }

    @Override
    public int hashCode() {
      return image.hashCode() + matchType.ordinal();
    }
  }

  public static class PatternParser {
    static final Token ZERO_OR_MORE = new Token("*", GlobDictionary.MatchType.WILDCARD);
    static final Token ANY = new Token("?", GlobDictionary.MatchType.ANY);

    public WordPattern parse(String pattern) throws ParseException {
      ArrayList<Token> tokens = new ArrayList<>();

      // A relatively simple state machine to avoid writing a full parser.
      for (int pos = 0, max = pattern.length(); ; ) {
        if (pos == max) {
          break;
        }

        final char chr;
        switch (chr = pattern.charAt(pos)) {
          case '?':
            tokens.add(ANY);
            pos = spaceOrEnd(pattern, pos + 1);
            break;

          case '+':
            // Syntactic sugar over "? *".
            tokens.add(ANY);
            tokens.add(ZERO_OR_MORE);
            pos = spaceOrEnd(pattern, pos + 1);
            break;

          case '*':
            if (tokens.isEmpty() || tokens.get(tokens.size() - 1) != ZERO_OR_MORE) {
              tokens.add(ZERO_OR_MORE);
            }
            pos = spaceOrEnd(pattern, pos + 1);
            break;

          case '\t':
          case ' ':
            // Ignorable whitespace.
            pos++;
            break;

          case '"':
          case '\'':
            pos = parseQuoted(pattern, chr, pos + 1, tokens);
            pos = spaceOrEnd(pattern, pos);
            break;

          default:
            pos = parseUnquoted(pattern, pos, tokens);
            break;
        }
      }

      // Handle the special odd cases.
      handleInvalid(tokens);

      return new WordPattern(tokens);
    }

    private void handleInvalid(ArrayList<Token> tokens) throws ParseException {
      if (tokens.size() == 0) {
        throw new ParseException("Empty patterns not allowed.", -1);
      }

      if (tokens.stream().noneMatch(t -> t.matchType.hasTokenImage())) {
        throw new ParseException("Wildcard-only patterns are invalid.", -1);
      }
    }

    private int spaceOrEnd(String pattern, int pos) throws ParseException {
      if (pattern.length() == pos) {
        return pos;
      }

      switch (pattern.charAt(pos)) {
        case ' ':
        case '\t':
          return pos + 1;

        default:
          throw new ParseException("Expected a whitespace or end of pattern.", pos);
      }
    }

    private int parseUnquoted(String pattern, int pos, ArrayList<Token> tokens)
        throws ParseException {
      StringBuilder sb = new StringBuilder();
      int max = pattern.length();
      outer:
      for (; pos < max; pos++) {
        switch (pattern.charAt(pos)) {
          case '\t':
          case ' ':
            break outer;

          case '"':
            throw new ParseException("Unescaped quote inside.", pos);

          case '*':
            throw new ParseException("Wildcard is a special character. Quote if necessary.", pos);

          case '\\':
            pos++;
            if (pos == max) {
              throw new ParseException("Terminating escape quote character.", pos - 1);
            }
            sb.append(pattern.charAt(pos));
            break;

          default:
            sb.append(pattern.charAt(pos));
            break;
        }
      }

      tokens.add(new Token(sb.toString(), GlobDictionary.MatchType.NORMALIZED));
      return pos;
    }

    private int parseQuoted(String pattern, char openingQuote, int pos, ArrayList<Token> tokens)
        throws ParseException {
      StringBuilder sb = new StringBuilder();
      int max = pattern.length();
      int quoteStart = pos;

      for (; pos < max; pos++) {
        final char chr = pattern.charAt(pos);
        switch (chr) {
          case '"':
          case '\'':
            // Skip the quote if it's consistent with the opening quote.
            if (openingQuote == chr) {
              pos++;
              tokens.add(new Token(sb.toString(), GlobDictionary.MatchType.VERBATIM));
              return pos;
            } else {
              sb.append(pattern.charAt(pos));
            }
            break;

          case '\\':
            pos++;
            if (pos == max) {
              throw new ParseException("Terminating escape quote character.", pos - 1);
            }
            sb.append(pattern.charAt(pos));
            break;

          default:
            sb.append(pattern.charAt(pos));
            break;
        }
      }

      throw new ParseException("Pattern ended (unbalanced quote).", quoteStart - 1);
    }
  }

  static Function<String, String> defaultTokenNormalization() {
    return (s) -> s.toLowerCase(Locale.ROOT);
  }

  public static GlobDictionary compilePatterns(Stream<String> entries) {
    GlobDictionary.PatternParser parser = new GlobDictionary.PatternParser();
    ArrayList<String> errors = new ArrayList<>();
    AtomicInteger warningsEmitted = new AtomicInteger();
    Stream<WordPattern> compiled =
        entries
            .filter(pattern -> !StringUtils.isNullOrEmpty(pattern.trim()))
            .map(
                (pattern) -> {
                  try {
                    return parser.parse(pattern);
                  } catch (ParseException e) {
                    if (warningsEmitted.get() < 10) {
                      StringBuilder positionMark = new StringBuilder();
                      int errorOffset = e.getErrorOffset();
                      if (errorOffset >= 0) {
                        positionMark.append(pattern);
                        positionMark.setLength(errorOffset);
                        positionMark.insert(errorOffset, "<here>");
                        positionMark.insert(0, ", at: ");
                      }

                      errors.add(
                          "Could not parse pattern: "
                              + pattern
                              + positionMark
                              + ", reason: "
                              + e.getMessage()
                              + (warningsEmitted.incrementAndGet() == 10
                                  ? " (any following warnings suppressed)"
                                  : ""));
                    }
                    return null;
                  }
                })
            .filter(Objects::nonNull);

    if (!errors.isEmpty()) {
      throw new RuntimeException(
          "Dictionary compilation errors occurred:\n"
              + errors.stream().map(e -> "  - " + e + ",\n").collect(Collectors.joining()));
    }

    return new GlobDictionary(compiled);
  }
}
