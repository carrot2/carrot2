

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.ahcfilter.groups;


import com.mwroblewski.carrot.lexical.*;
import java.util.Collections;
import java.util.Vector;


/**
 * @author Micha� Wr�blewski
 */
class GroupDescriptionsPostProcessor
{
    protected LexicalElement [] terms;
    protected float overlapThreshold;
    protected float coverageThreshold;

    GroupDescriptionsPostProcessor(
        LexicalElement [] terms, float overlapThreshold, float coverageThreshold
    )
    {
        this.terms = terms;
        this.overlapThreshold = overlapThreshold;
        this.coverageThreshold = coverageThreshold;
    }

    // returns false if both descriptions are equal !!!
    protected boolean isSubphrase(GroupDescription description1, GroupDescription description2)
    {
        LexicalElement element1 = terms[description1.descriptionID];
        LexicalElement element2 = terms[description2.descriptionID];

        if (element2 instanceof Phrase)
        {
            Phrase p2 = (Phrase) element2;

            if (element1 instanceof Phrase)
            {
                return ((Phrase) element1).isSubphraseOf(p2);
            }
            else
            {
                return p2.containsTerm(((Term) element1).toString());
            }
        }
        else
        {
            return false;
        }
    }


    protected Vector getSubphrases(GroupDescription description, Vector descriptions)
    {
        Vector subphrases = new Vector();

        for (int i = 0; i < descriptions.size(); i++)
        {
            GroupDescription subphrase = (GroupDescription) descriptions.elementAt(i);

            if (isSubphrase(subphrase, description))
            {
                subphrases.add(subphrase);
            }
        }

        return subphrases;
    }


    protected void removeIntermediatePhrases(Vector descriptions)
    {
        // looking for phrases to remove
        Vector descriptionsToRemove = new Vector();

        for (int i = 0; i < descriptions.size(); i++)
        {
            GroupDescription description = (GroupDescription) descriptions.elementAt(i);

            if (terms[description.descriptionID] instanceof Phrase)
            {
                // look for subphrases of this phrase
                Vector subphrases = getSubphrases(description, descriptions);

                // check which subphrases also have any subphrases of their own
                // (i.e. they are intermediate phrases)
                for (int j = 0; j < subphrases.size(); j++)
                {
                    GroupDescription subphrase = (GroupDescription) subphrases.elementAt(j);

                    if (
                        (terms[subphrase.descriptionID] instanceof Phrase)
                            && (getSubphrases(subphrase, descriptions).size() > 0)
                    )
                    {
                        descriptionsToRemove.add(subphrase);
                    }
                }
            }
        }

        descriptions.removeAll(descriptionsToRemove);
    }


    protected void removeMostGeneralPhrasesWithLowCoverage(
        Vector descriptions, float coverageThreshold
    )
    {
        // looking for phrases to remove
        Vector descriptionsToRemove = new Vector();

        for (int i = 0; i < descriptions.size(); i++)
        {
            GroupDescription description = (GroupDescription) descriptions.elementAt(i);

            if (terms[description.descriptionID] instanceof Phrase)
            {
                // look for subphrases of this phrase
                Vector subphrases = getSubphrases(description, descriptions);

                // check if subphrases have their coverage value significantly
                // higher than coverage of their superphrase
                for (int j = 0; j < subphrases.size(); j++)
                {
                    GroupDescription subphrase = (GroupDescription) subphrases.elementAt(j);

                    if ((description.occurrence - subphrase.occurrence) < coverageThreshold)
                    {
                        descriptionsToRemove.add(subphrase);
                    }
                }
            }
        }

        descriptions.removeAll(descriptionsToRemove);
    }


    protected void removeOverlappingPhrases(Vector descriptions, float overlapThreshold)
    {
        // looking for phrases to remove
        Vector descriptionsToRemove = new Vector();
        Collections.sort(descriptions, new DescriptionOccurrenceComparator());

        // we look only if less frequent occuring phrases overlap with
        // more frequent occurring
        for (int i = 0; i < (descriptions.size() - 1); i++)
        {
            GroupDescription description1 = (GroupDescription) descriptions.elementAt(i);

            if (terms[description1.descriptionID] instanceof Phrase)
            {
                Phrase phrase1 = (Phrase) terms[description1.descriptionID];

                for (int j = (i + 1); j < descriptions.size(); j++)
                {
                    GroupDescription description2 = (GroupDescription) descriptions.elementAt(j);

                    if (terms[description2.descriptionID] instanceof Phrase)
                    {
                        Phrase phrase2 = (Phrase) terms[description2.descriptionID];

                        if (phrase2.overlapsWith(phrase1, overlapThreshold))
                        {
                            descriptionsToRemove.add(description2);
                        }
                    }
                }

                descriptions.removeAll(descriptionsToRemove);
            }
        }
    }


    public void removeRedundantDescriptions(Vector descriptions)
    {
        removeIntermediatePhrases(descriptions);
        removeMostGeneralPhrasesWithLowCoverage(descriptions, coverageThreshold);
        removeOverlappingPhrases(descriptions, overlapThreshold);
    }
}
