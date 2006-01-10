
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

package fuzzyAnts;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;


/**
 * Extension of "Clustering" for snippet/document clustering.
 *
 * @author Steven Schockaert
 */
public class DocumentClustering
    extends Clustering
    implements Constants
{
    private final double EXTENDEDQUERYFACTOR = 0.5;
    private Map params;

    public DocumentClustering(
        int depth, List documents, List meta, List query, boolean stopwords, int weightSchema,
        Map params
    )
    {
        super(depth, documents, meta, query, stopwords, weightSchema);
        this.params = params;
    }


    public DocumentClustering(
        int depth, List documents, List meta, List query, boolean stopwords, int weightSchema,
        Map params, double [] documentWeights
    )
    {
        super(depth, documents, meta, query, stopwords, weightSchema, documentWeights);
        this.params = params;
    }

    /*
     * The actual clustering process.
     */
    protected void getSolution()
    {
	groups=new ArrayList();
	DocumentSet s = new DocumentSet(parser.getNonZeroIndices(), parser);
        ListBordModel bm = new ListBordModel(s, params);
        allDocIndices = new HashSet();

        //Execution of the fuzzy ant based clustering algorithm
        java.util.List antRes = bm.getSolution();
        java.util.List centra = new ArrayList();

        for (ListIterator it1 = antRes.listIterator(); it1.hasNext();)
        {
            Heap h = (Heap) it1.next();

            if (h.getNumber() >= minHeapSize)
            {
                centra.add(h.getCentrum());
            }
        }

        //Application of 1 iteration of the fuzzy c-means algorithm.
        java.util.List fcmRes = fcm(centra);

        //Determination of the most representative term of the cluster
        for (ListIterator it1 = fcmRes.listIterator(); it1.hasNext();)
        {
            Map m = (Map) it1.next();
            double [] newDocWeights = new double[documentWeights.length];

            for (Iterator it2 = m.keySet().iterator(); it2.hasNext();)
            {
                int index = ((Integer) it2.next()).intValue();
                double value = ((Double) m.get(new Integer(index))).doubleValue();
                newDocWeights[index] = Math.min(documentWeights[index], value);
            }

            Map termSum = getTermSum(m);
            int bestTerm = getBestTerm(termSum);
            java.util.List docIndices = getDocuments(newDocWeights);
            allDocIndices.addAll(docIndices);

            //recursive application if the number of documents in the cluster is greater than 25 and depth of the recursion
            //is less than 2 (starting with 0 -> 3 levels of recursion)
            if ((docIndices.size() > 25) && (depth < 2))
            {
                DocumentClustering subClusters = new DocumentClustering(
                        depth + 1, restrictDocuments(docIndices), meta,
                        extendedQuery(bestTerm, termSum), stopwords, weightSchema,
                        params, restrictWeights(newDocWeights, docIndices)
                    );
                addSubGroup(bestTerm, subClusters, docIndices);
            }
            else
            {
                addDocumentsGroup(bestTerm, docIndices);
            }
        }

        addOther(allDocIndices);
    }


    /*
     * Implementation of the first iteration of the fuzzy c-means algorithm
     */
    private java.util.List fcm(java.util.List centra)
    {
        ArrayList res = new ArrayList();

        for (int i = 0; i < centra.size(); i++)
        {
            res.add(new HashMap());
        }

        for (int j = 0; j < documentWeights.length; j++)
        {
            int pos = checkZero(j, centra);

            if (pos < 0)
            {
                for (int i = 0; i < centra.size(); i++)
                {
                    Map cluster = (Map) res.get(i);
                    int centrumIndex = ((Document) centra.get(i)).getIndex();
                    cluster.put(
                        new Integer(j), new Double(getMembershipValue(centrumIndex, j, centra))
                    );
                }
            }
            else
            {
                Map cluster = (Map) res.get(pos);
                cluster.put(new Integer(j), new Double(1));
            }
        }

        return res;
    }


    private int checkZero(int docIndex, java.util.List centra)
    {
        for (int i = 0; i < centra.size(); i++)
        {
            int centrumIndex = ((Document) centra.get(i)).getIndex();

            if (parser.roughDocNT(docIndex, centrumIndex) > 0.9999)
            {
                return i;
            }
        }

        return -1;
    }


    private double getMembershipValue(int centrIndex, int docIndex, java.util.List centra)
    {
        double dik = 1 - parser.roughDocNT(docIndex, centrIndex);
        double sum = 0;

        for (ListIterator it = centra.listIterator(); it.hasNext();)
        {
            int j = ((Document) it.next()).getIndex();
            double djk = 1 - parser.roughDocNT(docIndex, j);
            sum += Math.pow(dik / djk, 2.0 / (M - 1.0));
        }

        return 1.0 / sum;
    }


    /*
     * Returns a Map containing a weighted sum for all terms. The weights are passed as the parameter "clusterWeights".
     */
    private Map getTermSum(Map clusterWeights)
    {
        try
        {
            Map termSum = new HashMap();

            for (int i = 0; i < documentWeights.length; i++)
            {
                double clusterValue;

                if (clusterWeights.containsKey(new Integer(i)))
                {
                    clusterValue = ((Double) clusterWeights.get(new Integer(i))).doubleValue();
                }
                else
                {
                    clusterValue = 0;
                }

                if ((clusterValue * documentWeights[i]) > 0.05)
                {
                    Map termIndices = parser.getDocument(i);

                    for (Iterator it2 = termIndices.keySet().iterator(); it2.hasNext();)
                    {
                        Integer termIndex = ((Integer) it2.next());
                        double termWeight = ((Double) termIndices.get(termIndex)).doubleValue();

                        if (termSum.containsKey(termIndex))
                        {
                            double oudeValue = ((Double) termSum.get(termIndex)).doubleValue();
                            double newValue = oudeValue
                                + (clusterValue * documentWeights[i] * termWeight);
                            termSum.put(termIndex, new Double(newValue));
                        }
                        else
                        {
                            double newValue = clusterValue * documentWeights[i] * termWeight;

                            if (newValue > 0.05)
                            {
                                termSum.put(termIndex, new Double(newValue));
                            }
                        }
                    }
                }
            }

            return termSum;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }


    /*
     * Returns the term-index with the heighest weight. Weights are passed as the parameter "termSum"
     */
    private int getBestTerm(Map termSum)
    {
        try
        {
            int maxIndex = -1;
            double maxVal = -1;

            for (Iterator it = termSum.keySet().iterator(); it.hasNext();)
            {
                Integer termIndex = ((Integer) it.next());
                double w = ((Double) termSum.get(termIndex)).doubleValue();

                if (w > maxVal)
                {
                    maxVal = w;
                    maxIndex = termIndex.intValue();
                }
            }

            return maxIndex;
        }

        catch (Exception e)
        {
            e.printStackTrace();

            return -1;
        }
    }


    /*
     * For recursive application, all terms whose weight is greater than "EXTENDEDQUERYFACTOR" times the weight of
     * the term with maximal weight, are considered as query terms.
     */
    protected ArrayList extendedQuery(int maxIndex, Map termSum)
    {
        ArrayList res = new ArrayList();
        res.addAll(query);

        double maxValue = ((Double) termSum.get(new Integer(maxIndex))).doubleValue();

        final DocumentFactory factory = new DocumentFactory();
        for (Iterator it = termSum.keySet().iterator(); it.hasNext();)
        {
            int termIndex = ((Integer) it.next()).intValue();
            double termValue = ((Double) termSum.get(new Integer(termIndex))).doubleValue();

            if (termValue > (EXTENDEDQUERYFACTOR * maxValue))
            {
                Element e = factory.createElement("query");
                e.setText(parser.originalTerm(termIndex));
                res.add(e);
            }
        }

        return res;
    }
}
