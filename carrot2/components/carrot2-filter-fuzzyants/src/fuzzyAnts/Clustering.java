
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
package fuzzyAnts;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;


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
    protected List groups;
    protected Set allDocIndices;
    protected double [] documentWeights;
    protected List documents;
    protected List meta;
    protected List query;
    protected boolean stopwords;
    protected int weightSchema;
    protected int depth;
    protected int minHeapSize = 4;
    protected double minDocScore = 0.30;

    public Clustering(
        int depth, List documents, List meta, List query, boolean stopwords, int weightSchema
    )
    {
        this.documents = documents;
        this.meta = meta;
        this.query = query;
        this.stopwords = stopwords;
        this.weightSchema = weightSchema;
        this.depth = depth;

        documentWeights = new double[documents.size()];

        for (int i = 0; i < documentWeights.length; i++)
        {
            documentWeights[i] = 1;
        }

        parser = new SnippetParser(documents, meta, query, stopwords, weightSchema);
    }


    public Clustering(
        int depth, List documents, List meta, List query, boolean stopwords, int weightSchema,
        double [] documentWeights
    )
    {
        this.documents = documents;
        this.meta = meta;
        this.query = query;
        this.stopwords = stopwords;
        this.weightSchema = weightSchema;
        this.depth = depth;
        this.documentWeights = documentWeights;
        parser = new SnippetParser(documents, meta, query, stopwords, weightSchema);
    }

    /*
     *  the actual clustering process
     */
    protected abstract void getSolution();


    /*
     * Returns a list with all documents with weight greater than "minDocScore". The weights of all documents are
     * passed as an argument.
     */
    protected java.util.List getDocuments(double [] weights)
    {
        ArrayList res = new ArrayList();

        for (int i = 0; i < weights.length; i++)
        {
            if (weights[i] > minDocScore)
            {
                res.add(new Integer(i));
            }
        }

        return res;
    }


    /*
     * Returns a restriction of "documents" wich only contains those documents whose index is included in "docIndices"
     */
    protected ArrayList restrictDocuments(List docIndices)
    {
        ArrayList res = new ArrayList();

        for (Iterator it = docIndices.iterator(); it.hasNext();)
        {
            int index = ((Integer) it.next()).intValue();
            res.add(documents.get(index));
        }

        return res;
    }


    /*
     * Returns a restriction of the array of weights "weights" which only contains those weights corresponding with
     * documents whose index is included in "docIndices"
     */
    protected double [] restrictWeights(double [] weights, List docIndices)
    {
        double [] res = new double[docIndices.size()];
        int i = 0;

        for (Iterator it = docIndices.iterator(); it.hasNext();)
        {
            int index = ((Integer) it.next()).intValue();
            res[i++] = weights[index];
        }

        return res;
    }


    /*
     * Adds a new subcluster to the result. The label is chosen, based on the term with index "bestIndex".
     */
    protected void addSubGroup(int bestIndex, Clustering subCluster, Collection docIndices)
    {
        final DocumentFactory factory = new DocumentFactory();
        Element group = factory.createElement("group");
        List subGroups = subCluster.getGroups();

        if (subGroups.size() > 0)
        {
            for (ListIterator it = subGroups.listIterator(); it.hasNext();)
            {
                Element subGroup = (Element) it.next();
                group.add(subGroup);
            }

            Element title = factory.createElement("title");
            String label = getLabel(bestIndex, docIndices);
            Element phrase = factory.createElement("phrase");
            phrase.setText(label);
            title.add(phrase);
            group.add(title);
            groups.add(group);
        }
    }


    /*
     * Adds a cluster to the result. The label is chosen, based on the term with index "bestIndex".
     */
    protected void addDocumentsGroup(int bestIndex, List docIndices)
    {
        final DocumentFactory factory = new DocumentFactory();
        if (docIndices.size() > 0)
        {
            Element group = factory.createElement("group");

            for (Iterator it = docIndices.iterator(); it.hasNext();)
            {
                int index = ((Integer) it.next()).intValue();
                Element e = (Element) documents.get(index);
                String id = e.attributeValue("id");
                Element doc = factory.createElement("document");
                doc.addAttribute("refid", id);
                doc.addAttribute("score", "" + documentWeights[index]);
                group.add(doc);
            }

            Element title = factory.createElement("title");
            String label = getLabel(bestIndex, docIndices);
            Element phrase = factory.createElement("phrase");
            phrase.setText(label);
            title.add(phrase);
            group.add(title);
            groups.add(group);
        }
    }


    /*
     * Adds the cluster "Other..." which contains documents that belong to no other cluster.
     */
    protected void addOther(Set docIndices)
    {
        final DocumentFactory factory = new DocumentFactory();
        ArrayList indices = new ArrayList();

        for (int i = 0; i < documents.size(); i++)
        {
            indices.add(new Integer(i));
        }

        indices.removeAll(docIndices);

        if (indices.size() > 0)
        {
            Element group = factory.createElement("group");

            for (Iterator it = indices.iterator(); it.hasNext();)
            {
                int index = ((Integer) it.next()).intValue();
                Element e = (Element) documents.get(index);
                String id = e.attributeValue("id");
                Element doc = factory.createElement("document");
                doc.addAttribute("refid", id);
                doc.addAttribute("score", "" + documentWeights[index]);
                group.add(doc);
            }

            Element title = factory.createElement("title");
            String label = "Other...";
            Element phrase = factory.createElement("phrase");
            phrase.setText(label);
            title.add(phrase);
            group.add(title);
            groups.add(group);
        }
    }


    /*
     * Used for passing the result of the clustering to e.g. an instance of the class "FuzzyAnts"
     */
    public List getGroups()
    {
        getSolution();

        return groups;
    }


    /*
     * Determination of the label, based on an index of a term and a document collection. A softer variant of completion
     * in the sense of Zhang and Dong is used.
     */
    protected String getLabel(int bestIndex, Collection docIndices)
    {
        try
        {
            return parser.complete(parser.originalTerm(bestIndex), docIndices);
        }
        catch (Exception e)
        {
            return "ERROR";
        }
    }
}
