package org.carrot2.util.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A factory of {@link ResourceUtils}.
 */
public final class ResourceUtilsFactory
{
    /**
     * System property pointing to a folder on disk. If defined,
     * this folder is used as a base for resource-scanning.
     */
    private final static String LINGO3G_RESOURCES_PROPERTY = "resources.dir";

    /**
     * Return the default resource-lookup locators.
     */
    public static ResourceLocator [] getDefaultResourceLocators()
    {
        final ResourceLocator [] base = new ResourceLocator [] {
            // System property-defined directory
            new DirLocator(System.getProperty(LINGO3G_RESOURCES_PROPERTY)),
            // Current working directory
            new DirLocator(new File(".").getAbsolutePath()),
            // Context class loader-relative
            new ContextClassLoaderLocator(),
            // Given class-relative resources
            new ClassRelativeLocator(),
        };

        final ArrayList locators = new ArrayList(base.length + 1);
        locators.addAll(Arrays.asList(base));

        // Add the legacy prefix-lookup to all base locators.
        for (int i = 0; i < base.length; i++) {
            locators.add(new PrefixDecoratorLocator(base[i], "resources/"));
        }

        return (ResourceLocator []) locators.toArray(
            new ResourceLocator [locators.size()]);
    }

    /**
     * Return the default resource lookup proxy.
     */
    public static ResourceUtils getDefaultResourceUtils() {
        return new ResourceUtils(getDefaultResourceLocators());
    }
}
