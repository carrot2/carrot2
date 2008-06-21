package org.carrot2.text.preprocessing;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link PreprocessedDocumentScannerTest}.
 */
public class PreprocessedDocumentScannerTest extends PreprocessorTestBase
{
    @Test
    public void testEmpty()
    {
        createDocuments();

        final List<List<Integer>> expectedDocumentRanges = Lists.newArrayList();
        final List<List<Integer>> expectedFieldRanges = Lists.newArrayList();
        final List<List<Integer>> expectedSentenceRanges = Lists.newArrayList();

        checkAsserts(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testOneDocumentOneFieldOneSentence()
    {
        createDocuments("test");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 1);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 1);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1);

        checkAsserts(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testOneDocumentOneFieldMoreSentences()
    {
        createDocuments("test1 . test2 . test3");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 5);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 5);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1, 2, 1, 4, 1);

        checkAsserts(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testOneDocumentMoreFieldsMoreSentences()
    {
        createDocuments("test1 . test2 . ", "test3 . test4");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 8);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 4, 5, 3);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1, 2, 1, 4, 0, 5, 1,
            7, 1);

        checkAsserts(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    @Test
    public void testMoreDocumentsMoreFieldsMoreSentences()
    {
        createDocuments("test1", "test2 . test3", "test4", "test5 . test6");

        final List<List<Integer>> expectedDocumentRanges = ranges(0, 5, 6, 5);
        final List<List<Integer>> expectedFieldRanges = ranges(0, 1, 2, 3, 6, 1, 8, 3);
        final List<List<Integer>> expectedSentenceRanges = ranges(0, 1, 2, 1, 4, 1, 6, 1,
            8, 1, 10, 1);

        checkAsserts(expectedDocumentRanges, expectedFieldRanges, expectedSentenceRanges);
    }

    private List<List<Integer>> ranges(int... ranges)
    {
        final List<List<Integer>> result = Lists.newArrayList();
        for (int i = 0; i < ranges.length / 2; i++)
        {
            result.add(Lists.newArrayList(ranges[i * 2], ranges[i * 2 + 1]));
        }
        return result;
    }

    private void checkAsserts(List<List<Integer>> expectedDocumentRanges,
        List<List<Integer>> expectedFieldRanges,
        List<List<Integer>> expectedSentenceRanges)
    {
        final PreprocessingContext context = new PreprocessingContext();
        preprocessor.preprocess(context, PreprocessingTasks.TOKENIZE,
            PreprocessingTasks.CASE_NORMALIZE, PreprocessingTasks.STEMMING);

        final List<List<Integer>> actualDocumentRanges = Lists.newArrayList();
        final List<List<Integer>> actualFieldRanges = Lists.newArrayList();
        final List<List<Integer>> actualSentenceRanges = Lists.newArrayList();

        final PreprocessedDocumentScanner scanner = new PreprocessedDocumentScanner()
        {
            @Override
            protected void document(PreprocessingContext context, int start, int length)
            {
                super.document(context, start, length);
                actualDocumentRanges.add(Lists.newArrayList(start, length));
            }

            @Override
            protected void field(PreprocessingContext context, int start, int length)
            {
                super.field(context, start, length);
                actualFieldRanges.add(Lists.newArrayList(start, length));
            }

            @Override
            protected void sentence(PreprocessingContext context, int start, int length)
            {
                super.sentence(context, start, length);
                actualSentenceRanges.add(Lists.newArrayList(start, length));
            }
        };

        scanner.iterate(context);

        assertThat(actualDocumentRanges).as("documentRanges.size()").hasSize(
            documents.size());
        assertThat(actualDocumentRanges).as("documentRanges").isEqualTo(
            expectedDocumentRanges);
        assertThat(actualFieldRanges).as("fieldRanges").isEqualTo(expectedFieldRanges);
        assertThat(actualSentenceRanges).as("sentenceRanges").isEqualTo(
            expectedSentenceRanges);
    }
}
