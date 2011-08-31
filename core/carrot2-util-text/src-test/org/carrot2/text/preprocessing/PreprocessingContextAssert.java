package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import java.util.*;

import org.carrot2.text.util.CharArrayComparators;
import org.fest.util.Strings;

import com.google.common.collect.Lists;

/**
 * Fest-style assertions on the content of {@link PreprocessingContext}.
 */
class PreprocessingContextAssert
{
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
                    assertThat(expectedTf).isEqualTo(byDocTf[i + 1]);
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
        public void withExactDocumentTfs(int [][] docTfPairs)
        {
            for (int [] docTf : docTfPairs)
            {
                assertThat(docTf.length).isEqualTo(2);
                withDocumentTf(docTf[0], docTf[1]);
            }

            assertThat(context.allPhrases.tfByDocument[phraseIndex].length / 2)
                .describedAs("tfByDocument array size for phrase: '" + context.allPhrases.getPhrase(phraseIndex) + "'")
                .isEqualTo(docTfPairs.length);
        }

        public PreprocessingContextPhraseAssert withTf(int expectedTf)
        {
            assertThat(context.allPhrases.tf[phraseIndex])
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
        assertThat(context.allWords.image)
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
        assertThat(context.allPhrases.wordIndices)
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
        assertThat(processedTermImages).isNotEmpty();
        assertThat(context.allPhrases.wordIndices)
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
        assertThat(stemImages).isNotEmpty();
        assertThat(context.allPhrases.wordIndices)
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

    public static PreprocessingContextAssert on(PreprocessingContext context)
    {
        return new PreprocessingContextAssert(context);
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
            assertThat(context.allStems.tf[stemIndex])
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
                    assertThat(expectedTf).isEqualTo(byDocTf[i + 1]);
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
                assertThat(docTf.length).isEqualTo(2);
                withDocumentTf(docTf[0], docTf[1]);
            }

            assertThat(context.allStems.tfByDocument[stemIndex].length / 2)
                .describedAs("tfByDocument array size for stem: '" + stemImage + "'")
                .isEqualTo(docTfPairs.length);

            return this;
        }

        public StemAssert withFieldIndices(int... expectedIndices)
        {
            int [] indices = PreprocessingContext.toFieldIndexes(context.allStems.fieldIndices[stemIndex]);
            assertThat(expectedIndices).as("field indices of stem '" + stemImage + "'")
                .isEqualTo(indices);
            return this;
        }
    }
    
    StemAssert constainsStem(String stemImage)
    {
        assertThat(stemImage).isNotEmpty();
        assertThat(context.allStems.image)
            .describedAs("the context's allSems is not properly initialized.").isNotNull();

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
}