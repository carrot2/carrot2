package org.carrot2.sandbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.carrot2.core.DocumentSource;
import org.carrot2.core.Field;
import org.carrot2.core.parameters.ParameterGroup;
import org.carrot2.core.type.BoundedIntegerTypeWithDefaultValue;

public class ExampleDocumentSource implements DocumentSource
{
    private static final Field TEST_FIELD = new Field("test", new BoundedIntegerTypeWithDefaultValue(15,
        10, 20));

    public Collection<Field> getFields()
    {
        List<Field> fields = new ArrayList<Field>();

        fields.add(TEST_FIELD);

        return fields;
    }

    public ParameterGroup getParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
