package org.carrot2.language;

import org.carrot2.language.snowball.FinnishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class FinnishLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Finnish";

  public FinnishLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(FinnishLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new FinnishStemmer()));
  }
}
