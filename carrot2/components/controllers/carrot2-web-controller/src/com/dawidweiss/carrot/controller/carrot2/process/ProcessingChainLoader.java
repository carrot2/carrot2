

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.controller.carrot2.process;


import com.dawidweiss.carrot.controller.carrot2.components.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.*;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;


/**
 * Loads descriptions of processes.
 */
public class ProcessingChainLoader
{
    private static final Logger log = Logger.getLogger(ProcessingChainLoader.class);
    private List processes = new ArrayList(5);
    private ComponentsLoader components;

    /** A HashMap to speed up lookups. */
    private Map processesByNameKey = new HashMap();

    /** Anchors to process descriptor sources (used in reloading) */
    private Map anchorsByNameKey = new HashMap();

    /** If true, supports reloading (and anchors) */
    private final boolean reloading = true;

    /**
     * Creates an instance of ProcessLoader with a given set of components, which it can reference.
     */
    public ProcessingChainLoader(ComponentsLoader components)
    {
        this.components = components;
    }

    /**
     * Adds all processes contained in .xml files from a certain directory.
     *
     * @return The number of processes added.
     */
    public int addProcessesFromDirectory(File directory)
    {
        if (!directory.isDirectory())
        {
            throw new IllegalArgumentException(
                "Argument must be a directory: " + directory.getAbsolutePath()
            );
        }

        int count = 0;
        File [] files = directory.listFiles(
                new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith(".xml");
                    }
                }
            );

        // attempt to load all files and instantiate components in them.
        for (int i = 0; i < files.length; i++)
        {
            try
            {
                ProcessDescriptorAnchor anchor = new ProcessDescriptorFileAnchor(files[i]);
                InputStream stream = anchor.openStream();

                if (stream == null)
                {
                    throw new RuntimeException("Process descriptor stream cannot be opened.");
                }

                try
                {
                    ProcessDescriptor process = ProcessDescriptor.unmarshal(
                            new InputStreamReader(stream, "UTF-8")
                        );
                    addProcess(process, anchor);
                }
                finally
                {
                    stream.close();
                }
            }
            catch (ComponentNotAvailableException e)
            {
                log.error(
                    "Cannot add ProcessingChain file " + files[i]
                    + " because referenced component is not available: " + e.getMessage()
                );
            }
            catch (Exception e)
            {
                log.error(
                    "Problems adding ProcessingChain file: " + files[i] + " (" + e.toString() + ")",
                    e
                );
            }
        }

        return count;
    }


    /**
     * Removes the process from the list of valid processes.
     */
    protected void removeProcess(String nameKey)
    {
        synchronized (this)
        {
            Map processesByName = new HashMap(this.processesByNameKey);
            Object process = processesByName.remove(nameKey);
            this.processesByNameKey = processesByName;

            Map tmp = new HashMap(this.anchorsByNameKey);
            tmp.remove(nameKey);
            this.anchorsByNameKey = tmp;

            List list = new ArrayList(this.processes);
            list.remove(process);
            this.processes = list;

            updateSortedList();
        }
    }


    /**
     * Adds a process.
     */
    public void addProcess(ProcessDescriptor p, ProcessDescriptorAnchor anchor)
        throws ComponentNotAvailableException
    {
        synchronized (this)
        {
            if (processesByNameKey.containsKey(p.getId()))
            {
                // duplicated component. Just warn and do nothing.
                log.warn(
                    "Attempting to add a duplicated processing chain: " + p.getId() + " ("
                    + p.getDescription() + ")"
                );

                return;
            }

            ProcessDefinition process;

            if (p.getProcessingChain() != null)
            {
                // resolve the processing chain binding against the currently known
                // components.
                process = new ResolvedProcessingChain(this.getComponentLoader(), p);
            }
            else if (p.getProcessingScript() != null)
            {
                process = new ResolvedScriptedProcess(this.getComponentLoader(), p);
            }
            else
            {
                throw new RuntimeException("Unsupported process type.");
            }

            List list = new ArrayList(this.processes);
            list.add(process);
            this.processes = list;
            updateSortedList();

            Map processesByName = new HashMap(this.processesByNameKey);
            processesByName.put(process.getId(), process);
            this.processesByNameKey = processesByName;

            if (anchor != null)
            {
                anchorsByNameKey.put(process.getId(), anchor);
            }

            log.debug("Added processing chain: " + process.getId());
        }
    }


    /**
     * Returns the component loader associated with this process loader
     */
    public ComponentsLoader getComponentLoader()
    {
        return components;
    }


    /**
     * Get an iterator over all available processes.
     */
    public List getProcessDefinitions()
    {
        return sortedProcesses;
    }

    /** Get a list of groups of processes. */
    private Map processGroups;

    public Map getProcessGroups()
    {
        return processGroups;
    }

    private static final Comparator processSorter = new Comparator()
        {
            public int compare(Object a, Object b)
            {
                ProcessDefinition pa = (ProcessDefinition) a;
                ProcessDefinition pb = (ProcessDefinition) b;

                return pa.getId().compareTo(pb.getId());
            }
        };

    private List sortedProcesses;

    private final void updateSortedList()
    {
        synchronized (this)
        {
            List tmp = new ArrayList(this.processes);
            Collections.sort(tmp, processSorter);
            this.sortedProcesses = tmp;

            TreeMap processGroups = new TreeMap();

            for (Iterator i = tmp.iterator(); i.hasNext();)
            {
                ProcessDefinition p = (ProcessDefinition) i.next();
                String key;

                if (
                    p instanceof ResolvedScriptedProcess
                        && (((ResolvedScriptedProcess) p).getInputComponentId() != null)
                )
                {
                    key = ((ResolvedScriptedProcess) p).getInputComponentId();
                }
                else if (p instanceof ResolvedProcessingChain)
                {
                    key = ((ResolvedProcessingChain) p).getInputComponent().getId();
                }
                else
                {
                    key = "other";
                }

                if (processGroups.get(key) == null)
                {
                    processGroups.put(key, new ArrayList());
                }

                List l = (List) processGroups.get(key);
                l.add(p);
                Collections.sort(l, processSorter);
            }

            this.processGroups = processGroups;
        }
    }


    /**
     * Finds a given processing chain by its nameKey. The lookup is done on a hashmap and should be
     * fairly fast.
     *
     * @return The processing chain or null, if it couldn't be found.
     */
    public ProcessDefinition findProcessDefinition(String nameKey)
    {
        if (reloading)
        {
            ProcessDescriptorAnchor anchor = (ProcessDescriptorAnchor) anchorsByNameKey.get(
                    nameKey
                );

            if ((anchor != null) && (anchor.isUpToDate() == false))
            {
                synchronized (this)
                {
                    log.debug("Reloading process: " + nameKey);

                    InputStream is = anchor.openStream();

                    if (is == null)
                    {
                        log.error(
                            "Could not open data stream to process definition. Old one remains valid."
                        );
                    }
                    else
                    {
                        try
                        {
                            ProcessDescriptor process = ProcessDescriptor.unmarshal(
                                    new InputStreamReader(is, "UTF-8")
                                );

                            removeProcess(nameKey);
                            addProcess(process, anchor);
                        }
                        catch (Exception e)
                        {
                            log.error(
                                "Error reading data stream of process definition. Old one remains valid."
                            );
                        }
                        finally
                        {
                            try
                            {
                                is.close();
                            }
                            catch (IOException e)
                            {
                            }
                        }
                    }
                }
            }
        }

        return (ProcessDefinition) processesByNameKey.get(nameKey);
    }
}
