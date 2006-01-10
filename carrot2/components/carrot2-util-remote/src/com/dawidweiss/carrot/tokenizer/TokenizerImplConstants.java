
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.tokenizer;


public interface TokenizerImplConstants
{
    int EOF = 0;
    int URL = 1;
    int WWWADDR = 2;
    int URLPATH = 3;
    int TERM = 4;
    int HYPHTERM = 5;
    int PERSON = 6;
    int ACRONYM = 7;
    int EMAIL = 8;
    int SENTENCEMARKER = 9;
    int PUNCTUATION = 10;
    int NUMERIC = 11;
    int SYMBOL = 12;
    int WHSPACE = 13;
    int LETTERSEQ = 14;
    int LETTER = 15;
    int DIGIT = 16;
    int SPACES = 17;
    int NOISE = 18;
    int DEFAULT = 0;
    String [] tokenImage = 
    {
        "<EOF>", "<URL>", "<WWWADDR>", "<URLPATH>", "<TERM>", "<HYPHTERM>", "<PERSON>", "<ACRONYM>",
        "<EMAIL>", "<SENTENCEMARKER>", "<PUNCTUATION>", "<NUMERIC>", "<SYMBOL>", "<WHSPACE>",
        "<LETTERSEQ>", "<LETTER>", "<DIGIT>", "<SPACES>", "<NOISE>",
    };
}
