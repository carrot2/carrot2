
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

package com.mwroblewski.carrot.filter.ahcfilter;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage.LinkageMethod;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity.SimilarityMeasure;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.stop.StopCondition;
import java.util.LinkedList;
import java.util.Map;


/**
 * @author Micha� Wr�blewski
 */
public class AHCFilterParams
{
    protected Map params;

    public AHCFilterParams(Map params)
    {
        this.params = params;
    }

    protected String getParam(String paramName)
    {
        LinkedList param = (LinkedList) params.get(paramName);

        return (String) param.get(0);
    }


    public SimilarityMeasure getSimilarityMeasure()
        throws Exception
    {
        Class similarityMeasureClass = Class.forName(getParam("similarityMeasure"));

        return (SimilarityMeasure) similarityMeasureClass.newInstance();
    }


    public float getTreeCreatingThreshold()
    {
        return Float.parseFloat(getParam("treeCreatingThreshold"));
    }


    public LinkageMethod getLinkageMethod()
        throws Exception
    {
        Class linkageMethodClass = Class.forName(getParam("linkageMethod"));

        return (LinkageMethod) linkageMethodClass.newInstance();
    }


    public StopCondition getStopCondition()
        throws Exception
    {
        /*       Class stopConditionClass =
           Class.forName((String) params.get("stopCondition"));
        
                      return (StopCondition) stopConditionClass.newInstance();*/

        //return new GroupingThresholdReachedCondition(getTreeCreatingThreshold());
        return null;
    }


    public float getGroupsCreatingThreshold()
    {
        return Float.parseFloat(getParam("groupsCreatingThreshold"));
    }


    public boolean getRemoveGroupsSimilarWithParents()
    {
        return Boolean.valueOf(getParam("removeGroupsSimilarWithParents")).booleanValue();
    }


    public float getGroupsMergingGranularity()
    {
        return Float.parseFloat(getParam("groupsMergingGranularity"));
    }


    public int getMaxDescriptionLength()
    {
        return Integer.parseInt(getParam("maxDescriptionLength"));
    }


    public float getMinDescriptionOccurrence()
    {
        return Float.parseFloat(getParam("minDescriptionOccurrence"));
    }


    public boolean getShowDebugGroupDescription()
    {
        return Boolean.valueOf(getParam("showDebugGroupDescription")).booleanValue();
    }


    public float getGroupOverlapThreshold()
    {
        return Float.parseFloat(getParam("groupOverlapThreshold"));
    }


    public float getGroupCoverageThreshold()
    {
        return Float.parseFloat(getParam("groupCoverageThreshold"));
    }


    public boolean getRemoveGroupsWithoutDescription()
    {
        return Boolean.valueOf(getParam("removeGroupsWithoutDescription")).booleanValue();
    }


    public boolean getMergeGroupsWithSimilarDescriptions()
    {
        return Boolean.valueOf(getParam("mergeGroupsWithSimilarDescriptions")).booleanValue();
    }


    public boolean getRemoveTopGroup()
    {
        return Boolean.valueOf(getParam("removeTopGroup")).booleanValue();
    }
}
