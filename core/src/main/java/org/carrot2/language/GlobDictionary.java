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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.util.StringUtils;

/**
 * This dictionary implementation is a middle ground between the complexity of regular expressions
 * and sheer speed of plain text matching. It offers case sensitive and case insensitive matching,
 * as well as globs (wildcards matching any token sequence).
 *
 * <p>The following wildcards are available:
 *
 * <ul>
 *   <li>{@code *} - matches zero or more tokens (possessive match),
 *   <li>{@code *?} - matches zero or more tokens (reluctant match),
 *   <li>{@code +} - matches one or more tokens (possessive match),
 *   <li>{@code +?} - matches zero or more tokens (reluctant match),
 *   <li>{@code ?} - matches exactly one token (possessive).
 * </ul>
 *
 * <p>In addition, a token <em>type</em> matching is provide in the form of:
 *
 * <ul>
 *   <li>{@code {name}} - matches a token with flags named {@code name}.
 * </ul>
 *
 * <p>Token flags are an int bitfield.
 */
public class GlobDictionary implements Predicate<CharSequence> {
  private final Function<String, String> tokenNormalization;
  private final Function<CharSequence, String[]> termSplitter;

  private Map<String, List<WordPattern>> tokenToPatterns;
  private Map<Integer, List<WordPattern>> pureTypePatterns;

  public GlobDictionary(
      Stream<WordPattern> patterns,
      Function<String, String> tokenNormalization,
      Function<CharSequence, String[]> termSplitter) {
    this.tokenNormalization = tokenNormalization;
    this.termSplitter = termSplitter;
    compile(patterns, tokenNormalization);
  }

  public GlobDictionary(Stream<WordPattern> patterns) {
    this(patterns, defaultTokenNormalization(), defaultTermSplitter());
  }

  public static Function<CharSequence, String[]> defaultTermSplitter() {
    return chs -> {
      var seq = chs.toString();
      List<String> tokens = new ArrayList<>();

      for (int p = 0, max = seq.length(); p < max; ) {
        while (p < max && seq.charAt(p) == ' ') p++;
        int s = p;
        while (p < max && seq.charAt(p) != ' ') p++;
        if (s < p) {
          tokens.add(seq.substring(s, p));
        }
      }

      return tokens.toArray(String[]::new);
    };
  }

  @Override
  public boolean test(CharSequence input) {
    String[] inputTerms = split(input);

    // normalized inputTerms
    String[] normalizedTerms = normalize(inputTerms);

    return find(inputTerms, normalizedTerms, null, (p) -> true);
  }

  /**
   * Find all matching patterns, optionally aborting prematurely.
   *
   * @param inputTerms Input terms (verbatim).
   * @param normalizedTerms Normalized terms (must use the same normalizer as the dictionary).
   * @param types Token types (bitfield) used in {@link MatchType#ANY_OF_TYPE}.
   * @param earlyAbort A predicate that indicates early abort condition.
   * @return Returns {@code true} if at least one match was found, {@code false} otherwise.
   */
  public boolean find(
      String[] inputTerms,
      String[] normalizedTerms,
      int[] types,
      Predicate<WordPattern> earlyAbort) {
    // Already-checked terms and patterns, combined.
    boolean found = false;
    outer:
    for (String normalizedToken : normalizedTerms) {
      var patterns = tokenToPatterns.get(normalizedToken);
      if (patterns != null) {
        for (WordPattern pattern : patterns) {
          if (pattern.matches(inputTerms, normalizedTerms, types)) {
            found = true;
            if (earlyAbort.test(pattern)) {
              return found;
            }
          }
        }
      }
    }

    if (!pureTypePatterns.isEmpty() && types != null) {
      int allTypeBits = 0;
      for (int type : types) {
        allTypeBits |= type;
      }
      for (var e : pureTypePatterns.entrySet()) {
        int bitField = e.getKey();
        if ((bitField & allTypeBits) == bitField) {
          for (WordPattern pattern : e.getValue()) {
            if (pattern.matches(inputTerms, normalizedTerms, types)) {
              found = true;
              if (earlyAbort.test(pattern)) {
                return found;
              }
            }
          }
        }
      }
    }

    return found;
  }

