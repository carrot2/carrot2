
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

import java.util.HashMap;
import java.util.Map;

/**
 * A class for handling FuzzyAnts parameters and 
 * overriding them with mapped values.
 * 
 * @author Dawid Weiss
 */
public class FuzzyAntsParameters {
    public final static String N1 = "n1";
    public final static String M1 = "m1";
    public final static String N2 = "n2";
    public final static String M2 = "m2";
    public final static String NUMBER_OF_ITERATIONS = "numberOfIterations";

    private int n1; //5
    private int m1; //5
    private int n2; //10
    private int m2; //15
    private int numberOfIterations; //1000
    
    /**
     * Creates a new objects with default settings.
     */
    public FuzzyAntsParameters() {
        this.n1 = 5;
        this.m1 = 5;
        this.n2 = 10;
        this.m2 = 15;
        this.numberOfIterations = 1000;
    }

    public static FuzzyAntsParameters fromMap(Map map) {
        final FuzzyAntsParameters params = new FuzzyAntsParameters();

        String value;

        value = (String) map.get(FuzzyAntsParameters.N1);
        if (value != null) {
            params.n1 = Integer.parseInt(value);
            if (params.n1 < 0.0) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(FuzzyAntsParameters.M1);
        if (value != null) {
            params.m1 = Integer.parseInt(value);
            if (params.m1 < 0.0) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(FuzzyAntsParameters.N2);
        if (value != null) {
            params.n2 = Integer.parseInt(value);
            if (params.n2 < 0.0) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        value = (String) map.get(FuzzyAntsParameters.M2);
        if (value != null) {
            params.m2 = Integer.parseInt(value);
            if (params.m2 < 0.0) {
                throw new RuntimeException("Illegal value range.");
            }
        }
        
        value = (String) map.get(FuzzyAntsParameters.NUMBER_OF_ITERATIONS);
        if (value != null) {
            params.numberOfIterations = Integer.parseInt(value);
            if (params.numberOfIterations < 1 || 
                    params.numberOfIterations > 8000) {
                throw new RuntimeException("Illegal value range.");
            }
        }

        return params;
    }
    
    public Map toMap() {
        final HashMap map = new HashMap();
        map.put(FuzzyAntsParameters.N1, Integer.toString(n1));
        map.put(FuzzyAntsParameters.M1, Integer.toString(m1));
        map.put(FuzzyAntsParameters.N2, Integer.toString(n2));
        map.put(FuzzyAntsParameters.M2, Integer.toString(m2));
        map.put(FuzzyAntsParameters.NUMBER_OF_ITERATIONS, Integer.toString(numberOfIterations));
        return map;
    }

    public int getM1() {
        return m1;
    }

    public int getM2() {
        return m2;
    }

    public int getN1() {
        return n1;
    }

    public int getN2() {
        return n2;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }
}
