
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package biz.aduna.map.cluster.render.anim;

import biz.aduna.map.cluster.Classification;
import biz.aduna.map.cluster.Cluster;
import biz.aduna.map.cluster.graph.ClassificationEdge;
import biz.aduna.map.cluster.graph.ClassificationVertex;
import biz.aduna.map.cluster.graph.ClusterEdge;
import biz.aduna.map.cluster.graph.ClusterGraph;
import biz.aduna.map.cluster.graph.ClusterVertex;
import biz.aduna.map.cluster.graph.ObjectVertex;
import biz.aduna.map.cluster.render.ClassificationIdentityScheme;
import biz.aduna.map.cluster.render.ClassificationRenderer;
import biz.aduna.map.cluster.render.ClusterEdgeRenderer;
import biz.aduna.map.cluster.render.ClusterGraphRendering;
import biz.aduna.map.cluster.render.ClusterRenderer;
import biz.aduna.map.cluster.render.GraphRenderingProperties;
import biz.aduna.map.cluster.render.Scheme;
import biz.aduna.map.graph.Vertex;
import biz.aduna.util.State;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Quickfix for CARROT-1087
 */
@SuppressWarnings("all")
public class TransitionAnimator
{
    public static class AlphaImageKey
    {

        public boolean equals(Object obj)
        {
            AlphaImageKey alphaimagekey = (AlphaImageKey)obj;
            return image == alphaimagekey.image && alpha == alphaimagekey.alpha;
        }

        public int hashCode()
        {
            return image.hashCode() + (int)(100F * alpha);
        }

        public Image image;
        public float alpha;

        public AlphaImageKey(Image image1, float f1)
        {
            image = image1;
            alpha = f1;
        }
    }

    protected static class ClusterEdgeSprite extends EdgeSprite
    {

        public void prepare()
        {
            int k = classSprite.x + classSprite.width / 2;
            int l = classSprite.y + classSprite.height / 2;
            int i1 = subClusterSprite.diameter / 2;
            int j1 = subClusterSprite.x + i1;
            int k1 = subClusterSprite.y + i1;
            i1 = (int)((double)i1 * 0.69999999999999996D);
            int l1 = i1 * i1;
            int i2 = j1 - k;
            int j2 = k1 - l;
            int k2 = i2 * i2 + j2 * j2;
            if(l1 < k2)
            {
                double d1 = Math.sqrt(k2 - l1);
                double d2 = 0.0D;
                if(i2 == 0)
                {
                    if(j2 > 0)
                        d2 = 1.5707963267948966D;
                    else
                        d2 = -1.5707963267948966D;
                } else
                {
                    d2 = Math.atan((double)j2 / (double)i2);
                    if(i2 < 0)
                        d2 += 3.1415926535897931D;
                }
                double d3 = Math.asin((double)i1 / Math.sqrt(k2));
                double d4 = d2 - d3;
                double d5 = d2 + d3;
                shape = new GeneralPath(0, 3);
                shape.moveTo(k, l);
                shape.lineTo((int)(d1 * Math.cos(d4)) + k, (int)(d1 * Math.sin(d4)) + l);
                shape.lineTo((int)(d1 * Math.cos(d5)) + k, (int)(d1 * Math.sin(d5)) + l);
                shape.closePath();
            } else
            {
                shape = null;
            }
        }

        public void draw(Graphics2D graphics2d)
        {
            if(shape != null)
            {
                if(fillColor != null)
                {
                    graphics2d.setColor(fillColor);
                    graphics2d.fill(shape);
                }
                if(borderColor != null)
                {
                    graphics2d.setColor(borderColor);
                    graphics2d.draw(shape);
                }
            }
        }

        public Sprite classSprite;
        public SubClusterSprite subClusterSprite;
        public GeneralPath shape;
        public Color oldFillColor;
        public Color newFillColor;
        public Color fillColor;
        public Color borderColor;

        public ClusterEdgeSprite(Sprite sprite, SubClusterSprite subclustersprite)
        {
            classSprite = sprite;
            subClusterSprite = subclustersprite;
        }
    }

    protected static class ClassEdgeSprite extends EdgeSprite
    {

        public void prepare()
        {
            x1 = firstSprite.x + firstSprite.width / 2;
            y1 = firstSprite.y + firstSprite.height / 2;
            x2 = secondSprite.x + secondSprite.width / 2;
            y2 = secondSprite.y + secondSprite.height / 2;
        }

        public void draw(Graphics2D graphics2d)
        {
            graphics2d.setColor(TransitionAnimator.CLASS_EDGE_COLOR);
            graphics2d.setStroke(new BasicStroke(1.5F));
            graphics2d.drawLine(x1, y1, x2, y2);
        }

        public Sprite firstSprite;
        public Sprite secondSprite;
        public int x1;
        public int y1;
        public int x2;
        public int y2;

        public ClassEdgeSprite(Sprite sprite, Sprite sprite1)
        {
            firstSprite = sprite;
            secondSprite = sprite1;
        }
    }

    protected static abstract class EdgeSprite extends Sprite
    {

        public abstract void prepare();

        protected EdgeSprite()
        {
        }
    }

    protected static class SubClusterSprite extends Sprite
    {

        public void draw(Graphics2D graphics2d)
        {
            _drawDisc(graphics2d);
        }

