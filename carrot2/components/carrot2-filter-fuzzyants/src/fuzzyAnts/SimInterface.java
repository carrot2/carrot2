

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */


package fuzzyAnts;


import java.util.*;


/**
 * Possible extensions include "DocumentSet"
 *
 * @author Steven Schockaert
 */
public interface SimInterface
{
    public double similarity(int i1, int i2);


    public double leadervalue(int i);


    public Set getIndices();


    public int getNumber();
}
