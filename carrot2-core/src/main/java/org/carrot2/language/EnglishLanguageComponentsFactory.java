package org.carrot2.language;

import org.carrot2.language.snowball.EnglishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class EnglishLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "English";

  public EnglishLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(EnglishLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new EnglishStemmer()));
  }
}
