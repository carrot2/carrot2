package org.carrot2.util.attribute.constraint;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Test cases for {@link NotBlankConstraint}.
 */
public class NotBlankConstraintTest
{
    static class NonBlankAnnotationContainer
    {
        @NotBlank
        String string;
    }

    @Test
    public void testValidString() throws Exception
    {
        assertValid("  test  ");
    }

    @Test
    public void testInvalidString() throws Exception
    {
        assertInvalid("  \t");
    }

    @Test
    public void testValidCharSequence() throws Exception
    {
        assertValid(new StringBuffer(" test "));
    }

    @Test
    public void testInvalidCharSequence() throws Exception
    {
        assertInvalid(new StringBuffer(" \t  "));
    }

    @Test
    public void testInvalidNull() throws Exception
    {
        assertInvalid(null);
    }

    @Test(expected = RuntimeException.class)
    public void testNotCharSequence() throws Exception
    {
        assertValid(Integer.valueOf(20));
    }

    private void assertValid(final Object value) throws NoSuchFieldException
    {
        assertThat(
            ConstraintValidator.isMet(value, NonBlankAnnotationContainer.class
                .getDeclaredField("string").getAnnotation(NotBlank.class))).isEmpty();
    }

    private void assertInvalid(final Object value) throws NoSuchFieldException
    {
        final NotBlank annotation = NonBlankAnnotationContainer.class.getDeclaredField(
            "string").getAnnotation(NotBlank.class);
        assertThat(ConstraintValidator.isMet(value, annotation)).contains(annotation);
    }
}
