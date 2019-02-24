package org.carrot2.language;

import org.carrot2.language.snowball.TurkishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class TurkishLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Turkish";

  public TurkishLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(TurkishLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new TurkishStemmer()));
  }
}
