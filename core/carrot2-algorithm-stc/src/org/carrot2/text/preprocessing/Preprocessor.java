package org.carrot2.text.preprocessing;

import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.carrot2.core.Document;
import org.carrot2.text.CharSequenceIntMap;
import org.carrot2.text.linguistic.LanguageModel;

import com.google.common.collect.Sets;

/**
 * Utilities for transforming {@link Document}s into low-level data structures in
 * {@link PreprocessingContext}.
 */
public final class Preprocessor
{
    /** */
    private Analyzer analyzer;

    /** */
    private Collection<Document> documents;

    /** */
    private Collection<String> documentFields;

    /** */
    private LanguageModel language;

    /**
     * Run the selected preprocessing tasks.
     */
    public void preprocess(PreprocessingContext context, PreprocessingTasks... tasks)
    {
        /*
         * Assert the correct order of preprocessing tasks by throwing them all in a set
         * and checking for all possibilities.
         */
        final HashSet<PreprocessingTasks> taskSet = Sets.newHashSet();
        taskSet.addAll(Arrays.asList(tasks));

        /*
         * Tokenization first.
         */
        if (taskSet.remove(PreprocessingTasks.TOKENIZE))
        {
            assertParameterGiven(PreprocessingTasks.TOKENIZE, "analyzer", analyzer);
            assertParameterGiven(PreprocessingTasks.TOKENIZE, "documents", documents);
            assertParameterGiven(PreprocessingTasks.TOKENIZE, "documentFields",
                documentFields);

            final TokenizerTask task = new TokenizerTask(context.tokenCoder);
            task.add(documents, documentFields, analyzer);

            context.allTokens = task.getTokens();
            context.allTypes = task.getTokenTypes();

            context.allTokenImages = task.getTokenImages();
        }

        /*
         * Case normalization.
         */
        if (taskSet.remove(PreprocessingTasks.CASE_NORMALIZE))
        {
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokenImages);

            /*
             * We use the same token coder for case-normalized images, this should save
             * some memory (reuse existing token images) and allow for unique token image
             * indices. One could also create a new token coder here.
             */
            final CharSequenceIntMap coder = context.tokenCoder;
            final CaseNormalizerTask task = new CaseNormalizerTask();
            task.normalize(coder, context.allTokenImages, context.allTokens, language);

            context.allTokenImages = coder.getTokenImages();
            context.allTokensNormalized = task.getTokensNormalized();
        }

        /*
         * Stemming. If case normalization is applied, then stemming operates on
         * case-normalized tokens. Otherwise raw tokens are used.
         */
        if (taskSet.remove(PreprocessingTasks.STEMMING))
        {
            assertContextParameterGiven(PreprocessingTasks.STEMMING,
                PreprocessingTasks.TOKENIZE, context.allTokenImages);

            /*
             * We use the same token coder for stemmed images, this should save some
             * memory (reuse existing token images) and allow for unique token image
             * indices. One could also create a new token coder here.
             */
            final CharSequenceIntMap coder = context.tokenCoder;
            final StemmingTask task = new StemmingTask();
            task.stem(coder, context, language);

            context.allTokenImages = coder.getTokenImages();
            context.allTokensStemmed = task.getTokensStemmed();
        }

        /*
         * Common word marking based on stop word lists associated with the given
         * language.
         */
        if (taskSet.remove(PreprocessingTasks.MARK_TOKENS_STOPLIST))
        {
            assertContextParameterGiven(PreprocessingTasks.MARK_TOKENS_STOPLIST,
                PreprocessingTasks.TOKENIZE, context.allTokenImages);

            final StopListMarkerTask task = new StopListMarkerTask();
            task.mark(context, language);

            context.commonTermFlag = task.getCommonTermFlags();
        }

        /*
         * Left-overs tasks cause an exception.
         */
        if (taskSet.size() > 0)
        {
            throw new RuntimeException("Unimplemented preprocessing tasks remained: "
                + taskSet);
        }
    }

    /**
     * Sets the {@link Document}s required for {@link PreprocessingTasks#TOKENIZE}.
     */
    public void setDocuments(Collection<Document> documents)
    {
        this.documents = documents;
    }

    /**
     * Sets the {@link Analyzer} required for {@link PreprocessingTasks#TOKENIZE}.
     */
    public void setAnalyzer(Analyzer analyzer)
    {
        this.analyzer = analyzer;
    }

    /**
     * Sets the document fields required for {@link PreprocessingTasks#TOKENIZE}.
     */
    public void setDocumentFields(Collection<String> fields)
    {
        this.documentFields = fields;
    }

    /**
     * Set language model required for {@link PreprocessingTasks#CASE_NORMALIZE},
     * {@link PreprocessingTasks#STEMMING}.
     */
    public void setLanguageModel(LanguageModel model)
    {
        this.language = model;
    }

    /**
     * Asserts that the given parameter is not null.
     */
    private void assertParameterGiven(PreprocessingTasks task, String parameterName,
        Object parameterValue) throws IllegalArgumentException
    {
        if (parameterValue == null)
        {
            throw new IllegalArgumentException("Task " + task + " requires parameter: "
                + parameterName);
        }
    }

    /**
     * 
     */
    private void assertContextParameterGiven(PreprocessingTasks task,
        PreprocessingTasks requiredTask, Object parameterValue)
        throws IllegalArgumentException
    {
        if (parameterValue == null)
        {
            throw new IllegalArgumentException("Task " + task + " requires that task "
                + requiredTask + " is performed first (missing context parameter).");
        }
    }
}
