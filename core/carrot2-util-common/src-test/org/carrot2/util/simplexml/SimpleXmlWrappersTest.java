
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

import java.io.*;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.*;
import org.carrot2.util.resource.FileResource;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.*;

import org.carrot2.shaded.guava.common.collect.*;

/**
 * Test cases for {@link SimpleXmlWrappers}.
 */
public class SimpleXmlWrappersTest extends CarrotTestCase
{
    Map<Class<?>, Class<? extends ISimpleXmlWrapper<?>>> wrappers;
    Map<Class<?>, Boolean> strict;

    @Before
    public void clearWrappers()
    {
        wrappers = SimpleXmlWrappers.wrappers;
        strict = SimpleXmlWrappers.strict;
    }

    @After
    public void restoreWrappers()
    {
        SimpleXmlWrappers.wrappers = wrappers;
        SimpleXmlWrappers.strict = strict;
    }

    @Test
    public void testByte() throws Exception
    {
        check((byte) -10);
        check((byte) 0);
        check((byte) 10);
    }

    @Test
    public void testShort() throws Exception
    {
        check((short) -5);
        check((short) 0);
        check((short) 16);
    }

    @Test
    public void testInt() throws Exception
    {
        check(-120);
        check(0);
        check(20);
    }

    @Test
    public void testLong() throws Exception
    {
        check(-2093847298347L);
        check(0L);
        check(209293847298347L);
    }

    @Test
    public void testFloat() throws Exception
    {
        check(-0.25f);
        check(0f);
        check(0.5f);
    }

    @Test
    public void testDouble() throws Exception
    {
        check(-0.125);
        check(0);
        check(8.5);
    }

    @Test
    public void testBoolean() throws Exception
    {
        check(true);
        check(false);
    }

    @Test
    public void testChar() throws Exception
    {
        check('x');
        check('ą');
    }

    @Test
    public void testString() throws Exception
    {
        check("test");
        check("żółć");
    }

    @Test
    public void testClass() throws Exception
    {
        check(File.class);
        check(MapContainer.class);
    }

    @Test
    public void testFileResource() throws Exception
    {
        check(new FileResource(new File(".").getAbsoluteFile()));
    }

    @Test
    public void testNonprimitiveClassWithDefaultConstructor() throws Exception
    {
        check(new Nonprimitive());
    }

    enum TestEnum
    {
        TEST1, TEST2;

        @Override
        public String toString()
        {
            return name().toLowerCase();
        }
    }

    public static class Nonprimitive
    {
        @Override
        public boolean equals(Object obj)
        {
            // We're using the default constructor to deserialize instance of this
            // class, so it doesn't make sense to try to tell the difference between
            // different instances.
            return ObjectUtils.equals(this.getClass(), obj != null ? obj.getClass() : null);
        }

        @Override
        public int hashCode()
        {
            return getClass().hashCode();
        }
    }

    @Test
    public void testEnum() throws Exception
    {
        check(TestEnum.TEST1);
    }

    @Test
    public void testNull() throws Exception
    {
        check(null);
    }

    @Test
    public void testArrayList() throws Exception
    {
        check(Lists.newArrayList("test1", "test2"));
    }

    @Test
    public void testLinkedList() throws Exception
    {
        check(Lists.newLinkedList(Arrays.asList("test1", "test2")));
    }

