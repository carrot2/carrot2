package org.carrot2.workbench.core;

import static org.carrot2.workbench.core.helpers.ExtensionConfigurationUtils.getAttribute;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;

import com.google.common.collect.Lists;

/**
 * Parses and iterates over a given extension point, collecting information required to
 * instantiate {@link ProcessingComponent}s ({@link ClusteringAlgorithm}s or
 * {@link DocumentSource}s).
 */
public final class ExtensionLoader
{
    /**
     * List of all found and successfully loaded implementations.
     */
    private final List<ExtensionImpl> implementations = Lists
        .newArrayList();

    /**
     * Loads information about implementation of an extension point
     * <code>extensionPointId</code>.
     * 
     * @param extensionPointName extension point ID
     * @param elementName Name of the configuration element holding implementation
     *            information for the extension.
     */
    public ExtensionLoader(String extensionPointId, String elementName)
    {
        /*
         * Scan for and cache implementations.
         */

        final IExtension [] extensions = Platform.getExtensionRegistry()
            .getExtensionPoint(extensionPointId).getExtensions();

        for (IExtension extension : extensions)
        {
            parseExtension(extension.getConfigurationElements(), elementName);
        }
    }

    /**
     * Returns a list of implementation specifications.
     */
    public Collection<ExtensionImpl> getImplementations()
    {
        return Collections.unmodifiableCollection(implementations);
    }

    /**
     * 
     */
    public ExtensionImpl getImplementation(String id)
    {
        for (ExtensionImpl i : implementations)
        {
            if (i.id.equals(id))
            {
                return i;
            }
        }

        throw new RuntimeException("Missing extension point implementation: " + id);
    }

    /*
     * Parses extension point configuration and adds new implementations.
     */
    private void parseExtension(IConfigurationElement [] configurationElements,
        String implElementName)
    {
        for (int i = 0; i < configurationElements.length; i++)
        {
            final IConfigurationElement configurationElement = configurationElements[i];
            final IContributor contributor = configurationElement.getContributor();

            if (!configurationElement.getName().equals(implElementName))
            {
                continue;
            }

            /*
             * Extract the required data from the extension.
             */
            final String id = configurationElement.getDeclaringExtension()
                .getUniqueIdentifier();
            final String label = getAttribute(configurationElement, "label");
            final String clazz = getAttribute(configurationElement, "class");

            final String iconPath = getAttribute(configurationElement, "icon");
            final ImageDescriptor icon = WorkbenchCorePlugin.imageDescriptorFromPlugin(
                contributor.getName(), iconPath);
            if (icon == null)
            {
                throw new IllegalArgumentException("Resource " + iconPath + " in plugin "
                    + contributor.getName()
                    + " is not a correct image or does not exist.");
            }

            /*
             * Try to instantiate the implementation class early, skip these
             * implementations that cause runtime errors.
             */
            final ProcessingComponent instance;
            try
            {
                instance = (ProcessingComponent) configurationElement
                    .createExecutableExtension("class");
            }
            catch (Throwable t)
            {
                Utils.logError("Error while initializing component " + clazz
                    + " in plugin " + contributor.getName(), t, true);
                continue;
            }

            implementations.add(new ExtensionImpl(id, label, icon, instance
                .getClass(), contributor.getName()));
        }
    }
}
