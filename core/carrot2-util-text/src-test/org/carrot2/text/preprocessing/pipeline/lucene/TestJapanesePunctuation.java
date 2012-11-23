package org.carrot2.text.preprocessing.pipeline.lucene;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.analysis.ITokenizer.TokenType;
import org.carrot2.text.analysis.TokenTypeUtils;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline.ContextRequired;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

public class TestJapanesePunctuation extends CarrotTestCase
{
    int guard;

    @Test
    public void testTokyo() throws Exception
    {
        List<Document> input = ProcessingResult.deserialize(
            getClass().getResourceAsStream("punctuation.ja.xml")).getDocuments();

        IPreprocessingPipeline p = new LucenePreprocessingPipeline();
        PreprocessingContext ctx = p.preprocess(
            input, "", LanguageCode.JAPANESE, ContextRequired.COMPLETE);

        // Ensure the only terms are crocodiles and the remaining ones are punctuation.
        AllTokens allTokens = ctx.allTokens;
        for (int i = 0; i < allTokens.size(); i++)
        {
            final short tokenType = allTokens.type[i];
            if (!TokenTypeUtils.isTerminator(tokenType)) {
                switch (TokenTypeUtils.maskType(tokenType)) {
                    case ITokenizer.TT_PUNCTUATION:
                        break;
                    case ITokenizer.TT_TERM:
                        assertEquals("ワニ", new String(allTokens.image[i]));
                        break;
                    default:
                        fail("Unexpected token at: " + i + " " + tokenType + ", " + TokenType.toString(tokenType));
                }
            }
        }
    }
}
