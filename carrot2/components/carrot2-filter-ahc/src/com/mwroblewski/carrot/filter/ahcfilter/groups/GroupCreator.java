
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

package com.mwroblewski.carrot.filter.ahcfilter.groups;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.*;


/**
 * @author Michał Wróblewski
 */
public class GroupCreator
{
    public Group [] treeToGroups(DendrogramNode node, float groupingThreshold)
    {
        DendrogramItem left = node.getLeft();
        DendrogramItem right = node.getRight();

        float similarity = node.getSimilarity();

        if ((left instanceof DendrogramLeaf) && (right instanceof DendrogramLeaf))
        {
            // 2 leaves
            if (similarity >= groupingThreshold)
            {
                // link both leaves and return a group
                Group group = new Group();
                group.addDocumentID(left.getIndex());
                group.addDocumentID(right.getIndex());
                group.setSimilarity(similarity);

                Group [] result = new Group[1];
                result[0] = group;

                return result;
            }
            else
            {
                return null;
            }
        }
        else if (right instanceof DendrogramLeaf)
        {
            // 1 group and a leaf
            Group [] resultLeft = treeToGroups((DendrogramNode) left, groupingThreshold);

            if (similarity >= groupingThreshold)
            {
                // link the group with the leaf and return a group
                resultLeft[0].addDocumentID(right.getIndex());
                resultLeft[0].setSimilarity(similarity);

                return resultLeft;
            }
            else
            {
                // return the group / (groups ?) at the left side of
                // the current node
                return resultLeft;
            }
        }
        else
        {
            // 2 groups
            Group [] resultLeft = treeToGroups((DendrogramNode) left, groupingThreshold);
            Group [] resultRight = treeToGroups((DendrogramNode) right, groupingThreshold);

            if (similarity >= groupingThreshold)
            {
                // return union of both groups
                Group group = new Group();
                group.addDocumentIDs(resultLeft[0].getDocumentIDs());
                group.addDocumentIDs(resultRight[0].getDocumentIDs());
                group.addSubgroup(resultLeft[0]);
                group.addSubgroup(resultRight[0]);
                group.setSimilarity(similarity);

                Group [] result = new Group[1];
                result[0] = group;

                return result;
            }
            else
            {
                // return groups at the left and right side of the current node
                // separately
                if ((resultLeft == null) && (resultRight == null))
                {
                    return null;
                }
                else
                {
                    int ll = (resultLeft != null) ? resultLeft.length
                                                  : 0;
                    int rl = (resultRight != null) ? resultRight.length
                                                   : 0;

                    Group [] result = new Group[ll + rl];

                    if (resultLeft != null)
                    {
                        System.arraycopy(resultLeft, 0, result, 0, ll);
                    }

                    if (resultRight != null)
                    {
                        System.arraycopy(resultRight, 0, result, ll, rl);
                    }

                    return result;
                }
            }
        }
    }
}
