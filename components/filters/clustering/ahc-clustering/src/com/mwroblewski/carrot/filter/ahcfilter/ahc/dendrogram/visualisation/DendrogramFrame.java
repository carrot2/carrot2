

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