  public String[] split(CharSequence input) {
    return termSplitter.apply(input);
  }

  public String[] normalize(String[] tokens) {
    var normalized = new String[tokens.length];
    for (int i = 0; i < tokens.length; i++) {
      normalized[i] = tokenNormalization.apply(tokens[i]);
    }
    return normalized;
  }

  @Override
  public String toString() {
    return "GlobDictionary: " + this.tokenToPatterns;
  }

  /**
   * For each pattern, create an inverted index containing: {@code normalized(token) -> patternList}
   * so that we can quickly compute the list of candidate patterns that can (but may not) match a
   * given input.
   *
   * <p>This is similar in nature to this: http://swtch.com/~rsc/regexp/regexp4.html
   */
  private void compile(Stream<WordPattern> patterns, Function<String, String> tokenNormalization) {
    HashMap<String, String> cache = new HashMap<>();
    Function<String, String> normalize =
        (s) -> {
          String normalized = tokenNormalization.apply(s);
          return cache.computeIfAbsent(normalized, (x) -> normalized);
        };

    // Fail on invalid inputs.
    patterns = patterns.peek(GlobDictionary::checkInvalid);

    // Rewrite patterns so that tokens with NORMALIZED matching have a prenormalized image.
    patterns =
        patterns.map(
            (pattern) -> {
              List<Token> modifiedTokens = new ArrayList<>(pattern.tokens.size());
              boolean hadChanges = false;
              for (var t : pattern.tokens()) {
                if (t.matchType == GlobDictionary.MatchType.NORMALIZED) {
                  hadChanges = true;
                  modifiedTokens.add(new Token(normalize.apply(t.image), t.matchType, t.typeBits));
                } else {
                  modifiedTokens.add(t);
                }
              }

              return hadChanges ? new WordPattern(modifiedTokens, pattern.payload) : pattern;
            });

    // Sort patterns on input for hash consistency.
    patterns = patterns.sorted();

    // Create a simple inverted index from tokens to the patterns they occur in.
    HashMap<String, List<WordPattern>> tokenToPatterns = new HashMap<>();
    HashMap<Integer, List<WordPattern>> pureTypePatterns = new HashMap<>();
    patterns.forEach(
        (pattern) -> {
          Set<Object> useKey = new HashSet<>();
          boolean indexed = false;
          for (Token t : pattern.tokens) {
            if (t.matchType.isIndexable() && t.matchType != MatchType.ANY_OF_TYPE) {
              indexed = true;
              // Normalized tokens already have normalized images.
              String key =
                  (t.matchType == MatchType.NORMALIZED ? t.image : normalize.apply(t.image));
              if (useKey.add(key)) {
                tokenToPatterns.computeIfAbsent(key, k -> new ArrayList<>()).add(pattern);
              }
            }
          }

          if (!indexed) {
            // A pure type pattern.
            for (Token t : pattern.tokens) {
              if (t.matchType == MatchType.ANY_OF_TYPE) {
                var key = t.typeBits;
                if (useKey.add(key)) {
                  pureTypePatterns.computeIfAbsent(key, (k) -> new ArrayList<>()).add(pattern);
                }
              }
            }
          }
        });

    assert noDuplicateRules(tokenToPatterns.values());
    assert noDuplicateRules(pureTypePatterns.values());

    this.tokenToPatterns = tokenToPatterns;
    this.pureTypePatterns = pureTypePatterns;
  }

  private boolean noDuplicateRules(Collection<List<WordPattern>> values) {
    values.forEach(
        v -> {
          var unique = Collections.newSetFromMap(new IdentityHashMap<>());
          unique.addAll(values);
          if (unique.size() != values.size()) {
            throw new AssertionError("Duplicate rules detected.");
          }
        });
    return true;
  }