    @Test
    public void testSubList() throws Exception
    {
        check(Lists.newArrayList("test1", "test2", "test3").subList(0, 2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMixedTypeList() throws Exception
    {
        check(Lists.newArrayList("test1", 10, true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNestedList() throws Exception
    {
        check(Lists
            .newArrayList(Lists.newArrayList("e1", "e2"), Lists.newArrayList(1, 2)));
    }

    @Test
    public void testHashMap() throws Exception
    {
        final Map<String, Object> map = Maps.newHashMap();
        check(populateTestMap(map));
    }

    @Test
    public void testHashMapWithList() throws Exception
    {
        final Map<String, Object> map = Maps.newHashMap();
        map.put("list", Lists.newArrayList("test1", "test2", "test3"));
        check(map);
    }

    @Test
    public void testTreeMap() throws Exception
    {
        final Map<String, Object> map = Maps.newTreeMap();
        check(populateTestMap(map));
    }

    @Test
    public void testNestedMaps() throws Exception
    {
        final Map<String, Object> map = Maps.newTreeMap();
        final Map<String, Object> inner = Maps.newTreeMap();
        map.put("inner", populateTestMap(inner));
        check(map);
    }

    private Map<String, Object> populateTestMap(final Map<String, Object> map)
    {
        map.put("key1", "val1");
        map.put("key2", 10);
        map.put("key3", true);
        return map;
    }

    @Root(name = "annotated")
    static class AnnotatedClass
    {
        @Attribute(required = false)
        String string;

        @Attribute(required = false)
        Integer integer;

        AnnotatedClass()
        {
        }

        AnnotatedClass(Integer integer, String string)
        {
            this.integer = integer;
            this.string = string;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof AnnotatedClass))
            {
                return false;
            }
            return ObjectUtils.equals(((AnnotatedClass) obj).string, string)
                && ObjectUtils.equals(((AnnotatedClass) obj).integer, integer);
        }

        @Override
        public int hashCode()
        {
            return (string != null ? string.hashCode() : 0)
                ^ (integer != null ? integer.hashCode() : 0);
        }
    }

    @Test
    public void testSimpleXmlAnnotatedClass() throws Exception
    {
        check(new AnnotatedClass(10, "test"));
        check(new AnnotatedClass(-5, null));
        check(new AnnotatedClass(null, "test"));
        check(new AnnotatedClass(null, null));
    }

    static class ClassWithWrapper
    {
        String string;
        Integer integer;

        public ClassWithWrapper(Integer integer, String string)
        {
            this.integer = integer;
            this.string = string;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof ClassWithWrapper))
            {
                return false;
            }
            return ObjectUtils.equals(((ClassWithWrapper) obj).string, string)
                && ObjectUtils.equals(((ClassWithWrapper) obj).integer, integer);
        }

        @Override
        public int hashCode()
        {
            return (string != null ? string.hashCode() : 0)
                ^ (integer != null ? integer.hashCode() : 0);
        }
    }

    @Root(name = "with-wrapper")
    static class ClassWithWrapperWrapper implements ISimpleXmlWrapper<ClassWithWrapper>
    {
        ClassWithWrapper classWithWrapper;

        @Element
        AnnotatedClass forSerialization;

        public ClassWithWrapper getValue()
        {
            return classWithWrapper;
        }

        public void setValue(ClassWithWrapper value)
        {
            this.classWithWrapper = value;
        }

        @Persist
        void beforeSerialization()
        {
            forSerialization = new AnnotatedClass(classWithWrapper.integer,
                classWithWrapper.string);
        }

        @Commit
        void afterDeserialization()
        {
            classWithWrapper = new ClassWithWrapper(forSerialization.integer,
                forSerialization.string);
        }
    }

    static class SubclassWithWrapper extends ClassWithWrapper
    {
        public SubclassWithWrapper(Integer integer, String string)
        {
            super(integer, string);
        }
    }

    @Test
    public void testClassWithWrapperStrictMatched() throws Exception
    {
        SimpleXmlWrappers.addWrapper(ClassWithWrapper.class,
            ClassWithWrapperWrapper.class);
        check(new ClassWithWrapper(10, "test"));
        check(new ClassWithWrapper(-5, null));
        check(new ClassWithWrapper(null, "test"));
        check(new ClassWithWrapper(null, null));
    }

    @Test
    public void testClassWithWrapperStrictNotMatched() throws Exception
    {
        SimpleXmlWrappers.addWrapper(ClassWithWrapper.class,
            ClassWithWrapperWrapper.class);
        checkNotSerialized(new SubclassWithWrapper(10, "test"));
    }
    
    @Test
    public void testClassWithWrapperNotStrictMatched() throws Exception
    {
        SimpleXmlWrappers.addWrapper(ClassWithWrapper.class,
            ClassWithWrapperWrapper.class, false);
        check(new SubclassWithWrapper(10, "test"));
        check(new SubclassWithWrapper(-5, null));
        check(new SubclassWithWrapper(null, "test"));
        check(new SubclassWithWrapper(null, null));
    }

    @Test
    public void testValueWithDefaultStringType() throws Exception
    {
        final String input = "<map><attribute key='key'><value value='value'/></attribute></map>";
        final MapContainer deserialized = new Persister().read(MapContainer.class, input);
        assertThat(deserialized.map.get("key")).isEqualTo("value");
    }

    public void check(Object value) throws Exception
    {
        checkMap(Long.toString(value != null ? value.hashCode() : 0), value);
        checkList(value);
        checkSet(value);
    }

    public void checkNotSerialized(Object value) throws Exception
    {
        final StringWriter writer = new StringWriter();
        final Persister persister = new Persister();
        persister.write(SimpleXmlWrappers.wrap(value), writer);
        final SimpleXmlWrapperValue deserialized = persister.read(
            SimpleXmlWrapperValue.class, writer.toString());
        assertThat((Object) SimpleXmlWrappers.unwrap(deserialized)).isNull();
    }

    public void checkMap(String key, Object value) throws Exception
    {
        final Map<String, Object> original = Maps.newHashMap();
        original.put(key, value);
        final StringWriter writer = new StringWriter();
        new Persister().write(new MapContainer(original), writer);
        // System.out.println("---\n" + writer.toString());
        final MapContainer deserialized = new Persister().read(MapContainer.class,
            new StringReader(writer.getBuffer().toString()));
        assertThat(deserialized.map).isEqualTo(original);
    }

    public void checkList(Object value) throws Exception
    {
        final List<Object> original = Lists.newArrayList();
        original.add(value);
        final StringWriter writer = new StringWriter();
        new Persister().write(new ListContainer(original), writer);
        // System.out.println("---\n" + writer.toString());
        final ListContainer deserialized = new Persister().read(ListContainer.class,
            new StringReader(writer.getBuffer().toString()));
        assertThat(deserialized.list).isEqualTo(original);
    }

    public void checkSet(Object value) throws Exception
    {
        final Set<Object> original = Sets.newHashSet();
        original.add(value);
        final StringWriter writer = new StringWriter();
        new Persister().write(new SetContainer(original), writer);
        // System.out.println("---\n" + writer.toString());
        final SetContainer deserialized = new Persister().read(SetContainer.class,
            new StringReader(writer.getBuffer().toString()));
        assertThat(deserialized.set).isEqualTo(original);
    }

    @Root(name = "map")
    @SuppressWarnings("unused")
    private static class MapContainer
    {
        private Map<String, Object> map;

        @ElementMap(entry = "attribute", inline = true, value = "value", attribute = true, key = "key")
        private HashMap<String, SimpleXmlWrapperValue> mapToSerialize;

        MapContainer()
        {
        }

        MapContainer(Map<String, Object> map)
        {
            this.map = map;
        }

        @Persist
        void wrap()
        {
            mapToSerialize = MapUtils.asHashMap(SimpleXmlWrappers.wrap(map));
        }

        @Commit
        void unwrap()
        {
            map = SimpleXmlWrappers.unwrap(mapToSerialize);
        }
    }

    @Root(name = "list")
    @SuppressWarnings("unused")
    private static class ListContainer
    {
        private List<Object> list;

        @ElementList(entry = "attribute", inline = true)
        private ArrayList<SimpleXmlWrapperValue> listToSerialize;

        public ListContainer()
        {
        }

        public ListContainer(List<Object> list)
        {
            this.list = list;
        }

        @Persist
        void wrap()
        {
            listToSerialize = ListUtils.asArrayList(SimpleXmlWrappers.wrap(list));
        }

        @Commit
        void unwrap()
        {
            list = SimpleXmlWrappers.unwrap(listToSerialize);
        }
    }

    @Root(name = "set")
    @SuppressWarnings("unused")
    private static class SetContainer
    {
        private Set<Object> set;

        @ElementList(entry = "attribute", inline = true)
        private HashSet<SimpleXmlWrapperValue> setToSerialize;

        public SetContainer()
        {
        }

        public SetContainer(Set<Object> set)
        {
            this.set = set;
        }

        @Persist
        void wrap()
        {
            setToSerialize = SetUtils.asHashSet(SimpleXmlWrappers.wrap(set));
        }

        @Commit
        void unwrap()
        {
            set = SimpleXmlWrappers.unwrap(setToSerialize);
        }
    }
}
