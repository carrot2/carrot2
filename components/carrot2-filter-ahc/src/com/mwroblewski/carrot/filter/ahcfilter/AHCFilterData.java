
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
package com.mwroblewski.carrot.filter.ahcfilter;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Element;

import com.dawidweiss.carrot.util.Dom4jUtils;
import com.mwroblewski.carrot.filter.ahcfilter.groups.Group;
import com.mwroblewski.carrot.lexical.LexicalElement;


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

        List docsList = root.elements("document");
        List termsList = root.elements("term");

        // loading documents data
        docIDs = new String[docsList.size()];
        inverseDocIDsMap = new HashMap();
        snippets = new String[docsList.size()];

        Iterator docsIterator = docsList.iterator();
        int i = 0;

        for (i = 0; docsIterator.hasNext(); i++)
        {
            Element document = (Element) docsIterator.next();
            docIDs[i] = document.attributeValue("id");
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
            termsForms[i] = termElement.attributeValue("form");

            // loading term's weights
            Iterator docWeightsIterator = termElement.elements("doc").iterator();

            while (docWeightsIterator.hasNext())
            {
                Element docWeight = (Element) docWeightsIterator.next();
                String docID = docWeight.attributeValue("id");
                int docNo = ((Integer) inverseDocIDsMap.get(docID)).intValue();
                float weight = Float.parseFloat(docWeight.attributeValue("weight"));

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
        Dom4jUtils.removeChildren(root, "term");
        Dom4jUtils.removeChildren(root, "phrase");
    }


    public void removeQuery()
    {
        Dom4jUtils.removeChildren(root, "query");
    }


    public void saveGroups(Group rootGroup)
    {
        Vector subgroups = rootGroup.getSubgroups();
        Collections.sort(subgroups, Group.groupsComparator);

        for (int i = 0; i < subgroups.size(); i++)
        {
            root.add(((Group) subgroups.elementAt(i)).toXML());
        }
    }
}
