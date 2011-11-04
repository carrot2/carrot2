package org.carrot2.text.preprocessing;

import org.carrot2.util.tests.CarrotTestCase;

/**
 * Adds static assertThat on {@link PreprocessingContext}.
 */
public class PreprocessingContextTestBase extends CarrotTestCase
{
    public static PreprocessingContextAssert assertThat(PreprocessingContext ctx) 
    {
        return PreprocessingContextAssert.assertThat(ctx); 
    }
}
