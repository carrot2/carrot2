package org.carrot2.util.test;

/**
 * Additional FEST-style assertions.
 */
public class Assertions
{
    /**
     * Creates a {@link CharCharArrayAssert}.
     * 
     * @param actual the array to make assertions on.
     */
    public static CharCharArrayAssert assertThat(char [][] actual)
    {
        return new CharCharArrayAssert(actual);
    }

    /**
     * Creates an {@link IntIntArrayAssert}.
     */
    public static IntIntArrayAssert assertThat(int [][] actual)
    {
        return new IntIntArrayAssert(actual);
    }
}
