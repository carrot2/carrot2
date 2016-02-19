
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

package org.carrot2.util.simplexml;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.ListUtils;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

@Root(name = "list")
@SuppressWarnings(
{
    "unchecked", "rawtypes"
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
