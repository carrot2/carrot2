
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.fuzzyAnts;



/**
 * This interface contains some constants that are used in several classes.
 *
 * @author Steven Schockaert
 */
interface Constants
{
    public final int BINARY = 0;
    public final int TF = 1;
    public final int TFIDF = 2;
    public final double PLOAD = 0.9;
    public final double PDROP = 0.003;
    public final double M = 1.3;

    public static final FuzzyNumber VERYLOW = new FuzzyNumber(101, 0, 0, 25);
    public static final FuzzyNumber LOW = new FuzzyNumber(101, 0, 25, 50);
    public static final FuzzyNumber MEDIUM = new FuzzyNumber(101, 25, 50, 75);
    public static final FuzzyNumber HIGH = new FuzzyNumber(101, 50, 75, 100);
    public static final FuzzyNumber VERYHIGH = new FuzzyNumber(101, 75, 100, 100);

    //stimulus
    public static final FuzzyNumber VERYVERYLOWSTIMULUS = new FuzzyNumber(100, 0, 0, 12);
    public static final FuzzyNumber VERYLOWSTIMULUS = new FuzzyNumber(100, 0, 12, 24);
    public static final FuzzyNumber LOWSTIMULUS = new FuzzyNumber(100, 12, 24, 36);
    public static final FuzzyNumber RATHERLOWSTIMULUS = new FuzzyNumber(100, 24, 36, 50);
    public static final FuzzyNumber MEDIUMSTIMULUS = new FuzzyNumber(100, 36, 50, 64);
    public static final FuzzyNumber RATHERHIGHSTIMULUS = new FuzzyNumber(100, 50, 64, 76);
    public static final FuzzyNumber HIGHSTIMULUS = new FuzzyNumber(100, 64, 76, 88);
    public static final FuzzyNumber VERYHIGHSTIMULUS = new FuzzyNumber(100, 76, 88, 99);
    public static final FuzzyNumber VERYVERYHIGHSTIMULUS = new FuzzyNumber(100, 88, 99, 99);
}