        protected void _drawDisc(Graphics2D graphics2d)
        {
            if(fillColor != null)
            {
                graphics2d.setColor(fillColor);
                graphics2d.fillOval(x, y, diameter, diameter);
            }
            graphics2d.setColor(TransitionAnimator.EDGE_BORDER_COLOR);
            graphics2d.drawOval(x, y, diameter, diameter);
        }

        public int oldDiameter;
        public int newDiameter;
        public int diameter;
        public Color oldFillColor;
        public Color newFillColor;
        public Color fillColor;

        protected SubClusterSprite()
        {
        }
    }

    protected static class TextSprite extends Sprite
    {

        public void draw(Graphics2D graphics2d)
        {
            graphics2d.drawString(text, x, y);
        }

        public String text;

        public TextSprite(String s)
        {
            text = s;
        }
    }

    protected static class ImageSprite extends Sprite
    {

        public void draw(Graphics2D graphics2d)
        {
            if(image != null)
                graphics2d.drawImage(image, x, y, null);
        }

        public Image image;
        public Image baseImage;

        public ImageSprite(Image image1)
        {
            image = image1;
            if(image1 != null)
            {
                width = image1.getWidth(null);
                height = image1.getHeight(null);
            }
        }
    }

    protected static abstract class Sprite
    {

        public abstract void draw(Graphics2D graphics2d);

        public int x;
        public int y;
        public int oldX;
        public int oldY;
        public int newX;
        public int newY;
        public int width;
        public int height;

        protected Sprite()
        {
        }
    }


    public TransitionAnimator()
    {
        L = true;
        X = true;
        Q = new HashMap();
        f = new HashMap();
        i = null;
        UNDERSCORE = -1F;
    }

    public void setAnimatingEdges(boolean flag)
    {
        L = flag;
    }

    public boolean isAnimatingEdges()
    {
        return L;
    }

    public void setAnimatingText(boolean flag)
    {
        X = flag;
    }

    public boolean isAnimatingText()
    {
        return X;
    }

    public void prepareFor(ClusterGraph clustergraph, ClusterGraph clustergraph1, long l)
    {
        _oldGraph = clustergraph;
        _newGraph = clustergraph1;
        ClusterGraphRendering clustergraphrendering = (ClusterGraphRendering)clustergraph.getGraphRendering();
        _oldRendering = clustergraphrendering;
        _oldBoundingBox = clustergraphrendering.getBoundingBox(clustergraph);
        _oldSize = clustergraphrendering.getSize();
        _oldScale = clustergraphrendering.getScale();
        _oldMargin = clustergraphrendering.getMargin();
        clustergraphrendering = (ClusterGraphRendering)clustergraph1.getGraphRendering();
        _newRendering = clustergraphrendering;
        _newBoundingBox = clustergraphrendering.getBoundingBox(clustergraph1);
        _newSize = clustergraphrendering.getSize();
        _newScale = clustergraphrendering.getScale();
        _newMargin = clustergraphrendering.getMargin();
        g = clustergraphrendering.getGraphRenderingProperties().getClassificationNameFont();
        A(clustergraph, clustergraph1);
        D(clustergraph, clustergraph1);
        A(l);
        W = false;
        B = false;
    }

    private final void A(ClusterGraph clustergraph, ClusterGraph clustergraph1)
    {
        ArrayList arraylist = A(clustergraph);
        ArrayList arraylist1 = A(clustergraph1);
        E = new SubClusterModel(arraylist, arraylist1);
    }

    private final ArrayList A(ClusterGraph clustergraph)
    {
        java.util.List list = clustergraph.getClusterVertices();
        int k = list.size();
        ArrayList arraylist = new ArrayList(k);
        for(int l = 0; l < k; l++)
            arraylist.add(((ClusterVertex)list.get(l)).getCluster());

        return arraylist;
    }

    private final void D(ClusterGraph clustergraph, ClusterGraph clustergraph1)
    {
        C(clustergraph, clustergraph1);
        B(clustergraph, clustergraph1);
        E(clustergraph, clustergraph1);
    }

