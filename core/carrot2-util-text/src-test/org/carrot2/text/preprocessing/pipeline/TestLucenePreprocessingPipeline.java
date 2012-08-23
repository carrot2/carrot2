package org.carrot2.text.preprocessing.pipeline;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.junit.Test;

public class TestLucenePreprocessingPipeline
{
    @Test
    public void testLucenePipeline()
    {
        IPreprocessingPipeline pipeline = 
            new LucenePreprocessingPipeline();
            // new CompletePreprocessingPipeline();
        PreprocessingContext ctx = pipeline.preprocess(
            SampleDocumentData.DOCUMENTS_DATA_MINING.subList(0, 3), 
            "data mining",
            LanguageCode.ENGLISH);

        System.out.println(ctx);
    }
}
