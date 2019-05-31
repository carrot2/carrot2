package org.carrot2.language.extras;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.icu.tokenattributes.ScriptAttribute;
import org.apache.lucene.analysis.ko.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ko.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.carrot2.util.TabularOutput;
import org.carrot2.util.TabularOutput.Builder;

/** Various utilities related to Lucene {@link TokenStream}. */
final class TokenStreams {
  private TokenStreams() {}

  private static Function<Attribute, String> unknown = (attr) -> "???";
  private static Map<Class<? extends Attribute>, Function<Attribute, String>> formatters =
      new HashMap<>();

  static {
    formatters.put(
        KeywordAttribute.class, (attr) -> Boolean.toString(((KeywordAttribute) attr).isKeyword()));
    formatters.put(TypeAttribute.class, (attr) -> ((TypeAttribute) attr).type());
    formatters.put(
        TermFrequencyAttribute.class,
        (attr) -> "" + ((TermFrequencyAttribute) attr).getTermFrequency());
    formatters.put(KeywordAttribute.class, (attr) -> "" + ((KeywordAttribute) attr).isKeyword());
    formatters.put(
        PartOfSpeechAttribute.class,
        (attr) -> {
          PartOfSpeechAttribute pos = (PartOfSpeechAttribute) attr;
          return pos.getLeftPOS() + ":" + pos.getPOSType() + ":" + pos.getRightPOS();
        });

    formatters.put(ReadingAttribute.class, (attr) -> ((ReadingAttribute) attr).getReading());

    formatters.put(ScriptAttribute.class, (attr) -> ((ScriptAttribute) attr).getName());
  }

  private static Set<Class<? extends Attribute>> skip =
      new HashSet<>(
          Arrays.asList(
              TermToBytesRefAttribute.class,
              CharTermAttribute.class,
              OffsetAttribute.class,
              PositionIncrementAttribute.class,
              PositionLengthAttribute.class));

  public static <T extends Writer> T dumpAttributes(
      String input, String fieldName, Analyzer analyzer, T out) throws IOException {

    TokenStream ts = analyzer.tokenStream("", input);

    CharTermAttribute charTermAttr = ts.getAttribute(CharTermAttribute.class);
    OffsetAttribute offsetAttr = ts.getAttribute(OffsetAttribute.class);
    PositionIncrementAttribute posAttr = ts.getAttribute(PositionIncrementAttribute.class);
    PositionLengthAttribute posLenAttr = ts.getAttribute(PositionLengthAttribute.class);

    List<Class<? extends Attribute>> attrClasses = new ArrayList<>();
    Iterator<Class<? extends Attribute>> it = ts.getAttributeClassesIterator();
    while (it.hasNext()) {
      Class<? extends Attribute> clz = it.next();
      if (!skip.contains(clz)) {
        attrClasses.add(clz);
      }
    }

    Builder builder = TabularOutput.to(out).noAutoFlush();

    builder
        .addColumn("[+pos]", c -> c.alignRight())
        .addColumn("[pos]", c -> c.alignRight())
        .addColumn("[len]", c -> c.alignRight())
        .addColumn("[term]", c -> c.alignLeft());

    List<Supplier<String>> attrs = new ArrayList<>();
    attrClasses.forEach(
        clz -> {
          builder.addColumn(clz.getSimpleName(), c -> c.alignRight());
          Function<Attribute, String> fn = formatters.getOrDefault(clz, unknown);
          Attribute attr = ts.getAttribute(clz);
          attrs.add(() -> fn.apply(attr));
        });

    builder.addColumn("[source]", c -> c.alignLeft());

    TabularOutput t = builder.build();

    ts.reset();
    int position = 0;
    while (ts.incrementToken()) {
      int increment = posAttr == null ? 1 : posAttr.getPositionIncrement();
      String posLength = posLenAttr == null ? "-" : "" + posLenAttr.getPositionLength();
      position += increment;
      t.append(
          increment,
          position,
          posLength,
          charTermAttr.toString());
      attrs.forEach(fn -> t.append(fn.get()));
      t.append(markFragment(input, offsetAttr.startOffset(), offsetAttr.endOffset(), 10));
      t.nextRow();
    }
    ts.end();
    ts.close();

    t.flush();

    return out;
  }

  public static String markFragment(String value, int startOffset, int endOffset, int charWindow)
      throws IOException {
    // this may be costly; should be extract the context snippet statically as a payload?
    int beginIndex = Math.max(0, startOffset - charWindow);
    int endIndex = Math.min(value.length(), endOffset + charWindow);
    return (beginIndex == 0 ? "" : "...")
        + sanitize(value.subSequence(beginIndex, startOffset))
        + ">"
        + sanitize(value.subSequence(startOffset, endOffset))
        + "<"
        + sanitize(value.subSequence(endOffset, endIndex))
        + (endIndex == value.length() ? "" : "...");
  }

  private static CharSequence sanitize(CharSequence seq) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < seq.length(); i++) {
      char chr = seq.charAt(i);
      if (Character.isLetterOrDigit(chr) || (chr >= ' ' && chr <= '\u007e')) {
        b.append(chr);
      } else if (Character.isWhitespace(chr)) {
        b.append(" ");
      } else {
        b.append(".");
      }
    }
    return b.toString();
  }

}
