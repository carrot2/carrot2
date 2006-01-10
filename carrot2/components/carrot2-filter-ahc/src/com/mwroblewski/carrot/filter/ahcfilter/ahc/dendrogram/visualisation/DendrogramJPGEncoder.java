
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

package com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.visualisation;


import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;


/**
 * @author Mciha� Wr�blewski
 */
public class DendrogramJPGEncoder
{
    protected DendrogramView treeView;

    public DendrogramJPGEncoder(
        LinkedList trees, float [][] similarities, float minSim, float maxSim
    )
    {
        treeView = new DendrogramView(trees, similarities, minSim, maxSim);
    }

    public void encode(String fileName, int width, int height)
        throws FileNotFoundException, IOException
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0, 0, width, height);
        treeView.paint(g, width, height);

        FileOutputStream out = new FileOutputStream(fileName);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image);
        out.close();
    }
}
