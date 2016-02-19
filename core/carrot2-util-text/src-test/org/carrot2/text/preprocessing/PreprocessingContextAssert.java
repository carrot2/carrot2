
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.carrot2.text.analysis.TokenTypeUtils;
import org.carrot2.text.preprocessing.PreprocessingContext.AllPhrases;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.util.CharArrayComparators;
import org.carrot2.util.IntMapUtils;
import org.fest.assertions.Assertions;
import org.fest.util.Strings;

import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.procedures.IntIntProcedure;

import org.carrot2.shaded.guava.common.base.MoreObjects;
import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Fest-style assertions on the content of {@link PreprocessingContext}.
 */
class PreprocessingContextAssert
{
    /** missing word constant. */
    public final static String MW = "<MW>";
    /** document separator constant. */
    public final static String DS = "<DS>";
    /** field separator constant. */
    public final static String FS = "<FS>";
    /** end of stream constant. */
    public final static String EOS = "<EOS>";

    final PreprocessingContext context;

    final class PreprocessingContextPhraseAssert
    {
        private int phraseIndex;
        
        PreprocessingContextPhraseAssert(int index)
        {
            assert index >= 0;
            this.phraseIndex = index;
        }

        public PreprocessingContextPhraseAssert withDocumentTf(int documentIndex, int expectedTf)
        {
            int [] byDocTf = context.allPhrases.tfByDocument[phraseIndex];
            for (int i = 0; i < byDocTf.length; i += 2)
            {
                if (byDocTf[i] == documentIndex) {
                    Assertions.assertThat(expectedTf).isEqualTo(byDocTf[i + 1]);
                    return this;
                }
            }

            org.junit.Assert.fail("No document " + documentIndex + " for this phrase: "
                + context.allPhrases.getPhrase(phraseIndex) + "\n" + context.allPhrases);
            return this;
        }

        /** 
         * Asserts exact mapping of document-tf (the number of mappings and their value, regardless
         * of their order). 
         */
        public PreprocessingContextPhraseAssert withExactDocumentTfs(int [][] docTfPairs)
        {
            for (int [] docTf : docTfPairs)
            {
                Assertions.assertThat(docTf.length).isEqualTo(2);
                withDocumentTf(docTf[0], docTf[1]);
            }

            Assertions.assertThat(context.allPhrases.tfByDocument[phraseIndex].length / 2)
                .describedAs("tfByDocument array size for phrase: '" + context.allPhrases.getPhrase(phraseIndex) + "'")
                .isEqualTo(docTfPairs.length);

            return this;
        }

        public PreprocessingContextPhraseAssert withTf(int expectedTf)
        {
            Assertions.assertThat(context.allPhrases.tf[phraseIndex])
                .describedAs("tf different for phrase '" + context.allPhrases.getPhrase(phraseIndex) + "'")
                .isEqualTo(expectedTf);
            return this;
        }
    }

    PreprocessingContextAssert(PreprocessingContext context)
    {
        this.context = context;
    }

    public List<String> wordImages()
    {
        Assertions.assertThat(context.allWords.image)
            .describedAs("the context's allWords is not properly initialized.").isNotNull();

        List<String> result = Lists.newArrayList();
        for (int i = context.allWords.image.length; --i >= 0;)
        {
            result.add(new String(context.allWords.image[i]));
        }
        Collections.shuffle(result);
        return result;
    }

    /**
     * Return a list of random-ordered, space-separated phrase images.
     */
    public List<String> phraseImages()
    {
        Assertions.assertThat(context.allPhrases.wordIndices)
            .describedAs("the context's allPhrases is not properly initialized.").isNotNull();

        List<String> result = Lists.newArrayList();
        for (int i = context.allPhrases.wordIndices.length; --i >= 0;)
        {
            result.add(context.allPhrases.getPhrase(i).toString());
        }
        Collections.shuffle(result);
        return result;
    }

    /** Assert the context contains a phrase consisting of these exact images. */
    public PreprocessingContextPhraseAssert containsPhrase(List<String> processedTermImages)
    {
        return containsPhrase(processedTermImages.toArray(
            new String [processedTermImages.size()]));
    }

    /** Assert the context contains a phrase consisting of these exact images. */
    public PreprocessingContextPhraseAssert containsPhrase(String... processedTermImages)
    {
        Assertions.assertThat(processedTermImages).isNotEmpty();
        Assertions.assertThat(context.allPhrases.wordIndices)
            .describedAs("the context's allPhrases is not properly initialized.").isNotNull();

        // Naive scan over the set of extracted phrases.
        final String phraseImage = Strings.join(processedTermImages).with(" ");
        int foundAt = -1;
        for (int i = context.allPhrases.wordIndices.length; --i >= 0;)
        {
            if (phraseImage.equals(context.allPhrases.getPhrase(i).toString()))
            {
                if (foundAt >= 0) org.junit.Assert.fail("More than one phrase with an identical image '"
                    + phraseImage + "'?\n\n" + context.allPhrases);
                foundAt = i;
            }
        }

        if (foundAt < 0)
            org.junit.Assert.fail("No phrase '" + phraseImage + "' in allPhrases:\n" + context.allPhrases);

        return new PreprocessingContextPhraseAssert(foundAt);
    }

