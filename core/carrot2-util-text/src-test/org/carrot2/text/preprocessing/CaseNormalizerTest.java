
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
import static org.carrot2.text.preprocessing.PreprocessingContextAssert.MW;
import static org.carrot2.text.preprocessing.PreprocessingContextAssert.DS;
import static org.carrot2.text.preprocessing.PreprocessingContextAssert.FS;
import static org.carrot2.text.preprocessing.PreprocessingContextAssert.EOS;

import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link CaseNormalizer}.
 */
public class CaseNormalizerTest extends PreprocessingContextTestBase
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
    public void testEmpty()
    {
        PreprocessingContext ctx = contextBuilder.buildContext();

        assertThat(ctx).tokens().isEmpty();
        assertThat(ctx.allTokens.wordIndex).isEqualTo(new int [] {-1});
    }

    @Test
    public void testOneToken()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("test")
            .buildContext();

        assertThat(ctx).containsWord("test").withTf(1).withDocumentTf(0, 1).withFieldIndices(0);
        assertThat(tokens(ctx)).onProperty("wordImage")
            .containsExactly("test", EOS);
    }

    @Test
    public void testMoreSingleDifferentTokens()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("a simple testsymbol")
            .buildContext();

        assertThat(ctx).containsWord("a").withTf(1).withDocumentTf(0, 1).withFieldIndices(0);
        assertThat(ctx).containsWord("simple").withTf(1).withDocumentTf(0, 1).withFieldIndices(0);
        assertThat(ctx).containsWord("testsymbol").withTf(1).withDocumentTf(0, 1).withFieldIndices(0);
        assertThat(tokens(ctx)).onProperty("wordImage")
            .containsExactly("a", "simple", "testsymbol", EOS);
    }

    @Test
    public void testTokenTypes()
    {
        String input = "12.2 email@email.com IEEE www.test.com file_name";
        PreprocessingContext ctx = contextBuilder
            .newDoc(input)
            .buildContext();

        for (String term : input.split("\\s"))
            assertThat(ctx).containsWord(term).withTf(1).withDocumentTf(0, 1).withFieldIndices(0);

        assertThat(ctx).containsWord("12.2").withTokenType(ITokenizer.TT_NUMERIC);
        assertThat(ctx).containsWord("email@email.com").withTokenType(ITokenizer.TT_EMAIL);
        assertThat(ctx).containsWord("IEEE").withTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).containsWord("www.test.com").withTokenType(ITokenizer.TT_BARE_URL);
        assertThat(ctx).containsWord("file_name").withTokenType(ITokenizer.TT_FILE);
    }

    @Test
    public void testMoreRepeatedDifferentTokens()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("a simple test", "a test a")
            .buildContext();

        assertThat(ctx).containsWord("a").withTf(3).withFieldIndices(0, 1).withDocumentTf(0, 3);
        assertThat(ctx).containsWord("simple").withTf(1).withFieldIndices(0).withDocumentTf(0, 1);
        assertThat(ctx).containsWord("test").withTf(2).withFieldIndices(0, 1).withDocumentTf(0, 2);
        assertThat(ctx.allWords.image.length).isEqualTo(3);
    }

    @Test
    public void testOneTokenVariantEqualFrequencies()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("abc abc ABC aBc")
            .buildContext();

        assertThat(ctx).containsWord("abc").withTf(4).withFieldIndices(0).withDocumentTf(0, 4);
        assertThat(ctx.allWords.image.length).isEqualTo(1);
        
        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "abc", "abc", "abc", "abc", EOS);
    }

    @Test
    public void testDemos()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("demo demo demos demos DEMO DEMOs Demo Demos")
            .buildContext();

        assertThat(ctx).containsWord("demo")
            .withTf(4).withFieldIndices(0).withDocumentTf(0, 4);
        assertThat(ctx).containsWord("demos")
            .withTf(4).withFieldIndices(0).withDocumentTf(0, 4);

        assertThat(ctx.allWords.image.length).isEqualTo(2);

        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "demo", "demo", "demos", "demos", "demo", "demos", "demo", "demos", EOS);
    }

    @Test
    public void testOneTokenVariantNonequalFrequencies()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("abc ABC ABC aBc aBc ABC")
            .buildContext();

        assertThat(ctx).containsWord("ABC")
            .withTf(6).withFieldIndices(0).withDocumentTf(0, 6);
    }

    @Test
    public void testMoreTokenVariants()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("abc bcd ABC bcD ABC efg", "aBc aBc ABC BCD bcd bcd")
            .buildContext();

        assertThat(ctx).containsWord("ABC")
            .withTf(6).withFieldIndices(0, 1).withDocumentTf(0, 6);
        assertThat(ctx).containsWord("bcd")
            .withTf(5).withFieldIndices(0, 1).withDocumentTf(0, 5);
        assertThat(ctx).containsWord("efg")
            .withTf(1).withFieldIndices(0).withDocumentTf(0, 1);

        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "ABC", "bcd", "ABC", "bcd", "ABC", "efg", FS,
            "ABC", "ABC", "ABC", "bcd", "bcd", "bcd", EOS);
    }

    @Test
    public void testDfThresholding()
    {
        PreprocessingContext ctx = contextBuilder
            .setAttribute(AttributeUtils.getKey(CaseNormalizer.class, "dfThreshold"), 2)
            .newDoc("a b c", "d e f")
            .newDoc("a c", "a")
            .buildContext();

        assertThat(ctx).containsWord("a")
            .withTf(3).withFieldIndices(0, 1)
            .withExactDocumentTfs(new int [][] {{0, 1}, {1, 2}});
        assertThat(ctx).containsWord("c")
            .withTf(2).withFieldIndices(0)
            .withExactDocumentTfs(new int [][] {{0, 1}, {1, 1}});

        assertThat(ctx.allWords.image.length).isEqualTo(2);

        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "a", MW, "c", FS, MW, MW, MW, DS,
            "a", "c", FS, "a", EOS);
    }
    
    @Test
    public void testTokenFiltering()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("a . b ,", "a . b ,")
            .buildContext();

        assertThat(ctx).containsWord("a")
            .withTf(2).withFieldIndices(0, 1).withDocumentTf(0, 2);
        assertThat(ctx).containsWord("b")
            .withTf(2).withFieldIndices(0, 1).withDocumentTf(0, 2);

        assertThat(ctx.allWords.image.length).isEqualTo(2);

        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "a", MW, "b", MW, FS, 
            "a", MW, "b", MW, EOS);
    }

    @Test
    public void testPunctuation()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("aba . , aba", ", .")
            .buildContext();

        assertThat(ctx).containsWord("aba")
            .withTf(2).withFieldIndices(0).withDocumentTf(0, 2);
        assertThat(ctx.allWords.image.length).isEqualTo(1);

        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "aba", MW, MW, "aba", FS, 
            MW, MW, EOS);
    }

    @Test
    public void testMoreDocuments()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc(null, "ABC abc")
            .newDoc("bcd", "BCD")
            .newDoc("ABC", "BCD")
            .newDoc("def DEF DEF", "DEF")
            .buildContext();

        assertThat(ctx).containsWord("ABC")
            .withTf(3).withFieldIndices(0, 1)
            .withExactDocumentTfs(new int [][] {{0, 2}, {2, 1}});
        assertThat(ctx).containsWord("BCD")
            .withTf(3).withFieldIndices(0, 1)
            .withExactDocumentTfs(new int [][] {{1, 2}, {2, 1}});
        assertThat(ctx).containsWord("DEF")
            .withTf(4).withFieldIndices(0, 1)
            .withDocumentTf(3, 4);
        assertThat(ctx.allWords.image.length).isEqualTo(3);

        assertThat(tokens(ctx)).onProperty("wordImage").containsExactly(
            "ABC", "ABC", DS,
            "BCD", FS, "BCD", DS,
            "ABC", FS, "BCD", DS,
            "DEF", "DEF", "DEF", FS, "DEF", EOS);
    }

    @Test
    public void testPunctuationTokenFirst()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("aa", "bb")
            .newDoc("", "bb . cc")
            .newDoc("", "aa . cc . cc")
            .buildContext();

        assertThat(ctx).containsWord("aa")
            .withTf(2).withFieldIndices(0, 1)
            .withExactDocumentTfs(new int [][] {{0, 1}, {2, 1}});
        assertThat(ctx).containsWord("bb")
            .withTf(2).withFieldIndices(1)
            .withExactDocumentTfs(new int [][] {{0, 1}, {1, 1}});
        assertThat(ctx).containsWord("cc")
            .withTf(3).withFieldIndices(1)
            .withExactDocumentTfs(new int [][] {{1, 1}, {2, 2}});
        assertThat(ctx.allWords.image.length).isEqualTo(3);
    }

    // @formatter:on
}

