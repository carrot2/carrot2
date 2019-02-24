package org.carrot2.language;

import org.carrot2.language.snowball.FrenchStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class FrenchLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "French";

  public FrenchLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(FrenchLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new FrenchStemmer()));
  }
}
