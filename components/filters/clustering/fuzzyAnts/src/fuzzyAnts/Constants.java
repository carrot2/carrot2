

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package fuzzyAnts;



/**
 * This interface contains some constants that are used in several classes.
 *
 * @author Steven Schockaert
 */
interface Constants
{
    public final int BINAIR = 0;
    public final int TF = 1;
    public final int TFIDF = 2;
    public final int BT = 3;
    public final double PLOAD = 0.9;
    public final double PDROP = 0.003;
    public final double M = 1.3;
    public static final FuzzyNumber HEELKLEIN = new FuzzyNumber(101, 0, 0, 25);
    public static final FuzzyNumber KLEIN = new FuzzyNumber(101, 0, 25, 50);
    public static final FuzzyNumber MATIG = new FuzzyNumber(101, 25, 50, 75);
    public static final FuzzyNumber GROOT = new FuzzyNumber(101, 50, 75, 100);
    public static final FuzzyNumber HEELGROOT = new FuzzyNumber(101, 75, 100, 100);

    //nut
    public static final FuzzyNumber HEELHEELKLEINNUT = new FuzzyNumber(100, 0, 0, 12);
    public static final FuzzyNumber HEELKLEINNUT = new FuzzyNumber(100, 0, 12, 24);
    public static final FuzzyNumber KLEINNUT = new FuzzyNumber(100, 12, 24, 36);
    public static final FuzzyNumber REDELIJKKLEINNUT = new FuzzyNumber(100, 24, 36, 50);
    public static final FuzzyNumber MATIGNUT = new FuzzyNumber(100, 36, 50, 64);
    public static final FuzzyNumber REDELIJKGROOTNUT = new FuzzyNumber(100, 50, 64, 76);
    public static final FuzzyNumber GROOTNUT = new FuzzyNumber(100, 64, 76, 88);
    public static final FuzzyNumber HEELGROOTNUT = new FuzzyNumber(100, 76, 88, 99);
    public static final FuzzyNumber HEELHEELGROOTNUT = new FuzzyNumber(100, 88, 99, 99);
}