    private final void C(ClusterGraph clustergraph, ClusterGraph clustergraph1)
    {
        Rectangle rectangle = new Rectangle();
        Collection collection = clustergraph.getClusterModel().getClassifications();
        Collection collection1 = clustergraph1.getClusterModel().getClassifications();
        ArrayList arraylist = new ArrayList(300);
        ArrayList arraylist1 = new ArrayList(300);
        ArrayList arraylist2 = new ArrayList(300);
        ArrayList arraylist3 = new ArrayList(1000);
        arraylist3.addAll(clustergraph.getClassificationVertices());
        arraylist3.addAll(_getAnimatedObjectVertices(clustergraph));
        HashMap hashmap = new HashMap(401);
        int k = arraylist3.size();
        for(int l = 0; l < k; l++)
        {
            Vertex vertex = (Vertex)arraylist3.get(l);
            Object obj = A(vertex);
            ImageSprite imagesprite = new ImageSprite(_oldRendering.getImageFor(vertex));
            _oldRendering.getShapeBoundingBox(vertex, rectangle);
            imagesprite.oldX = rectangle.x - _oldBoundingBox.x;
            imagesprite.oldY = rectangle.y - _oldBoundingBox.y;
            hashmap.put(obj, imagesprite);
            if(!(vertex instanceof ClassificationVertex))
                continue;
            Classification classification = ((ClassificationVertex)vertex).getClassification();
            Q.put(classification, imagesprite);
            ClassificationRenderer classificationrenderer = (ClassificationRenderer)vertex.getRenderingAttribute();
            if(!classificationrenderer.isImageVisible() && !collection1.contains(classification))
                imagesprite.image = null;
        }

        arraylist3.clear();
        arraylist3.addAll(clustergraph1.getClassificationVertices());
        arraylist3.addAll(_getAnimatedObjectVertices(clustergraph1));
        k = arraylist3.size();
        for(int i1 = 0; i1 < k; i1++)
        {
            Vertex vertex1 = (Vertex)arraylist3.get(i1);
            Object obj1 = A(vertex1);
            ImageSprite imagesprite1 = (ImageSprite)hashmap.remove(obj1);
            if(imagesprite1 == null)
            {
                imagesprite1 = new ImageSprite(_newRendering.getImageFor(vertex1));
                arraylist2.add(imagesprite1);
                if(obj1 instanceof Classification)
                {
                    Q.put(obj1, imagesprite1);
                    ClassificationRenderer classificationrenderer1 = (ClassificationRenderer)vertex1.getRenderingAttribute();
                    if(!classificationrenderer1.isImageVisible() && !collection.contains(obj1))
                        imagesprite1.image = null;
                }
            } else
            {
                arraylist1.add(imagesprite1);
            }
            _newRendering.getShapeBoundingBox(vertex1, rectangle);
            imagesprite1.newX = rectangle.x - _newBoundingBox.x;
            imagesprite1.newY = rectangle.y - _newBoundingBox.y;
        }

        for(Iterator iterator = hashmap.values().iterator(); iterator.hasNext(); arraylist.add(iterator.next()));
        A = new ImageSprite[arraylist.size()];
        A = (ImageSprite[])arraylist.toArray(A);
        H = new ImageSprite[arraylist1.size()];
        H = (ImageSprite[])arraylist1.toArray(H);
        Y = new ImageSprite[arraylist2.size()];
        Y = (ImageSprite[])arraylist2.toArray(Y);
    }

    protected java.util.List _getAnimatedObjectVertices(ClusterGraph clustergraph)
    {
        ArrayList arraylist = new ArrayList(clustergraph.getObjectVertices().size());
        java.util.List list = clustergraph.getClusterVertices();
        int k = list.size();
        for(int l = 0; l < k; l++)
        {
            ClusterVertex clustervertex = (ClusterVertex)list.get(l);
            ClusterRenderer clusterrenderer = (ClusterRenderer)clustervertex.getRenderingAttribute();
            if(clusterrenderer.getDisplayMode() == GraphRenderingProperties.MULTIPLE_ENTITIES_MODE)
                arraylist.addAll(clustervertex.getObjectVertices());
        }

        return arraylist;
    }

    private final void B(ClusterGraph clustergraph, ClusterGraph clustergraph1)
    {
        Point point = new Point();
        ArrayList arraylist = new ArrayList();
        ArrayList arraylist1 = new ArrayList();
        ArrayList arraylist2 = new ArrayList();
        if(X)
        {
            ArrayList arraylist3 = new ArrayList(clustergraph.getClassificationVertices());
            HashMap hashmap = new HashMap();
            int k = arraylist3.size();
            for(int l = 0; l < k; l++)
            {
                Vertex vertex = (Vertex)arraylist3.get(l);
                TextSprite textsprite = new TextSprite(_oldRendering.getTextFor(vertex));
                _oldRendering.getTextCoordinate(vertex, point);
                textsprite.oldX = point.x - _oldBoundingBox.x;
                textsprite.oldY = point.y - _oldBoundingBox.y;
                hashmap.put(A(vertex), textsprite);
            }

            arraylist3.clear();
            arraylist3.addAll(clustergraph1.getClassificationVertices());
            k = arraylist3.size();
            for(int i1 = 0; i1 < k; i1++)
            {
                Vertex vertex1 = (Vertex)arraylist3.get(i1);
                Object obj = A(vertex1);
                TextSprite textsprite1 = (TextSprite)hashmap.remove(obj);
                if(textsprite1 == null)
                {
                    textsprite1 = new TextSprite(_newRendering.getTextFor(vertex1));
                    arraylist2.add(textsprite1);
                } else
                {
                    arraylist1.add(textsprite1);
                }
                _newRendering.getTextCoordinate(vertex1, point);
                textsprite1.newX = point.x - _newBoundingBox.x;
                textsprite1.newY = point.y - _newBoundingBox.y;
            }

            for(Iterator iterator = hashmap.values().iterator(); iterator.hasNext(); arraylist.add(iterator.next()));
        }
        M = new TextSprite[arraylist.size()];
        M = (TextSprite[])arraylist.toArray(M);
        Z = new TextSprite[arraylist1.size()];
        Z = (TextSprite[])arraylist1.toArray(Z);
        N = new TextSprite[arraylist2.size()];
        N = (TextSprite[])arraylist2.toArray(N);
    }

