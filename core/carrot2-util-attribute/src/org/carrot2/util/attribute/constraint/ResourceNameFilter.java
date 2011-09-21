package org.carrot2.util.attribute.constraint;

/**
 * A single resource filter (pattern and description).
 */
public @interface ResourceNameFilter
{
    /** Resource pattern. Example: <code>*.xml;*.XML</code> */
    String pattern();

    /** Description of the pattern. Example: "XML files". */
    String description();
}
