

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


package com.mwroblewski.carrot.filter.ahcfilter.groups;


import com.mwroblewski.carrot.lexical.*;
import java.util.Arrays;
import java.util.Vector;


/**
 * @author Micha� Wr�blewski
 */
public class GroupDescriptor
{
    protected LexicalElement [] terms;
    protected String [] termsForms;
    protected float [][] termsWeights;
    protected int maxTerms;
    protected float minOccurrence;
    protected GroupDescriptionsPostProcessor descriptionsPostProcessor;

    public GroupDescriptor(
        LexicalElement [] terms, String [] termsForms, float [][] termsWeights, int maxTerms,
        float minOccurrence, float overlapThreshold, float coverageThreshold
    )
    {
        this.terms = terms;
        this.termsForms = termsForms;
        this.termsWeights = termsWeights;
        this.maxTerms = maxTerms;
        this.minOccurrence = minOccurrence;
        descriptionsPostProcessor = new GroupDescriptionsPostProcessor(
                terms, overlapThreshold, coverageThreshold
            );
    }

    protected boolean isStopDescription(LexicalElement description, Vector stopList)
    {
        for (int i = 0; i < stopList.size(); i++)
        {
            LexicalElement stopDescription = (LexicalElement) stopList.elementAt(i);

            if (stopDescription.equals(description))
            {
                return true;
            }
            else if (stopDescription instanceof Phrase)
            {
                Phrase p = (Phrase) stopDescription;

                if (description instanceof Phrase)
                {
                    if (((Phrase) description).isSubphraseOf(p))
                    {
                        return true;
                    }
                }
                else
                {
                    if (p.containsTerm(((Term) description).toString()))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    protected GroupDescription [] findDescriptions(
        int [] docIDs, int maxTerms, int [] occurrences, float minOccurrence, Vector stopList
    )
    {
        Vector descriptions = new Vector();

        // preparing data
        for (int i = 0; i < terms.length; i++)
        {
            // checking if i-th term may may be a part of description
            float occurrence = (float) occurrences[i] / (float) docIDs.length;

            if ((occurrence >= minOccurrence) && !isStopDescription(terms[i], stopList))
            {
                // current term MAY be a part of description (it is not present
                // in the "stop-list" and occurs in group enough often to be one.
                // It will be a description, if it is among the best terms
                // calculating summary weight of this term in the docuemnts
                // of given group
                float descriptionWeight = 0.0f;

                for (int j = 0; j < docIDs.length; j++)
                {
                    descriptionWeight += termsWeights[docIDs[j]][i];
                }

                descriptions.add(new GroupDescription(i, descriptionWeight, occurrence));
            }
        }

        // removing redundant descriptions
        descriptionsPostProcessor.removeRedundantDescriptions(descriptions);

        // sorting descriptions according to their weight in group
        GroupDescription [] descriptionsArray = new GroupDescription[0];
        descriptionsArray = (GroupDescription []) descriptions.toArray(descriptionsArray);
        Arrays.sort(descriptionsArray, new DescriptionWeightComparator());

        // returning max. maxTerms best elements
        int descriptionsCount = (descriptionsArray.length >= maxTerms) ? maxTerms
                                                                       : descriptionsArray.length;
        GroupDescription [] result = new GroupDescription[descriptionsCount];

        System.arraycopy(descriptionsArray, 0, result, 0, descriptionsCount);

        return result;
    }


    public void describeGroup(Group group, Vector stopList)
    {
        if (maxTerms < 1)
        {
            return;
        }

        if (stopList == null)
        {
            stopList = new Vector();
        }
        else
        {
            stopList = (Vector) stopList.clone();
        }

        // creating description for the current group
        // checking in how many documents of the current group do the
        // individual terms occur, and creating an array with IDs of
        // documents in the current group
        Vector docs = group.getDocumentIDs();

        int [] occurrences = new int[termsForms.length];
        int [] docIDs = new int[docs.size()];

        for (int i = 0; i < docs.size(); i++)
        {
            // creating an array wiht IDs of documents of the current group
            int id = Integer.parseInt((String) docs.elementAt(i));
            docIDs[i] = id;

            for (int j = 0; j < termsWeights[id].length; j++)
            {
                if (termsWeights[id][j] > 0.0f)
                {
                    // increasing j-th term's occurrence
                    occurrences[j]++;
                }
            }
        }

        // searching for max. maxTerms most important common terms
        GroupDescription [] descriptions = findDescriptions(
                docIDs, maxTerms, occurrences, minOccurrence, stopList
            );

        //group.addDebugPhrase("sim: " + group.similarity);
        // creating group's description based on previously found
        // most important common terms
        for (int i = 0; i < descriptions.length; i++)
        {
            // mapping terms / phrases stems in groups descriptions to
            // their respective forms
            String form = termsForms[descriptions[i].descriptionID];
            group.addPhrase(form);

            //group.addPhrase(descriptions[i].description.toString());
            // appending terms in description to stop-list
            stopList.add(terms[descriptions[i].descriptionID]);
        }

        // creating of descriptions for the subgroups of current group
        Vector subgroups = group.getSubgroups();

        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);
            describeGroup(subgroup, stopList);
        }
    }
}
