/**
 * 
 */
package org.carrot2.core.type;

import org.carrot2.core.Configurable;

/**
 *
 */
public class ConfigurableType<T extends Configurable> extends AbstractType<T>
{
    public ConfigurableType(Class<T> type)
    {
        super(type);
    }

    @Override
    public T valueOf(String s)
    {
        // TODO: Is there any way we can provide a conversion from String to Configurable
        // here? If s was a fully qualified class name we could try to instantiate
        // (checking if it's a subclass of Configurable). Another question if this
        // kind of method would be useful at all...
        throw new UnsupportedOperationException("Not supported");
    }
}
