

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


package com.mwroblewski.carrot.utils;


/**
 * @author Micha³ Wróblewski
 */
public class MathUtils
{
    public static float log(float base, float value)
    {
        // calculating ln (x)
        float result = (float) Math.log(value);

        // converting to log a (x)
        result /= Math.log(base);

        return value;
    }


    public static float round(float value, float precision)
    {
        int tmp = Math.round(value / precision);

        return tmp * precision;
    }
}
