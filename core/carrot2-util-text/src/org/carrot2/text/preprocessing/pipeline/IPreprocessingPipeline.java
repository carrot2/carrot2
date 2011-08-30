package org.carrot2.text.preprocessing.pipeline;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * A preprocessing pipeline filling in {@link PreprocessingContext} with the required data.
 */
public interface IPreprocessingPipeline
{
    /**
     * Performs preprocessing on the provided list of documents, creating a new preprocessing
     * context on the way. Results can be obtained from the returned {@link PreprocessingContext}.
     */
    PreprocessingContext preprocess(List<Document> documents, String query, LanguageCode language);

    /**
     * Performs preprocessing on the provided {@link PreprocessingContext}.
     */
    void preprocess(PreprocessingContext context);
}
