/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.local.benchmark;

import java.util.*;

import com.dawidweiss.carrot.util.common.*;

/**
 * Stores details about some characteristics of an ODP query, such as the number
 * of topics mixed, separation level, query type etc.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ODPQuery
{
    /** Query type */
    private String type;

    /** The number of topics mixed */
    private int topics = 0;

    /** Separation level */
    private int separation = -1;

    /** Number of outlier categories */
    private int outlierCount = -1;

    /** Outlier size */
    private int outlierSize = -1;

    /** String representation */
    private String stringRepresentation;

    /** Are the topic sizes balanced */
    private boolean balanced = false;

    /** Query text */
    private String queryText;

    /**
     * No public constructor.
     */
    private ODPQuery()
    {
    }

    /**
     * @param separationLevel
     * @param query
     * @return
     */
    public static ODPQuery createSeparationTest(int separationLevel,
        boolean balanced, String queryText)
    {
        ODPQuery query = new ODPQuery();

        query.setQueryText(queryText);
        query.separation = separationLevel;
        query.balanced = balanced;
        query.type = "Sept";

        return query;
    }

    /**
     * @param separationLevel
     * @param queryText
     * @return
     */
    public static ODPQuery createOutlierTest(int separationLevel,
        int outlierCount, int outlierSize, String queryText)
    {
        ODPQuery query = new ODPQuery();

        query.setQueryText(queryText);
        query.outlierCount = outlierCount;
        query.outlierSize = outlierSize;
        query.separation = separationLevel;
        query.balanced = false;
        query.type = "Outl";

        return query;
    }

    /**
     * @param separationLevel
     * @param queryText
     * @return
     */
    public static ODPQuery createPerformanceTest(String queryText)
    {
        ODPQuery query = new ODPQuery();

        query.setQueryText(queryText);
        query.outlierCount = -1;
        query.outlierSize = -1;
        query.separation = -1;
        query.balanced = false;
        query.type = "Perf";

        return query;
    }

    /**
     * @param queryText
     */
    private void setQueryText(String queryText)
    {
        this.queryText = queryText;
        this.topics = StringUtils.split(
            queryText.substring(queryText.indexOf(':') + 1), ' ',
            new ArrayList()).size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (stringRepresentation == null)
        {
            StringBuffer stringBuffer = new StringBuffer();

            stringBuffer.append(type);

            if (topics != -1)
            {
                stringBuffer.append("-tc-");
                stringBuffer.append(Integer.toString(topics));
            }

            stringBuffer.append("-ba-");
            if (balanced)
            {
                stringBuffer.append("t");
            }
            else
            {
                stringBuffer.append("f");
            }

            if (separation != -1)
            {
                stringBuffer.append("-sl-");
                stringBuffer.append(Integer.toString(separation));
            }

            if (outlierCount != -1)
            {
                stringBuffer.append("-oc-");
                stringBuffer.append(Integer.toString(outlierCount));
            }

            if (outlierSize != -1)
            {
                stringBuffer.append("-os-");
                stringBuffer.append(Integer.toString(outlierSize));
            }

            stringRepresentation = stringBuffer.toString();
        }

        return stringRepresentation;
    }

    /**
     * Returns this ODPQuery's <code>queryText</code>.
     * 
     * @return
     */
    public String getQueryText()
    {
        return queryText;
    }

    /**
     * @param info
     */
    public void addToMap(Map info)
    {
        info.put("Query", toString());
        info.put("Test", type);

        if (balanced)
        {
            info.put("Balanced Size", "true");
        }
        else
        {
            info.put("Balanced Size", "false");
        }

        if (topics != -1)
        {
            info.put("Topic Count", Integer.toString(topics));
        }
        else
        {
            info.put("Topic Count", "");
        }

        if (separation != -1)
        {
            info.put("Separation", Integer.toString(separation));
        }
        else
        {
            info.put("Separation", "");
        }

        if (outlierCount != -1)
        {
            info.put("Outlier Count", Integer.toString(outlierCount));
        }
        else
        {
            info.put("Outlier Count", "");
        }   

        if (outlierSize != -1)
        {
            info.put("Outlier Size", Integer.toString(outlierSize));
        }
        else
        {
            info.put("Outlier Size", "");
        }
    }
}