package org.carrot2.util.resources;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Resource loading abstraction layer.
 */
public final class ResourceUtils
{
    /**
     * Logger instance. 
     */
    private final static Logger logger = Logger.getLogger(ResourceUtils.class);

    /**
     * An array of resource locators (used first to last).
     */
    private final ResourceLocator [] locators;

    /**
     * No direct instantiation.
     */
    public ResourceUtils(ResourceLocator [] locators)
    {
        this.locators = new ResourceLocator [locators.length];
        System.arraycopy(locators, 0, this.locators, 0, locators.length);
    }

    /**
     * Scans all resource locators and returns matching resources.
     * 
     * @param resource Resource name.
     * @param clazz Optional class for class-relative resources.
     * @return Returns an empty array if no resource matched the given name.
     */
    public Resource [] getAll(String resource, Class clazz) {
        final ArrayList result = new ArrayList();
        for (int i = 0; i < locators.length; i++) {
            final Resource [] current = locators[i].getAll(resource, clazz);
            // There shouldn't be too many matching resources, 
            // so linear search is ok.
            for (int j = 0; j < current.length; j++) {
                if (!result.contains(current[j])) {
                    result.add(current[j]);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            final StringBuffer buf = new StringBuffer("All matching: " + resource + ", ");
            for (int i = 0; i < result.size(); i++) {
                if (i > 0) buf.append(", ");
                buf.append(((Resource) result.get(i)).toString());
            }
            if (result.size() == 0) {
                buf.append("(none found)");
            }
            logger.debug(buf.toString());            
        }

        return (Resource []) result.toArray(new Resource[result.size()]); 
    }

    /**
     * Scans through resource locators and returns the first matching resource.
     * 
     * @param resource Resource name.
     * @param clazz Optional class for class-relative resources.
     * @return Returns null if no resource was found for the given name.
     */
    public Resource getFirst(String resource, Class clazz) {
        for (int i = 0; i < locators.length; i++) {
            final Resource [] result = locators[i].getAll(resource, clazz);
            if (result != null && result.length > 0) {
                logger.debug("First matching " + resource + ", " + result[0].toString());
                return result[0];
            }
        }
        logger.debug("First matching " + resource + ", (none found)");
        return null; 
    }

    /**
     * Same as {@link #getFirst(String, Class)} but without the clazz argument.
     */
    public Resource getFirst(String resource)
    {
        return getFirst(resource, null);
    }
}
