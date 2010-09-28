package org.carrot2.util.attribute;

import static org.apache.commons.lang.ClassUtils.*;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Utilities related to generated bindable descriptors.
 */
public final class BindableDescriptorUtils
{
    private BindableDescriptorUtils()
    {
        // no instances.
    }

    /**
     * Return the mapped descriptor class name for a given class name (descriptors for nested
     * classes become top-level classes).
     */
    public static String getDescriptorClassName(String className)
    {
        String packageName = getPackageName(className);

        // We apply getShortClassName() twice; the first call returns .-separated class nesting,
        // the second call removes outer classes (considered packages).
        String shortClassName = getShortClassName(getShortClassName(className));

        return packageName
             + (isEmpty(packageName) ? "" : ".")
             + shortClassName
             + "Descriptor";
    }
}
