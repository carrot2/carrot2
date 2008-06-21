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
import org.carrot2.util.attribute.constraint.IntRange;

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

    /**
     * A list of documents to be processed.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

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
     * The Document Frequency cut-off. Words appearing in less than <code>dfCutoff</code>
     * documents will be ignored.
     * 
     * @level Advanced
     * @group Preprocessing
     */
    @Processing
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    public int dfCutoff = 1;

    /**
     * Linguistic resources. Exposes current processing language internally.
     */
    public LanguageModelFactory languageFactory = new SnowballLanguageModelFactory();

    /**
     * Run the selected preprocessing tasks.
     */
    public void preprocess(PreprocessingContext context, PreprocessingTasks... tasks)
    {
        final LanguageModel language = languageFactory.getCurrentLanguage();
        context.documents = documents;

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
            final Tokenizer tokenizer = new Tokenizer();

            assertParameterGiven(PreprocessingTasks.TOKENIZE, "analyzer", analyzer);
            assertParameterGiven(PreprocessingTasks.TOKENIZE, "documents", documents);
            assertParameterGiven(PreprocessingTasks.TOKENIZE, "documentFields",
                documentFields);

            tokenizer.tokenize(context, documents, documentFields, analyzer);
        }

        /*
         * Case normalization.
         */
        if (taskSet.remove(PreprocessingTasks.CASE_NORMALIZE))
        {
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokens.image);
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokens.type);
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokens.documentIndex);
            assertContextParameterGiven(PreprocessingTasks.CASE_NORMALIZE,
                PreprocessingTasks.TOKENIZE, context.allTokens.fieldIndex);

            CaseNormalizer caseNormalizer = new CaseNormalizer(dfCutoff);
            caseNormalizer.normalize(context, languageFactory);
        }

        /*
         * Stemming.
         */
        if (taskSet.remove(PreprocessingTasks.STEMMING))
        {
            assertContextParameterGiven(PreprocessingTasks.STEMMING,
                PreprocessingTasks.TOKENIZE, context.allWords.image);
            assertContextParameterGiven(PreprocessingTasks.STEMMING,
                PreprocessingTasks.TOKENIZE, context.allWords.tf);
            assertContextParameterGiven(PreprocessingTasks.STEMMING,
                PreprocessingTasks.TOKENIZE, context.allWords.tfByDocument);

            final LanguageModelStemmer stemmer = new LanguageModelStemmer();
            stemmer.stem(context, language);
        }

        /*
         * Common word marking.
         */
        if (taskSet.remove(PreprocessingTasks.MARK_TOKENS_STOPLIST))
        {
            assertContextParameterGiven(PreprocessingTasks.MARK_TOKENS_STOPLIST,
                PreprocessingTasks.TOKENIZE, context.allWords.image);

            final StopListMarker task = new StopListMarker();
            task.mark(context, language);
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
