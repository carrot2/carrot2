
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

import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link PhraseExtractor}.
 */
public class PhraseExtractorTest extends PreprocessingComponentTestBase
{
    /** Phrase extractor under tests */
    private PhraseExtractor phraseExtractor;

    /** Other preprocessing components required for the test */
    private Tokenizer tokenizer;
    private CaseNormalizer caseNormalizer;
    private LanguageModelStemmer languageModelStemmer;

    @Before
    public void setUpPreprocessingComponents()
    {
        tokenizer = new Tokenizer();
        caseNormalizer = new CaseNormalizer();
        languageModelStemmer = new LanguageModelStemmer();
        phraseExtractor = new PhraseExtractor();
    }

    @Test
    public void testEmpty()
    {
        int [][] expectedPhraseWordIndexes = new int [] [] {};

        int [] expectedPhraseTf = new int [] {};

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testEmptySnippet()
    {
        createDocuments((String) null);

        int [][] expectedPhraseWordIndexes = new int [] [] {};

        int [] expectedPhraseTf = new int [] {};

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testSinglePhrase()
    {
        createDocuments("a a", "a a");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                0, 0
            },
        };

        int [] expectedPhraseTf = new int []
        {
            2
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testTwoPhrases()
    {
        createDocuments("a b", "a b");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("a"), wordIndices.get("b")
            },
        };

        int [] expectedPhraseTf = new int []
        {
            2
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testSubphrases()
    {
        createDocuments("a b . a b", "a b c d . a b c d");

        int [] expectedPhraseTf = new int []
        {
            4, 2, 2, 2, 2, 2
        };

        check(get4TokenSubphrases(), expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testSubphrasesOnly()
    {
        createDocuments(null, "a b c d . a b c d");

        int [][] expectedPhraseWordIndexes = get4TokenSubphrases();

        int [] expectedPhraseTf = new int []
        {
            2, 2, 2, 2, 2, 2
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    private int [][] get4TokenSubphrases()
    {
        return new int [] []
        {
            new int []
            {
                wordIndices.get("a"), wordIndices.get("b")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d")
            },
        };
    }

    @Test
    public void testNestedPhrases()
    {
        createDocuments("a b c d . a b c d", "a b d . a b d");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("a"), wordIndices.get("b")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d")
            },

        };

        int [] expectedPhraseTf = new int []
        {
            4, 2, 2, 2, 2, 2, 2, 2
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testMaxPhraseLength()
    {
        createDocuments("a b c d e f g h i", "a b c d e f g h i");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("a"), wordIndices.get("b")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("d"), wordIndices.get("e")
            },

            new int []
            {
                wordIndices.get("e"), wordIndices.get("f")
            },

            new int []
            {
                wordIndices.get("f"), wordIndices.get("g")
            },

            new int []
            {
                wordIndices.get("g"), wordIndices.get("h")
            },

            new int []
            {
                wordIndices.get("h"), wordIndices.get("i")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d"), wordIndices.get("e")
            },

            new int []
            {
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f")
            },

            new int []
            {
                wordIndices.get("e"), wordIndices.get("f"), wordIndices.get("g")
            },

            new int []
            {
                wordIndices.get("f"), wordIndices.get("g"), wordIndices.get("h"),
            },

            new int []
            {
                wordIndices.get("g"), wordIndices.get("h"), wordIndices.get("i")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d"),
                wordIndices.get("e")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d"), wordIndices.get("e"),
                wordIndices.get("f")
            },

            new int []
            {
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f"),
                wordIndices.get("g")
            },

            new int []
            {
                wordIndices.get("e"), wordIndices.get("f"), wordIndices.get("g"),
                wordIndices.get("h")
            },

            new int []
            {
                wordIndices.get("f"), wordIndices.get("g"), wordIndices.get("h"),
                wordIndices.get("i")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d"), wordIndices.get("e")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d"),
                wordIndices.get("e"), wordIndices.get("f")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d"), wordIndices.get("e"),
                wordIndices.get("f"), wordIndices.get("g")
            },

            new int []
            {
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f"),
                wordIndices.get("g"), wordIndices.get("h")
            },

            new int []
            {
                wordIndices.get("e"), wordIndices.get("f"), wordIndices.get("g"),
                wordIndices.get("h"), wordIndices.get("i")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d"),
                wordIndices.get("e"), wordIndices.get("f"), wordIndices.get("g")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d"), wordIndices.get("e"),
                wordIndices.get("f"), wordIndices.get("g"), wordIndices.get("h")
            },

            new int []
            {
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f"),
                wordIndices.get("g"), wordIndices.get("h"), wordIndices.get("i")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f"),
                wordIndices.get("g")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d"),
                wordIndices.get("e"), wordIndices.get("f"), wordIndices.get("g"),
                wordIndices.get("h")
            },

            new int []
            {
                wordIndices.get("c"), wordIndices.get("d"), wordIndices.get("e"),
                wordIndices.get("f"), wordIndices.get("g"), wordIndices.get("h"),
                wordIndices.get("i")
            },

            new int []
            {
                wordIndices.get("a"), wordIndices.get("b"), wordIndices.get("c"),
                wordIndices.get("d"), wordIndices.get("e"), wordIndices.get("f"),
                wordIndices.get("g"), wordIndices.get("h")
            },

            new int []
            {
                wordIndices.get("b"), wordIndices.get("c"), wordIndices.get("d"),
                wordIndices.get("e"), wordIndices.get("f"), wordIndices.get("g"),
                wordIndices.get("h"), wordIndices.get("i")
            },
        };

        int [] expectedPhraseTf = new int []
        {
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testTwoExtendedPhrases()
    {
        createDocuments("a b c", "a b d");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("a"), wordIndices.get("b")
            },
        };

        int [] expectedPhraseTf = new int []
        {
            2
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    @Test
    public void testNoFrequentPhrases()
    {
        createDocuments("a b c", "d e f");

        int [][] expectedPhraseWordIndexes = new int [] [] {};

        int [] expectedPhraseTf = new int [] {};

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    /**
     * For efficiency reasons we don't care about phrases that ARE frequent in general,
     * but do not have at least two occurrences of one specific variant.
     */
    @Test
    public void testGeneralizedPhraseWithSingleOriginals()
    {
        createDocuments("abc bcd", "abd bce", "abe bcf", "abf bcg");

        int [][] expectedPhraseWordIndexes = new int [] [] {};

        int [] expectedPhraseTf = new int [] {};

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    /**
     * For efficiency reasons we don't care about phrases that ARE frequent in general,
     * but do not have at least two occurrences of one specific variant.
     */
    @Test
    public void testGeneralizedPhrasesWithSingleOriginals()
    {
        createDocuments("abc bcd", "abd bce", "abe bcf", "abf bcg", "efg fgh", "efh fgi",
            "efi fgj", "efj fgk");

        int [][] expectedPhraseWordIndexes = new int [] [] {};

        int [] expectedPhraseTf = new int [] {};

        check(expectedPhraseWordIndexes, expectedPhraseTf,
            createTrivialTfByDocument(expectedPhraseTf));
    }

    /**
     * For efficiency reasons we don't care about phrases that ARE frequent in general,
     * but do not have at least two occurrences of one specific variant.
     */
    @Test
    public void testComposition()
    {
        createDocuments("abc bcd cde", "abc bcd cdf", "abc bcd cdg", "abc bcd cdh");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("abc"), wordIndices.get("bcd")
            },

        };

        int [] expectedPhraseTf = new int []
        {
            4
        };

        int [][] expectedPhraseTfByDocument = new int [] []
        {
            {
                1, 2, 0, 2
            }
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf, expectedPhraseTfByDocument);
    }

    @Test
    public void testGeneralizedPhraseWithMultipleOriginals()
    {
        createDocuments("abd bce", "abe bcf", "abd bce", "abe bcf . abe bcf",
            "abc bcd . abc bcd . abc bcd . abc bcd");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("abc"), wordIndices.get("bcd")
            }
        };

        int [] expectedPhraseTf = new int []
        {
            9
        };

        int [][] expectedPhraseTfByDocument = new int [] []
        {
            {
                1, 3, 0, 2, 2, 4
            }
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf, expectedPhraseTfByDocument);
    }

    @Test
    public void testGeneralizedPhraseFrequencyAggregation()
    {
        createDocuments("abc bcd", "abc bcd", "abd cde",
            "abd cde . abe bcd . abe bcd . abe bcd");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("abe"), wordIndices.get("bcd")
            },

            new int []
            {
                wordIndices.get("abd"), wordIndices.get("cde")
            }
        };

        int [] expectedPhraseTf = new int []
        {
            5, 2
        };

        int [][] expectedPhraseTfByDocument = new int [] []
        {
            {
                1, 3, 0, 2
            },

            {
                1, 2
            }
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf, expectedPhraseTfByDocument);
    }

    @Test
    public void testTf2Phrase()
    {
        createDocuments("abc bcd", "", "abc bcd cde", "", "abc bcd cde", "",
            "abc bcd cde", "");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("abc"), wordIndices.get("bcd")
            },

            new int []
            {
                wordIndices.get("bcd"), wordIndices.get("cde")
            },

            new int []
            {
                wordIndices.get("abc"), wordIndices.get("bcd"), wordIndices.get("cde")
            }

        };

