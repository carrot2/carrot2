package org.carrot2.language;

import org.carrot2.language.snowball.SwedishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class SwedishLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Swedish";

  public SwedishLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(SwedishLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new SwedishStemmer()));
  }
}