  private static void checkInvalid(WordPattern pattern) {
    if (pattern.tokens().isEmpty()) {
      throw new IllegalArgumentException("Empty pattern is not valid.");
    }

    if (pattern.tokens().stream().noneMatch(t -> t.matchType.isIndexable())) {
      throw new IllegalArgumentException("A wildcard-only pattern is not valid: " + pattern);
    }
  }

  public static final class WordPattern implements Comparable<WordPattern> {
    private static final EnumSet<GlobDictionary.MatchType> FIXED_POSITION =
        EnumSet.of(
            GlobDictionary.MatchType.ANY_OF_TYPE,
            GlobDictionary.MatchType.ANY,
            GlobDictionary.MatchType.NORMALIZED,
            GlobDictionary.MatchType.VERBATIM);

    /** Any payload associated with the pattern. */
    private final Object payload;

    private final int concreteTokens;
    private final List<Token> tokens;

    @SuppressWarnings("unchecked")
    public <T> T getPayload() {
      return (T) payload;
    }

    private interface MatchPredicate {
      boolean matches(String[] tokens, String[] normalized, int[] types);
    }

    private final MatchPredicate matchTest;

    public WordPattern(List<Token> tokens) {
      this(tokens, null);
    }

    public WordPattern(List<Token> tokens, Object payload) {
      if (tokens.isEmpty()) {
        throw new RuntimeException("Empty patterns not allowed.");
      }

      this.payload = payload;

      this.concreteTokens =
          (int) tokens.stream().filter(t -> FIXED_POSITION.contains(t.matchType)).count();

      // If we're running with assertions (tests), make tokens immutable.
      if (getClass().desiredAssertionStatus()) {
        this.tokens = Collections.unmodifiableList(tokens);
      } else {
        this.tokens = tokens;
      }

      this.matchTest = determineMatchTest(tokens);
    }

