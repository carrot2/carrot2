

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
 *
 * Abstract class that contains some methods wich are needed during the snippet clustering process. Possible extensions
 * include snippet/document clustering and term clustering. We will assume that the class is used for document/snippet
 * clustering.
 *
 * @author Steven Schockaert
 */
public abstract class Clustering
    implements Constants
{
    protected SnippetParser parser;
    protected List groepen;
    protected Set alleDocIndices;
    protected double [] documentGewichten;
    protected List documenten;
    protected List meta;
    protected List query;
    protected boolean stopwoorden;
    protected int gewichtSchema;
    protected int diepte;
    protected int minHoopSize = 4;
    protected double minDocScore = 0.30;

    public Clustering(
        int diepte, List documenten, List meta, List query, boolean stopwoorden, int gewichtSchema
    )
    {
        this.documenten = documenten;
        this.meta = meta;
        this.query = query;
        this.stopwoorden = stopwoorden;
        this.gewichtSchema = gewichtSchema;
        this.diepte = diepte;

        documentGewichten = new double[documenten.size()];

        for (int i = 0; i < documentGewichten.length; i++)
        {
            documentGewichten[i] = 1;
        }

        parser = new SnippetParser(documenten, meta, query, stopwoorden, gewichtSchema);
    }


    public Clustering(
        int diepte, List documenten, List meta, List query, boolean stopwoorden, int gewichtSchema,
        double [] documentGewichten
    )
    {
        this.documenten = documenten;
        this.meta = meta;
        this.query = query;
        this.stopwoorden = stopwoorden;
        this.gewichtSchema = gewichtSchema;
        this.diepte = diepte;
        this.documentGewichten = documentGewichten;
        parser = new SnippetParser(documenten, meta, query, stopwoorden, gewichtSchema);
    }

    /*
     *  the actual clustering process
     */
    protected abstract void bepaalOplossing();


    /*
     * Returns a list with all documents with weight greater than "minDocScore". The weights of all documents are
     * passed as an argument.
     */
    protected java.util.List bepaalDocumenten(double [] gewichten)
    {
        ArrayList res = new ArrayList();

        for (int i = 0; i < gewichten.length; i++)
        {
            if (gewichten[i] > minDocScore)
            {
                res.add(new Integer(i));
            }
        }

        return res;
    }


    /*
     * Returns a restriction of "documenten" wich only contains those documents whose index is included in "docIndices"
     */
    protected ArrayList beperkDocumenten(List docIndices)
    {
        ArrayList res = new ArrayList();

        for (Iterator it = docIndices.iterator(); it.hasNext();)
        {
            int index = ((Integer) it.next()).intValue();
            res.add(documenten.get(index));
        }

        return res;
    }


    /*
     * Returns a restriction of the array of weights "gewichten" which only contains those weights corresponding with
     * documents whose index is included in "docIndices"
     */
    protected double [] beperkGewichten(double [] gewichten, List docIndices)
    {
        double [] res = new double[docIndices.size()];
        int i = 0;

        for (Iterator it = docIndices.iterator(); it.hasNext();)
        {
            int index = ((Integer) it.next()).intValue();
            res[i++] = gewichten[index];
        }

        return res;
    }


    /*
     * Adds a new subcluster to the result. The label is chosen, based on the term with index "sterksteIndex".
     */
    protected void addSubGroep(int sterksteIndex, Clustering subCluster, Collection docIndices)
    {
        Element groep = new Element("group");
        List subGroepen = subCluster.geefGroepen();

        if (subGroepen.size() > 0)
        {
            for (ListIterator it = subGroepen.listIterator(); it.hasNext();)
            {
                Element subGroep = (Element) it.next();
                groep.addContent(subGroep);
            }

            Element title = new Element("title");
            String label = bepaalLabel(sterksteIndex, docIndices);
            Element phrase = new Element("phrase");
            phrase.setText(label);
            title.addContent(phrase);
            groep.addContent(title);
            groepen.add(groep);
        }
    }


    /*
     * Adds a cluster to the result. The label is chosen, based on the term with index "sterksteIndex".
     */
    protected void addDocumentenGroep(int sterksteIndex, List docIndices)
    {
        if (docIndices.size() > 0)
        {
            Element groep = new Element("group");

            for (Iterator it = docIndices.iterator(); it.hasNext();)
            {
                int index = ((Integer) it.next()).intValue();
                Element e = (Element) documenten.get(index);
                String id = e.getAttributeValue("id");
                Element doc = new Element("document");
                doc.setAttribute("refid", id);
                doc.setAttribute("score", "" + documentGewichten[index]);
                groep.addContent(doc);
            }

            Element title = new Element("title");
            String label = bepaalLabel(sterksteIndex, docIndices);
            Element phrase = new Element("phrase");
            phrase.setText(label);
            title.addContent(phrase);
            groep.addContent(title);
            groepen.add(groep);
        }
    }


    /*
     * Adds the cluster "Other..." which contains documents that belong to no other cluster.
     */
    protected void addAndere(Set docIndices)
    {
        ArrayList indices = new ArrayList();

        for (int i = 0; i < documenten.size(); i++)
        {
            indices.add(new Integer(i));
        }

        indices.removeAll(docIndices);

        if (indices.size() > 0)
        {
            Element groep = new Element("group");

            for (Iterator it = indices.iterator(); it.hasNext();)
            {
                int index = ((Integer) it.next()).intValue();
                Element e = (Element) documenten.get(index);
                String id = e.getAttributeValue("id");
                Element doc = new Element("document");
                doc.setAttribute("refid", id);
                doc.setAttribute("score", "" + documentGewichten[index]);
                groep.addContent(doc);
            }

            Element title = new Element("title");
            String label = "Other...";
            Element phrase = new Element("phrase");
            phrase.setText(label);
            title.addContent(phrase);
            groep.addContent(title);
            groepen.add(groep);
        }
    }


    /*
     * Used for passing the result of the clustering to e.g. an instance of the class "FuzzyAnts"
     */
    public List geefGroepen()
    {
        bepaalOplossing();

        return groepen;
    }


    /*
     * Determination of the label, based on an index of a term and a document collection. A softer variant of completion
     * in the sense of Zhang and Dong is used.
     */
    protected String bepaalLabel(int sterksteIndex, Collection docIndices)
    {
        try
        {
            return parser.compleet(parser.origineleTerm(sterksteIndex), docIndices);
        }
        catch (Exception e)
        {
            return "ERROR";
        }
    }
}
