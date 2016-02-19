
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

import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.text.linguistic.DefaultStemmerFactory;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link DocumentAssigner}.
 */
public class DocumentAssignerTest extends LabelFilterTestBase
{
    /** Document assigner under tests */
    private DocumentAssigner documentAssigner;

    @Before
    public void setUpDocumentAssigner()
    {
        documentAssigner = new DocumentAssigner();
    }

    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.stopWordLabelFilter.enabled = true;
        filterProcessor.completeLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [][] expectedDocumentIndices = new int [] [] {};
        check(expectedDocumentIndices, -1);
    }

    @Test
    public void testSingleWordLabels()
    {
        createDocuments("coal is", "coal is", "mining", "mining");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0
            },

            new int []
            {
                1
            }
        };

        documentAssigner.minClusterSize = 1;
        check(expectedDocumentIndices, -1);
    }

    @Test
    public void testStemmedSingleWordLabelConflation()
    {
        createDocuments("cat", "cat", "cat", "cat", "cats", "cats", "cats", "cats");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2, 3
            }
        };

        documentAssigner.minClusterSize = 1;
        check(expectedDocumentIndices, -1);
    }

    @Test
    public void testStemmedPhraseLabelConflation()
    {
        createDocuments("cat horse", "cat horse", "cats horse", "cats horse",
            "cat horses", "cat horses", "cats horses", "cats horses");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2, 3
            },

            new int []
            {
                0, 1, 2, 3
            },

            new int []
            {
                0, 1, 2, 3
            }
        };

        documentAssigner.minClusterSize = 1;
        check(expectedDocumentIndices, 2);
    }

    @Test
    public void testMinClusterSize()
    {
        createDocuments("test coal", "test coal", "coal test . mining",
            "coal test . mining");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            }
        };

        documentAssigner.minClusterSize = 2;
        check(expectedDocumentIndices, 2);
    }

    @Test
    public void testPhraseLabelsExactMatch()
    {
        createDocuments("data is cool", "data is cool", "data is cool", "data is cool",
            "data cool", "data cool");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1
            }
        };

        documentAssigner.exactPhraseAssignment = true;
        documentAssigner.minClusterSize = 2;
        check(expectedDocumentIndices, 0);
    }

    @Test
    public void testPhraseLabelsNonExactMatch()
    {
        createDocuments("data is cool", "data is cool", "data is cool", "data is cool",
            "data cool", "data cool");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1, 2
            }
        };

        documentAssigner.exactPhraseAssignment = false;
        documentAssigner.minClusterSize = 2;
        check(expectedDocumentIndices, 0);
    }

    @Test
    public void testPhraseLabelsNonExactMatchOtherLabels()
    {
        createDocuments("aa bb cc dd", "aa bb cc dd", "dd . cc . bb . aa",
            "dd . cc . bb . aa", "cc . bb . aa", "aa . bb . cc");

        final int [][] expectedDocumentIndices = new int [] []
        {
            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1, 2
            },

            new int []
            {
                0, 1
            },

            new int []
            {
                0, 1
            }
        };

        check(expectedDocumentIndices, 4);
    }

    private void check(int [][] expectedDocumentIndices, int expectedFirstPhraseIndex)
    {
        runPreprocessing();
        documentAssigner.assign(context);

        assertThat(context.allLabels.firstPhraseIndex).as("allLabels.firstPhraseIndex")
            .isEqualTo(expectedFirstPhraseIndex);
        assertThat(context.allLabels.documentIndices).as("allLabels.documentIndices")
            .hasSize(expectedDocumentIndices.length);
        for (int i = 0; i < expectedDocumentIndices.length; i++)
        {
            assertThat(context.allLabels.documentIndices[i].asIntLookupContainer().toArray()).as(
                "allLabels.documentIndices[" + i + "]").isEqualTo(
                expectedDocumentIndices[i]);
        }
    }

    @Override
    protected ILexicalDataFactory createLexicalDataFactory()
    {
        return new DefaultLexicalDataFactory();
    }

    @Override
    protected IStemmerFactory createStemmerFactory()
    {
        return new DefaultStemmerFactory();
    }
}