    /** Determine if a quick-check is available for the pattern's token sequence. */
    private MatchPredicate determineMatchTest(List<Token> tokens) {
      int tokenCount = tokens.size();

      // Concrete token sequence or token sequence with single-position wildcards only.
      if (concreteTokens == tokenCount) {
        return this::matchFixedSequence;
      }

      // If there is a non-zero trailing sequence of concrete tokens, check from the right side.
      int rightConcrete = countRightFixedTokens(tokens);
      if (rightConcrete > 0) {
        return (verbatim, normalized, types) -> {
          if (verbatim.length < concreteTokens) {
            return false;
          }
          int tokMax = verbatim.length;
          int tokIdx = tokMax - rightConcrete;
          int patMax = tokenCount;
          int patIdx = patMax - rightConcrete;
          return matchSubrange(verbatim, normalized, types, tokIdx, tokMax, patIdx, patMax)
              && matchCheckFull(verbatim, normalized, types);
        };
      }

      // The input must have at least as many tokens as concrete token count.
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

    public boolean matches(String[] verbatimTerms, String[] normalizedTerms, int[] types) {
      return matchTest.matches(verbatimTerms, normalizedTerms, types);
    }

    private boolean matchFixedSequence(
        String[] verbatimTerms, String[] normalizedTerms, int[] type) {
      if (tokens.size() != verbatimTerms.length) {
        return false;
      }

      for (int i = 0; i < verbatimTerms.length; i++) {
        if (!tokenMatches(
            tokens.get(i), verbatimTerms[i], normalizedTerms[i], type == null ? 0 : type[i])) {
          return false;
        }
      }

      return true;
    }

    private boolean matchCheckFull(String[] verbatimTerms, String[] normalizedTerms, int[] types) {
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
      return matchSubrange(verbatimTerms, normalizedTerms, types, tIndex, tMax, pIndex, pMax);
    }

    private boolean matchSubrange(
        String[] verbatim,
        String[] normalized,
        int[] types,
        int tokIdx,
        int tokMax,
        int patIdx,
        int patMax) {
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

          case ANY_OF_TYPE:
            if (tokIdx == tokMax || !pToken.hasType(types == null ? 0 : types[tokIdx])) {
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

          case ZERO_OR_MORE_RELUCTANT:
          case ZERO_OR_MORE_POSSESSIVE:
            if (patIdx + 1 == patMax) {
              // This is a trailing wildcard. The input matched, regardless
              // of any remaining tokens.
              return true;
            }

            // Get the next non-wildcard token from the pattern.
            Token nextToken = patternTokens.get(++patIdx);
            assert nextToken.matchType != MatchType.ZERO_OR_MORE_RELUCTANT
                && nextToken.matchType != MatchType.ZERO_OR_MORE_POSSESSIVE;

            boolean reluctant = pToken.matchType == MatchType.ZERO_OR_MORE_RELUCTANT;
            if (reluctant) {
              // Reluctant match: seek for the next non-wildcard pattern's token.
              while (tokIdx < tokMax
                  && !tokenMatches(
                      nextToken,
                      verbatim[tokIdx],
                      normalized[tokIdx],
                      types == null ? 0 : types[tokIdx])) {
                tokIdx++;
              }
            } else {
              // Possessive match: seek for the last non-wildcard pattern's token.
              int min = tokIdx;
              tokIdx = tokMax;
              for (int i = tokMax - 1; i >= min; i--) {
                if (tokenMatches(
                    nextToken, verbatim[i], normalized[i], types == null ? 0 : types[i])) {
                  tokIdx = i;
                  break;
                }
              }
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

    private boolean tokenMatches(Token token, String verbatim, String normalized, int type) {
      switch (token.matchType) {
        case ANY:
          return true;
        case ANY_OF_TYPE:
          return token.hasType(type);
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
    /** Wildcard match (zero or more tokens, possessive variant). */
    ZERO_OR_MORE_POSSESSIVE,
    /** Wildcard match (zero or more tokens, reluctant variant). */
    ZERO_OR_MORE_RELUCTANT,
    /** Any single token (possessive). */
    ANY,
    /** Any single token matching a type bitfield. */
    ANY_OF_TYPE,
    /** Vermatim token image match. */
    VERBATIM,
    /** Normalized token image match. */
    NORMALIZED;

    boolean isIndexable() {
      return this == VERBATIM || this == NORMALIZED || this == ANY_OF_TYPE;
    }
  }

  public static final class Token implements Comparable<Token> {
    final MatchType matchType;
    final String image;
    final int typeBits;

    public Token(String image, MatchType matchType, int typeBits) {
      this.matchType = matchType;
      this.image = image;
      this.typeBits = typeBits;
    }

    public int compareTo(Token other) {
      int v = this.image.compareTo(other.image);
      if (v == 0) {
        v = this.matchType.compareTo(other.matchType);
      }
      if (v == 0) {
        v = Integer.compare(this.typeBits, other.typeBits);
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
        case ANY_OF_TYPE:
          return image();
        case VERBATIM:
          return "'" + image() + "'";
        case ZERO_OR_MORE_POSSESSIVE:
          assert image().equals("*");
          return "*";
        case ZERO_OR_MORE_RELUCTANT:
          assert image().equals("*?");
          return "*";
        case ANY:
          assert image().equals("?");
          return "?";
      }
      throw new RuntimeException();
    }

    @Override
    public int hashCode() {
      return image.hashCode() + 31 * matchType.ordinal() + 31 * typeBits;
    }

    @Override
    public boolean equals(Object obj) {
      return getClass().isInstance(obj) && compareTo((Token) obj) == 0;
    }

    public boolean hasType(int tokenType) {
      return (tokenType & typeBits) == typeBits;
    }
  }

  public static class PatternParser {
    static final Token ZERO_OR_MORE_POSSESSIVE =
        new Token("*", MatchType.ZERO_OR_MORE_POSSESSIVE, 0);
    static final Token ZERO_OR_MORE_RELUCTANT =
        new Token("*?", MatchType.ZERO_OR_MORE_RELUCTANT, 0);
    static final Token ANY = new Token("?", GlobDictionary.MatchType.ANY, 0);

    private final Map<String, Integer> typeMap;

    public PatternParser() {
      this(Collections.emptyMap());
    }

    /**
     * Creates a pattern parser recognizing specific types.
     *
     * @param typeMap A type name to bitfield type representation.
     */
    public PatternParser(Map<String, Integer> typeMap) {
      this.typeMap = typeMap;
    }

    public WordPattern parse(String pattern) throws ParseException {
      return parse(pattern, null);
    }

    public WordPattern parse(String pattern, Object payload) throws ParseException {
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
            if (isReluctant(pattern, pos + 1)) {
              pos++; // eat the '?'
              noConsecutiveWildcards(pattern, pos, tokens);
              // Syntactic sugar over "? *?".
              tokens.add(ANY);
              tokens.add(ZERO_OR_MORE_RELUCTANT);
            } else {
              noConsecutiveWildcards(pattern, pos, tokens);
              // Syntactic sugar over "? *".
              tokens.add(ANY);
              tokens.add(ZERO_OR_MORE_POSSESSIVE);
            }
            pos = spaceOrEnd(pattern, pos + 1);
            break;

          case '*':
            if (isReluctant(pattern, pos + 1)) {
              pos++; // eat the '?'
              noConsecutiveWildcards(pattern, pos, tokens);
              tokens.add(ZERO_OR_MORE_RELUCTANT);
              pos = spaceOrEnd(pattern, pos + 1);
            } else {
              noConsecutiveWildcards(pattern, pos, tokens);
              tokens.add(ZERO_OR_MORE_POSSESSIVE);
              pos = spaceOrEnd(pattern, pos + 1);
            }
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

      return new WordPattern(tokens, payload);
    }

    private boolean isReluctant(String pattern, int pos) {
      return pos < pattern.length() && pattern.charAt(pos) == '?';
    }

    private void noConsecutiveWildcards(String pattern, int pos, ArrayList<Token> tokens)
        throws ParseException {
      if (tokens.size() > 0) {
        switch (tokens.get(tokens.size() - 1).matchType) {
          case ZERO_OR_MORE_POSSESSIVE:
          case ZERO_OR_MORE_RELUCTANT:
            throw new ParseException("Consecutive wildcards not supported: " + pattern, pos);
        }
      }
    }

    private void handleInvalid(ArrayList<Token> tokens) throws ParseException {
      if (tokens.size() == 0) {
        throw new ParseException("Empty patterns not allowed.", -1);
      }

      if (tokens.stream().noneMatch(t -> t.matchType.isIndexable())) {
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

      var image = sb.toString();
      if (!typeMap.isEmpty() && image.startsWith("{") && image.endsWith("}")) {
        int typeBits = 0;
        for (String type : image.substring(1, image.length() - 1).split("\\s*&\\s*")) {
          if (!typeMap.containsKey(type)) {
            throw new ParseException(
                "Type name not recognized in pattern '"
                    + pattern
                    + "': "
                    + type
                    + ", expected one of: "
                    + new TreeSet<>(typeMap.keySet()),
                pos);
          }

          typeBits |= typeMap.get(type);
        }

        tokens.add(new Token(image, MatchType.ANY_OF_TYPE, typeBits));
      } else {
        tokens.add(new Token(image, GlobDictionary.MatchType.NORMALIZED, 0));
      }

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
              tokens.add(new Token(sb.toString(), GlobDictionary.MatchType.VERBATIM, 0));
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

  public static Function<String, String> defaultTokenNormalization() {
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
