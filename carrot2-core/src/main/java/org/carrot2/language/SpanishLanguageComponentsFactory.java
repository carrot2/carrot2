package org.carrot2.language;

import org.carrot2.language.snowball.SpanishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class SpanishLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Spanish";

  public SpanishLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(SpanishLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new SpanishStemmer()));
  }
}