    private final void E(ClusterGraph clustergraph, ClusterGraph clustergraph1)
    {
        Point point = new Point();
        Collection collection2 = clustergraph.getClusterModel().getClassifications();
        Collection collection3 = clustergraph1.getClusterModel().getClassifications();
        ArrayList arraylist = new ArrayList(100);
        ArrayList arraylist1 = new ArrayList(100);
        ArrayList arraylist2 = new ArrayList(100);
        ArrayList arraylist3 = new ArrayList();
        ArrayList arraylist4 = new ArrayList();
        ArrayList arraylist5 = new ArrayList();
        ArrayList arraylist6 = new ArrayList(100);
        ArrayList arraylist7 = new ArrayList(100);
        ArrayList arraylist8 = new ArrayList(100);
        if(L)
        {
            Scheme scheme = _newRendering.getGraphRenderingProperties().getScheme();
            Color color = (scheme instanceof ClassificationIdentityScheme) ? null : Scheme.CLUSTER_EDGE_BORDER_COLOR_NORMAL;
            Collection collection4 = E.getSubClusters();
            Iterator iterator = collection4.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                SubCluster subcluster = (SubCluster)iterator.next();
                Cluster cluster = subcluster.getFirstCluster();
                Cluster cluster1 = subcluster.getSecondCluster();
                SubClusterSprite subclustersprite = new SubClusterSprite();
                A(subclustersprite, subcluster);
                if(cluster == null)
                {
                    subclustersprite.diameter = A(subcluster, cluster1, clustergraph1, _newRendering);
                    A(cluster1, _newRendering, _newBoundingBox, subclustersprite.diameter / 2, point);
                    subclustersprite.newX = point.x;
                    subclustersprite.newY = point.y;
                    subclustersprite.oldX = subclustersprite.newX;
                    subclustersprite.oldY = subclustersprite.newY;
                    arraylist2.add(subclustersprite);
                    boolean flag = true;
                    Collection collection = cluster1.getClassifications();
                    if(collection.size() == 1)
                    {
                        Classification classification = (Classification)collection.iterator().next();
                        ClassificationVertex classificationvertex = clustergraph1.getVertexFor(classification);
                        flag = ((ClassificationRenderer)classificationvertex.getRenderingAttribute()).isImageVisible();
                    }
                    if(flag)
                        A(subclustersprite, cluster1, color, _newGraph, arraylist8);
                } else
                if(cluster1 == null)
                {
                    subclustersprite.diameter = A(subcluster, cluster, clustergraph, _oldRendering);
                    A(cluster, _oldRendering, _oldBoundingBox, subclustersprite.diameter / 2, point);
                    subclustersprite.oldX = point.x;
                    subclustersprite.oldY = point.y;
                    subclustersprite.newX = subclustersprite.oldX;
                    subclustersprite.newY = subclustersprite.oldY;
                    arraylist.add(subclustersprite);
                    boolean flag1 = true;
                    Collection collection1 = cluster.getClassifications();
                    if(collection1.size() == 1)
                    {
                        Classification classification1 = (Classification)collection1.iterator().next();
                        ClassificationVertex classificationvertex1 = clustergraph.getVertexFor(classification1);
                        flag1 = ((ClassificationRenderer)classificationvertex1.getRenderingAttribute()).isImageVisible();
                    }
                    if(flag1)
                        A(subclustersprite, cluster, color, _oldGraph, arraylist6);
                } else
                {
                    subclustersprite.oldDiameter = A(subcluster, cluster, clustergraph, _oldRendering);
                    A(cluster, _oldRendering, _oldBoundingBox, subclustersprite.oldDiameter / 2, point);
                    subclustersprite.oldX = point.x;
                    subclustersprite.oldY = point.y;
                    subclustersprite.newDiameter = A(subcluster, cluster1, clustergraph1, _newRendering);
                    A(cluster1, _newRendering, _newBoundingBox, subclustersprite.newDiameter / 2, point);
                    subclustersprite.newX = point.x;
                    subclustersprite.newY = point.y;
                    subclustersprite.diameter = subclustersprite.oldDiameter;
                    arraylist1.add(subclustersprite);
                    A(subcluster, subclustersprite, color, arraylist6, arraylist7, arraylist8);
                }
            } while(true);
            ArrayList arraylist9 = new ArrayList(clustergraph.getClassificationEdges());
            arraylist9.addAll(clustergraph1.getClassificationEdges());
            ArrayList arraylist10 = new ArrayList();
            int k = arraylist9.size();
            for(int l = 0; l < k; l++)
            {
                ClassificationEdge classificationedge = (ClassificationEdge)arraylist9.get(l);
                Classification classification2 = classificationedge.getParentVertex().getClassification();
                Classification classification3 = classificationedge.getChildVertex().getClassification();
                if(arraylist10.contains(classification3))
                    continue;
                arraylist10.add(classification3);
                ImageSprite imagesprite = (ImageSprite)Q.get(classification2);
                ImageSprite imagesprite1 = (ImageSprite)Q.get(classification3);
                ClassEdgeSprite classedgesprite = new ClassEdgeSprite(imagesprite, imagesprite1);
                if(!collection3.contains(classification2) || !collection3.contains(classification3))
                {
                    arraylist3.add(classedgesprite);
                    continue;
                }
                if(!collection2.contains(classification2) || !collection2.contains(classification3))
                    arraylist5.add(classedgesprite);
                else
                    arraylist4.add(classedgesprite);
            }

        }
        a = new SubClusterSprite[arraylist.size()];
        a = (SubClusterSprite[])arraylist.toArray(a);
        c = new SubClusterSprite[arraylist1.size()];
        c = (SubClusterSprite[])arraylist1.toArray(c);
        d = new SubClusterSprite[arraylist2.size()];
        d = (SubClusterSprite[])arraylist2.toArray(d);
        R = new ClassEdgeSprite[arraylist3.size()];
        R = (ClassEdgeSprite[])arraylist3.toArray(R);
        O = new ClassEdgeSprite[arraylist4.size()];
        O = (ClassEdgeSprite[])arraylist4.toArray(O);
        K = new ClassEdgeSprite[arraylist5.size()];
        K = (ClassEdgeSprite[])arraylist5.toArray(K);
        V = new ClusterEdgeSprite[arraylist6.size()];
        V = (ClusterEdgeSprite[])arraylist6.toArray(V);
        h = new ClusterEdgeSprite[arraylist7.size()];
        h = (ClusterEdgeSprite[])arraylist7.toArray(h);
        S = new ClusterEdgeSprite[arraylist8.size()];
        S = (ClusterEdgeSprite[])arraylist8.toArray(S);
    }

    private final int A(SubCluster subcluster, Cluster cluster, ClusterGraph clustergraph, ClusterGraphRendering clustergraphrendering)
    {
        ClusterVertex clustervertex = clustergraph.getVertexFor(cluster);
        ClusterRenderer clusterrenderer = (ClusterRenderer)clustergraph.getVertexFor(cluster).getRenderingAttribute();
        int k = clustergraphrendering.getDiameter(clustervertex);
        if(clusterrenderer.getDisplayMode() == GraphRenderingProperties.MULTIPLE_ENTITIES_MODE)
        {
            return k;
        } else
        {
            double d1 = (double)k / 2D;
            double d2 = cluster.getSize();
            double d3 = subcluster.getSize();
            double d4 = Math.sqrt((d3 * d1 * d1) / d2);
            return (int)Math.round(2D * d4);
        }
    }

    private final void A(Cluster cluster, ClusterGraphRendering clustergraphrendering, Rectangle rectangle, int k, Point point)
    {
        Point point1 = new Point();
        clustergraphrendering.getCoordinate(cluster, point1);
        point.x = point1.x - k - rectangle.x;
        point.y = point1.y - k - rectangle.y;
    }

    private void A(SubClusterSprite subclustersprite, SubCluster subcluster)
    {
        Cluster cluster = subcluster.getFirstCluster();
        Cluster cluster1 = subcluster.getSecondCluster();
        if(cluster == null)
            subclustersprite.fillColor = A(cluster1, _newGraph);
        else
        if(cluster1 == null)
        {
            subclustersprite.fillColor = A(cluster, _oldGraph);
        } else
        {
            subclustersprite.oldFillColor = A(cluster, _oldGraph);
            subclustersprite.newFillColor = A(cluster1, _newGraph);
            subclustersprite.fillColor = subclustersprite.oldFillColor;
        }
    }

    private Color A(Cluster cluster, ClusterGraph clustergraph)
    {
        ClusterVertex clustervertex = clustergraph.getVertexFor(cluster);
        ClusterRenderer clusterrenderer = (ClusterRenderer)clustervertex.getRenderingAttribute();
        if(clusterrenderer.getDisplayMode() == GraphRenderingProperties.SINGLE_ENTITY_MODE)
            return clusterrenderer.getFillColor();
        double d1 = 255D;
        double d2 = 255D;
        double d3 = 255D;
        java.util.List list = clustervertex.getEdges();
        int k = list.size();
        for(int l = 0; l < k; l++)
        {
            ClusterEdge clusteredge = (ClusterEdge)list.get(l);
            ClusterEdgeRenderer clusteredgerenderer = (ClusterEdgeRenderer)clusteredge.getRenderingAttribute();
            Color color = clusteredgerenderer.getFillColor();
            double d4 = (double)color.getAlpha() / 255D;
            double d5 = 1.0D - d4;
            d1 = d4 * (double)color.getRed() + d5 * d1;
            d2 = d4 * (double)color.getGreen() + d5 * d2;
            d3 = d4 * (double)color.getBlue() + d5 * d3;
        }

        return new Color(A(d1), A(d2), A(d3));
    }

    private final Object A(Vertex vertex)
    {
        if(vertex instanceof ObjectVertex)
            return ((ObjectVertex)vertex).getObject();
        else
            return ((ClassificationVertex)vertex).getClassification();
    }

    private final void A(SubClusterSprite subclustersprite, Cluster cluster, Color color, ClusterGraph clustergraph, ArrayList arraylist)
    {
        if(L)
        {
            ClusterVertex clustervertex = clustergraph.getVertexFor(cluster);
            java.util.List list = clustervertex.getEdges();
            int k = list.size();
            for(int l = 0; l < k; l++)
            {
                ClusterEdge clusteredge = (ClusterEdge)list.get(l);
                Sprite sprite = (Sprite)Q.get(clusteredge.getClassification());
                ClusterEdgeSprite clusteredgesprite = new ClusterEdgeSprite(sprite, subclustersprite);
                clusteredgesprite.fillColor = A(clusteredge);
                clusteredgesprite.borderColor = color;
                arraylist.add(clusteredgesprite);
            }

        }
    }

    private Color A(ClusterEdge clusteredge)
    {
        ClusterEdgeRenderer clusteredgerenderer = (ClusterEdgeRenderer)clusteredge.getRenderingAttribute();
        Color color = clusteredgerenderer.getFillColor();
        double d1 = (double)color.getAlpha() / 255D;
        double d2 = (1.0D - d1) * 255D;
        double d3 = d1 * (double)color.getRed() + d2;
        double d4 = d1 * (double)color.getGreen() + d2;
        double d5 = d1 * (double)color.getBlue() + d2;
        return new Color(A(d3), A(d4), A(d5));
    }

    private final void A(SubCluster subcluster, SubClusterSprite subclustersprite, Color color, ArrayList arraylist, ArrayList arraylist1, ArrayList arraylist2)
    {
        if(!L)
            return;
        Cluster cluster = subcluster.getFirstCluster();
        Cluster cluster1 = subcluster.getSecondCluster();
        Collection collection = cluster.getMostSpecificClassifications();
        Collection collection1 = cluster1.getMostSpecificClassifications();
        ClusterVertex clustervertex = _oldGraph.getVertexFor(cluster);
        java.util.List list = clustervertex.getEdges();
        int k = list.size();
        for(int l = 0; l < k; l++)
        {
            ClusterEdge clusteredge = (ClusterEdge)list.get(l);
            Classification classification = clusteredge.getClassification();
            Sprite sprite = (Sprite)Q.get(classification);
            ClusterEdgeSprite clusteredgesprite = new ClusterEdgeSprite(sprite, subclustersprite);
            clusteredgesprite.borderColor = color;
            if(collection1.contains(classification))
            {
                clusteredgesprite.oldFillColor = A(clusteredge);
                clusteredgesprite.newFillColor = A(A(cluster1, classification, _newGraph));
                clusteredgesprite.fillColor = clusteredgesprite.oldFillColor;
                arraylist1.add(clusteredgesprite);
            } else
            {
                clusteredgesprite.fillColor = A(clusteredge);
                arraylist.add(clusteredgesprite);
            }
        }

        ClusterVertex clustervertex1 = _newGraph.getVertexFor(cluster1);
        list = clustervertex1.getEdges();
        k = list.size();
        for(int i1 = 0; i1 < k; i1++)
        {
            ClusterEdge clusteredge1 = (ClusterEdge)list.get(i1);
            Classification classification1 = clusteredge1.getClassification();
            if(!collection.contains(classification1))
            {
                Sprite sprite1 = (Sprite)Q.get(classification1);
                ClusterEdgeSprite clusteredgesprite1 = new ClusterEdgeSprite(sprite1, subclustersprite);
                clusteredgesprite1.fillColor = A(clusteredge1);
                clusteredgesprite1.borderColor = color;
                arraylist2.add(clusteredgesprite1);
            }
        }

    }

    private ClusterEdge A(Cluster cluster, Classification classification, ClusterGraph clustergraph)
    {
        ClusterVertex clustervertex = clustergraph.getVertexFor(cluster);
        java.util.List list = clustervertex.getEdges();
        int k = list.size();
        for(int l = 0; l < k; l++)
        {
            ClusterEdge clusteredge = (ClusterEdge)list.get(l);
            if(clusteredge.getClassification() == classification)
                return clusteredge;
        }

        throw new IllegalStateException("edge not found");
    }

    private final void A(long l)
    {
        D = (long)(0.34999999999999998D * (double)l);
        I = (long)(0.30000000000000004D * (double)l);
        P = (long)(0.34999999999999998D * (double)l);
        if(A.length == 0 && M.length == 0 && a.length == 0 && R.length == 0 && V.length == 0)
            D = 0L;
        if(H.length == 0 && Z.length == 0 && c.length == 0 && O.length == 0 && h.length == 0)
            I = 0L;
        if(Y.length == 0 && N.length == 0 && d.length == 0 && K.length == 0 && S.length == 0)
            P = 0L;
    }

    public void cleanUp()
    {
        A = null;
        M = null;
        a = null;
        R = null;
        V = null;
        H = null;
        Z = null;
        c = null;
        O = null;
        h = null;
        Y = null;
        N = null;
        d = null;
        K = null;
        S = null;
        Q.clear();
        f.clear();
        i = null;
        E = null;
    }

    public void prepareForFrame(long l)
    {
        J = l;
        if(l < D)
        {
            G = FIRST_PHASE;
            B();
        } else
        if(l < D + I)
        {
            G = SECOND_PHASE;
            A();
        } else
        {
            G = THIRD_PHASE;
            C();
        }
    }

    private final void B()
    {
        if(!W)
        {
            C = _oldMargin.left;
            T = _oldMargin.top;
            U = _oldScale;
            A(H);
            A(Z);
            A(c);
            A(A);
            A(M);
            A(a);
            A(R);
            A(V);
            A(O);
            A(h);
            for(int k = 0; k < A.length; k++)
            {
                ImageSprite imagesprite = A[k];
                imagesprite.baseImage = imagesprite.image;
            }

            W = true;
        }
        float f1 = (float)(D - J) / (float)D;
        f1 = A(f1);
        for(int l = 0; l < A.length; l++)
        {
            ImageSprite imagesprite1 = A[l];
            if(imagesprite1.baseImage != null)
                imagesprite1.image = A(imagesprite1.baseImage, f1);
        }

    }

    private final void A()
    {
        float f1 = (float)(J - D) / (float)I;
        b = f1;
        float f2 = 1.0F - f1;
        C = f1 * (float)_newMargin.left + f2 * (float)_oldMargin.left;
        T = f1 * (float)_newMargin.top + f2 * (float)_oldMargin.top;
        U = (double)f1 * _newScale + (double)f2 * _oldScale;
        C(H);
        C(Z);
        C(c);
        for(int k = 0; k < c.length; k++)
        {
            SubClusterSprite subclustersprite = c[k];
            subclustersprite.diameter = subclustersprite.oldDiameter + (int)(f1 * (float)(subclustersprite.newDiameter - subclustersprite.oldDiameter));
            Color color = subclustersprite.oldFillColor;
            Color color2 = subclustersprite.newFillColor;
            if(color == color2)
                subclustersprite.fillColor = color2;
            else
                subclustersprite.fillColor = new Color(A(f2 * (float)color.getRed() + f1 * (float)color2.getRed()), A(f2 * (float)color.getGreen() + f1 * (float)color2.getGreen()), A(f2 * (float)color.getBlue() + f1 * (float)color2.getBlue()));
        }

        for(int l = 0; l < h.length; l++)
        {
            ClusterEdgeSprite clusteredgesprite = h[l];
            Color color1 = clusteredgesprite.oldFillColor;
            Color color3 = clusteredgesprite.newFillColor;
            clusteredgesprite.fillColor = new Color(A(f2 * (float)color1.getRed() + f1 * (float)color3.getRed()), A(f2 * (float)color1.getGreen() + f1 * (float)color3.getGreen()), A(f2 * (float)color1.getBlue() + f1 * (float)color3.getBlue()));
        }

        A(((EdgeSprite []) (O)));
        A(((EdgeSprite []) (h)));
    }

    private int A(double d1)
    {
        int k = (int)d1;
        k = Math.max(k, 0);
        k = Math.min(k, 255);
        return k;
    }

    private final void C()
    {
        if(!B)
        {
            C = _newMargin.left;
            T = _newMargin.top;
            U = _newScale;
            B(H);
            B(Z);
            B(c);
            B(Y);
            B(N);
            B(d);
            A(O);
            A(h);
            A(K);
            A(S);
            for(int k = 0; k < Y.length; k++)
            {
                ImageSprite imagesprite = Y[k];
                imagesprite.baseImage = imagesprite.image;
            }

            B = true;
        }
        float f1 = (float)(J - I - D) / (float)P;
        f1 = A(f1);
        for(int l = 0; l < Y.length; l++)
        {
            ImageSprite imagesprite1 = Y[l];
            if(imagesprite1.baseImage != null)
                imagesprite1.image = A(imagesprite1.baseImage, f1);
        }

    }

    private final void A(Sprite asprite[])
    {
        for(int k = 0; k < asprite.length; k++)
        {
            Sprite sprite = asprite[k];
            sprite.x = sprite.oldX;
            sprite.y = sprite.oldY;
        }

    }

    private final void B(Sprite asprite[])
    {
        for(int k = 0; k < asprite.length; k++)
        {
            Sprite sprite = asprite[k];
            sprite.x = sprite.newX;
            sprite.y = sprite.newY;
        }

    }

    private final void A(EdgeSprite aedgesprite[])
    {
        for(int k = 0; k < aedgesprite.length; k++)
            aedgesprite[k].prepare();

    }

    private final void C(Sprite asprite[])
    {
        float f1 = b;
        float f2 = 1.0F - f1;
        for(int k = 0; k < asprite.length; k++)
        {
            Sprite sprite = asprite[k];
            sprite.x = (int)(f1 * (float)sprite.newX + f2 * (float)sprite.oldX);
            sprite.y = (int)(f1 * (float)sprite.newY + f2 * (float)sprite.oldY);
        }

    }

    private final BufferedImage A(Image image, float f1)
    {
        if(image == i && f1 == UNDERSCORE)
            return j;
        i = image;
        UNDERSCORE = f1;
        AlphaImageKey alphaimagekey = new AlphaImageKey(image, f1);
        BufferedImage bufferedimage = (BufferedImage)f.get(alphaimagekey);
        if(bufferedimage != null)
        {
            j = bufferedimage;
            return bufferedimage;
        } else
        {
            int k = image.getWidth(null);
            int l = image.getHeight(null);
            BufferedImage bufferedimage1 = new BufferedImage(k, l, 2);
            Graphics2D graphics2d = bufferedimage1.createGraphics();
            graphics2d.setComposite(AlphaComposite.getInstance(3, sanitizeAlpha(f1)));
            graphics2d.drawImage(image, 0, 0, null);
            f.put(alphaimagekey, bufferedimage1);
            j = bufferedimage1;
            return bufferedimage1;
        }
    }

    private float sanitizeAlpha(float f1) {
      return Math.max(0f, Math.min(1f, f1));
    }

    private final float A(float f1)
    {
        float f2 = Math.min(f1, 1.0F);
        f2 = Math.max(f2, 0.0F);
        return f2;
    }

    public void paint(Graphics2D graphics2d)
    {
        graphics2d.translate(C, T);
        graphics2d.scale(U, U);
        graphics2d.setFont(g);
        if(G == FIRST_PHASE)
            C(graphics2d);
        else
        if(G == SECOND_PHASE)
            A(graphics2d);
        else
            B(graphics2d);
    }

    private final void C(Graphics2D graphics2d)
    {
        A(h, graphics2d);
        A(O, graphics2d);
        java.awt.Composite composite = graphics2d.getComposite();
        float f1 = (float)(D - J) / (float)D;
        f1 = A(f1);
        AlphaComposite alphacomposite = AlphaComposite.getInstance(3, sanitizeAlpha(f1));
        graphics2d.setComposite(alphacomposite);
        A(V, graphics2d);
        A(R, graphics2d);
        A(a, graphics2d);
        graphics2d.setComposite(composite);
        A(c, graphics2d);
        A(H, graphics2d);
        A(A, graphics2d);
        graphics2d.setColor(Color.black);
        A(Z, graphics2d);
        graphics2d.setComposite(alphacomposite);
        A(M, graphics2d);
    }

    private final void A(Graphics2D graphics2d)
    {
        A(((Sprite []) (h)), graphics2d);
        A(((Sprite []) (O)), graphics2d);
        A(((Sprite []) (c)), graphics2d);
        A(((Sprite []) (H)), graphics2d);
        graphics2d.setColor(Color.black);
        A(((Sprite []) (Z)), graphics2d);
    }

    private final void B(Graphics2D graphics2d)
    {
        A(h, graphics2d);
        A(O, graphics2d);
        java.awt.Composite composite = graphics2d.getComposite();
        float f1 = (float)(J - I - D) / (float)P;
        f1 = A(f1);
        AlphaComposite alphacomposite = AlphaComposite.getInstance(3, sanitizeAlpha(f1));
        graphics2d.setComposite(alphacomposite);
        A(S, graphics2d);
        A(K, graphics2d);
        A(d, graphics2d);
        graphics2d.setComposite(composite);
        A(c, graphics2d);
        A(H, graphics2d);
        A(Y, graphics2d);
        graphics2d.setColor(Color.black);
        A(Z, graphics2d);
        graphics2d.setComposite(alphacomposite);
        A(N, graphics2d);
    }

    private final void A(Sprite asprite[], Graphics2D graphics2d)
    {
        for(int k = 0; k < asprite.length; k++)
            asprite[k].draw(graphics2d);

    }

    public long getNeededDuration()
    {
        return D + I + P;
    }

    public Dimension getSize()
        throws IllegalStateException
    {
        if(G == FIRST_PHASE)
            return new Dimension(_oldSize);
        if(G == SECOND_PHASE)
        {
            float f1 = 1.0F - b;
            return new Dimension((int)(b * (float)_newSize.width + f1 * (float)_oldSize.width), (int)(b * (float)_newSize.height + f1 * (float)_oldSize.height));
        }
        if(G == THIRD_PHASE)
            return new Dimension(_newSize);
        else
            throw new IllegalStateException("no current phase has been set yet");
    }

    public static final boolean DEFAULT_ANIMATING_EDGES = true;
    public static final boolean DEFAULT_ANIMATING_TEXT = true;
    public static final double FIRST_SHIFT = 0.34999999999999998D;
    public static final double SECOND_SHIFT = 0.65000000000000002D;
    public static final State FIRST_PHASE = new State("first phase");
    public static final State SECOND_PHASE = new State("second phase");
    public static final State THIRD_PHASE = new State("third phase");
    public static final Color EDGE_BORDER_COLOR = new Color(194, 194, 194);
    public static final Color CLASS_EDGE_COLOR;
    public static final float CLASS_EDGE_THICKNESS = 1.5F;
    private static final double F = 1.5707963267948966D;
    private static final double e = -1.5707963267948966D;
    private boolean L;
    private boolean X;
    private State G;
    private long D;
    private long I;
    private long P;
    private long J;
    private float b;
    private boolean W;
    private boolean B;
    private double C;
    private double T;
    private double U;
    private Font g;
    private HashMap f;
    private Image i;
    private float UNDERSCORE;
    private BufferedImage j;
    private ImageSprite A[];
    private TextSprite M[];
    private SubClusterSprite a[];
    private ClassEdgeSprite R[];
    private ClusterEdgeSprite V[];
    private ImageSprite H[];
    private TextSprite Z[];
    private SubClusterSprite c[];
    private ClassEdgeSprite O[];
    private ClusterEdgeSprite h[];
    private ImageSprite Y[];
    private TextSprite N[];
    private SubClusterSprite d[];
    private ClassEdgeSprite K[];
    private ClusterEdgeSprite S[];
    protected ClusterGraph _oldGraph;
    protected ClusterGraph _newGraph;
    protected ClusterGraphRendering _oldRendering;
    protected ClusterGraphRendering _newRendering;
    protected Rectangle _oldBoundingBox;
    protected Rectangle _newBoundingBox;
    protected Dimension _oldSize;
    protected Dimension _newSize;
    protected double _oldScale;
    protected double _newScale;
    protected Insets _oldMargin;
    protected Insets _newMargin;
    private SubClusterModel E;
    private HashMap Q;

    static 
    {
        CLASS_EDGE_COLOR = Scheme.CLASSIFICATION_EDGE_COLOR_NORMAL;
    }
}
