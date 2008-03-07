package org.carrot2.util.reflect;

import static org.carrot2.util.reflect.ObjectEquivalenceHelper.wrap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link ObjectEquivalenceHelper}.
 */
public class ObjectEquivalenceHelperTest
{
    static class NoCollections
    {
        final String string;
        final int integer;

        NoCollections(String string, int integer)
        {
            this.string = string;
            this.integer = integer;
        }
    }

    static class ModifiableCollection
    {
        final ArrayList<NoCollections> collection;

        ModifiableCollection(ArrayList<NoCollections> collection)
        {
            this.collection = collection;
        }
    }

    static class UnmodifiableCollection
    {
        final List<NoCollections> collection;

        UnmodifiableCollection(ArrayList<NoCollections> collection)
        {
            this.collection = Collections.unmodifiableList(collection);
        }
    }

    @Test
    public void testNoCollections()
    {
        NoCollections instanceA = new NoCollections("x", 10);
        NoCollections instanceAeq = new NoCollections("x", 10);
        NoCollections instanceB = new NoCollections("z", 10);

        assertTrue(wrap(instanceA).equals(wrap(instanceAeq)));
        assertTrue(wrap(instanceA, true).equals(wrap(instanceAeq, true)));
        assertFalse(wrap(instanceA, true).equals(wrap(instanceB, true)));
        assertTrue(wrap(instanceA, false).equals(wrap(instanceAeq, false)));
        assertFalse(wrap(instanceA, false).equals(wrap(instanceB, false)));
    }

    @Test
    public void testModifiableCollection()
    {
        ModifiableCollection instanceA = new ModifiableCollection(Lists
            .<NoCollections> newArrayList(new NoCollections("x", 10)));
        ModifiableCollection instanceAeq = new ModifiableCollection(Lists
            .<NoCollections> newArrayList(new NoCollections("x", 10)));
        ModifiableCollection instanceB = new ModifiableCollection(Lists
            .<NoCollections> newArrayList(new NoCollections("z", 10)));

        assertTrue(wrap(instanceA, true).equals(wrap(instanceAeq, true)));
        assertFalse(wrap(instanceA, true).equals(wrap(instanceB, true)));
        assertFalse(wrap(instanceA, false).equals(wrap(instanceAeq, false)));
        assertFalse(wrap(instanceA, false).equals(wrap(instanceB, false)));
    }

    @Test
    public void testUnmodifiableCollection()
    {
        UnmodifiableCollection instanceA = new UnmodifiableCollection(Lists
            .<NoCollections> newArrayList(new NoCollections("x", 10)));
        UnmodifiableCollection instanceAeq = new UnmodifiableCollection(Lists
            .<NoCollections> newArrayList(new NoCollections("x", 10)));
        UnmodifiableCollection instanceB = new UnmodifiableCollection(Lists
            .<NoCollections> newArrayList(new NoCollections("z", 10)));

        // We can't do much with unmodifiable collections
        assertFalse(wrap(instanceA, true).equals(wrap(instanceAeq, true)));
        assertFalse(wrap(instanceA, true).equals(wrap(instanceB, true)));
        assertFalse(wrap(instanceA, false).equals(wrap(instanceAeq, false)));
        assertFalse(wrap(instanceA, false).equals(wrap(instanceB, false)));
    }
}
