
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

package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

import cern.colt.matrix.*;

import com.dawidweiss.carrot.util.common.*;

/**
 * Converts {@link DoubleMatrix2DWrapper}s to XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class DoubleMatrix2DWrapperElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        DoubleMatrix2DWrapper matrixWrapper = (DoubleMatrix2DWrapper) object;
        DoubleMatrix2D matrix = matrixWrapper.getMatrix();
        List columnLabels = matrixWrapper.getColumnLabels();
        List rowLabels = matrixWrapper.getRowLabels();
        Element matrixElement = DocumentHelper.createElement("matrix");

        // Add column names
        Element columnLabelsElement = matrixElement.addElement("column-labels");
        for (int c = 0; c < matrix.columns(); c++)
        {
            if (columnLabels != null && columnLabels.size() > c)
            {
                columnLabelsElement.addElement("label").addText(
                    columnLabels.get(c).toString());
            }
            else
            {
                columnLabelsElement.addElement("label").addText(
                    Integer.toString(c));
            }
        }

        // Add values + row label
        Element bodyElement = matrixElement.addElement("body");
        for (int r = 0; r < matrix.rows(); r++)
        {
            Element rowElement = bodyElement.addElement("row");

            // Label
            if (rowLabels != null && rowLabels.size() > r)
            {
                rowElement.addElement("label").addText(
                    rowLabels.get(r).toString());
            }
            else
            {
                rowElement.addElement("label").addText(Integer.toString(r));
            }

            Element valuesElement = rowElement.addElement("values");
            for (int c = 0; c < matrix.columns(); c++)
            {
                valuesElement.addElement("v").addText(
                    StringUtils.toString(new Double(matrix.getQuick(r, c)),
                        "#.##"));
            }
        }

        return matrixElement;
    }
}