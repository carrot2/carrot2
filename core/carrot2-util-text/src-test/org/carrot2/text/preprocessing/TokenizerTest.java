
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

import static org.carrot2.text.preprocessing.PreprocessingContextAssert.tokens;
import static org.carrot2.text.preprocessing.PreprocessingContextBuilder.FieldValue.*;

import java.util.Arrays;

import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link Tokenizer}.
 */
public class TokenizerTest extends PreprocessingContextTestBase
{
    PreprocessingContextBuilder contextBuilder;

    @Before
    public void prepareContextBuilder()
    {
        contextBuilder = new PreprocessingContextBuilder()
            .withPreprocessingPipeline(new BasicPreprocessingPipeline());
    }

    // @formatter:off

    @Test
    public void testNoDocuments()
    {
        PreprocessingContext ctx = contextBuilder
            .buildContext();

        assertThat(ctx).tokenAt(0)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_TERMINATOR);
    }

    @Test
    public void testEmptyDocuments()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc(null, null)
            .newDoc("", "")
            .buildContext();

        assertThat(ctx).tokenAt(0)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_SEPARATOR_DOCUMENT);
        assertThat(ctx).tokenAt(1)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_TERMINATOR);
    }

    @Test
    public void testEmptyFirstField()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc(null, "a")
            .buildContext();

        assertThat(ctx).tokenAt(0)
            .hasImage("a").hasDocIndex(0).hasFieldIndex(1)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(1)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_TERMINATOR);
    }

    @Test
    public void testEmptyField()
    {
        PreprocessingContext ctx = contextBuilder
            .setAttribute(AttributeUtils.getKey(Tokenizer.class, "documentFields"), Arrays.asList("field1", "field2", "field3"))
            .newDoc(
                fv("field1", "data mining"),
                fv("field2", ""),
                fv("field3", "web site"))
            .buildContext();

        assertThat(ctx.allFields.name).isEqualTo(new String [] {"field1", "field2", "field3"});

        assertThat(ctx).tokenAt(0)
            .hasImage("data").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(1)
            .hasImage("mining").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(2)
            .hasImage(null).hasDocIndex(0).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_SEPARATOR_FIELD);
        assertThat(ctx).tokenAt(3)
            .hasImage("web").hasDocIndex(0).hasFieldIndex(2)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(4)
            .hasImage("site").hasDocIndex(0).hasFieldIndex(2)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(5)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_TERMINATOR);
    }

    @Test
    public void testOneDocument()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("data mining", "web site")
            .buildContext();
        
        assertThat(ctx).tokenAt(0)
            .hasImage("data").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(1)
            .hasImage("mining").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(2)
            .hasImage(null).hasDocIndex(0).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_SEPARATOR_FIELD);
        assertThat(ctx).tokenAt(3)
            .hasImage("web").hasDocIndex(0).hasFieldIndex(1)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(4)
            .hasImage("site").hasDocIndex(0).hasFieldIndex(1)
            .hasExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).tokenAt(5)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_TERMINATOR);
    }
    
    @Test
    public void testSentenceSeparator()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("data . mining", "")
            .buildContext();
        
        assertThat(ctx).tokenAt(0)
            .hasImage("data").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TT_TERM);

        assertThat(ctx).tokenAt(1)
            .hasImage(".").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TF_SEPARATOR_SENTENCE | ITokenizer.TT_PUNCTUATION);

        assertThat(ctx).tokenAt(2)
            .hasImage("mining").hasDocIndex(0).hasFieldIndex(0)
            .hasExactTokenType(ITokenizer.TT_TERM);

        assertThat(ctx).tokenAt(3)
            .hasImage(null).hasDocIndex(-1).hasFieldIndex(-1)
            .hasExactTokenType(ITokenizer.TF_TERMINATOR);
    }

    @Test
    public void testMoreDocuments()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("data mining", "web site")
            .newDoc("artificial intelligence", "ai")
            .newDoc("test", "test")
            .buildContext();

        assertThat(tokens(ctx)).onProperty("tokenImage").isEqualTo(Arrays.asList(
            "data", "mining", null, "web", "site", null,
            "artificial", "intelligence", null, "ai", null,
            "test", null, "test", null));
        
        assertThat(ctx.allTokens.documentIndex).isEqualTo(new int [] {
            0, 0, 0, 0, 0, -1, 1, 1, 1, 1, -1, 2, 2, 2, -1
        });

        assertThat(ctx.allTokens.type).isEqualTo(new short [] {
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_FIELD,
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_DOCUMENT,
            ITokenizer.TT_TERM, ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_FIELD,
            ITokenizer.TT_TERM, ITokenizer.TF_SEPARATOR_DOCUMENT, ITokenizer.TT_TERM,
            ITokenizer.TF_SEPARATOR_FIELD, ITokenizer.TT_TERM, ITokenizer.TF_TERMINATOR
        });
        
        assertThat(ctx.allTokens.fieldIndex).isEqualTo(new byte [] {
            0, 0, -1, 1, 1, -1, 0, 0, -1, 1, -1, 0, -1, 1, -1
        });
    }

    @Test
    public void testUnicodeNextLine()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("Foo\u0085 Bar")
            .buildContext();

        assertThat(tokens(ctx)).onProperty("tokenImage").isEqualTo(Arrays.asList(
            "Foo", "Bar", null));
        
        assertThat(ctx.allTokens.type).isEqualTo(new short [] {
            ITokenizer.TT_TERM, ITokenizer.TT_TERM,
            ITokenizer.TF_TERMINATOR
            });
    }

    // @formatter:on
}
