package org.carrot2.text.preprocessing;

import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Payload;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.*;
import org.carrot2.text.analysis.*;
import org.carrot2.text.linguistic.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

import com.google.common.collect.Sets;

/**
 * Utilities for transforming {@link Document}s into low-level data structures in
 * {@link PreprocessingContext}.
 */
@Bindable
public final class Preprocessor
{
    /**
     * Analyzer used to split {@link #documents} into individual tokens (terms). This
     * analyzer must provide token {@link Payload} implementing {@link TokenType}.
     * 
     * @level Medium
     * @group Preprocessing
     */
    @Init
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        ExtendedWhitespaceAnalyzer.class
    })
    public Analyzer analyzer = new ExtendedWhitespaceAnalyzer();

    /** */
    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    public Collection<Document> documents;

    /**
     * Textual fields of a {@link Document} that should be tokenized and parsed for
     * clustering.
     * 
     * @level Advanced
     * @group Preprocessing
     */
    @Init
    @Input
    @Attribute
    public Collection<String> documentFields = Arrays.asList(new String []
    {
        Document.TITLE, Document.SUMMARY
    });

    /**
     * Text tokenizer. Performs {@link PreprocessingTasks#TOKENIZE} task.
     * 
     * @level Medium
     * @group Preprocessing
     */
    @Processing
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        TokenizerTaskImpl.class
    })
    public TokenizerTask tokenizer = new TokenizerTaskImpl();

    /**
     * Case normalizer. Performs {@link PreprocessingTasks#CASE_NORMALIZE} task.
     * 
     * @level Medium
     * @group Preprocessing
     */
    @Processing
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        LocaleCaseNormalizer.class
    })
    public CaseNormalizerTask caseNormalizer = new LocaleCaseNormalizer();

    /**
     * Stemmer. Performs {@link PreprocessingTasks#STEMMING} task.
     * 
     * @level Medium
     * @group Preprocessing
     */
    @Processing
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        LanguageModelStemmingTask.class
    })
    public StemmingTask stemmer = new LanguageModelStemmingTask();

    /**
     * Linguistic resources. Exposes current processing language internally.
     */
    public LanguageModelFactory languageFactory = new LanguageModelFactory();

    /**
     * Run the selected preprocessing tasks.
     */
    public void preprocess(PreprocessingContext context, PreprocessingTasks... tasks)
    {
        final LanguageModel language = languageFactory.getCurrentLanguage();

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

            tokenizer.add(documents, documentFields, analyzer);

            context.tokenMap = tokenizer.getTokenMap();
            context.allTokens = tokenizer.getTokens();
            context.allTypes = tokenizer.getTokenTypes();
            context.allTokenImages = tokenizer.getTokenImages();
        }

        /*
         * Case normalization.
         */
        if (taskSet.remove(PreprocessingTasks.CASE_NORMALIZE))
        {
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokenImages);
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokens);

            caseNormalizer.normalize(context.tokenMap, context.allTokenImages,
                context.allTokens, languageFactory);

            context.allTokensNormalized = caseNormalizer.getTokensNormalized();
            context.allTokenImages = context.tokenMap.getTokenImages();
        }

        /*
         * Stemming.
         */
        if (taskSet.remove(PreprocessingTasks.STEMMING))
        {
            assertContextParameterGiven(PreprocessingTasks.STEMMING,
                PreprocessingTasks.TOKENIZE, context.allTokenImages);

            stemmer.stem(context.tokenMap, context, language);

            context.allTokenImages = context.tokenMap.getTokenImages();
            context.allTokensStemmed = stemmer.getTokensStemmed();
        }

        /*
         * Common word marking.
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
     * Asserts that the given parameter is not null.
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
