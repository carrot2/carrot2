package org.carrot2.util.attribute;

import java.lang.reflect.Field;

import org.carrot2.util.attribute.AttributeBinder.IAttributeTransformer;

/**
 * Implements instance cration via delegation to {@link IObjectFactory} factories.
 */
final class AttributeTransformerFactory implements IAttributeTransformer
{
    public static IAttributeTransformer INSTANCE = new AttributeTransformerFactory();
    
    private AttributeTransformerFactory() {}

    @Override
    public Object transform(Object value, String key, Field field)
    {
        if (value == null) return value;

        // Factory instance provided. 
        if (IObjectFactory.class.isInstance(value)) {
            IObjectFactory<?> factory = (IObjectFactory<?>) value;
            return factory.create();
        }
        
        // Factory class provided.
        if (Class.class.isInstance(value))
        {
            final Class<?> clazz = (Class<?>) value;
            if (IObjectFactory.class.isAssignableFrom(clazz)) {
                final IObjectFactory<?> factory;
                try {
                    factory = (IObjectFactory<?>) clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Could not create instance of factory class: " + clazz.getName()
                            + " for attribute " + key, e);
                }
                return factory.create();
            }
        }
        
        return value;
    }
}