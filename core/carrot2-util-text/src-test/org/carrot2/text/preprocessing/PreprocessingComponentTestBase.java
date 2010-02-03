
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.List;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.linguistic.DefaultLanguageModelFactory;
import org.junit.Before;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Base class for {@link PreprocessingContext} tasks tests.
 */
public class PreprocessingComponentTestBase
{
    /** Language model factory for preprocessing components being tested */
    protected ILanguageModelFactory languageFactory;

    /** Preprocessing context for the component being tested */
    protected PreprocessingContext context;

    /** Documents each test sets up */
    private List<Document> documents;

    /** Word image to index mapping */
    protected Map<String, Integer> wordIndices;

    @Before
    public void setUpPreprocessingInfrastructure()
    {
        documents = Lists.newArrayList();
        createPreprocessingContext(null);
    }

    /**
     * Creates the {@link PreprocessingContext} for tests.
     */
    protected void createPreprocessingContext(String query)
    {
        final ILanguageModelFactory languageModelFactory = createLanguageModelFactory();
        if (languageModelFactory != null)
        {
            languageFactory = languageModelFactory;
        }
        else
        {
            languageFactory = new DefaultLanguageModelFactory();
        }

        context = new PreprocessingContext(languageFactory
            .getLanguageModel(LanguageCode.ENGLISH), documents, query);
    }

    /**
     * Creates the {@link ILanguageModelFactory} to be used in tests. Override to use a
     * factory that's different from the default.
     */
    protected ILanguageModelFactory createLanguageModelFactory()
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
        int fieldValuesIndex = 0;
        while (fieldValuesIndex < fieldValues.length)
        {
            Document document = new Document();
            for (String fieldName : fields)
            {
                document.setField(fieldName, fieldValues[fieldValuesIndex++]);

                if (fieldValuesIndex >= fieldValues.length)
                {
                    break;
                }
            }
            documents.add(document);
        }

        Document.assignDocumentIds(documents);
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
    final static String [] DEFAULT_DOCUMENT_FIELD_NAMES = new String []
    {
        Document.TITLE, Document.SUMMARY
    };

    /**
     * Initializes the map with word image to index.
     */
    protected void prepareWordIndices()
    {
        final Tokenizer temporaryTokenizer = new Tokenizer();
        final CaseNormalizer temporaryCaseNormalizer = new CaseNormalizer();
        final PreprocessingContext temporaryContext = new PreprocessingContext(
            languageFactory.getLanguageModel(LanguageCode.ENGLISH), documents, null);
        beforePrepareWordIndices(temporaryTokenizer, temporaryCaseNormalizer);

        temporaryTokenizer.tokenize(temporaryContext);
        temporaryCaseNormalizer.normalize(temporaryContext);

        final char [][] images = temporaryContext.allWords.image;
        wordIndices = Maps.newHashMap();
        for (int i = 0; i < images.length; i++)
        {
            wordIndices.put(new String(images[i]), i);
        }
    }

    /**
     * A hook that allows some customization in the word index preparation stage.
     */
    protected void beforePrepareWordIndices(Tokenizer temporaryTokenizer,
        CaseNormalizer temporaryCaseNormalizer)
    {
    }
}
