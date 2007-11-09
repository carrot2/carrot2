package org.carrot2.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExampleDocumentSource implements DocumentSource
{
    private static final Field TEST_FIELD = new Field("test", new IntegerTypeMetadata(10,
        20));

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
