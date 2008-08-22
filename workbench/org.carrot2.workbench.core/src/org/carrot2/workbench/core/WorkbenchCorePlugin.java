package org.carrot2.workbench.core;

import java.net.URL;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptorBuilder;
import org.carrot2.util.resource.URLResource;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.*;
import org.carrot2.workbench.core.ui.SearchEditor.SectionReference;
import org.carrot2.workbench.core.ui.adapters.SearchResultAdapterFactory;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    private CachingController controller;

    /**
     * All loaded components ({@link DocumentSource}s and {@link ClusteringAlgorithm}.
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

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;

        // Scan the list of suite extension points.
        scanSuites();

        controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap<String, Object>(), componentSuite);

        /*
         * Register adapters.
         */
        SearchResultAdapterFactory.register(Platform.getAdapterManager());
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
    public CachingController getController()
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
     * Returns all loaded components ({@link ClusteringAlgorithm} and
     * {@link DocumentSource}.
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
            d = getImageDescriptor("icons/carrot2-16x16.png");
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
        extLoop: for (IExtension extension : extensions)
        {
            final IConfigurationElement [] configElements = extension
                .getConfigurationElements();
            if (configElements.length == 1 && "suite".equals(configElements[0].getName()))
            {
                final String suiteResource = configElements[0].getAttribute("resource");
                if (StringUtils.isEmpty(suiteResource))
                {
                    continue;
                }

                final IContributor c = extension.getContributor();
                final Bundle b = Platform.getBundle(c.getName());

                if (b.getState() != Bundle.ACTIVE)
                {
                    try
                    {
                        b.start();
                    }
                    catch (BundleException e)
                    {
                        continue extLoop;
                    }
                }

                final URL bundleURL = b.getEntry(suiteResource);

                /*
                 * We rely on Eclipse-BuddyPolicy declared on the simplexml framework
                 * here. This policy could be removed if we passed an explicit Persister
                 * with a strategy substituting the context class loader with the given
                 * Bundle's loadClass() call. I leave it for now.
                 */
                try
                {
                    final ProcessingComponentSuite suite = ProcessingComponentSuite
                        .deserialize(new URLResource(bundleURL));

                    /*
                     * Cache icons.
                     */
                    for (ProcessingComponentDescriptor d : suite.getComponents())
                    {
                        final String iconPath = d.getIconPath();
                        if (StringUtils.isEmpty(iconPath))
                        {
                            continue;
                        }

                        componentImages.put(d.getId(), imageDescriptorFromPlugin(c
                            .getName(), iconPath));
                    }

                    suites.add(suite);
                }
                catch (Exception e)
                {
                    Logger.getRootLogger().error("Failed to load extension.", e);
                    // Skip errors, logging them.
                    Utils.logError("Failed to load suite extension.", e, false);
                }
            }
        }

        // Merge all available suites
        final List<DocumentSourceDescriptor> sources = Lists.newArrayList();
        final List<ProcessingComponentDescriptor> algorithms = Lists.newArrayList();

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
                final ProcessingComponent pc = pcd.getComponentClass().newInstance();

                processingDescriptors.put(pcd.getId(), pcd);

                bindableDescriptors.put(pcd.getId(), BindableDescriptorBuilder
                    .buildDescriptor(pc));
            }
            catch (Exception e)
            {
                Utils
                    .logError("Could not extract descriptor from: " + pcd.getId(), false);
            }
        }
    }

    /**
     * Restore the state of {@link SearchEditor}'s sections from the most recent global
     * state.
     */
    public void restoreSectionsState(
        EnumMap<SearchEditorSections, SearchEditor.SectionReference> sections)
    {
        final IPreferenceStore store = getPreferenceStore();
        for (Map.Entry<SearchEditorSections, SearchEditor.SectionReference> s : sections
            .entrySet())
        {
            final SearchEditorSections section = s.getKey();
            final SectionReference ref = s.getValue();

            final String key = PreferenceConstants.getSectionWeightKey(section);
            final String key2 = PreferenceConstants.getSectionVisibilityKey(section);
            if (!store.isDefault(key))
            {
                ref.weight = store.getInt(key);
                ref.visibility = store.getBoolean(key2);
            }
        }
    }

    /**
     * Keep a reference to the most recently updated {@link SearchEditor}'s sections.
     */
    public void storeSectionsState(
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IPreferenceStore store = getPreferenceStore();
        for (Map.Entry<SearchEditorSections, SearchEditor.SectionReference> s : sections
            .entrySet())
        {
            final SearchEditorSections section = s.getKey();
            final SectionReference ref = s.getValue();

            final String key = PreferenceConstants.getSectionWeightKey(section);
            final String key2 = PreferenceConstants.getSectionVisibilityKey(section);
            store.setValue(key, ref.weight);
            store.setValue(key2, ref.visibility);
        }
    }
}
