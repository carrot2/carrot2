package org.carrot2.util.simplexml;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.ListUtils;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

@Root(name = "list")
@SuppressWarnings(
{
    "unchecked", "unused"
})
class ListSimpleXmlWrapper implements ISimpleXmlWrapper<List>
{
    private List values;

    @ElementList(name = "list")
    private ArrayList<SimpleXmlWrapperValue> forSerialization;

    public List getValue()
    {
        return values;
    }

    public void setValue(List value)
    {
        values = ListUtils.asArrayList(value);
    }

    @Persist
    private void beforeSerialization()
    {
        forSerialization = ListUtils.asArrayList(SimpleXmlWrappers.wrap(values));
    }

    @Commit
    private void afterSerialization()
    {
        values = SimpleXmlWrappers.unwrap(forSerialization);
    }
}
