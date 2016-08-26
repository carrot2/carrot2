
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.DocumentSourceDescriptor;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.text.linguistic.DefaultLexicalDataFactoryDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.resource.DirLocator;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.PrefixDecoratorLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.carrot2.shaded.guava.common.base.Objects;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * The activator class (plug-in's entry point), controls the life-cycle and contains a
 * reference to the Carrot2 {@link Controller}.
 */
public class WorkbenchCorePlugin extends AbstractUIPlugin
{
    /** Plug-in ID. */
    public static final String PLUGIN_ID = "org.carrot2.workbench.core";

    /** Source extension identifier. */
    public static final String COMPONENT_SUITE_EXTENSION_ID = "org.carrot2.core.componentSuite";

    /** The shared instance. */
    private static WorkbenchCorePlugin plugin;

    /**
     * Shared, thread-safe caching controller instance.
     */
    private Controller controller;

    /**
     * All loaded components ({@link IDocumentSource}s and {@link IClusteringAlgorithm}.
     */
    private ProcessingComponentSuite componentSuite;

    /**
     * Cached {@link BindableDescriptor}s of all available components in
     * {@link #componentSuite}.
     */
    private HashMap<String, BindableDescriptor> bindableDescriptors = Maps.newHashMap();

    /**
     * Cached component bindableDescriptors of all available components in
     * {@link #componentSuite}.
     */
    private HashMap<String, ProcessingComponentDescriptor> processingDescriptors = Maps
        .newHashMap();

    /**
     * Cached image descriptors of components.
     */
    private HashMap<String, ImageDescriptor> componentImages = Maps.newHashMap();

    /**
     * List of failed components.
     */
    private List<ProcessingComponentDescriptor> failed = Lists.newArrayList();

    /**
     * Workspace locator.
     */
    private IResourceLocator workspaceLocator;

    /**
     * Starts the bundle: scan suites and initialize the controller.
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
        
        // Fix instance location first.
        fixInstanceLocation();
        
        // Workspace resource locator.
        workspaceLocator = getWorkspaceResourceLocator();

        // Scan the list of suite extension points.
        scanSuites();

        ArrayList<IResourceLocator> locators = Lists.newArrayList();
        if (workspaceLocator != null)
        {
            locators.add(workspaceLocator);
        }
        locators.add(Location.CONTEXT_CLASS_LOADER.locator);

        Map<String, Object> initAttributes = Maps.newHashMap();
        initAttributes.put(
            DefaultLexicalDataFactoryDescriptor.Keys.RESOURCE_LOOKUP,
            new ResourceLookup(locators));

        controller = ControllerFactory.createCachingPooling(IDocumentSource.class);
        controller.init(initAttributes, componentSuite.getComponentConfigurations());        
    }

    private void fixInstanceLocation()
    {
        Logger logger = LoggerFactory.getLogger(WorkbenchCorePlugin.class);

        org.eclipse.osgi.service.datalocation.Location instanceLocation = 
            Platform.getInstanceLocation();
        if (!instanceLocation.isSet())
        {
            org.eclipse.osgi.service.datalocation.Location installLocation = 
                Platform.getInstallLocation();
            if (installLocation.isSet())
            {
                try
                {
                    // CARROT-1147: if instance location is inside the .app folder, search for
                    // workspace.
                    if (Objects.equal(Platform.getOS(), Platform.OS_MACOSX)) {
                      Path installPath = Paths.get(installLocation.getURL().toURI());
                      Path workspace = installPath.resolve("../../../workspace");
                      if (Files.exists(workspace)) {
                        instanceLocation.set(workspace.toUri().toURL(), true);
                      } else {
                        instanceLocation.set(Platform.getUserLocation().getDataArea("workspace"), true);
                      }
                    } else {
                      instanceLocation.set(installLocation.getDataArea("workspace"), true);
                    }

                    logger.info("Changed instanceLocation to: " + instanceLocation.getURL());
                }
                catch (Exception e)
                {
                    logger.error("Unable to set instanceLocation to: " + instanceLocation.getURL());
                }
            }
            else
            {
                logger.error("Could not determine install location.");
            }
        }
        else
        {
            logger.info("Instance location already set to: " + instanceLocation.getURL());
        }
        logger.debug("User location: " + Platform.getUserLocation().getURL());
        logger.debug("Install location: " + Platform.getInstallLocation().getURL());
        logger.debug("Instance location: " + Platform.getInstanceLocation().getURL());
        logger.debug("Platform working location: " + Platform.getLocation());
        logger.debug("Configuration location: " + Platform.getConfigurationLocation().getURL());        
    }

    /*
     * 
     */
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;

        controller.dispose();
        controller = null;

        super.stop(context);
    }

    /**
     * Returns an initialized shared controller instance.
     */
    public Controller getController()
    {
        return controller;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Returns all loaded components ({@link IClusteringAlgorithm} and
     * {@link IDocumentSource}.
     */
    public ProcessingComponentSuite getComponentSuite()
    {
        return componentSuite;
    }

    /**
     * Returns a {@link BindableDescriptor} for a given component ID or <code>null</code>
     * if this component is not available.
     */
    public BindableDescriptor getComponentDescriptor(String componentID)
    {
        return bindableDescriptors.get(componentID);
    }

    /**
     * Returns a {@link ProcessingComponentDescriptor} for a given component ID or
     * <code>null<code>.
     */
    public ProcessingComponentDescriptor getComponent(String componentID)
    {
        return processingDescriptors.get(componentID);
    }

    /**
     * Returns a {@link ImageDescriptor} for a given component or a default image if the
     * component did not contain any icon.
     */
    public ImageDescriptor getComponentImageDescriptor(String componentID)
    {
        ImageDescriptor d = componentImages.get(componentID);
        if (d == null)
        {
            d = getImageDescriptor("icons/missing-component.png");
        }
        return d;
    }

    /**
     * Returns the shared instance.
     */
    public static WorkbenchCorePlugin getDefault()
    {
        return plugin;
    }

    /**
     * Scan all declared extensions of {@link #COMPONENT_SUITE_EXTENSION_ID} extension
     * point.
     */
    private void scanSuites()
    {
        final List<ProcessingComponentSuite> suites = Lists.newArrayList();

        final IExtension [] extensions = Platform.getExtensionRegistry()
            .getExtensionPoint(COMPONENT_SUITE_EXTENSION_ID).getExtensions();

        // Load suites from extension points.
        for (IExtension extension : extensions)
        {
            final IConfigurationElement [] configElements = extension
                .getConfigurationElements();
            if (configElements.length == 1 && "suite".equals(configElements[0].getName()))
            {
                String suiteRoot = configElements[0].getAttribute("resourceRoot");
                if (StringUtils.isEmpty(suiteRoot)) suiteRoot = "";

                final String suiteResourceName = configElements[0].getAttribute("resource");
                if (StringUtils.isEmpty(suiteResourceName))
                {
                    continue;
                }

                String bundleId = configElements[0].getAttribute("bundleId");
                if (StringUtils.isEmpty(bundleId))
                {
                    final IContributor c = extension.getContributor();
                    bundleId = c.getName();
                }

                final Bundle b = Platform.getBundle(bundleId);
                if (b == null)
                {
                    Utils.logError("Suite's bundle not found: " + bundleId, false);
                    continue;
                }

                if (b.getState() != Bundle.ACTIVE)
                {
                    try
                    {
                        b.start();
                    }
                    catch (BundleException e)
                    {
                        Utils.logError("Bundle inactive: " + bundleId, false);                        
                        continue;
                    }
                }

                final ResourceLookup resourceLookup = new ResourceLookup(
                    workspaceLocator,
                    new PrefixDecoratorLocator(new BundleResourceLocator(b), suiteRoot));

                IResource suiteResource = resourceLookup.getFirst(suiteResourceName);
                if (suiteResource == null)
                {
                    String message = "Suite extension resource not found in " 
                        + b.getSymbolicName() + ": " + bundleId;
                    Utils.logError(message, false);
                    continue;
                }

                /* This piece of code is currently quite fragile and hacky, but works. 
                 * 
                 * First, we rely on Eclipse-BuddyPolicy declared on the simplexml framework
                 * to instantiate arbitrary classes (from sources and algorithms). 
                 * This policy could be removed if we passed an explicit Persister
                 * with a strategy substituting the context class loader with the given
                 * Bundle's loadClass() call. I leave it for now.
                 * 
                 * We use a custom resource locator that searches the contributing
                 * plugin for resources matching the included resource.
                 */
                try
                {
                    final ProcessingComponentSuite suite = ProcessingComponentSuite
                        .deserialize(suiteResource, resourceLookup);

                    /*
                     * Remove invalid descriptors, cache icons.
                     */
                    failed.addAll(suite.removeUnavailableComponents());
                    for (ProcessingComponentDescriptor d : suite.getComponents())
                    {
                        final String iconPath = d.getIconPath();
                        if (StringUtils.isEmpty(iconPath))
                        {
                            continue;
                        }

                        componentImages.put(d.getId(), 
                            imageDescriptorFromPlugin(bundleId, iconPath));
                    }

                    suites.add(suite);
                }
                catch (Exception e)
                {
                    // Skip errors, logging them.
                    Utils.logError("Failed to load suite extension.", e, false);
                }
            }
        }

        // Merge all available suites
        final ArrayList<DocumentSourceDescriptor> sources = Lists.newArrayList();
        final ArrayList<ProcessingComponentDescriptor> algorithms = Lists.newArrayList();

        for (ProcessingComponentSuite s : suites)
        {
            sources.addAll(s.getSources());
            algorithms.addAll(s.getAlgorithms());
        }

        this.componentSuite = new ProcessingComponentSuite(sources, algorithms);

        // Extract and cache bindableDescriptors.
        for (ProcessingComponentDescriptor pcd : componentSuite.getComponents())
        {
            try
            {
                final String id = pcd.getId();
                BindableDescriptor bindableDescriptor = pcd.getBindableDescriptor();
                bindableDescriptors.put(id, bindableDescriptor);
                processingDescriptors.put(id, pcd);
            }
            catch (Exception e)
            {
                Utils.logError("Failed to extract descriptor from: " 
                    + pcd.getId(), e, false);
            }
        }
        
        /*
         * Log errors.
         */
        if (!failed.isEmpty())
        {
            for (ProcessingComponentDescriptor d : failed)
            {
                getLog().log(
                    new Status(Status.ERROR, PLUGIN_ID, 
                        "Plugin loading failure: " + d.getId()
                        + " (" + d.getTitle() + ")"
                        + "\n" + StringUtils.defaultIfEmpty(d.getInitializationFailure().getMessage(), 
                            "(no message)"), d.getInitializationFailure()));
            }
        }
    }

    /**
     * @return Return failed component descriptors, if any.
     */
    public List<ProcessingComponentDescriptor> getFailed()
    {
        return failed;
    }

    /**
     * Return a resource locator pointing to the user's workspace or
     * <code>null</code> if not available.
     */
    private IResourceLocator getWorkspaceResourceLocator()
    {
        final URL instanceLocation = Platform.getInstanceLocation().getURL();
        if (instanceLocation == null)
        {
            // Issue a warning about read-only location.
            Utils.logError("Instance location not available.", false);
            return null;
        }

        if (!"file".equalsIgnoreCase(instanceLocation.getProtocol()))
        {
            // Issue a warning about read-only location.
            Utils.logError("Instance location not a file URL: "
                + instanceLocation, false);
            return null;
        }
        
        // Invalid URLs may fail when converting to an URI. If so, try brute-force approach.
        File workspacePath;
        try {
            workspacePath = URIUtil.toFile(instanceLocation.toURI());
        } catch (URISyntaxException e) {
            workspacePath = new File(instanceLocation.getFile());
        }
        
        workspacePath = workspacePath.getAbsoluteFile();
        if (!workspacePath.exists())
        {
            workspacePath.mkdirs();
        }

        if (!workspacePath.exists())
        {
            // Issue a warning about read-only location.
            Utils.logError("Instance location does not exist: " + workspacePath, false);
            return null;
        }

        return new DirLocator(workspacePath.getAbsoluteFile());
    }

    /**
     * @return Returns the plugin's instance preferences.
     */
    public static IEclipsePreferences getPreferences()
    {
        return InstanceScope.INSTANCE.getNode(WorkbenchCorePlugin.PLUGIN_ID);
    }
}
