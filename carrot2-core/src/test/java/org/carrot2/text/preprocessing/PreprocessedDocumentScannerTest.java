
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test cases for {@link PreprocessedDocumentScannerTest}.
 */
public class PreprocessedDocumentScannerTest extends PreprocessingComponentTestBase
{
    @Test
    public void testEmpty()
    {
        createDocuments();

        final List<List<Integer>> expectedDocumentRanges = new ArrayList<>();
        final List<List<Integer>> expectedFieldRanges = new ArrayList<>();
        final List<List<Integer>> expectedSentenceRanges = new ArrayList<>();

        check(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testOneDocumentOneFieldOneSentence()
    {
        createDocuments("test");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 1);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 1);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1);

        check(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testOneDocumentOneFieldMoreSentences()
    {
        createDocuments("test1 . test2 . test3");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 5);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 5);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1, 2, 1, 4, 1);

        check(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testOneDocumentMoreFieldsMoreSentences()
    {
        createDocuments("test1 . test2 . ", "test3 . test4");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 8);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 4, 5, 3);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1, 2, 1, 4, 0, 5, 1,
            7, 1);

        check(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testMoreDocumentsMoreFieldsMoreSentences()
    {
        createDocuments("test1", "test2 . test3", "test4", "test5 . test6");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 5, 6, 5);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 1, 2, 3, 6, 1, 8, 3);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1, 2, 1, 4, 1, 6, 1,
            8, 1, 10, 1);

        check(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    private List<List<Integer>> ranges(int... ranges)
    {
        final List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < ranges.length / 2; i++)
        {
            result.add(Arrays.asList(ranges[i * 2], ranges[i * 2 + 1]));
        }
        return result;
    }

    private void check(List<List<Integer>> expectedDocumentRanges,
        List<List<Integer>> expectedFieldRanges,
        List<List<Integer>> expectedSentenceRanges)
    {
        final Tokenizer tokenizer = new Tokenizer();
        final CaseNormalizer caseNormalizer = new CaseNormalizer();
        final LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();

        tokenizer.tokenize(context, documents.iterator());
        caseNormalizer.normalize(context, 1);
        languageModelStemmer.stem(context, query);

        final List<List<Integer>> actualDocumentRanges = new ArrayList<>();
        final List<List<Integer>> actualFieldRanges = new ArrayList<>();
        final List<List<Integer>> actualSentenceRanges = new ArrayList<>();

        final PreprocessedDocumentScanner scanner = new PreprocessedDocumentScanner()
        {
            @Override
            protected void document(PreprocessingContext context, int start, int length)
            {
                super.document(context, start, length);
                actualDocumentRanges.add(Arrays.asList(start, length));
            }

            @Override
            protected void field(PreprocessingContext context, int start, int length)
            {
                super.field(context, start, length);
                actualFieldRanges.add(Arrays.asList(start, length));
            }

            @Override
            protected void sentence(PreprocessingContext context, int start, int length)
            {
                super.sentence(context, start, length);
                actualSentenceRanges.add(Arrays.asList(start, length));
            }
        };

        scanner.iterate(context);

        assertThat(actualDocumentRanges).as("documentRanges.size()").hasSize(
            context.documentCount);
        assertThat(actualDocumentRanges).as("documentRanges").isEqualTo(
            expectedDocumentRanges);
        assertThat(actualFieldRanges).as("fieldRanges").isEqualTo(expectedFieldRanges);
        assertThat(actualSentenceRanges).as("sentenceRanges").isEqualTo(
            expectedSentenceRanges);
    }
}
