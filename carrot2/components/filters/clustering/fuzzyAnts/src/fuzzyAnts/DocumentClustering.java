

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


package fuzzyAnts;


import org.jdom.Element;
import java.io.*;
import java.util.*;
import javax.servlet.http.*;


/**
 * Extension of "Clustering" for snippet/document clustering.
 *
 * @author Steven Schockaert
 */
public class DocumentClustering
    extends Clustering
    implements Constants
{
    private final double UITGEBREIDEQUERYFACTOR = 0.5;
    private Map params;

    public DocumentClustering(
        int diepte, List documenten, List meta, List query, boolean stopwoorden, int gewichtSchema,
        Map params
    )
    {
        super(diepte, documenten, meta, query, stopwoorden, gewichtSchema);
        this.params = params;
    }


    public DocumentClustering(
        int diepte, List documenten, List meta, List query, boolean stopwoorden, int gewichtSchema,
        Map params, double [] documentGewichten
    )
    {
        super(diepte, documenten, meta, query, stopwoorden, gewichtSchema, documentGewichten);
        this.params = params;
    }

    /*
     * The actual clustering process.
     */
    protected void bepaalOplossing()
    {
        groepen = new ArrayList();

        DocumentSet s = new DocumentSet(parser.geefNietNulIndices(), parser);
        LijstBordModel bm = new LijstBordModel(s, params);
        alleDocIndices = new HashSet();

        //Execution of the fuzzy ant based clustering algorithm
        java.util.List mierRes = bm.berekenOplossing();
        java.util.List centra = new ArrayList();

        for (ListIterator it1 = mierRes.listIterator(); it1.hasNext();)
        {
            Hoop h = (Hoop) it1.next();

            if (h.geefAantal() >= minHoopSize)
            {
                centra.add(h.geefCentrum());
            }
        }

        //Application of 1 iteration of the fuzzy c-means algorithm.
        java.util.List fcmRes = fcm(centra);

        //Determination of the most representative term of the cluster
        for (ListIterator it1 = fcmRes.listIterator(); it1.hasNext();)
        {
            Map m = (Map) it1.next();
            double [] nieuweDocGewichten = new double[documentGewichten.length];

            for (Iterator it2 = m.keySet().iterator(); it2.hasNext();)
            {
                int index = ((Integer) it2.next()).intValue();
                double waarde = ((Double) m.get(new Integer(index))).doubleValue();
                nieuweDocGewichten[index] = Math.min(documentGewichten[index], waarde);
            }

            Map termenSom = termenSom(m);
            int sterksteTerm = geefSterksteTerm(termenSom);
            java.util.List docIndices = bepaalDocumenten(nieuweDocGewichten);
            alleDocIndices.addAll(docIndices);

            //recursive application if the number of documents in the cluster is greater than 25 and depth of the recursion
            //is less than 2 (starting with 0 -> 3 levels of recursion)
            if ((docIndices.size() > 25) && (diepte < 2))
            {
                DocumentClustering subClusters = new DocumentClustering(
                        diepte + 1, beperkDocumenten(docIndices), meta,
                        uitgebreideQuery(sterksteTerm, termenSom), stopwoorden, gewichtSchema,
                        params, beperkGewichten(nieuweDocGewichten, docIndices)
                    );
                addSubGroep(sterksteTerm, subClusters, docIndices);
            }
            else
            {
                addDocumentenGroep(sterksteTerm, docIndices);
            }
        }

        addAndere(alleDocIndices);
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

        for (int j = 0; j < documentGewichten.length; j++)
        {
            int pos = checkZero(j, centra);

            if (pos < 0)
            {
                for (int i = 0; i < centra.size(); i++)
                {
                    Map cluster = (Map) res.get(i);
                    int centrumIndex = ((Document) centra.get(i)).geefIndex();
                    cluster.put(
                        new Integer(j), new Double(berekenLidmaatschap(centrumIndex, j, centra))
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
            int centrumIndex = ((Document) centra.get(i)).geefIndex();

            if (parser.ruwDocNT(docIndex, centrumIndex) > 0.9999)
            {
                return i;
            }
        }

        return -1;
    }


    private double berekenLidmaatschap(int centrIndex, int docIndex, java.util.List centra)
    {
        double dik = 1 - parser.ruwDocNT(docIndex, centrIndex);
        double som = 0;

        for (ListIterator it = centra.listIterator(); it.hasNext();)
        {
            int j = ((Document) it.next()).geefIndex();
            double djk = 1 - parser.ruwDocNT(docIndex, j);
            som += Math.pow(dik / djk, 2.0 / (M - 1.0));
        }

        return 1.0 / som;
    }


    /*
     * Returns a Map containing a weighted sum for all terms. The weights are passed as the parameter "clusterGewichten".
     */
    private Map termenSom(Map clusterGewichten)
    {
        try
        {
            Map termSom = new HashMap();

            for (int i = 0; i < documentGewichten.length; i++)
            {
                double clusterWaarde;

                if (clusterGewichten.containsKey(new Integer(i)))
                {
                    clusterWaarde = ((Double) clusterGewichten.get(new Integer(i))).doubleValue();
                }
                else
                {
                    clusterWaarde = 0;
                }

                if ((clusterWaarde * documentGewichten[i]) > 0.05)
                {
                    Map termIndices = parser.geefDocument(i);

                    for (Iterator it2 = termIndices.keySet().iterator(); it2.hasNext();)
                    {
                        Integer termIndex = ((Integer) it2.next());
                        double termGewicht = ((Double) termIndices.get(termIndex)).doubleValue();

                        if (termSom.containsKey(termIndex))
                        {
                            double oudeWaarde = ((Double) termSom.get(termIndex)).doubleValue();
                            double nieuweWaarde = oudeWaarde
                                + (clusterWaarde * documentGewichten[i] * termGewicht);
                            termSom.put(termIndex, new Double(nieuweWaarde));
                        }
                        else
                        {
                            double nieuweWaarde = clusterWaarde * documentGewichten[i] * termGewicht;

                            if (nieuweWaarde > 0.05)
                            {
                                termSom.put(termIndex, new Double(nieuweWaarde));
                            }
                        }
                    }
                }
            }

            return termSom;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }


    /*
     * Returns the term-index with the heighest weight. Weights are passed as the parameter "termSom"
     */
    private int geefSterksteTerm(Map termSom)
    {
        try
        {
            int maxIndex = -1;
            double maxVal = -1;

            for (Iterator it = termSom.keySet().iterator(); it.hasNext();)
            {
                Integer termIndex = ((Integer) it.next());
                double w = ((Double) termSom.get(termIndex)).doubleValue();

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
     * For recursive application, all terms whose weight is greater than "UITGEBREIDEQUERYFACTOR" times the weight of
     * the term with maximal weight, are considered as query terms.
     */
    protected ArrayList uitgebreideQuery(int maxIndex, Map termSom)
    {
        ArrayList res = new ArrayList();
        res.addAll(query);

        double maxValue = ((Double) termSom.get(new Integer(maxIndex))).doubleValue();

        for (Iterator it = termSom.keySet().iterator(); it.hasNext();)
        {
            int termIndex = ((Integer) it.next()).intValue();
            double termWaarde = ((Double) termSom.get(new Integer(termIndex))).doubleValue();

            if (termWaarde > (UITGEBREIDEQUERYFACTOR * maxValue))
            {
                Element e = new Element("query");
                e.setText(parser.origineleTerm(termIndex));
                res.add(e);
            }
        }

        return res;
    }
}
