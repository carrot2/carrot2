package org.carrot2.util.tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import org.fest.assertions.*;

import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Listeners;
import com.carrotsearch.randomizedtesting.annotations.Timeout;
import com.carrotsearch.randomizedtesting.listeners.VerboseTestInfoPrinter;

/**
 * Base class for Carrot2 test classes. Contains common hooks and setups.
 */
@Timeout(millis = 60 * 1000) // No test should last longer than 60 seconds.
@Listeners({VerboseTestInfoPrinter.class, SuiteResultInfoWriter.class})
public class CarrotTestCase extends RandomizedTest
{
    /*
     * Carrot-2 specific assertions, fest-style.
     */

    /*
     * Declare fest-assertion shortcuts. If not declared there is no way to use static
     * imports because superclass's Assert.assertThat always takes precedence.   
     */

    public static BigDecimalAssert assertThat(BigDecimal actual) { return Assertions.assertThat(actual); }
    public static BooleanAssert    assertThat(boolean actual)    { return Assertions.assertThat(actual); }
    public static BooleanAssert assertThat(Boolean actual) { return Assertions.assertThat(actual); }
    public static BooleanArrayAssert assertThat(boolean[] actual) { return Assertions.assertThat(actual); }
    public static ImageAssert assertThat(BufferedImage actual) { return Assertions.assertThat(actual); }
    public static ByteAssert assertThat(byte actual) { return Assertions.assertThat(actual); }
    public static ByteAssert assertThat(Byte actual) { return Assertions.assertThat(actual); }
    public static ByteArrayAssert assertThat(byte[] actual) { return Assertions.assertThat(actual); }
    public static CharAssert assertThat(char actual) { return Assertions.assertThat(actual); }
    public static CharAssert assertThat(Character actual) { return Assertions.assertThat(actual); }
    public static CharArrayAssert assertThat(char[] actual) { return Assertions.assertThat(actual); }
    public static CollectionAssert assertThat(Collection<?> actual) { return Assertions.assertThat(actual); }
    public static ListAssert assertThat(List<?> actual) { return Assertions.assertThat(actual); }
    public static DoubleAssert assertThat(double actual) { return Assertions.assertThat(actual); }
    public static DoubleAssert assertThat(Double actual) { return Assertions.assertThat(actual); }
    public static DoubleArrayAssert assertThat(double[] actual) { return Assertions.assertThat(actual); }
    public static FileAssert assertThat(File actual) { return Assertions.assertThat(actual); }
    public static FloatAssert assertThat(float actual) { return Assertions.assertThat(actual); }
    public static FloatAssert assertThat(Float actual) { return Assertions.assertThat(actual); }
    public static FloatArrayAssert assertThat(float[] actual) { return Assertions.assertThat(actual); }
    public static IntAssert assertThat(int actual) { return Assertions.assertThat(actual); }
    public static IntAssert assertThat(Integer actual) { return Assertions.assertThat(actual); }
    public static IntArrayAssert assertThat(int[] actual) { return Assertions.assertThat(actual); }
    public static IteratorAssert assertThat(Iterable<?> actual) { return Assertions.assertThat(actual); }
    public static IteratorAssert assertThat(Iterator<?> actual) { return Assertions.assertThat(actual); }
    public static LongAssert assertThat(long actual) { return Assertions.assertThat(actual); }
    public static LongAssert assertThat(Long actual) { return Assertions.assertThat(actual); }
    public static LongArrayAssert assertThat(long[] actual) { return Assertions.assertThat(actual); }
    public static MapAssert assertThat(Map<?, ?> actual) { return Assertions.assertThat(actual); }
    public static ObjectAssert assertThat(Object actual) { return Assertions.assertThat(actual); }
    public static ObjectArrayAssert assertThat(Object[] actual) { return Assertions.assertThat(actual); }
    public static ShortAssert assertThat(short actual) { return Assertions.assertThat(actual); }
    public static ShortAssert assertThat(Short actual) { return Assertions.assertThat(actual); }
    public static ShortArrayAssert assertThat(short[] actual) { return Assertions.assertThat(actual); }
    public static StringAssert assertThat(String actual) { return Assertions.assertThat(actual); }
    public static <T extends AssertExtension> T assertThat(T assertion) { return Assertions.assertThat(assertion); }
    public static ThrowableAssert assertThat(Throwable actual) { return Assertions.assertThat(actual); }
}
