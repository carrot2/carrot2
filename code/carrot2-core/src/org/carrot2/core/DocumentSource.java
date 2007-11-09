package org.carrot2.core;

import java.util.Collection;

public interface DocumentSource extends Configurable
{
    Collection<Field> getFields();
}