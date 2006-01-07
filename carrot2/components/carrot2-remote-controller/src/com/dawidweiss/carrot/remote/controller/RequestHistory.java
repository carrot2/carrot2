
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
package com.dawidweiss.carrot.remote.controller;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Query;
import com.dawidweiss.carrot.remote.controller.process.ProcessDefinition;


/**
 * A request history that can be attached to QueryProcessor.
 */
public final class RequestHistory
{
    private final Query [] queries;
    private final ProcessDefinition [] processes;
    private final long [] tstamp;
    private final int max;
    private int position;
    private int last;  
    
    /**
     * Creates a new request history with some predefined length.
     */
    public RequestHistory(int length)
    {
        position = 0;
        last = 0;
        queries = new Query [ length + 1 ];
        processes = new ProcessDefinition [ length + 1 ];
        tstamp = new long [ length + 1 ];
        max = length + 1;
    }

    public void push(Query query, ProcessDefinition process)
    {
        synchronized (this) {
            tstamp[position] = System.currentTimeMillis();
            queries[position] = query;
            processes[position] = process;
            position = (position + 1) % max;
            if (position == last) {
                last = (last + 1) % max;
            }
        }
    }

    public final int getHistory(int max, Query [] query, ProcessDefinition [] process, long [] tstamps)
    {
        synchronized (this) {
            int from = position;
            int k = 0;
            
            while (from != last && max > 0) {
            	from = from - 1;
            	if (from == -1) from = this.max-1;

                query[k] = this.queries[from];
                process[k] = this.processes[from];
                tstamps[k] = this.tstamp[from];
                
                max--;
                k++;
            }
            return k;
        }
    }


    public final int getHistory(int max, Query [] query, ProcessDefinition [] process)
    {
        synchronized (this) {
            int from = position;
            int k = 0;
            
            while (from != last && max > 0) {
            	from = from - 1;
            	if (from == -1) from = this.max-1;

                query[k] = this.queries[from];
                process[k] = this.processes[from];
                
                max--;
                k++;
            }
            return k;
        }
    }
}
