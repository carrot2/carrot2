
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

package org.carrot2.workbench.core.helpers;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Utility methods for accessing elements of {@link IConfigurationElement}.
 */
public final class ExtensionConfigurationUtils
{
    private ExtensionConfigurationUtils()
    {
        // No instances.
    }

    /**
     * Checks if given attribute exists in given element and returns it, if it does.
     * 
     * @param element
     * @param attName
     * @return value of given attribute
     * @throws IllegalArgumentException if attribute with given name does not exist
     * @see #getAttribute(IConfigurationElement, String, boolean)
     */
    public static String getAttribute(IConfigurationElement element, String attName)
    {
        return getAttribute(element, attName, true);
    }

    /**
     * Checks if given attribute exists in given element and returns it, if it does.
     * Otherwise return <code>null</code>.
     * 
     * @param element
     * @param attName
     * @param throwOnError if set to true, method will throw exception if attribute with
     *            given name not found; if set to false, method will return
     *            <code>null</code>
     * @return value of given attribute or null, if attribute does not exist
     * @throws IllegalArgumentException if attribute with given name does not exist and
     *             <code>throwOnError</code> is true
     * @see #getAttribute(IConfigurationElement, String)
     */
    public static String getAttribute(IConfigurationElement element, String attName,
        boolean throwOnError)
    {
        String classAtt = element.getAttribute(attName);
        if (throwOnError && isBlank(classAtt))
        {
            throw new IllegalArgumentException("Missing " + attName + " attribute");
        }
        return classAtt;
    }

    /**
     * Checks if given attribute exists in given element and returns it, if it does.
     * 
     * @param element
     * @param attName
     * @return value of given attribute
     * @throws IllegalArgumentException if attribute with given name does not exist
     * @see #getAttribute(IConfigurationElement, String, boolean)
     */
    public static boolean getBooleanAttribute(IConfigurationElement element,
        String attName)
    {
        return getBooleanAttribute(element, attName, true, false);
    }

    /**
     * Checks if given attribute exists in given element and returns it, if it does.
     * Otherwise return <code>defaultValue</code>.
     * 
     * @param element
     * @param attName
     * @param throwOnError if set to true, method will throw exception if attribute with
     *            given name not found; if set to false, method will return
     *            <code>null</code>
     * @param defaultValue value that should be returned if given attribute does not exist
     * @return value of given attribute or null, if attribute does not exist
     * @throws IllegalArgumentException if attribute with given name does not exist and
     *             <code>throwOnError</code> is true
     * @see #getBooleanAttribute(IConfigurationElement, String)
     */
    public static boolean getBooleanAttribute(IConfigurationElement element,
        String attName, boolean throwOnError, boolean defaultValue)
    {
        String booleanAtt = getAttribute(element, attName, throwOnError);
        if (booleanAtt == null)
        {
            return defaultValue;
        }
        return Boolean.parseBoolean(booleanAtt);
    }

    /**
     * Checks if given child element exists in given element and returns it, if it does.
     * If there is more than one child element with the given name, the first one is
     * returned.
     * 
     * @param parent
     * @param elementName
     * @return child element
     * @throws IllegalArgumentException if child element with given name does not exist
     * @see #getElement(IConfigurationElement, String, boolean)
     */
    public static IConfigurationElement getElement(IConfigurationElement parent,
        String elementName)
    {
        return getElement(parent, elementName, true);
    }

    /**
     * Checks if given child element exists in given element and returns it, if it does.
     * Otherwise return <code>null</code>. If there is more than one child element with
     * the given name, the first one is returned.
     * 
     * @param parent
     * @param elementName
     * @param throwOnError if set to true, method will throw exception if child element
     *            with given name not found; if set to false, method will return
     *            <code>null</code>
     * @return child element or null, if child element does not exist
     * @throws IllegalArgumentException if child element with given name does not exist
     *             and <code>throwOnError</code> is true
     * @see #getElement(IConfigurationElement, String)
     */
    public static IConfigurationElement getElement(IConfigurationElement parent,
        String elementName, boolean throwOnError)
    {
        IConfigurationElement [] children = parent.getChildren(elementName);
        if (throwOnError && children.length == 0)
        {
            throw new IllegalArgumentException("Missing " + elementName
                + " child element");
        }
        if (children.length == 0)
        {
            return null;
        }
        return children[0];
    }

    /**
     * Checks if children elements exist in given element and returns them, if they do.
     * 
     * @param parent
     * @param elementName
     * @return children
     * @throws IllegalArgumentException if child element with given name does not exist
     * @see #getChildren(IConfigurationElement, String, boolean)
     */
    public static IConfigurationElement [] getChildren(IConfigurationElement parent,
        String elementName)
    {
        return getChildren(parent, elementName, true);
    }

    /**
     * Checks if given child element exists in given element and returns it, if it does.
     * Otherwise return <code>null</code>. If there is more than one child element with
     * the given name, the first one is returned.
     * 
     * @param parent
     * @param elementName
     * @param throwOnError if set to true, method will throw exception if child element
     *            with given name not found; if set to false, method will return
     *            <code>null</code>
     * @return child element or null, if child element does not exist
     * @throws IllegalArgumentException if child element with given name does not exist
     *             and <code>throwOnError</code> is true
     * @see #getElement(IConfigurationElement, String)
     */
    public static IConfigurationElement [] getChildren(IConfigurationElement parent,
        String elementName, boolean throwOnError)
    {
        IConfigurationElement [] children = parent.getChildren(elementName);
        if (throwOnError && children.length == 0)
        {
            throw new IllegalArgumentException("Missing " + elementName
                + " child element");
        }
        if (children.length == 0)
        {
            return null;
        }
        return children;
    }
}
