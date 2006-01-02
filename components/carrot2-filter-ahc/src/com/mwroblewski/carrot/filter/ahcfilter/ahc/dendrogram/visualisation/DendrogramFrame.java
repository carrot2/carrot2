
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
package com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.visualisation;


import java.awt.Canvas;
import java.awt.Graphics;
import java.util.LinkedList;
import javax.swing.JFrame;


/**
 * @author Mciha� Wr�blewski
 */
public class DendrogramFrame
    extends JFrame
{
    protected DendrogramView treeView;
    protected Canvas treeCanvas;

    public DendrogramFrame(
        LinkedList trees, float [][] similarities, float minSim, float maxSim, int width, int height
    )
    {
        super();
        this.treeView = new DendrogramView(trees, similarities, minSim, maxSim);
        treeCanvas = new Canvas()
                {
                    public void paint(Graphics g)
                    {
                        treeView.paint(g, getWidth(), getHeight());
                    }
                };

        // showing of complete frame
        this.setSize(width, height);
        getContentPane().add(treeCanvas);
        show();
    }
}
