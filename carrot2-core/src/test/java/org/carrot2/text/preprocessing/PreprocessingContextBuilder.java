
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

import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.linguistic.LanguageModels;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.AttributeBindingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Preprocessing context builder for tests.
 */
class PreprocessingContextBuilder
{
    private String query;
    private ArrayList<Document> documents = new ArrayList<>();
    private LanguageModel languageModel = LanguageModels.english();

    private Map<String, Object> attributes = new HashMap<>();
    private IPreprocessingPipeline pipeline = new CompletePreprocessingPipeline();

    public final static class FieldValue
    {
        String field;
        String value;
        
        public FieldValue(String field, String value)
        {
            this.field = field;
            this.value = value;
        }

        public static FieldValue fv(String fieldName, String value)
        {
            return new FieldValue(fieldName, value);
        }        
    }

    public PreprocessingContextBuilder newDoc(String title) {
        return newDoc(title, null, null);
    }

    public PreprocessingContextBuilder newDoc(String title, String summary) {
        return newDoc(title, summary, null);
    }

    public PreprocessingContextBuilder newDoc(String title, String summary, String contentUrl)
    {
        documents.add(new Document(title, summary, contentUrl));
        return this;
    }

    public PreprocessingContextBuilder newDoc(FieldValue... fields)
    {
        Document doc = new Document();
        for (FieldValue fv : fields)
            doc.setField(fv.field, fv.value);
        documents.add(doc);
        return this;
    }

    public PreprocessingContextBuilder withPreprocessingPipeline(IPreprocessingPipeline pipeline)
    {
        this.pipeline = pipeline;
        return this;
    }

    public void withStemmer(IStemmer stemmer) {
        this.languageModel.stemmer = stemmer;
    }

    public PreprocessingContextBuilder withQuery(String query)
    {
        this.query = query;
        return setAttribute(AttributeNames.QUERY, query);
    }

    public PreprocessingContextBuilder setAttribute(String key, Object value)
    {
        this.attributes.put(key, value);
        return this;
    }

    public PreprocessingContextAssert buildContextAssert()
    {
        return new PreprocessingContextAssert(buildContext());
    }
    
    public PreprocessingContext buildContext()
    {
        try
        {
            AttributeBinder.set(pipeline, attributes, true);
            return pipeline.preprocess(documents, query, languageModel);
        }
        catch (AttributeBindingException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
    }
}
