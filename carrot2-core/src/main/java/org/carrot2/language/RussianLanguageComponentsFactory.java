package org.carrot2.language;

import org.carrot2.language.snowball.RussianStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class RussianLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Russian";

  public RussianLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(RussianLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new RussianStemmer()));
  }
}