        int [] expectedPhraseTf = new int []
        {
            4, 3, 3
        };

        int [][] expectedPhraseTfByDocument = new int [] []
        {
            {
                3, 1, 1, 1, 0, 1, 2, 1
            },

            {
                3, 1, 1, 1, 2, 1
            },

            {
                3, 1, 1, 1, 2, 1
            }
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf, expectedPhraseTfByDocument);
    }

    @Test
    public void testOverlappingGeneralizedPhrase()
    {
        createDocuments("abc bcd cde def", "abd bce", "abd bce cde deg",
            "cdf deg efg . abc fgh cde def");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                wordIndices.get("abd"), wordIndices.get("bce")
            },

            new int []
            {
                wordIndices.get("cde"), wordIndices.get("def")
            }
        };

        int [] expectedPhraseTf = new int []
        {
            2, 2
        };

        int [][] expectedPhraseTfByDocument = new int [] []
        {
            {
                1, 1, 0, 1
            },

            {
                1, 1, 0, 1
            }
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf, expectedPhraseTfByDocument);
    }

    @Test
    public void testDfThreshold()
    {
        phraseExtractor.dfThreshold = 2;
        createDocuments("a a", "a a", "a a . b b . c c", "a a . b b", "a a", "a a . c c");

        int [][] expectedPhraseWordIndexes = new int [] []
        {
            new int []
            {
                0, 0
            },

            new int []
            {
                2, 2
            },
        };

        int [] expectedPhraseTf = new int []
        {
            6, 2
        };

        int [][] expectedPhraseTfByDocument = new int [] []
        {
            {
                1, 2, 0, 2, 2, 2
            },

            {
                1, 1, 2, 1
            }
        };

        check(expectedPhraseWordIndexes, expectedPhraseTf, expectedPhraseTfByDocument);
    }

    private void check(int [][] expectedPhraseWordIndexes, int [] expectedPhraseTf,
        int [][] expectedPhraseTfByDocument)
    {
        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        phraseExtractor.extractPhrases(context);

        assertThat(context.allPhrases.wordIndices).as("allPhrases.wordIndices")
            .isEqualTo(expectedPhraseWordIndexes);
        assertThat(context.allPhrases.tf).as("allPhrases.tf").isEqualTo(expectedPhraseTf);
        assertThat(context.allPhrases.tfByDocument).as("allPhrases.tfByDocument")
            .isEqualTo(expectedPhraseTfByDocument);

    }

    private int [][] createTrivialTfByDocument(int [] phraseTf)
    {
        int [][] result = new int [phraseTf.length] [];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = new int []
            {
                0, phraseTf[i]
            };
        }

        return result;
    }

    @Override
    protected ILanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
