

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.ahcfilter;


import com.mwroblewski.carrot.filter.ahcfilter.groups.Group;
import com.mwroblewski.carrot.lexical.LexicalElement;
import org.jdom.Element;
import java.util.*;


public class AHCFilterData
{
    protected Element root;
    protected LexicalElement [] terms;
    protected String [] termsForms;
    protected float [][] termsWeights;
    protected String [] docIDs;
    protected HashMap inverseDocIDsMap;
    protected String [] snippets;

    public AHCFilterData(Element root)
    {
        this.root = root;

        List docsList = root.getChildren("document");
        List termsList = root.getChildren("term");

        // loading documents data
        docIDs = new String[docsList.size()];
        inverseDocIDsMap = new HashMap();
        snippets = new String[docsList.size()];

        Iterator docsIterator = docsList.iterator();
        int i = 0;

        for (i = 0; docsIterator.hasNext(); i++)
        {
            Element document = (Element) docsIterator.next();
            docIDs[i] = document.getAttributeValue("id");
            inverseDocIDsMap.put(docIDs[i], new Integer(i));
        }

        // loading lexical elements data
        terms = new LexicalElement[termsList.size()];
        termsForms = new String[terms.length];
        termsWeights = new float[docsList.size()][terms.length];

        Iterator termsIterator = termsList.iterator();

        for (i = 0; termsIterator.hasNext(); i++)
        {
            Element termElement = (Element) termsIterator.next();
            LexicalElement lexicalElement = LexicalElement.fromXML(termElement);
            terms[i] = lexicalElement;

            // loading term's inflected form
            termsForms[i] = termElement.getAttributeValue("form");

            // loading term's weights
            Iterator docWeightsIterator = termElement.getChildren("doc").iterator();

            while (docWeightsIterator.hasNext())
            {
                Element docWeight = (Element) docWeightsIterator.next();
                String docID = docWeight.getAttributeValue("id");
                int docNo = ((Integer) inverseDocIDsMap.get(docID)).intValue();
                float weight = Float.parseFloat(docWeight.getAttributeValue("weight"));

                // attention - rows in termsWeights correspond to documents,
                // columns to terms !!!
                termsWeights[docNo][i] = weight;
            }
        }
    }

    public LexicalElement [] getTerms()
    {
        return terms;
    }


    public String [] getTermsForms()
    {
        return termsForms;
    }


    public float [][] getTermsWeights()
    {
        return termsWeights;
    }


    public String [] getDocIDs()
    {
        return docIDs;
    }


    public String [] getSnippets()
    {
        return snippets;
    }


    public void removeLexicalElements()
    {
        root.removeChildren("term");
        root.removeChildren("phrase");
    }


    public void removeQuery()
    {
        root.removeChildren("query");
    }


    public void saveGroups(Group rootGroup)
    {
        Vector subgroups = rootGroup.getSubgroups();
        Collections.sort(subgroups, Group.groupsComparator);

        for (int i = 0; i < subgroups.size(); i++)
        {
            root.addContent(((Group) subgroups.elementAt(i)).toXML());
        }
    }
}
