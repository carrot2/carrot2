package org.carrot2.language;

import org.carrot2.language.snowball.DutchStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class DutchLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Dutch";

  public DutchLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(DutchLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new DutchStemmer()));
  }
}
