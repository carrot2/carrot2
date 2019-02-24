package org.carrot2.language;

import org.carrot2.language.snowball.ItalianStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class ItalianLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Italian";

  public ItalianLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(ItalianLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new ItalianStemmer()));
  }
}
