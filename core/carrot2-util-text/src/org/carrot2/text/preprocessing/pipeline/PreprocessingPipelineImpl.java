package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.Bindable;

/**
 * Implements {@link IPreprocessingPipeline} delegation to 
 * {@link CompletePreprocessingPipeline} or
 * {@link LucenePreprocessingPipeline} depending on the language.
 * 
 */
@Bindable
public class PreprocessingPipelineImpl implements IPreprocessingPipeline
{
    private IPreprocessingPipeline defaultPipeline = new CompletePreprocessingPipeline();
    private IPreprocessingPipeline lucenePipeline = new LucenePreprocessingPipeline();

    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query, LanguageCode language, ContextRequired contextRequired)
    {
        switch (language)
        {
            case JAPANESE:
                return lucenePipeline.preprocess(documents, query, language, contextRequired);
            default:
                return defaultPipeline.preprocess(documents, query, language, contextRequired);
        }
    }
}
