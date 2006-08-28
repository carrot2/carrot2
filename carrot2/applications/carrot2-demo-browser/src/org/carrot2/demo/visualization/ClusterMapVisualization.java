
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

package org.carrot2.demo.visualization;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.carrot2.core.LocalInputComponent;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent.Result;

import biz.aduna.map.cluster.*;

/**
 * @author Stanislaw Osinski
 */
public class ClusterMapVisualization
{
    public static void showMapFrame(Result result, Point location, int width,
        int height)
    {
        if (result == null)
        {
            return;
        }

        ClusterMapFactory factory = ClusterMapFactory.createFactory();
        ClusterMap clusterMap = factory.createClusterMap();
        ClusterMapMediator mapMediator = factory.createMediator(clusterMap);
        mapMediator.setClassificationTree(buildClusterTree(result.clusters));

        final JFrame frame = new JFrame("Cluster map for query: "
            + result.context.get(LocalInputComponent.PARAM_QUERY));
        frame.getContentPane().add(factory.createGUI(mapMediator),
            BorderLayout.CENTER);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Scale"));
        topPanel.add(mapMediator.getGraphScaler());
        frame.getContentPane().add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        location.translate(50, 50);
        frame.setLocation(location);
        frame.setSize(new Dimension(width, height));
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                frame.setVisible(true);
            }
        });
    }

    private static Classification buildClusterTree(List clusters)
    {
        Map mapObjects = new HashMap();

        DefaultClassification result = new DefaultClassification("All results");

        for (Iterator it = clusters.iterator(); it.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) it.next();
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) == null)
            {
                result
                    .addChild(asClassification(mapObjects, result, rawCluster));
                addAllDocuments(mapObjects, result, rawCluster);
            }
        }

        return result;
    }

    private static Classification asClassification(Map mapObjects,
        Classification parent, RawCluster cluster)
    {
        DefaultClassification result = new DefaultClassification(cluster
            .getClusterDescription().get(0).toString(), parent);

        // Add documents (recursively from all subclusters)
        addAllDocuments(mapObjects, result, cluster);

        // Add subclusters
        List subclusters = cluster.getSubclusters();
        if (subclusters != null)
        {
            for (Iterator it = subclusters.iterator(); it.hasNext();)
            {
                RawCluster subcluster = (RawCluster) it.next();
                if (subcluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) == null)
                {
                    result.addChild(asClassification(mapObjects, result,
                        subcluster));
                }
            }
        }

        return result;
    }

    private static void addAllDocuments(Map mapObjects,
        DefaultClassification classification, RawCluster cluster)
    {
        List rawDocuments = cluster.getDocuments();
        for (Iterator it = rawDocuments.iterator(); it.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) it.next();
            classification.add(asMapObject(mapObjects, rawDocument));
        }

        List subclusters = cluster.getSubclusters();
        if (subclusters != null)
        {
            for (Iterator it = subclusters.iterator(); it.hasNext();)
            {
                RawCluster subcluster = (RawCluster) it.next();
                addAllDocuments(mapObjects, classification, subcluster);
            }
        }
    }

    private static Object asMapObject(Map mapObjects, RawDocument rawDocument)
    {
        if (!mapObjects.containsKey(rawDocument))
        {
            mapObjects.put(rawDocument, new DefaultObject(rawDocument
                .getTitle(), rawDocument.getUrl()));
        }
        return mapObjects.get(rawDocument);
    }
}
