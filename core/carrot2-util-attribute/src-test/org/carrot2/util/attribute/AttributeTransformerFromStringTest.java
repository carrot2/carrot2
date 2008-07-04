package org.carrot2.util.attribute;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Field;

import org.carrot2.util.attribute.AttributeBinder.AttributeTransformerFromString;
import org.junit.Test;

/**
 * Test cases for {@link AttributeTransformerFromString}.
 */
public class AttributeTransformerFromStringTest
{
    @SuppressWarnings("unused")
    private Integer integerField;

    @SuppressWarnings("unused")
    private String stringField;

    enum TestEnum
    {
        VALUE1, VALUE2;
    }

    @SuppressWarnings("unused")
    private TestEnum enumField;

    @SuppressWarnings("unused")
    private AttributeTransformerFromStringTest loadableClassField;

    @Test
    public void testNonStringValue()
    {
        final Integer integer = Integer.valueOf(10);

        assertThat(
            AttributeTransformerFromString.INSTANCE.transform(integer, null, null, null))
            .isSameAs(integer);
    }

    @Test
    public void testStringField() throws Exception
    {
        final String string = "test";
        check("stringField", string, string);
    }

    @Test
    public void testExistingValueOfInteger() throws Exception
    {
        final Integer integer = Integer.valueOf(10);
        check("integerField", integer.toString(), integer);
    }

    @Test
    public void testExistingValueOfEnum() throws Exception
    {
        final TestEnum eenum = TestEnum.VALUE1;
        check("enumField", eenum.name(), eenum);
    }

    @Test
    public void testLoadableClass() throws Exception
    {
        check("loadableClassField", AttributeTransformerFromStringTest.class.getName(),
            AttributeTransformerFromStringTest.class);
    }

    @Test
    public void testNonLoadableClass() throws Exception
    {
        final String value = "x" + AttributeTransformerFromStringTest.class.getName();
        check("loadableClassField", value, value);
    }

    private void check(String fieldName, String stringValue,
        Object expectedTransformedValue) throws Exception
    {
        final Field field = AttributeTransformerFromStringTest.class
            .getDeclaredField(fieldName);
        assertThat(
            AttributeTransformerFromString.INSTANCE.transform(stringValue, null, field,
                null)).isEqualTo(expectedTransformedValue);
    }
}
