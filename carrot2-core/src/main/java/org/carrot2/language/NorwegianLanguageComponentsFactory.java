package org.carrot2.language;

import org.carrot2.language.snowball.NorwegianStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class NorwegianLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Norwegian";

  public NorwegianLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(NorwegianLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new NorwegianStemmer()));
  }
}
