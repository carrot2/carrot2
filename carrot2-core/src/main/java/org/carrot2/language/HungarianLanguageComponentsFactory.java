package org.carrot2.language;

import org.carrot2.language.snowball.HungarianStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;

import java.io.IOException;

/**
 *
 */
public class HungarianLanguageComponentsFactory extends AbstractLanguageComponentsFactory {
  public static final String NAME = "Hungarian";

  public HungarianLanguageComponentsFactory() throws IOException {
    super(new ClassRelativeResourceLoader(HungarianLanguageComponentsFactory.class),
        NAME,
        () -> new SnowballStemmerAdapter(new HungarianStemmer()));
  }
}
