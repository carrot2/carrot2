package org.carrot2.language;

import org.carrot2.language.snowball.GermanStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class GermanLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "German";

  public GermanLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(GermanLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new GermanStemmer()));
  }
}
