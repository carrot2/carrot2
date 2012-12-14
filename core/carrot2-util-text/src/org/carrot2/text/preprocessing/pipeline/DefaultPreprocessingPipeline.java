package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.builtin.BuiltinPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.lucene.LucenePreprocessingPipeline;
import org.carrot2.util.attribute.Bindable;

/**
 * Implements {@link IPreprocessingPipeline} delegation to 
 * {@link BuiltinPreprocessingPipeline} or
 * {@link LucenePreprocessingPipeline} depending on the language.
 */
@Bindable
public class DefaultPreprocessingPipeline implements IPreprocessingPipeline
{
    private IPreprocessingPipeline builtinPipeline = new BuiltinPreprocessingPipeline();
    private IPreprocessingPipeline lucenePipeline = new LucenePreprocessingPipeline();

    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query, LanguageCode language, ContextRequired contextRequired)
    {
        switch (language)
        {
            case JAPANESE:
                return lucenePipeline.preprocess(documents, query, language, contextRequired);
            default:
                return builtinPipeline.preprocess(documents, query, language, contextRequired);
        }
    }
}
