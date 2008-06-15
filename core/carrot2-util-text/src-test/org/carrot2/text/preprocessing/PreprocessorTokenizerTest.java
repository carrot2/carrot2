package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.text.analysis.ExtendedWhitespaceAnalyzer;
import org.carrot2.text.analysis.TokenType;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PreprocessorTokenizerTest
{
    protected Preprocessor preprocessor;
    protected List<Document> documents;

    @Before
    public void setUpPreprocessor()
    {
        preprocessor = new Preprocessor();
        preprocessor.analyzer = new ExtendedWhitespaceAnalyzer();
    }

    protected void createDocuments(String... fieldValues)
    {
        createDocuments(DEFAULT_DOCUMENT_FIELD_NAMES, fieldValues);
    }

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
    }

    private final static String [] DEFAULT_DOCUMENT_FIELD_NAMES = new String []
    {
        "title", "snippet"
    };

    @Test
    public void testNoDocuments()
    {
        createDocuments();

        final int [] expectedTokensDocumentIndices = new int [] {};
        final int [] expectedTokensTypes = new int [] {};
        final byte [] expectedTokensFieldIndices = new byte [] {};

        check(0, 0, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testEmptyDocuments()
    {
        createDocuments("", "", null, null);

        final int [] expectedTokensDocumentIndices = new int []
        {
            -1
        };
        final int [] expectedTokensTypes = new int []
        {
            TokenType.TF_SEPARATOR_DOCUMENT
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            -1
        };

        check(0, 0, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testOneDocument()
    {
        createDocuments("data mining", "web site");

        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, 0, 0
        };
        final int [] expectedTokensTypes = new int []
        {
            TokenType.TT_TERM, TokenType.TT_TERM, TokenType.TF_SEPARATOR_FIELD,
            TokenType.TT_TERM, TokenType.TT_TERM
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, -1, 1, 1
        };

        check(0, 0, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testMoreDocuments()
    {
        createDocuments("data mining", "web site", "artificial intelligence", "ai",
            "test", "test");

        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, 0, 0, -1, 1, 1, 1, 1, -1, 2, 2, 2
        };
        final int [] expectedTokensTypes = new int []
        {
            TokenType.TT_TERM, TokenType.TT_TERM, TokenType.TF_SEPARATOR_FIELD,
            TokenType.TT_TERM, TokenType.TT_TERM, TokenType.TF_SEPARATOR_DOCUMENT,
            TokenType.TT_TERM, TokenType.TT_TERM, TokenType.TF_SEPARATOR_FIELD,
            TokenType.TT_TERM, TokenType.TF_SEPARATOR_DOCUMENT, TokenType.TT_TERM,
            TokenType.TF_SEPARATOR_FIELD, TokenType.TT_TERM
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, -1, 1, 1, -1, 0, 0, -1, 1, -1, 0, -1, 1
        };

        check(0, 0, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testEmptyField()
    {
        final String [] fieldNames = new String []
        {
            "title", "snippet", "body"
        };
        createDocuments(fieldNames, "data mining", "", "web site");

        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, 0, 0
        };
        final int [] expectedTokensTypes = new int []
        {
            TokenType.TT_TERM, TokenType.TT_TERM, TokenType.TF_SEPARATOR_FIELD,
            TokenType.TT_TERM, TokenType.TT_TERM
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, -1, 2, 2
        };

        check(0, 0, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, fieldNames);
    }

    private void check(int expectedTokensLength, int expectedFieldsLength,
        final int [] expectedTokensDocumentIndices, final int [] expectedTokensTypes,
        final byte [] expectedTokensFieldIndices, String [] expectedFieldsNames)
    {
        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE);
        assertThat(context.allTokens.length).as("allTokens.length").isEqualTo(
            expectedTokensLength);
        assertThat(context.allFields.length).as("allFields.length").isEqualTo(
            expectedFieldsLength);
        assertThat(context.allFields.names).as("allFields.names").isEqualTo(
            expectedFieldsNames);
        assertThat(context.allTokens.documentIndices).as("allTokens.documentIndices")
            .isEqualTo(expectedTokensDocumentIndices);
        assertThat(context.allTokens.fieldIndices).as("allTokens.fieldIndices")
            .isEqualTo(expectedTokensFieldIndices);
        assertThat(context.allTokens.types).as("allTokens.types").isEqualTo(
            expectedTokensTypes);
    }
}
