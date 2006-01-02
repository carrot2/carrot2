
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
package com.mwroblewski.carrot.filter.ahcfilter.groups;


import com.mwroblewski.carrot.utils.MathUtils;
import org.apache.log4j.Logger;
import java.util.Vector;


/**
 * @author Micha� Wr�blewski
 */
public class GroupPostProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());

    public void mapDocumentIDs(Group group, String [] newIDs)
    {
        Vector subgroups = group.getSubgroups();

        // invoking mapping recursively for subgroups of current group
        for (int i = 0; i < subgroups.size(); i++)
        {
            mapDocumentIDs((Group) subgroups.elementAt(i), newIDs);
        }

        // mapping of documents IDs in the current group
        Vector documentIDs = group.getDocumentIDs();

        for (int i = 0; i < documentIDs.size(); i++)
        {
            int id = Integer.parseInt((String) documentIDs.elementAt(i));
            documentIDs.set(i, newIDs[id]);
        }
    }


    public void removeGroupsSimilarWithParents(Group group, float groupsMergingGranularity)
    {
        // invoking removing recursively for subgroups of current group
        Vector subgroups = group.getSubgroups();

        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);
            removeGroupsSimilarWithParents(subgroup, groupsMergingGranularity);
        }

        // looking for subgroups which have equal similarity (with given
        // precision) as current group
        float similarity = group.getSimilarity();
        similarity = MathUtils.round(similarity, groupsMergingGranularity);
        group.setSimilarity(similarity);

        Vector subgroupsToRemove = new Vector();

        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);

            float subgroupSimilairty = subgroup.getSimilarity();
            subgroupSimilairty = MathUtils.round(subgroupSimilairty, groupsMergingGranularity);

            if (similarity == subgroupSimilairty)
            {
                subgroupsToRemove.add(subgroup);
            }
        }

        // removing them
        group.removeSubgroupsWithMerging(subgroupsToRemove);
    }


    public void removeGroupsWithoutDescription(Group group)
    {
        Vector subgroups = group.getSubgroups();

        // invoking removing recursively for subgroups of current group
        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);
            removeGroupsWithoutDescription(subgroup);
        }

        // looking for subgroups which have no description
        Vector subgroupsToRemove = new Vector();

        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);

            if (!subgroup.hasDescription())
            {
                subgroupsToRemove.add(subgroup);
            }
        }

        // removing them
        group.removeSubgroupsWithMerging(subgroupsToRemove);
    }


    public void mergeGroupsWithSimilarDescriptions(Group group)
    {
        Vector subgroups = group.getSubgroups();

        // invoking merging recursively for subgroups of current group
        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup = (Group) subgroups.elementAt(i);
            mergeGroupsWithSimilarDescriptions(subgroup);
        }

        // looking for subgroups with equal descriptions
        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup1 = (Group) subgroups.elementAt(i);

            // create a list of subgroups that should be attached to this subgroup
            Vector subgroupsToAttach = new Vector();

            for (int j = (i + 1); j < subgroups.size(); j++)
            {
                Group subgroup2 = (Group) subgroups.elementAt(j);

                if (subgroup1.descriptionEquals(subgroup2))
                {
                    log.debug(subgroup1 + " equals " + subgroup2);
                    subgroupsToAttach.add(subgroup2);
                }
            }

            // merging subgroups
            if (subgroupsToAttach.size() > 0)
            {
                subgroup1.addDebugPhrase("EQUALS");

                for (int j = 0; j < subgroupsToAttach.size(); j++)
                {
                    Group subgroup2 = (Group) subgroupsToAttach.elementAt(j);
                    subgroup1.addSubgroups(subgroup2.getSubgroups());
                    subgroup1.addDocumentIDs(subgroup2.getDocumentIDs());
                }

                group.removeSubgroups(subgroupsToAttach);
                log.debug("group1 after adding: " + subgroup1);
            }
        }

        // looking for subgroups whose descirptions are subsets of
        // descriptions of other groups
        for (int i = 0; i < subgroups.size(); i++)
        {
            Group subgroup1 = (Group) subgroups.elementAt(i);

            // create a list of subgroups that should be attached to this subgroup
            int shift = 0;
            Vector subgroupsToAttach = new Vector();

            for (int j = 0; j < subgroups.size(); j++)
            {
                if (j != i)
                {
                    Group subgroup2 = (Group) subgroups.elementAt(j);

                    if (subgroup1.descriptionIsSubset(subgroup2))
                    {
                        log.debug(subgroup1 + " is a subset of: " + subgroup2);
                        subgroupsToAttach.add(subgroup2);
                        subgroup2.addDebugPhrase("SUPERSET");

                        if (j < i)
                        {
                            shift--;
                        }
                    }
                }
            }

            // attaching subgroups
            if (subgroupsToAttach.size() > 0)
            {
                subgroup1.addDebugPhrase("SUBSET");
                subgroup1.addSubgroups(subgroupsToAttach);

                for (int j = 0; j < subgroupsToAttach.size(); j++)
                {
                    Group subgroup2 = (Group) subgroupsToAttach.elementAt(j);
                    subgroup2.removePhrases(subgroup1.getPhrases());
                    subgroup1.addDocumentIDs(subgroup2.getDocumentIDs());
                }

                group.removeSubgroups(subgroupsToAttach);
                i += shift;
                log.debug("group1 after adding: " + subgroup1);
            }
        }
    }


    public Group createOthersGroup(Vector groups, String [] docIDs)
    {
        Group othersGroup = new Group();

        for (int i = 0; i < docIDs.length; i++)
        {
            String id = docIDs[i];
            boolean isContained = false;

            for (int j = 0; j < groups.size(); j++)
            {
                Group group = (Group) groups.elementAt(j);

                if (group.containsDocumentID(id))
                {
                    isContained = true;

                    break;
                }
            }

            if (!isContained)
            {
                othersGroup.addDocumentID(id);
            }
        }

        if (othersGroup.size() > 0)
        {
            othersGroup.addPhrase(Group.OTHER_TOPICS);

            return othersGroup;
        }
        else
        {
            return null;
        }
    }
}
