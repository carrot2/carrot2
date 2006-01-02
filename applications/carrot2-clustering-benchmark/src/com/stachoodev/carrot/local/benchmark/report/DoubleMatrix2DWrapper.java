
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import cern.colt.matrix.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class DoubleMatrix2DWrapper
{
    /** */
    private DoubleMatrix2D matrix;

    /** */
    private List rowLabels;
    
    /** */
    private List columnLabels;
    
    /**
     * @param matrix
     * @param rowLabels
     * @param columnLabels
     */
    public DoubleMatrix2DWrapper(DoubleMatrix2D matrix, List rowLabels,
        List columnLabels)
    {
        this.matrix = matrix;
        this.rowLabels = rowLabels;
        this.columnLabels = columnLabels;
    }
    
    /**
     * Returns this DoubleMatrix2DWrapper's <code>matrix</code>.
     * 
     * @return 
     */
    public DoubleMatrix2D getMatrix()
    {
        return matrix;
    }
    
    /**
     * Returns this DoubleMatrix2DWrapper's <code>rowLabels</code>.
     * 
     * @return 
     */
    public List getRowLabels()
    {
        return rowLabels;
    }
    
    /**
     * Returns this DoubleMatrix2DWrapper's <code>columnLabels</code>.
     * 
     * @return 
     */
    public List getColumnLabels()
    {
        return columnLabels;
    }
}