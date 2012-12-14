package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.builtin.BuiltinPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.lucene.LucenePreprocessingPipeline;
import org.carrot2.util.attribute.Bindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

/**
 * Implements {@link IPreprocessingPipeline} delegation to 
 * {@link BuiltinPreprocessingPipeline} or
 * {@link LucenePreprocessingPipeline} depending on the language.
 */
@Bindable
public class DefaultPreprocessingPipeline implements IPreprocessingPipeline
{
    private final static Logger logger = LoggerFactory
        .getLogger(DefaultPreprocessingPipeline.class);

    private IPreprocessingPipeline builtinPipeline = new BuiltinPreprocessingPipeline();
    private IPreprocessingPipeline lucenePipeline;

    public DefaultPreprocessingPipeline()
    {
        try {
            lucenePipeline = new LucenePreprocessingPipelineIndirect().get();
        } catch (Throwable t) {
            logger.warn("Lucene preprocessing pipeline not available. Clustering in certain" +
            		" languages may not be possible. Cause: {}",
            		t.toString() + 
            		(logger.isDebugEnabled() ? "\n" + Throwables.getStackTraceAsString(t) : ""));
        }
    }

    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query, LanguageCode language, ContextRequired contextRequired)
    {
        switch (language)
        {
            case JAPANESE:
                if (lucenePipeline == null) {
                    throw new RuntimeException("Apache Lucene is required for Japanese " +
                    		"preprocessing and clustering. Include it in your classpath.");
                }
                return lucenePipeline.preprocess(documents, query, language, contextRequired);
            default:
                return builtinPipeline.preprocess(documents, query, language, contextRequired);
        }
    }
}


/**
 * Indirect construction of {@link LucenePreprocessingPipeline} for .NET port where
 * linking is done earlier than in Java.
 */
class LucenePreprocessingPipelineIndirect {

    public IPreprocessingPipeline get()
    {
        return new LucenePreprocessingPipeline();
    }
}