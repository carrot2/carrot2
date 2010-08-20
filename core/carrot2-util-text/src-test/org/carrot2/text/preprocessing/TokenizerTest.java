
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

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.carrot2.text.analysis.ITokenizer;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link Tokenizer}.
 */
public class TokenizerTest extends PreprocessingComponentTestBase
{
    /** The tokenizer under tests */
    private Tokenizer tokenizer;
    
    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
    }

    @Test
    public void testNoDocuments()
    {
        createDocuments();

        final char [][] expectedTokensImages = new char [] []
        {
            null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            -1
        };

        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testEmptyDocuments()
    {
        createDocuments("", "", null, null);

        final char [][] expectedTokensImages = new char [] []
        {
            null, null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            -1, -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TF_SEPARATOR_DOCUMENT, ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            -1, -1
        };

        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testEmptyFirstField()
    {
        createDocuments(null, "a");

        final char [][] expectedTokensImages = new char [] []
        {
            "a".toCharArray(), null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            0, -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TT_TERM, ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            1, -1
        };

        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testOneDocument()
    {
        createDocuments("data mining", "web site");

        final char [][] expectedTokensImages = new char [] []
        {
            "data".toCharArray(), "mining".toCharArray(), null, "web".toCharArray(),
            "site".toCharArray(), null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, 0, 0, -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_FIELD,
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, -1, 1, 1, -1
        };

        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    @Test
    public void testMoreDocuments()
    {
        createDocuments("data mining", "web site", "artificial intelligence", "ai",
            "test", "test");

        final char [][] expectedTokensImages = new char [] []
        {
            "data".toCharArray(), "mining".toCharArray(), null, "web".toCharArray(),
            "site".toCharArray(), null, "artificial".toCharArray(),
            "intelligence".toCharArray(), null, "ai".toCharArray(), null,
            "test".toCharArray(), null, "test".toCharArray(), null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, 0, 0, -1, 1, 1, 1, 1, -1, 2, 2, 2, -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_FIELD,
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_DOCUMENT,
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_FIELD,
            ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_DOCUMENT, ITokenizer.TT_TERM,
            ITokenizer.TF_SEPARATOR_FIELD, ITokenizer.TT_TERM, ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, -1, 1, 1, -1, 0, 0, -1, 1, -1, 0, -1, 1, -1
        };

        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
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

        final char [][] expectedTokensImages = new char [] []
        {
            "data".toCharArray(), "mining".toCharArray(), null, "web".toCharArray(),
            "site".toCharArray(), null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, 0, 0, -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_FIELD,
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, -1, 2, 2, -1
        };

        tokenizer.documentFields = Arrays.asList(fieldNames);
        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, fieldNames);
    }

    @Test
    public void testSentenceSeparator()
    {
        createDocuments("data . mining", "");

        final char [][] expectedTokensImages = new char [] []
        {
            "data".toCharArray(), ".".toCharArray(), "mining".toCharArray(), null
        };
        final int [] expectedTokensDocumentIndices = new int []
        {
            0, 0, 0, -1
        };
        final short [] expectedTokensTypes = new short []
        {
            ITokenizer.TT_TERM,
            ITokenizer.TF_SEPARATOR_SENTENCE | ITokenizer.TT_PUNCTUATION,
            ITokenizer.TT_TERM, ITokenizer.TF_TERMINATOR
        };
        final byte [] expectedTokensFieldIndices = new byte []
        {
            0, 0, 0, -1
        };

        check(expectedTokensImages, expectedTokensDocumentIndices, expectedTokensTypes,
            expectedTokensFieldIndices, DEFAULT_DOCUMENT_FIELD_NAMES);
    }

    private void check(char [][] expectedTokensImages,
        final int [] expectedTokensDocumentIndices, final short [] expectedTokensTypes,
        final byte [] expectedTokensFieldIndices, String [] expectedFieldNames)
    {
        tokenizer.tokenize(context);

        assertThat(context.allFields.name).as("allFields.names").isEqualTo(
            expectedFieldNames);
        assertThat(context.allTokens.image).as("allTokens.images").isEqualTo(
            expectedTokensImages);
        assertThat(context.allTokens.documentIndex).as("allTokens.documentIndices")
            .isEqualTo(expectedTokensDocumentIndices);
        assertThat(context.allTokens.fieldIndex).as("allTokens.fieldIndices").isEqualTo(
            expectedTokensFieldIndices);
        assertThat(context.allTokens.type).as("allTokens.types").isEqualTo(
            expectedTokensTypes);
    }
}
