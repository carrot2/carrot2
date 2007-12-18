package org.carrot2.core.parameters;

final class BindableUtils
{
    public static String getPrefix(Object instance)
    {
        final Bindable bindable = instance.getClass().getAnnotation(Bindable.class);
        if (bindable == null)
        {
            throw new IllegalArgumentException();
        }

        if (bindable.prefix().equals("")) {
            return instance.getClass().getName();
        } else {
            return bindable.prefix();
        }
    }
}