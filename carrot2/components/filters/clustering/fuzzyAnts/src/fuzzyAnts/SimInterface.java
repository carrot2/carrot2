

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


import java.util.*;


/**
 * Possible extensions include "DocumentSet"
 *
 * @author Steven Schockaert
 */
public interface SimInterface
{
    public double similariteit(int i1, int i2);


    public double leiderwaarde(int i);


    public Set geefIndices();


    public int geefAantal();
}
