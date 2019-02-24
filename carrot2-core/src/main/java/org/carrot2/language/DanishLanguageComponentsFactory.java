package org.carrot2.language;

import org.carrot2.language.snowball.DanishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class DanishLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Danish";

  public DanishLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(DanishLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new DanishStemmer()));
  }
}
