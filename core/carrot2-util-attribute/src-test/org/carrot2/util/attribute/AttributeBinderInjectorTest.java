
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Test cases for {@link AttributeBinderInjector}.
 */
public class AttributeBinderInjectorTest
{
    static class A
    {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface TestInjectionMarker
    {
    }

    @TestInjectionMarker
    static class InjectionReceiver
    {
        private A a;
        private String string;
    }

    @TestInjectionMarker
    static class CompositeInjectionReceiver
    {
        private A a;
        private InjectionReceiver values = new InjectionReceiver();
    }
    
    @TestInjectionMarker
    static class InjectionReceiverWithStaticFields 
    {
        private static A a = new A();
        private String string;
    }

    @Test
    public void testNullInjectionReceiver() throws Exception
    {
        final InjectionReceiver object = new InjectionReceiver();
        final A a = new A();
        object.a = a;

        AttributeBinderInjector.injectByType(TestInjectionMarker.class,
            ImmutableMap.<Class<?>, Object> of(), (Object) null);
    }

    @Test
    public void testNoValuesToInject() throws Exception
    {
        final InjectionReceiver object = new InjectionReceiver();
        final A a = new A();
        object.a = a;

        AttributeBinderInjector.injectByType(TestInjectionMarker.class,
            ImmutableMap.<Class<?>, Object> of(), object);
        assertThat(object.a).isSameAs(a);
        assertThat(object.string).isNull();
    }

    @Test
    public void testNoFieldsToReceiveInjection() throws Exception
    {
        final InjectionReceiver object = new InjectionReceiver();
        final A a = new A();
        object.a = a;

        AttributeBinderInjector.injectByType(TestInjectionMarker.class,
            ImmutableMap.<Class<?>, Object> of(Integer.class, 1), object);
        assertThat(object.a).isSameAs(a);
        assertThat(object.string).isNull();
    }

    @Test
    public void testInjectionOfNonNullValues() throws Exception
    {
        final InjectionReceiver object = new InjectionReceiver();
        final A a = new A();

        AttributeBinderInjector.injectByType(TestInjectionMarker.class,
            ImmutableMap.<Class<?>, Object> of(A.class, a, String.class, "x"), object);
        assertThat(object.a).isSameAs(a);
        assertThat(object.string).isEqualTo("x");
    }

    @Test
    public void testInjectionOfNullValues() throws Exception
    {
        final InjectionReceiver object = new InjectionReceiver();
        final A a = new A();
        object.a = a;
        object.string = "x";

        final Map<Class<?>, Object> values = Maps.newHashMap();
        values.put(String.class, null);
        values.put(A.class, null);
        AttributeBinderInjector.injectByType(TestInjectionMarker.class, values, object);
        assertThat(object.a).isNull();
        assertThat(object.string).isNull();
    }

    @Test
    public void testCompositeInjectionReceiver() throws Exception
    {
        final CompositeInjectionReceiver object = new CompositeInjectionReceiver();
        object.a = null;
        final A a = new A();

        AttributeBinderInjector.injectByType(TestInjectionMarker.class,
            ImmutableMap.<Class<?>, Object> of(A.class, a, String.class, "x"), object);
        assertThat(object.a).isSameAs(a);
        assertThat(object.values.a).isSameAs(a);
        assertThat(object.values.string).isEqualTo("x");
    }

    @Test
    public void staticFieldsInInjectionReceiverNotBound() throws Exception
    {
        final InjectionReceiverWithStaticFields object = new InjectionReceiverWithStaticFields();
        object.string = "z";
        final A oldA = InjectionReceiverWithStaticFields.a;
        final A newA = new A();

        AttributeBinderInjector.injectByType(TestInjectionMarker.class,
            ImmutableMap.<Class<?>, Object> of(A.class, newA, String.class, "x"), object);
        assertThat(InjectionReceiverWithStaticFields.a).isSameAs(oldA);
        assertThat(object.string).isEqualTo("x");
    }
}
