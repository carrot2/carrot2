package org.carrot2.clustering.kmeans;

import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.linguistic.LanguageModels;
import org.carrot2.text.util.MutableCharArray;

public class TestLanguageModel {
  public static LanguageModel createNew() {
    IStemmer stemmer = (word) -> word;
    ITokenizer tokenizer = LanguageModels.english().tokenizer;
    ILexicalData lexicalData = new ILexicalData() {
      @Override
      public boolean isCommonWord(MutableCharArray word) {
        return false;
      }

      @Override
      public boolean isStopLabel(CharSequence formattedLabel) {
        return false;
      }

      @Override
      public boolean usesSpaceDelimiters() {
        return true;
      }
    };
    return new LanguageModel(stemmer, tokenizer, lexicalData);
  }
}
