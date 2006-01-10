
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

package com.stachoodev.carrot.odp.tools;

import java.io.*;
import java.util.*;

import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.index.*;

/**
 * Dumps some information (catid, size, path) about ODP topics to a file.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TopicDumper
{
    /**
     * @param odpIndexDir
     * @param outFileBase
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void dump(String odpIndexDir, String outFileBase)
        throws IOException, ClassNotFoundException
    {
        ODPIndex.initialize(odpIndexDir);
        PrimaryTopicIndex primaryIndex = ODPIndex.getPrimaryTopicIndex();

        List dump = new ArrayList();
        int i = 0;
        for (Iterator paths = primaryIndex.getAllLocations(); paths.hasNext();)
        {
            Topic topic = ODPIndex.getTopic((Location) paths.next());
            Object [] line = new Object [3];
            line[0] = Integer.toString(topic.getCatid());
            line[1] = new Integer(topic.getExternalPages().size());
            line[2] = topic.getId();

            dump.add(line);

            if (i % 500 == 0)
            {
                System.out.println(i + " topics dupmed");
            }
            i++;
        }

        // Sort by size, path
        System.out.println("Sorting by size...");
        Collections.sort(dump, new Comparator()
        {
            public boolean equals(Object obj)
            {
                return false;
            }

            public int compare(Object o1, Object o2)
            {
                Object [] l1 = (Object []) o1;
                Object [] l2 = (Object []) o2;

                if (((Integer) l1[1]).intValue() > ((Integer) l2[1]).intValue())
                {
                    return 1;
                }
                else if (((Integer) l1[1]).intValue() < ((Integer) l2[1])
                    .intValue())
                {
                    return -1;
                }
                else
                {
                    return ((String) l1[2]).compareToIgnoreCase((String) l2[2]);
                }
            }
        });

        System.out.println("Writing...");
        PrintStream out = new PrintStream(new FileOutputStream(outFileBase
            + "-size"));
        for (Iterator iter = dump.iterator(); iter.hasNext();)
        {
            Object [] line = (Object []) iter.next();
            out.println(line[0].toString() + "\t" + line[1].toString() + "\t"
                + line[2].toString());
        }
        out.close();

        // Sort by size, path
        System.out.println("Sorting by path...");
        Collections.sort(dump, new Comparator()
        {
            public boolean equals(Object obj)
            {
                return false;
            }

            public int compare(Object o1, Object o2)
            {
                Object [] l1 = (Object []) o1;
                Object [] l2 = (Object []) o2;

                int result = ((String) l1[2])
                    .compareToIgnoreCase((String) l2[2]);
                if (result != 0)
                {
                    return result;
                }
                else
                {
                    return ((Integer) l1[1]).compareTo((Integer)l2[1]);
                }
            }
        });

        System.out.println("Writing...");
        out = new PrintStream(new FileOutputStream(outFileBase + "-path"));
        for (Iterator iter = dump.iterator(); iter.hasNext();)
        {
            Object [] line = (Object []) iter.next();
            out.println(line[0].toString() + "\t" + line[1].toString() + "\t"
                + line[2].toString());
        }
        out.close();
    }

    /**
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String [] args) throws IOException,
        ClassNotFoundException
    {
        String outFileBase = System.getProperty("odp.dump.out");
        if (outFileBase == null)
        {
            throw new RuntimeException(
                "Property 'odp.dump.out' must be defined to point to the output file path.");
        }

        String odpIndexDir = System.getProperty("odp.index.dir");
        if (odpIndexDir == null)
        {
            throw new RuntimeException(
                "Property 'odp.index.dir' must be defined to point an ODP index.");
        }

        TopicDumper topicDumper = new TopicDumper();
        topicDumper.dump(odpIndexDir, outFileBase);
    }
}