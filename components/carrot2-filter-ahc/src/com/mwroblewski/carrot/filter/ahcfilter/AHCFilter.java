

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


import com.dawidweiss.carrot.filter.FilterRequestProcessor;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.AHC;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.*;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage.LinkageMethod;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity.SimilarityMeasure;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.stop.StopCondition;
import com.mwroblewski.carrot.filter.ahcfilter.groups.*;
import com.mwroblewski.carrot.lexical.LexicalElement;
import com.mwroblewski.carrot.utils.LogUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import java.io.InputStream;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Micha� Wr�blewski
 */
public class AHCFilter
    extends FilterRequestProcessor
{
    private final Logger log = Logger.getLogger(this.getClass());

    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response, Map params
    )
        throws Exception
    {
        // obtaining values of parameters
        log.debug("Obtained params:\n" + params);

        AHCFilterParams filterParams = new AHCFilterParams(params);

        // similarity calculating options
        SimilarityMeasure similarityMeasure = filterParams.getSimilarityMeasure();

        // AHC options
        StopCondition condition = filterParams.getStopCondition();

        //float treeCreatingThreshold = filterParams.getTreeCreatingThreshold();
        LinkageMethod linkageMethod = filterParams.getLinkageMethod();

        // groups creation options
        float groupCreatingThreshold = filterParams.getGroupsCreatingThreshold();

        // groups description options
        int maxTerms = filterParams.getMaxDescriptionLength();
        float minOccurrence = filterParams.getMinDescriptionOccurrence();
        Group.showDebugGroupDescription = filterParams.getShowDebugGroupDescription();

        float overlapThreshold = filterParams.getGroupOverlapThreshold();
        float coverageThreshold = filterParams.getGroupOverlapThreshold();

        // groups prunning options
        boolean removeGroupsSimilarWithParents = filterParams.getRemoveGroupsSimilarWithParents();
        float groupsMergingGranularity = 0.0f;

        if (removeGroupsSimilarWithParents)
        {
            groupsMergingGranularity = filterParams.getGroupsMergingGranularity();
        }

        boolean removeGroupsWithoutDescription = filterParams.getRemoveGroupsWithoutDescription();
        boolean mergeGroupsWithSimilarDescriptions = filterParams
            .getMergeGroupsWithSimilarDescriptions();
        boolean removeTopGroup = filterParams.getRemoveTopGroup();

        // phase 1 -
        // creating arrays of terms, their weights in documents
        // and documents IDs from XML input stream
        Date before = new Date();

        Element root = parseXmlStream(carrotData, "UTF-8");
        AHCFilterData data = new AHCFilterData(root);

        LexicalElement [] terms = data.getTerms();
        String [] termsForms = data.getTermsForms();
        float [][] termsWeights = data.getTermsWeights();
        String [] docIDs = data.getDocIDs();

        log.info("read input & created data in " + LogUtils.timeTillNow(before) + " ms.");

        // phase 2 -
        // calculating of similarities between documents
        before = new Date();

        float [][] similarities = similarityMeasure.calculateSimilarity(termsWeights);

        log.info("calcualted similarities in " + LogUtils.timeTillNow(before) + " ms.");
        log.debug("similarities array: ");
        log.debug(LogUtils.arrayToString(similarities));

        // 3 etap -
        // invoking of AHC algorithm which creates a binary tree(s) of documents
        before = new Date();

        AHC ahc = new AHC(
                similarities, similarityMeasure.minSimilarity(), linkageMethod, condition
            );
        LinkedList trees = ahc.group();

        log.info("created tree in " + LogUtils.timeTillNow(before) + " ms.");
        log.debug("And the result is... " + trees);

        // phase 4 -
        // creating clusters and their descriptions (based on the trees)
        before = new Date();

        // creating groups
        Group rootGroup = new Group();
        GroupCreator groupCreator = new GroupCreator();
        GroupDescriptor groupDescriptor = new GroupDescriptor(
                terms, termsForms, termsWeights, maxTerms, minOccurrence, overlapThreshold,
                coverageThreshold
            );
        GroupPostProcessor groupProcessor = new GroupPostProcessor();
        Iterator treesIterator = trees.iterator();

        for (int i = 0; treesIterator.hasNext(); i++)
        {
            // for each of the trees created by AHC
            DendrogramItem tree = (DendrogramItem) treesIterator.next();

            if (tree instanceof DendrogramLeaf)
            {
                // if it's just a single document, it will never form any group
                continue;
            }

            // creating group(s) of this particular tree
            Group [] subgroups = groupCreator.treeToGroups(
                    (DendrogramNode) tree, groupCreatingThreshold
                );

            for (int j = 0; j < subgroups.length; j++)
            {
                rootGroup.addSubgroup(subgroups[j]);
            }
        }

        // creating descriptions and prunning of the result groups
        // merging groups that have equal similarity in node
        // (with given precision) as their parents
        if (removeGroupsSimilarWithParents)
        {
            groupProcessor.removeGroupsSimilarWithParents(rootGroup, groupsMergingGranularity);
        }

        // creating descriptions of the groups
        groupDescriptor.describeGroup(rootGroup, null);

        // mapping documents numbers in groups to their respective IDs
        groupProcessor.mapDocumentIDs(rootGroup, docIDs);

        // merging groups that have no description with their parents
        if (removeGroupsWithoutDescription)
        {
            groupProcessor.removeGroupsWithoutDescription(rootGroup);
        }

        // merging groups that have similar topics and have a common top group
        if (mergeGroupsWithSimilarDescriptions)
        {
            groupProcessor.mergeGroupsWithSimilarDescriptions(rootGroup);
        }

        // if only one top group exists - then replace it by it's children
        // (if it has any, else let it be)
        Vector resultGroups = rootGroup.getSubgroups();

        if (removeTopGroup && (resultGroups.size() == 1))
        {
            Group group = (Group) resultGroups.elementAt(0);

            if (group.size() > 0)
            {
                rootGroup.removeSubgroupsWithMerging((Vector) resultGroups.clone());
            }
        }

        // creating group "others" (containing documents that aren't contained
        // in any of other groups)
        if (resultGroups.size() > 0)
        {
            Group othersGroup = groupProcessor.createOthersGroup(resultGroups, docIDs);

            if (othersGroup != null)
            {
                rootGroup.addSubgroup(othersGroup);
            }
        }

        log.info("created groups & descriptions in " + LogUtils.timeTillNow(before) + " ms.");

        // phase 5 -
        // saving the new data (created groups and their descriptions) to and removing
        // old data (terms, their occurrence in documents and old groups information)
        // from the output XML stream
        before = new Date();

        data.removeLexicalElements();
        data.removeQuery();
        data.saveGroups(rootGroup);
        serializeXmlStream(root, response.getOutputStream(), "UTF-8");

        log.info("saved data in " + LogUtils.timeTillNow(before) + " ms.");
    }
}
