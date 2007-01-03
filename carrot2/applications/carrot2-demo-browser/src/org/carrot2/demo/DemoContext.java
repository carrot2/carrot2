
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.controller.*;
import org.carrot2.core.controller.loaders.BeanShellFactoryDescriptionLoader;
import org.carrot2.core.controller.loaders.ComponentInitializationException;

/**
 * A Carrot2 clustering demo context.
 * 
 * @author Dawid Weiss
 */
public class DemoContext {
    private final static String CLUSTER_INFO_RENDERER_CLASS = "cluster.info.renderer.class";
    private final static String PROCESS_SETTINGS_CLASS = "process.settings.class";
    private final static String PROCESS_DEFAULT = "process.default";

    /** Local Carrot2 controller */
    private final LocalControllerBase controller;

    /** Maps process identifiers to their user interface names. */
    private HashMap processIdToName;

    /** 
     * Default process ID in {@link #processIdToName}. 
     */
    private String defaultProcess;
    
    /** 
     * A list of {@link org.carrot2.core.controller.LoadedProcess} objects
     * loaded from processes folder.
     */
    private List loadedProcesses;
    
    /** 
     * A map of processid(String)-ProcessSettings objects.
     */
    private HashMap loadedSettings = new HashMap();
    
    /** 
     * A map of processid(String)-ClusterInfoRenderer objects.
     */
    private HashMap loadedClusterInfoRenderers = new HashMap();
    
    /**
     * If <code>true</code>, initialization of components and processes
     * is performed on local folders named <code>components</code> and
     * <code>processes</code> (these folders must exist).
     */
    private boolean localDefinitionFolders;
    
    private URL [] processUrls;
    private URL [] componentUrls;
    
    /**
     * Creates a new demo context with initialization using local folders.
     */
    public DemoContext() {
        this.localDefinitionFolders = true;
        this.controller = new LocalControllerBase();
        this.controller.setComponentAutoload(true);
    }

    /**
     * Creates a new demo context with initialization using URLs to
     * component and process descriptors.
    */
    public DemoContext(URL [] components, URL [] processes) {
        this.localDefinitionFolders = false;
        this.componentUrls = components;
        this.processUrls = processes;
        this.controller = new LocalControllerBase();
        this.controller.setComponentAutoload(true);
    }