    /**
     * Looks up a phrase that matches the list of stemmed images. Stem images 
     * are preprocessed in this method and underscore "_" 
     * character is removed (clearer test input in conjunction with {@link TestStemmerFactory}).
     */
    public PreprocessingContextPhraseAssert containsPhraseStemmedAs(String... stemImages)
    {
        Assertions.assertThat(stemImages).isNotEmpty();
        Assertions.assertThat(context.allPhrases.wordIndices)
            .describedAs("the context's allPhrases is not properly initialized.").isNotNull();

        for (int i = 0; i < stemImages.length; i++)
            stemImages[i] = stemImages[i].replaceAll("_", "");

        // Naive scan over the set of extracted phrases.
        Comparator<char[]> comp = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR;
        int foundAt = -1;
nextPhrase:
        for (int i = context.allPhrases.wordIndices.length; --i >= 0;)
        {
            int [] wordIdxs = context.allPhrases.wordIndices[i];

            if (wordIdxs.length == stemImages.length)
            {
                for (int j = 0; j < wordIdxs.length; j++)
                {
                    if (comp.compare(
                        context.allStems.image[context.allWords.stemIndex[wordIdxs[j]]],
                        stemImages[j].toCharArray()) != 0)
                    {
                        continue nextPhrase;
                    }
                }

                if (foundAt >= 0)
                {
                    org.junit.Assert.fail("More than one phrase corresponds to stem sequence '" + 
                        Arrays.toString(stemImages) + "':\n" + context.allPhrases);
                }
                foundAt = i;
            }
        }

        if (foundAt < 0)
            org.junit.Assert.fail("No phrase corresponding to stem sequence '" + 
                Arrays.toString(stemImages) + "' in allPhrases:\n" + context.allPhrases);

        return new PreprocessingContextPhraseAssert(foundAt);
    }

    public static PreprocessingContextAssert assertThat(PreprocessingContext context)
    {
        return new PreprocessingContextAssert(context);
    }
    
    public static List<TokenEntry> tokens(PreprocessingContext context)
    {
        return new PreprocessingContextAssert(context).tokens();
    }

    final class StemAssert
    {
        private final int stemIndex;
        private final String stemImage;

        public StemAssert(int stemIndex)
        {
            this.stemIndex = stemIndex;
            this.stemImage = new String(context.allStems.image[stemIndex]);
        }

        public StemAssert withTf(int expectedTf)
        {
            Assertions.assertThat(context.allStems.tf[stemIndex])
                .describedAs("tf different for stem " + stemImage)
                    .isEqualTo(expectedTf);
            return this;
        }

        public StemAssert withDocumentTf(int documentIndex, int expectedTf)
        {
            int [] byDocTf = context.allStems.tfByDocument[stemIndex];
            for (int i = 0; i < byDocTf.length; i += 2)
            {
                if (byDocTf[i] == documentIndex) {
                    Assertions.assertThat(expectedTf).isEqualTo(byDocTf[i + 1]);
                    return this;
                }
            }

            org.junit.Assert.fail("No document " + documentIndex + " for this stem: "
                + stemImage + "\n" + context.allPhrases);
            return this;
        }

        public StemAssert withExactDocumentTfs(int [][] docTfPairs)
        {
            for (int [] docTf : docTfPairs)
            {
                Assertions.assertThat(docTf.length).isEqualTo(2);
                withDocumentTf(docTf[0], docTf[1]);
            }

            Assertions.assertThat(context.allStems.tfByDocument[stemIndex].length / 2)
                .describedAs("tfByDocument array size for stem: '" + stemImage + "'")
                .isEqualTo(docTfPairs.length);

            return this;
        }

        public StemAssert withFieldIndices(int... expectedIndices)
        {
            int [] indices = PreprocessingContext.toFieldIndexes(context.allStems.fieldIndices[stemIndex]);
            Assertions.assertThat(expectedIndices).as("field indices of stem '" + stemImage + "'")
                .isEqualTo(indices);
            return this;
        }
    }

