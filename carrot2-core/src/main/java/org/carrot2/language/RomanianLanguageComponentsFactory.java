package org.carrot2.language;

import org.carrot2.language.snowball.RomanianStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class RomanianLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Romanian";

  public RomanianLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(RomanianLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new RomanianStemmer()));
  }
}
