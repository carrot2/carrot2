
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

package carrot2.demo;

import java.io.File;
import java.net.URL;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.LoadedProcess;
import com.dawidweiss.carrot.local.controller.loaders.ComponentInitializationException;

/**
 * A Carrot2 clustering demo context.
 * 
 * @author Dawid Weiss
 */
public class DemoContext {
    private final String CLUSTER_INFO_RENDERER_CLASS = "cluster.info.renderer.class";
    private final String PROCESS_SETTINGS_CLASS = "process.settings.class";
    private final String PROCESS_DEFAULT = "process.default";

    /** Local Carrot2 controller */
    private LocalController controller = new LocalControllerBase();

    /** Maps process identifiers to their user interface names. */
    private HashMap processIdToName;

    /** 
     * Default process ID in {@link #processIdToName}. 
     */
    private String defaultProcess;
    
    /** 
     * A list of {@link com.dawidweiss.carrot.local.controller.LoadedProcess} objects
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
    }

    /**
     * Creates a new demo context with initialization using URLs to
     * component and process descriptors.
    */
    public DemoContext(URL [] components, URL [] processes) {
        this.localDefinitionFolders = false;
        this.componentUrls = components;
        this.processUrls = processes;
    }

    /**
     * Initialize the demo context, create local controller 
     * and component factories.
     */
    public void initialize() {
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
    
            try {
                cl.addComponentFactoriesFromDirectory(controller, componentsDir);
                this.loadedProcesses = cl.loadProcessesFromDirectory(processesDir);
            } catch (Exception e) {
                throw new RuntimeException("Unhandled exception when initializing components and processes.", e);
            }
        } else {
            try {
                // Initialization from resource files.
                for (int i = 0; i < componentUrls.length; i++) {
                    final String external = componentUrls[i].toExternalForm();
                    final int lastDotIndex = external.lastIndexOf('.');
                    final String extension = external.substring(lastDotIndex+1);
                    cl.addComponentFactory(controller, extension, componentUrls[i].openStream());
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
        try {
            for (Iterator i = loadedProcesses.iterator(); i.hasNext();) {
                final LoadedProcess lp = (LoadedProcess) i.next();
                if (lp.getAttributes().containsKey(PROCESS_SETTINGS_CLASS)) {
                    final String processSettingsClass = (String) lp.getAttributes().get(PROCESS_SETTINGS_CLASS);
                    try {
                        final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(processSettingsClass);
                        final ProcessSettings st = (ProcessSettings) clazz.newInstance();
                        this.loadedSettings.put(lp.getId(), st);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not load process settings: "
                                + processSettingsClass, e);
                    }
                    if (lp.getAttributes().containsKey(PROCESS_DEFAULT)) {
                        this.defaultProcess = lp.getId();
                    }
                }
                if (lp.getAttributes().containsKey(CLUSTER_INFO_RENDERER_CLASS)) {
                    final String clusterInfoRendererClass = (String) lp.getAttributes().get(CLUSTER_INFO_RENDERER_CLASS);
                    try {
                        final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(clusterInfoRendererClass);
                        final ClusterInfoRenderer cir = (ClusterInfoRenderer) clazz.newInstance();
                        this.loadedClusterInfoRenderers.put(lp.getId(), cir);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not load process settings: "
                            + clusterInfoRendererClass, e);
                    }
                    if (lp.getAttributes().containsKey(PROCESS_DEFAULT)) {
                        this.defaultProcess = lp.getId();
                    }
                }
                controller.addProcess(lp.getId(), lp.getProcess());
            }
        } catch (DuplicatedKeyException e) {
            throw new RuntimeException("Identifiers of components and processes must be unique.", e);
        } catch (ComponentInitializationException e) {
            throw new RuntimeException("Cannot initialize component.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unhandled exception when initializing components and processes.", e);
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
                throw new Error("Process identifier not associated with any name?", e);
            }
        }
        this.processIdToName = processIdToName;
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