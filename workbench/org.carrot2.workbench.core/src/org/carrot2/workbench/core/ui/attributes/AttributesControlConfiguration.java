package org.carrot2.workbench.core.ui.attributes;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;

public class AttributesControlConfiguration
{
    /**
     * Attributes with the given keys will not be shown.
     */
    public List<String> ignoredAttributes = new ArrayList<String>();

    /**
     * Only attributes with the given annotations will be shown.
     */
    public List<Class<? extends Annotation>> filterAnnotations =
        new ArrayList<Class<? extends Annotation>>();

    /**
     * Only group with this key will be shown.
     */
    public Object filterGroupKey;

    /**
     * Attributes should be grouped using this method.
     */
    public GroupingMethod groupingMethod;

    public Class<? extends Annotation> [] getFilterAnnotationsArray()
    {
        Class<?> [] result = new Class<?> [filterAnnotations.size()];
        for (int i = 0; i < filterAnnotations.size(); i++)
        {
            result[i] = filterAnnotations.get(i);
        }
        return (Class<? extends Annotation> []) result;
    }
}
