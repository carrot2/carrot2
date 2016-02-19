
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.ArrayList;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.preprocessing.pipeline.*;
import org.carrot2.util.attribute.*;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Preprocessing context builder for tests.
 */
class PreprocessingContextBuilder
{
    private String query;
    private ArrayList<Document> documents = Lists.newArrayList();
    private LanguageCode language = LanguageCode.ENGLISH;

    private Map<String, Object> attributes = Maps.newHashMap();
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

    public PreprocessingContextBuilder withStemmerFactory(IStemmerFactory stemmerFactory)
    {
        return setAttribute(
            AttributeUtils.getKey(BasicPreprocessingPipeline.class, "stemmerFactory"),
            stemmerFactory);
    }

    public PreprocessingContextBuilder withLexicalDataFactory(ILexicalDataFactory lexicalFactory)
    {
        return setAttribute(
            AttributeUtils.getKey(BasicPreprocessingPipeline.class, "lexicalDataFactory"), 
            lexicalFactory);
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
            return pipeline.preprocess(documents, query, language);
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
