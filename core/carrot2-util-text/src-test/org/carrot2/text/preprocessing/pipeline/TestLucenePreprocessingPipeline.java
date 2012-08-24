package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.test.SampleDocumentData;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TestLucenePreprocessingPipeline
{
    int guard;

    @Test
    public void testTokyo() throws Exception
    {
        List<Document> input = ProcessingResult.deserialize(
            getClass().getResourceAsStream("tokyo.ja.xml")).getDocuments();

        IPreprocessingPipeline p = new LucenePreprocessingPipeline();
        PreprocessingContext ctx = p.preprocess(
            input.subList(0, 3), "東京", LanguageCode.JAPANESE);

        System.out.println(ctx);
    }

    @Test @Ignore
    public void testVerbose()
    {
        List<Document> input = SampleDocumentData.DOCUMENTS_DATA_MINING;

        IPreprocessingPipeline p = new LucenePreprocessingPipeline();
        PreprocessingContext ctx = p.preprocess(
            input, "data mining", LanguageCode.ENGLISH);
        
        System.out.println(ctx);
    }

    @Test @Ignore
    public void testSpeed()
    {
        List<Document> all = Lists.newArrayList();
        for (List<Document> dl : SampleDocumentData.ALL) {
            all.addAll(dl);
        }
        
        for (int i = 0; i < 20; i++)
        {
            for (IPreprocessingPipeline p : new IPreprocessingPipeline [] {
                new LucenePreprocessingPipeline(),
                new BasicPreprocessingPipeline(),
                new CompletePreprocessingPipeline()
            }) 
            {
                long start = System.currentTimeMillis();
                PreprocessingContext ctx = p.preprocess(all, "data mining", LanguageCode.ENGLISH);
                guard += ctx.allTokens.image.length;
                long end = System.currentTimeMillis();

                System.out.println(String.format("%8.3f %s",
                    (end - start) / 1000.0,
                    p.getClass().getSimpleName()));
            }
        }
    }
}
