
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.*;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;

/**
 * Base class for {@link PreprocessingContext} tasks tests.
 */
public class PreprocessingComponentTestBase extends CarrotTestCase
{
    /** Preprocessing context for the component being tested */
    protected PreprocessingContext context;
    protected String query;

    /** Documents each test sets up */
    protected List<Document> documents;

    /** Word image to index mapping */
    protected Map<String, Integer> wordIndices;

    protected LanguageModel languageModel;

    @Before
    public void setUpPreprocessingInfrastructure()
    {
        this.documents = new ArrayList<>();
        this.languageModel = new LanguageModel(createStemmer(), createTokenizer(), createLexicalData());
        setupPreprocessingContext(null);
    }

    protected void setupPreprocessingContext(String query)
    {
        this.query = query;
        this.context = new PreprocessingContext(languageModel);
    }

    /**
     */
    protected ITokenizer createTokenizer()
    {
        return LanguageModels.english().tokenizer;
    }

    /**
     */
    protected IStemmer createStemmer()
    {
        return LanguageModels.english().stemmer;
    }

    /**
     */
    protected ILexicalData createLexicalData()
    {
        return LanguageModels.english().lexicalData;
    }

    /**
     * A utility method for creating documents for tests. See subclasses for usage
     * examples.
     * 
     * @param fields names of fields to create
     * @param fieldValues values for fields, for each <code>fields.length</code> values,
     *            one document will be created.
     */
    protected void createDocumentsWithFields(String [] fields, String... fieldValues)
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
        createDocumentsWithFields(DEFAULT_DOCUMENT_FIELD_NAMES, fieldValues);
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
        final PreprocessingContext temporaryContext =
            new BasicPreprocessingPipeline().preprocess(documents, null, languageModel);

        final char [][] images = temporaryContext.allWords.image;
        wordIndices = new HashMap<>();
        for (int i = 0; i < images.length; i++)
        {
            wordIndices.put(new String(images[i]), i);
        }
    }
}
