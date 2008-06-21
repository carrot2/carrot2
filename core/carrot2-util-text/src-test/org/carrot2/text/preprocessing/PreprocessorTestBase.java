package org.carrot2.text.preprocessing;

import java.util.List;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.text.analysis.ExtendedWhitespaceAnalyzer;
import org.carrot2.text.linguistic.LanguageModelFactory;
import org.junit.Before;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Base class for {@link Preprocessor} tasks tests.
 */
public class PreprocessorTestBase
{
    /** The preprocessor we test */
    protected Preprocessor preprocessor;

    /** Documents each test sets up */
    protected List<Document> documents;

    /** Word image to index mapping */
    protected Map<String, Integer> wordIndices;

    @Before
    public void setUpPreprocessor()
    {
        preprocessor = new Preprocessor();
        preprocessor.analyzer = new ExtendedWhitespaceAnalyzer();

        final LanguageModelFactory languageModelFactory = createLanguageModelFactory();
        if (languageModelFactory != null)
        {
            preprocessor.languageFactory = languageModelFactory;
        }
    }

    /**
     * Creates the {@link LanguageModelFactory} to be used in tests. Override to use a
     * factory that's different from the default.
     */
    protected LanguageModelFactory createLanguageModelFactory()
    {
        return null;
    }

    /**
     * A utility method for creating documents for tests. See subclasses for usage
     * examples.
     * 
     * @param fields names of fields to create
     * @param fieldValues values for fields, for each <code>fields.length</code> values,
     *            one document will be created.
     */
    protected void createDocuments(String [] fields, String... fieldValues)
    {
        documents = Lists.newArrayList();

        int fieldValuesIndex = 0;
        while (fieldValuesIndex < fieldValues.length)
        {
            Document document = new Document();
            for (String fieldName : fields)
            {
                document.addField(fieldName, fieldValues[fieldValuesIndex++]);

                if (fieldValuesIndex >= fieldValues.length)
                {
                    break;
                }
            }
            documents.add(document);
        }

        Document.assignDocumentIds(documents);
        preprocessor.documents = documents;
        preprocessor.documentFields = Lists.newArrayList(fields);

        prepareWordIndices();
    }

    /**
     * Creates documents with {@link #DEFAULT_DOCUMENT_FIELD_NAMES}.
     */
    protected void createDocuments(String... fieldValues)
    {
        createDocuments(DEFAULT_DOCUMENT_FIELD_NAMES, fieldValues);
    }

    /**
     * Default field names.
     */
    protected final static String [] DEFAULT_DOCUMENT_FIELD_NAMES = new String []
    {
        "title", "snippet"
    };

    /**
     * Initializes the map with word image to index.
     */
    protected void prepareWordIndices()
    {
        Preprocessor temporaryPreprocessor = new Preprocessor();
        temporaryPreprocessor.analyzer = new ExtendedWhitespaceAnalyzer();
        temporaryPreprocessor.documents = documents;
        temporaryPreprocessor.documentFields = preprocessor.documentFields;
        temporaryPreprocessor.dfCutoff = preprocessor.dfCutoff;

        PreprocessingContext temporaryContext = new PreprocessingContext();
        temporaryPreprocessor.preprocess(temporaryContext, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE);

        final char [][] images = temporaryContext.allWords.image;
        wordIndices = Maps.newHashMap();
        for (int i = 0; i < images.length; i++)
        {
            wordIndices.put(new String(images[i]), i);
        }
    }
}