    StemAssert constainsStem(String stemImage)
    {
        Assertions.assertThat(stemImage).isNotEmpty();
        Assertions.assertThat(context.allStems.image)
            .describedAs("the context's allStems is not properly initialized.").isNotNull();

        Comparator<char[]> comp = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR;
        int found = -1;
        for (int i = 0; i < context.allStems.image.length; i++)
        {
            if (comp.compare(context.allStems.image[i], stemImage.toCharArray()) == 0)
            {
                if (found >= 0)
                    org.junit.Assert.fail("Duplicate stem with image '" + stemImage + "' in stems:\n"
                        + context.allStems);
                found = i;
            }
        }
        
        if (found == -1) 
            org.junit.Assert.fail("No stem with image '" + stemImage + "' in stems:\n"
                + context.allStems);
        
        return new StemAssert(found);
    }

    final class WordAssert
    {
        private final int wordIndex;
        private final String wordImage;

        public WordAssert(int wordIndex)
        {
            this.wordIndex = wordIndex;
            this.wordImage = new String(context.allWords.image[wordIndex]);
        }

        public WordAssert withTf(int expectedTf)
        {
            Assertions.assertThat(context.allWords.tf[wordIndex])
                .describedAs("tf different for word " + wordImage)
                    .isEqualTo(expectedTf);
            return this;
        }

        public WordAssert withDocumentTf(int documentIndex, int expectedTf)
        {
            int [] byDocTf = context.allWords.tfByDocument[wordIndex];
            for (int i = 0; i < byDocTf.length; i += 2)
            {
                if (byDocTf[i] == documentIndex) {
                    Assertions.assertThat(expectedTf).isEqualTo(byDocTf[i + 1]);
                    return this;
                }
            }

            org.junit.Assert.fail("No document " + documentIndex + " for this word: "
                + wordImage + "\n" + context.allPhrases);
            return this;
        }

        public WordAssert withExactDocumentTfs(int [][] docTfPairs)
        {
            for (int [] docTf : docTfPairs)
            {
                Assertions.assertThat(docTf.length).isEqualTo(2);
                withDocumentTf(docTf[0], docTf[1]);
            }

            Assertions.assertThat(context.allWords.tfByDocument[wordIndex].length / 2)
                .describedAs("tfByDocument array size for word: '" + wordImage + "'")
                .isEqualTo(docTfPairs.length);

            return this;
        }

        public WordAssert withFieldIndices(int... expectedIndices)
        {
            int [] indices = PreprocessingContext.toFieldIndexes(context.allWords.fieldIndices[wordIndex]);
            Assertions.assertThat(expectedIndices).as("field indices of word '" + wordImage + "'")
                .isEqualTo(indices);
            return this;
        }

        /** type masked to token type only. */
        public void withTokenType(int tokenType)
        {
            Assertions.assertThat(TokenTypeUtils.maskType(context.allWords.type[wordIndex]))
                .as("token type (masked) of word '" + wordImage + "'")
                .isEqualTo(tokenType);
        }

        /** raw value of token type field. */
        public void withExactTokenType(int tokenType)
        {
            Assertions.assertThat(tokenType)
                .as("token type of word '" + wordImage + "'")
                .isEqualTo(context.allWords.type[wordIndex]);
        }
    }

    public WordAssert containsWord(String wordImage)
    {
        Assertions.assertThat(wordImage).isNotEmpty();
        Assertions.assertThat(context.allWords.image)
            .describedAs("the context's allWords is not properly initialized.").isNotNull();

        Comparator<char[]> comp = CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR;
        int found = -1;
        for (int i = 0; i < context.allWords.image.length; i++)
        {
            if (comp.compare(context.allWords.image[i], wordImage.toCharArray()) == 0)
            {
                if (found >= 0)
                    org.junit.Assert.fail("Duplicate word with image '" + wordImage + "' in words:\n"
                        + context.allWords);
                found = i;
            }
        }
        
        if (found == -1) 
            org.junit.Assert.fail("No word with image '" + wordImage + "' in words:\n"
                + context.allStems);

        return new WordAssert(found);
    }

    public final class TokenEntry
    {
        final int tokenIndex;
        
        TokenEntry(int tokenIndex)
        {
            this.tokenIndex = tokenIndex;
        }
        
        public String getTokenImage() 
        { 
            if (context.allTokens.image[tokenIndex] == null)
                return null;
            return new String(context.allTokens.image[tokenIndex]);
        }

        public String getWordImage() 
        { 
            if (context.allTokens.image[tokenIndex] == null)
            {
                if (TokenTypeUtils.isDocumentSeparator(context.allTokens.type[tokenIndex]))
                    return DS;
                if (TokenTypeUtils.isFieldSeparator(context.allTokens.type[tokenIndex]))
                    return FS;
                if (TokenTypeUtils.isTerminator(context.allTokens.type[tokenIndex]))
                    return EOS;
                throw new RuntimeException();
            }
            int wordIndex = context.allTokens.wordIndex[tokenIndex];
            if (wordIndex < 0) 
                return MW;
            return new String(context.allWords.image[wordIndex]);
        }

