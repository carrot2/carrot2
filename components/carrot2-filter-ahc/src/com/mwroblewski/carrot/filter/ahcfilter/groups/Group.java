

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


import org.jdom.Element;
import java.util.*;


/**
 * @author Micha� Wr�blewski
 */
class GroupSizeComparator
    implements Comparator
{
    // inverse order !!!
    public int compare(Object o1, Object o2)
        throws ClassCastException
    {
        Group g1 = (Group) o1;
        Group g2 = (Group) o2;

        boolean g1IsOtherTopics = (g1.phrases.size() == 1)
            && (g1.phrases.elementAt(0).equals(Group.OTHER_TOPICS));
        boolean g2IsOtherTopics = (g2.phrases.size() == 1)
            && (g2.phrases.elementAt(0).equals(Group.OTHER_TOPICS));

        if (g1IsOtherTopics)
        {
            return 1;
        }
        else if (g2IsOtherTopics)
        {
            return -1;
        }

        if (g1.size() < g2.size())
        {
            return 1;
        }
        else if (g1.size() == g2.size())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }


    public boolean equals(Object o1, Object o2)
        throws ClassCastException
    {
        Group g1 = (Group) o1;
        Group g2 = (Group) o2;

        return (g1.size() == g2.size());
    }
}



public class Group
{
    public static boolean showDebugGroupDescription = false;
    public static final GroupSizeComparator groupsComparator = new GroupSizeComparator();
    public static final String OTHER_TOPICS = "OTHER TOPICS";
    protected float similarity;
    protected Vector phrases;
    protected Vector debugPhrases;
    protected Vector documentIDs;
    protected Vector subgroups;

    public Group()
    {
        phrases = new Vector();
        debugPhrases = new Vector();
        subgroups = new Vector();
        documentIDs = new Vector();
    }

    public float getSimilarity()
    {
        return similarity;
    }


    public void setSimilarity(float similarity)
    {
        this.similarity = similarity;
    }


    public boolean hasDescription()
    {
        return (phrases.size() > 0);
    }


    public void addPhrase(String phrase)
    {
        phrases.add(phrase);
    }


    public Vector getPhrases()
    {
        return phrases;
    }


    public void addDebugPhrase(String debugPhrase)
    {
        debugPhrases.add(debugPhrase);
    }


    public Vector getDebugPhrases()
    {
        return debugPhrases;
    }


    public void removePhrases(Vector phrases)
    {
        this.phrases.removeAll(phrases);
    }


    public boolean descriptionEquals(Group otherGroup)
    {
        return ((phrases.size() != 0) && phrases.equals(otherGroup.phrases));
    }


    public boolean descriptionIsSubset(Group otherGroup)
    {
        return ((phrases.size() != 0) && otherGroup.phrases.containsAll(phrases)
        && !phrases.equals(otherGroup.phrases));
    }


    public boolean containsDocumentID(String docID)
    {
        return documentIDs.contains(docID);
    }


    public int size()
    {
        return documentIDs.size();
    }


    public Vector getDocumentIDs()
    {
        return documentIDs;
    }


    public void addDocumentID(int documentID)
    {
        documentIDs.add(documentID + "");
    }


    public void addDocumentID(String documentID)
    {
        documentIDs.add(documentID);
    }


    public void addDocumentIDs(Vector documentIDs)
    {
        this.documentIDs.addAll(documentIDs);
    }


    public void addSubgroup(Group subgroup)
    {
        subgroups.add(subgroup);
    }


    public void addSubgroups(Vector subgroups)
    {
        this.subgroups.addAll(subgroups);
    }


    public void removeSubgroups(Vector subgroups)
    // removes all given subgroups from this group's subgroups (but doesn't
    // remove documents contained in these subgroups from this group)
    {
        this.subgroups.removeAll(subgroups);
    }


    public void removeSubgroupsWithMerging(Vector subgroups)
    // removes all given subgroups from this group's subgroups (but doesn't
    // remove documents contained in these subgroups from this group), and
    // adds subgroups of removed subgroups to subgroups of this group ;)
    {
        this.subgroups.removeAll(subgroups);

        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);
            addSubgroups(subgroup.subgroups);
        }
    }


    public Vector getSubgroups()
    {
        return subgroups;
    }


    public Element toXML()
    {
        Element group = new Element("group");

        // creating group's <title> subelement
        Element title = new Element("title");
        Element phrase = new Element("phrase");
        phrase.addContent(similarity + "");

        for (int i = 0; i < phrases.size(); i++)
        {
            phrase = new Element("phrase");
            phrase.addContent((String) phrases.elementAt(i));
            title.addContent(phrase);
        }

        if (showDebugGroupDescription)
        {
            for (int i = 0; i < debugPhrases.size(); i++)
            {
                phrase = new Element("phrase");
                phrase.addContent("|" + (String) debugPhrases.elementAt(i) + "|");
                title.addContent(phrase);
            }
        }

        group.addContent(title);

        // creating group's <document> subelements
        for (int i = 0; i < documentIDs.size(); i++)
        {
            Element document = new Element("document");
            document.setAttribute("refid", (String) documentIDs.elementAt(i));
            group.addContent(document);
        }

        // creating group's <group> subelements
        Collections.sort(subgroups, groupsComparator);

        for (int i = 0; i < subgroups.size(); i++)
        {
            Element subgroup = ((Group) subgroups.elementAt(i)).toXML();
            group.addContent(subgroup);
        }

        return group;
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer("");
        result.append(phrases);
        result.append(subgroups);
        result.append(documentIDs);

        return result.toString();

        /*StringBuffer result = new StringBuffer("");
        
                      result.append("---------------\n");
                      result.append("Similarity: " + similarity + "\n");
        
                      result.append("Title: ");
                      for (int i = 0; i < phrases.size(); i++)
                      {
                          result.append((String) phrases.elementAt(i) + " ");
                      }
                      result.append("\n");
        
                      result.append("Documents: ");
                      for (int i = 0; i < documentIDs.size(); i++)
                      {
                          result.append((String) documentIDs.elementAt(i) + " ");
                      }
                      result.append("\n");
        
                      result.append("Subgroups:\n");
                      // invoking recursively for subgroups
                      for (int i = 0; i < subgroups.size(); i++)
                      {
                          result.append(((Group) subgroups.elementAt(i)).toString());
                      }
                      result.append("---------------\n");
        
                      return result.toString();*/
    }
}
