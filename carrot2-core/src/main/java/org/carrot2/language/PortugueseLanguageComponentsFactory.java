package org.carrot2.language;

import org.carrot2.language.snowball.PortugueseStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class PortugueseLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Portuguese";

  public PortugueseLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(PortugueseLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new PortugueseStemmer()));
  }
}