        public String getStemImage() 
        {
            if (getTokenImage() == null)
                return null;

            int wordIndex = context.allTokens.wordIndex[tokenIndex];
            int stemIndex = context.allWords.stemIndex[wordIndex];
            return new String(context.allStems.image[stemIndex]);
        }

        public Integer getWordType()
        {
            if (getTokenImage() == null)
                return null;

            return (int) context.allWords.type[context.allTokens.wordIndex[tokenIndex]];
        }
    }

    public List<TokenEntry> tokens()
    {
        List<TokenEntry> result = Lists.newArrayList();
        for (int i = 0; i < context.allTokens.image.length; i++)
            result.add(new TokenEntry(i));
        return result;
    }

    final class TokenAssert
    {
        private final int tokenIndex;
        private final String tokenImage;

        public TokenAssert(int tokenIndex)
        {
            this.tokenIndex = tokenIndex;
            this.tokenImage = tokenIndex + ":"
                + (context.allTokens.image[tokenIndex] != null ? new String(context.allTokens.image[tokenIndex]) : "<null>");
        }

        /** type masked to token type only. */
        public TokenAssert hasTokenType(int tokenType)
        {
            Assertions.assertThat(tokenType)
                .as("token type (masked) of token '" + tokenImage + "'")
                .isEqualTo(TokenTypeUtils.maskType(context.allTokens.type[tokenIndex]));
            return this;
        }

        /** raw value of token type field. */
        public TokenAssert hasExactTokenType(int tokenType)
        {
            Assertions.assertThat(tokenType)
                .as("token type of token '" + tokenImage + "'")
                .isEqualTo(context.allTokens.type[tokenIndex]);
            return this;
        }

        public TokenAssert hasImage(String image)
        {
            Assertions.assertThat(
                CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR.compare(
                    image != null ? image.toCharArray() : null,
                    context.allTokens.image[tokenIndex]) == 0)
                    .as("token image equality: " + image + " vs. " + 
                    new String(
                        MoreObjects.firstNonNull(context.allTokens.image[tokenIndex], "<null>".toCharArray())))
                    .isTrue();
            return this;
        }

        public TokenAssert hasDocIndex(int expectedDocIndex)
        {
            Assertions.assertThat(context.allTokens.documentIndex[tokenIndex])
                .as("documentIndex")
                .isEqualTo(expectedDocIndex);
            return this;
        }

        public TokenAssert hasFieldIndex(int expectedFieldIndex)
        {
            Assertions.assertThat(context.allTokens.fieldIndex[tokenIndex])
                .as("fieldIndex")
                .isEqualTo((byte) expectedFieldIndex);
            return this;
        }
    }
    
    public TokenAssert tokenAt(int tokenIndex)
    {
        return new TokenAssert(tokenIndex);
    }


    /**
     * Make sure term frequencies and 
     */
    public void phraseTfsCorrect()
    {
        // for each discovered phrase, do manual count and verify if tf and tfByDocument are correct.
        AllPhrases allPhrases = context.allPhrases;
        for (int index = 0; index < allPhrases.size(); index++)
        {
            IntIntHashMap realTfByDocuments = countManually(context, allPhrases.wordIndices[index]);
            final int realTf = realTfByDocuments.forEach(new IntIntProcedure()
            {
                int tf;
                public void apply(int key, int value)
                {
                    tf += value;
                }
            }).tf;

            Assertions.assertThat(allPhrases.tf[index]).as("Phrase: " + allPhrases.getPhrase(index))
                .isEqualTo(realTf);
            
            // Phrase extractor does not sort the byDocumentTf, so we need to addAllFromFlattened
            // to a map and then flatten with sorting.
            Assertions
                .assertThat(
                    IntMapUtils.flattenSortedByKey(IntMapUtils.addAllFromFlattened(
                        new IntIntHashMap(), allPhrases.tfByDocument[index])))
                .as("Phrase: " + allPhrases.getPhrase(index))
                .isEqualTo(IntMapUtils.flattenSortedByKey(realTfByDocuments));
        }
    }

    /**
     * Manually and naively count doc->tf for the given word sequence.
     */
    private IntIntHashMap countManually(PreprocessingContext context, int [] phraseWordIndices)
    {
        IntIntHashMap tfByDoc = new IntIntHashMap();
        AllTokens allTokens = context.allTokens;
outer:
        for (int i = allTokens.wordIndex.length - phraseWordIndices.length; --i >=0 ;)
        {
            for (int j = 0; j < phraseWordIndices.length; j++)
            {
                int wordInPhrase = phraseWordIndices[j];
                int wordInTokens = allTokens.wordIndex[i + j];
                if (wordInPhrase != wordInTokens)
                    continue outer;
            }
            tfByDoc.putOrAdd(allTokens.documentIndex[i], 1, 1);
        }
        return tfByDoc;
    }
}