    /**
     * Initialize the demo context, create local controller 
     * and component factories.
     */
    public void initialize() throws InitializationException, MissingComponentException, 
        DuplicatedKeyException, IOException, ComponentInitializationException
    {
        final ControllerHelper cl = new ControllerHelper();

        if (localDefinitionFolders) {
            final File componentsDir = new File("components");
            if (componentsDir.isDirectory() == false) {
                throw new RuntimeException("Components directory not found: "
                        + componentsDir.getAbsolutePath());
            }
            final File processesDir = new File("processes");
            if (processesDir.isDirectory() == false) {
                throw new RuntimeException("Components directory not found: "
                        + componentsDir.getAbsolutePath());
            }

            // Register context path for beanshell scripts.
            final ComponentFactoryLoader bshLoader = cl.getComponentFactoryLoader(
                    ControllerHelper.EXT_COMPONENT_FACTORY_LOADER_BEANSHELL);
            if (bshLoader != null) {
                final HashMap globals = new HashMap();
                globals.put("inputsDirFile", componentsDir);
                ((BeanShellFactoryDescriptionLoader) bshLoader).setGlobals(globals);
            }
            
            cl.addAll(controller, cl.loadComponentFactoriesFromDir(componentsDir));
            this.loadedProcesses = Arrays.asList(cl.loadProcessesFromDir(processesDir));
        } else {
            try {
                // Initialization from resource files.
                for (int i = 0; i < componentUrls.length; i++) {
                    final String external = componentUrls[i].toExternalForm();
                    final int lastDotIndex = external.lastIndexOf('.');
                    final String extension = external.substring(lastDotIndex+1);
                    LoadedComponentFactory lcf = cl.loadComponentFactory(extension, componentUrls[i].openStream());
                    controller.addLocalComponentFactory(lcf.getId(), lcf.getFactory());
                }
                this.loadedProcesses = new ArrayList();
                for (int i = 0; i < processUrls.length; i++) {
                    final String external = processUrls[i].toExternalForm();
                    final int lastDotIndex = external.lastIndexOf('.');
                    final String extension = external.substring(lastDotIndex+1);
                    loadedProcesses.add(cl.loadProcess(extension, processUrls[i].openStream()));
                }
            } catch (Exception e) {
                throw new RuntimeException("Exception when initializing components and processes.", e);
            }
        }

        //
        // Add scripted/ custom components and processes
        //
        for (Iterator i = loadedProcesses.iterator(); i.hasNext();) {
            final LoadedProcess lp = (LoadedProcess) i.next();

            this.loadedSettings.put(lp.getId(), getProcessSettings(lp.getAttributes()));

            if (lp.getAttributes().containsKey(CLUSTER_INFO_RENDERER_CLASS)) {
                final String clusterInfoRendererClass = (String) lp.getAttributes().get(CLUSTER_INFO_RENDERER_CLASS);
                try {
                    final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(clusterInfoRendererClass);
                    final ClusterInfoRenderer cir = (ClusterInfoRenderer) clazz.newInstance();
                    this.loadedClusterInfoRenderers.put(lp.getId(), cir);
                } catch (Exception e) {
                    throw new RuntimeException("Could not load info renderer: "
                        + clusterInfoRendererClass, e);
                }
            }

            if (lp.getAttributes().containsKey(PROCESS_DEFAULT)) {
                this.defaultProcess = lp.getId();
            }
            controller.addProcess(lp.getId(), lp.getProcess());
        }

        final HashMap processIdToName = new HashMap();
        final List processIds = controller.getProcessIds();
        for (final Iterator i = processIds.iterator(); i.hasNext();) {
            try {
                final String processId = (String) i.next();
                String processName = controller.getProcessName(processId);
                if (processName == null || "".equals(processName)) {
                    processName = processId;
                }
                processIdToName.put(processId, processName);
            } catch (MissingProcessException e) {
                throw new RuntimeException("Process identifier not associated with any name?", e);
            }
        }
        this.processIdToName = processIdToName;
    }

    /**
     * Iterates through attributes, looking for these starting with
     * {@link #PROCESS_SETTINGS_CLASS} and returns an instance of
     * {@link ProcessSettings}.  
     */
    private ProcessSettings getProcessSettings(Map attributes) {
        final ArrayList settingsClasses = new ArrayList();
        for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
            final String key = (String) i.next();
            if (key.startsWith(PROCESS_SETTINGS_CLASS)) {
                settingsClasses.add(key);
            }
        }

        Collections.sort(settingsClasses);
        final ProcessSettings [] settings = new ProcessSettings[settingsClasses.size()];
        for (int i = 0; i < settings.length; i++) {
            final String processSettingsClass = (String) attributes.get(settingsClasses.get(i));
            try {
                final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(processSettingsClass);
                settings[i] = (ProcessSettings) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not load process settings: " + processSettingsClass, e);
            }
        }
        
        if (settings.length == 0) {
            return new EmptyProcessSettings();
        } else if (settings.length == 1) {
            return settings[0];
        } else {
            return new CompoundProcessSettings(settings); 
        }
    }

    /**
     * Returns a map of process identifier - process name.
     */
    public Map getProcessIdToProcessNameMap() {
        return processIdToName;
    }
    
    /**
     * An identifier of the  default process. If there is no default,
     * <code>null</code> is returned. 
     */
    public String getDefaultProcessId() {
        return this.defaultProcess;
    }

    /**
     * Returns process settings for the given process or <code>null</code> if no
     * settings are present for this process.
     */
    public ProcessSettings getSettingsObject(final String processId) {
        if (this.loadedSettings.containsKey(processId)) {
            return (ProcessSettings) loadedSettings.get(processId);
        } else {
            return new EmptyProcessSettings();
        }
    }
    
    /**
     * Returns cluster information renderer object for the given process or
     * <code>null</code> if no renderer is available for this process.
     */
    public ClusterInfoRenderer getClusterInfoRenderer(final String processId) {
        if (this.loadedSettings.containsKey(processId)) {
            return (ClusterInfoRenderer) loadedClusterInfoRenderers.get(processId);
        } else {
            return null;
        }
    }

    /**
     * Returns the process controller preconfigured in the context.
     */
    public LocalController getController() {
        return this.controller;
    }
}