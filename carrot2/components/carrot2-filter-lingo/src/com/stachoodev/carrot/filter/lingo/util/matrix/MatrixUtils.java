
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.stachoodev.carrot.filter.lingo.util.matrix;

import Jama.Matrix;

import com.dawidweiss.carrot.util.common.StringUtils;

import java.io.PrintWriter;

import java.text.NumberFormat;


/**
 *
 */
public class MatrixUtils {
    /**
     * Method normalizeColumns.
     *
     * @param m
     */
    public static void normalizeColumnLengths(Matrix m) {
        for (int c = 0; c < m.getColumnDimension(); c++) {
            double len = getColumnVector2Norm(m, c);

            if (len != 0) {
                for (int r = 0; r < m.getRowDimension(); r++) {
                    m.set(r, c, m.get(r, c) / len);
                }
            }
        }
    }

    /**
     * Method normalizeColumns.
     *
     * @param m
     */
    public static void normalizeRowLengths(Matrix m) {
        for (int r = 0; r < m.getRowDimension(); r++) {
            double len = getRowVector2Norm(m, r);

            if (len != 0) {
                for (int c = 0; c < m.getColumnDimension(); c++) {
                    m.set(r, c, m.get(r, c) / len);
                }
            }
        }
    }

    /**
     * Method normalizeColumns.
     *
     * @param m
     */
    public static void normalizeValues(Matrix m) {
        // Find min and max values
        double min = m.get(0, 0);
        double max = m.get(0, 0);

        for (int c = 0; c < m.getColumnDimension(); c++) {
            for (int r = 0; r < m.getRowDimension(); r++) {
                if (m.get(r, c) < min) {
                    min = m.get(r, c);
                }

                if (m.get(r, c) > max) {
                    max = m.get(r, c);
                }
            }
        }

        if (max == min) {
            return;
        }

        // Scale the values		
        double scale = 1 / (max - min);

        for (int c = 0; c < m.getColumnDimension(); c++) {
            for (int r = 0; r < m.getRowDimension(); r++) {
                m.set(r, c, m.get(r, c) * scale);
            }
        }
    }

    /**
     * Method getColumnVector2Norm.
     *
     * @param m
     * @param c
     *
     * @return double
     */
    public static double getColumnVector2Norm(Matrix m, int c) {
        double len = 0;

        for (int r = 0; r < m.getRowDimension(); r++) {
            len += (m.get(r, c) * m.get(r, c));
        }

        return len = Math.sqrt(len);
    }

    /**
     * Method getColumnVector2Norm.
     *
     * @param m
     * @param c
     *
     * @return double
     */
    public static double getRowVector2Norm(Matrix m, int r) {
        double len = 0;

        for (int c = 0; c < m.getColumnDimension(); c++) {
            len += (m.get(r, c) * m.get(r, c));
        }

        return len = Math.sqrt(len);
    }

    /**
     * DOCUMENT ME!
     *
     * @param m DOCUMENT ME!
     * @param printWriter DOCUMENT ME!
     * @param numberFormat DOCUMENT ME!
     * @param width DOCUMENT ME!
     */
    public static void print(Matrix m, PrintWriter printWriter,
        NumberFormat numberFormat, int width) {
        // Column header
        printWriter.print(StringUtils.addLeftPadding("", width));

        for (int c = 0; c < m.getColumnDimension(); c++) {
            printWriter.print(StringUtils.addLeftPadding(Integer.toString(c),
                    width));
        }

        printWriter.print("\n");

        // Matrix
        for (int r = 0; r < m.getRowDimension(); r++) {
            // Row header
            printWriter.print(StringUtils.addLeftPadding(Integer.toString(r),
                    width));

            for (int c = 0; c < m.getColumnDimension(); c++) {
                printWriter.print(StringUtils.addLeftPadding(
                        numberFormat.format(m.get(r, c)), width));
            }

            printWriter.print("\n");
        }
    }
}
