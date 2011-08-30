package org.carrot2.text.preprocessing;

import java.util.ArrayList;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.preprocessing.pipeline.*;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

    public PreprocessingContextBuilder withStemmerFactory(IStemmerFactory stemmerFactory)
    {
        return setAttribute(
            AttributeUtils.getKey(BasicPreprocessingPipeline.class, "stemmerFactory"),
            stemmerFactory);
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
    
    @SuppressWarnings("unchecked")
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