
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


import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.*;
import org.apache.log4j.Logger;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author Mciha� Wr�blewski
 */
public class DendrogramView
{
    private final Logger log = Logger.getLogger(this.getClass());
    protected LinkedList trees;
    protected int size;
    protected float [][] similarities;
    protected float minSim;
    protected float maxSim;
    protected float xScale;
    protected float yScale;

    protected class Node
    {
        float x;
        float y;

        Node(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public String toString()
        {
            return ("Node - x: " + x + " y: " + y);
        }
    }

    public DendrogramView(LinkedList trees, float [][] similarities, float minSim, float maxSim)
    {
        super();

        this.trees = trees;
        this.similarities = similarities;
        this.minSim = minSim;
        this.maxSim = maxSim;

        Iterator treesIterator = trees.iterator();

        while (treesIterator.hasNext())
        {
            size += ((DendrogramItem) treesIterator.next()).size();
        }
    }

    protected int x(double x)
    {
        return (int) ((x + 1) * xScale);
    }


    protected int y(double y)
    {
        return (int) ((y - minSim) * yScale) + 10;
    }


    protected void drawBackground(Graphics g)
    {
        g.setColor(Color.red);

        float simDif = 0.1f * (maxSim - minSim);

        // drawing grid & legend
        for (float sim = minSim; sim < maxSim; sim += simDif)
        {
            String legend = sim + "";
            int dotIndex = legend.indexOf('.');

            if (dotIndex != -1)
            {
                int length = dotIndex + 3;

                if (length > legend.length())
                {
                    length = legend.length();
                }

                legend = legend.substring(0, length);
            }

            g.drawChars(legend.toCharArray(), 0, legend.length(), x(-0.5) - 30, y(sim) + 5);

            g.drawLine(x(-0.5), y(sim), x(-0.5), y(sim + simDif));
            g.drawLine(x(-0.5), y(sim + simDif), x(size - 0.5), y(sim + simDif));
            g.drawLine(x(size - 0.5), y(sim + simDif), x(size - 0.5), y(sim));
            g.drawLine(x(size - 0.5), y(sim), x(-0.5), y(sim));
        }
    }


    protected void drawLeaf(int index, int pos, Graphics g)
    {
        g.setColor(Color.black);
        g.drawOval(x(pos) - 2, y(maxSim) - 2, 4, 4);

        char [] data = (index + "").toCharArray();
        g.setFont(new Font(null, 0, 9));
        g.drawChars(data, 0, data.length, x(pos) - 4, y(maxSim) + 15);

        log.debug("drawing leaf at x: " + pos);
    }


    protected void drawLink(
        float ldx, float ldy, float uy, float rdx, float rdy, boolean drawHigher, Graphics g
    )
    {
        if (drawHigher)
        {
            g.drawLine(x(ldx), y(ldy), x(ldx), y(uy) - 1);
            g.drawLine(x(ldx), y(uy) - 1, x(rdx), y(uy) - 1);
            g.drawLine(x(rdx), y(uy) - 1, x(rdx), y(rdy));
        }
        else
        {
            g.drawLine(x(ldx), y(ldy), x(ldx), y(uy));
            g.drawLine(x(ldx), y(uy), x(rdx), y(uy));
            g.drawLine(x(rdx), y(uy), x(rdx), y(rdy));
        }

        log.debug("linking: (" + ldx + "," + ldy + ") (" + rdx + "," + rdy + ")");
    }


    protected Node drawNode(DendrogramNode node, int pos, Graphics g)
    {
        DendrogramItem left = node.getLeft();
        DendrogramItem right = node.getRight();

        Node nodeLeft;
        Node nodeRight;
        float uy = node.getSimilarity();

        // drawing subtrees
        if (left instanceof DendrogramNode)
        {
            nodeLeft = drawNode((DendrogramNode) left, pos, g);
        }
        else
        {
            nodeLeft = new Node(pos, maxSim);
            drawLeaf(left.getIndex(), pos, g);
        }

        pos += left.size();

        if (right instanceof DendrogramNode)
        {
            nodeRight = drawNode((DendrogramNode) right, pos, g);
        }
        else
        {
            nodeRight = new Node(pos, maxSim);
            drawLeaf(right.getIndex(), pos, g);
        }

        pos += right.size();

        // linking subtrees
        g.setColor(Color.black);

        if ((nodeRight.y == uy) || (nodeLeft.y == uy))
        {
            drawLink(nodeLeft.x, nodeLeft.y, uy, nodeRight.x, nodeRight.y, true, g);
        }
        else
        {
            drawLink(nodeLeft.x, nodeLeft.y, uy, nodeRight.x, nodeRight.y, false, g);
        }

        return new Node(((nodeLeft.x + nodeRight.x) / 2.0f), uy);
    }


    public void paint(Graphics g, int width, int height)
    {
        // how many pixels for a single tree item
        xScale = (float) width / (size + 1);

        // how many pixels for (maxSim - min Sim)
        yScale = (height - 30) / (maxSim - minSim);

        // drawing background
        drawBackground(g);

        // drawing tree
        int pos = 0;
        Iterator treesIterator = trees.iterator();

        while (treesIterator.hasNext())
        {
            DendrogramItem item = (DendrogramItem) treesIterator.next();

            if (item instanceof DendrogramLeaf)
            {
                drawLeaf(item.getIndex(), pos, g);
            }
            else
            {
                drawNode((DendrogramNode) item, pos, g);
            }

            pos += item.size();
        }
    }
}
