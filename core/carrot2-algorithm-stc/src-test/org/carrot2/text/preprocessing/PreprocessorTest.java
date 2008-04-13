package org.carrot2.text.preprocessing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.*;

import org.carrot2.core.Document;
import org.carrot2.text.analysis.ExtendedWhitespaceAnalyzer;
import org.carrot2.text.linguistic.*;
import org.junit.*;

public class PreprocessorTest
{
    public Collection<Document> testDocuments1;
    public Collection<Document> testDocuments2;
    public Collection<Document> testDocuments3;
    public Collection<String> testFields;

    @Before
    public void prepareDocuments()
    {
        testDocuments1 = Arrays.asList(new Document []
        {
            new Document().addField("title", "abc def").addField("snippet", "abc xyz"),
            new Document().addField("title", "xyz").addField("snippet", "abc def"),
            new Document().addField("snippet", "abc def"),
        });

        testDocuments2 = Arrays.asList(new Document []
        {
            new Document().addField("title", "Data Mining").addField("snippet",
                "data mining"),
            new Document().addField("title", "dAtA mInInG").addField("snippet",
                "DaTa MiNiNg"),
        });

        testDocuments3 = Arrays.asList(new Document []
        {
            new Document().addField("title", "Data And Mining and or abc"),
        });

        testFields = Arrays.asList(new String []
        {
            "title", "snippet"
        });
    }

    @Test
    public void testTokenization() throws IOException
    {
        final Preprocessor preprocessor = new Preprocessor();

        preprocessor.setAnalyzer(new ExtendedWhitespaceAnalyzer());
        preprocessor.setDocuments(testDocuments1);
        preprocessor.setDocumentFields(testFields);

        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE);

        final CharSequence [] images = context.allTokenImages;
        final int [] tokens = context.allTokens;

        // unique token images.
        assertEquals("abc", images[0].toString());
        assertEquals("def", images[1].toString());
        assertEquals("xyz", images[2].toString());
        assertEquals(3, images.length);

        // abc on position 0 and 3 (abc def FIELD_SEP abc [...]).
        assertEquals(tokens[0], tokens[3]);

        // Check separators.
        assertEquals(PreprocessingContext.SEPARATOR_FIELD, tokens[2]);
        assertEquals(PreprocessingContext.SEPARATOR_DOCUMENT, tokens[5]);
    }

    @Test
    public void testCaseNormalization() throws IOException
    {
        final Preprocessor preprocessor = new Preprocessor();

        preprocessor.setAnalyzer(new ExtendedWhitespaceAnalyzer());
        preprocessor.setDocuments(testDocuments2);
        preprocessor.setDocumentFields(testFields);
        preprocessor.setLanguageModel(new LanguageModelFactory()
            .getLanguage(LanguageCode.ENGLISH));

        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE);

        final CharSequence [] images = context.allTokenImages;
        final int [] tokens = context.allTokensNormalized;

        // Check case-normalized tokens.
        int i = 0;
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mining", images[tokens[i++]].toString());
        assertEquals(PreprocessingContext.SEPARATOR_FIELD, tokens[i++]);
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mining", images[tokens[i++]].toString());
        assertEquals(PreprocessingContext.SEPARATOR_DOCUMENT, tokens[i++]);
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mining", images[tokens[i++]].toString());
        assertEquals(PreprocessingContext.SEPARATOR_FIELD, tokens[i++]);
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mining", images[tokens[i++]].toString());
    }

    @Test
    public void testStemmingWithCaseNormalization() throws IOException
    {
        final Preprocessor preprocessor = new Preprocessor();

        preprocessor.setAnalyzer(new ExtendedWhitespaceAnalyzer());
        preprocessor.setDocuments(testDocuments2);
        preprocessor.setDocumentFields(testFields);
        preprocessor.setLanguageModel(new LanguageModelFactory()
            .getLanguage(LanguageCode.ENGLISH));

        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE, PreprocessingTasks.STEMMING);

        final CharSequence [] images = context.allTokenImages;
        final int [] tokens = context.allTokensStemmed;

        // Check tokens.
        int i = 0;
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mine", images[tokens[i++]].toString());
        assertEquals(PreprocessingContext.SEPARATOR_FIELD, tokens[i++]);
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mine", images[tokens[i++]].toString());
        assertEquals(PreprocessingContext.SEPARATOR_DOCUMENT, tokens[i++]);
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mine", images[tokens[i++]].toString());
        assertEquals(PreprocessingContext.SEPARATOR_FIELD, tokens[i++]);
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("mine", images[tokens[i++]].toString());
    }

    @Test
    public void testStopWordMarking() throws IOException
    {
        final Preprocessor preprocessor = new Preprocessor();

        preprocessor.setAnalyzer(new ExtendedWhitespaceAnalyzer());
        preprocessor.setDocuments(testDocuments3);
        preprocessor.setDocumentFields(testFields);
        preprocessor.setLanguageModel(new LanguageModelFactory()
            .getLanguage(LanguageCode.ENGLISH));

        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE, PreprocessingTasks.STEMMING,
            PreprocessingTasks.MARK_TOKENS_STOPLIST);

        final CharSequence [] images = context.allTokenImages;
        final boolean [] common = context.commonTermFlag;
        final int [] tokens = context.allTokensNormalized;

        // Check tokens.
        // Data And Mining and or abc
        int i = 0;
        assertEquals("data", images[tokens[i++]].toString());
        assertEquals("and", images[tokens[i++]].toString());
        assertEquals("mining", images[tokens[i++]].toString());
        assertEquals("and", images[tokens[i++]].toString());
        assertEquals("or", images[tokens[i++]].toString());
        assertEquals("abc", images[tokens[i++]].toString());
        
        i = 0;
        assertEquals(false, common[tokens[i++]]);
        assertEquals(true, common[tokens[i++]]);
        assertEquals(false, common[tokens[i++]]);
        assertEquals(true, common[tokens[i++]]);
        assertEquals(true, common[tokens[i++]]);
        assertEquals(false, common[tokens[i++]]);
    }
}
