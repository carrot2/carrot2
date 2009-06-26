package org.carrot2.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Random;

import org.junit.Test;

import bak.pcj.set.IntBitSet;

/**
 * Test cases for {@link EnhancedIntBitSet}.
 */
public class EnhancedIntBitSetTest
{
    @Test
    public void testAddEmpty()
    {
        final EnhancedIntBitSet empty = new EnhancedIntBitSet();
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSize : longSizes)
        {
            final EnhancedIntBitSet addTarget = create(longSize);
            checkAddAll(addTarget, empty, addTarget, false);
        }
    }

    @Test
    public void testAddSame()
    {
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSize : longSizes)
        {
            final EnhancedIntBitSet addTarget = create(longSize);
            checkAddAll(addTarget, addTarget, addTarget, false);
        }
    }

    @Test
    public void testAddNonEmpty()
    {
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSizeA : longSizes)
        {
            for (int longSizeB : longSizes)
            {
                final EnhancedIntBitSet addTarget = create(longSizeA);
                final EnhancedIntBitSet toAdd = create(longSizeB, 1);

                // We'll use IntBitSet to compute the expected results for us
                final IntBitSet expectedResult = new IntBitSet(addTarget);
                expectedResult.addAll(toAdd);

                checkAddAll(addTarget, toAdd, expectedResult, longSizeB != 0);
            }
        }
    }

    @Test
    public void testRemoveEmpty()
    {
        final EnhancedIntBitSet empty = new EnhancedIntBitSet();
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSize : longSizes)
        {
            final EnhancedIntBitSet removeTarget = create(longSize);
            checkRemoveAll(removeTarget, empty, removeTarget, false);
        }
    }

    @Test
    public void testRemoveSame()
    {
        final EnhancedIntBitSet empty = new EnhancedIntBitSet();
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSize : longSizes)
        {
            final EnhancedIntBitSet removeTarget = create(longSize);
            checkRemoveAll(removeTarget, empty, removeTarget, false);
        }
    }

    @Test
    public void testRemoveNonEmpty()
    {
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSizeA : longSizes)
        {
            for (int longSizeB : longSizes)
            {
                final EnhancedIntBitSet removeTarget = create(longSizeA);
                if (longSizeA > 0)
                {
                    removeTarget.add(1);
                }
                final EnhancedIntBitSet toRemove = create(longSizeB, 1);
                if (longSizeB > 0)
                {
                    toRemove.add(1);
                }

                // We'll use IntBitSet to compute the expected results for us
                final IntBitSet expectedResult = new IntBitSet(removeTarget);
                expectedResult.removeAll(toRemove);

                System.out.println(longSizeA + " " + longSizeB);
                checkRemoveAll(removeTarget, toRemove, expectedResult, longSizeA != 0
                    && longSizeB != 0);
            }
        }
    }

    @Test
    public void testRetainEmpty()
    {
        final EnhancedIntBitSet empty = new EnhancedIntBitSet();
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSize : longSizes)
        {
            final EnhancedIntBitSet retainTarget = create(longSize);
            checkRetainAll(retainTarget, empty, empty, longSize != 0);
        }
    }

    @Test
    public void testRetainSame()
    {
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSize : longSizes)
        {
            final EnhancedIntBitSet retainTarget = create(longSize);
            checkRetainAll(retainTarget, retainTarget, retainTarget, false);
        }
    }

    @Test
    public void testRetainNonEmpty()
    {
        final int [] longSizes = new int []
        {
            0, 1, 2, 16
        };
        for (int longSizeA : longSizes)
        {
            for (int longSizeB : longSizes)
            {
                final EnhancedIntBitSet retainTarget = create(longSizeA);
                final EnhancedIntBitSet toRetain = create(longSizeB, 1);

                // We'll use IntBitSet to compute the expected results for us
                final IntBitSet expectedResult = new IntBitSet(retainTarget);
                expectedResult.retainAll(toRetain);

                checkRetainAll(retainTarget, toRetain, expectedResult, longSizeA != 0);
            }
        }
    }

    private void checkAddAll(EnhancedIntBitSet addTarget, EnhancedIntBitSet toAdd,
        IntBitSet expectedResult, boolean expectedChanged)
    {
        final EnhancedIntBitSet actualResult = new EnhancedIntBitSet(addTarget);
        final boolean actualChanged = actualResult.addAll(new EnhancedIntBitSet(toAdd));
        checkResult(expectedResult, expectedChanged, actualResult, actualChanged);

        final EnhancedIntBitSet addTargetCopy = new EnhancedIntBitSet(addTarget);
        assertThat(addTarget.addAllSize(toAdd)).isEqualTo(expectedResult.size());
        assertThat(addTarget).isEqualTo(addTargetCopy); // addTarget must not change
    }

    private void checkRemoveAll(EnhancedIntBitSet removeTarget,
        EnhancedIntBitSet toRemove, IntBitSet expectedResult, boolean expectedChanged)
    {
        final EnhancedIntBitSet actualResult = new EnhancedIntBitSet(removeTarget);
        final boolean actualChanged = actualResult.removeAll(new EnhancedIntBitSet(
            toRemove));
        checkResult(expectedResult, expectedChanged, actualResult, actualChanged);

        final EnhancedIntBitSet removeTargetCopy = new EnhancedIntBitSet(removeTarget);
        assertThat(removeTarget.removeAllSize(toRemove)).isEqualTo(expectedResult.size());
        assertThat(removeTarget).isEqualTo(removeTargetCopy); // addTarget must not change
    }

    private void checkRetainAll(EnhancedIntBitSet retainTarget,
        EnhancedIntBitSet toRetain, IntBitSet expectedResult, boolean expectedChanged)
    {
        final EnhancedIntBitSet actualResult = new EnhancedIntBitSet(retainTarget);
        final boolean actualChanged = actualResult.retainAll(toRetain);
        checkResult(expectedResult, expectedChanged, actualResult, actualChanged);

        final EnhancedIntBitSet retainTargetCopy = new EnhancedIntBitSet(retainTarget);
        assertThat(retainTarget.retainAllSize(toRetain)).isEqualTo(expectedResult.size());
        assertThat(retainTarget).isEqualTo(retainTargetCopy); // addTarget must not change
    }

    private void checkResult(IntBitSet expectedResult, boolean expectedChanged,
        final EnhancedIntBitSet actualResult, final boolean actualChanged)
    {
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
        assertThat(actualResult).isEqualTo(expectedResult);
        assertThat(actualChanged).isEqualTo(expectedChanged);
    }

    private EnhancedIntBitSet create(int longs)
    {
        return create(longs, 0);
    }

    private EnhancedIntBitSet create(int longs, int seed)
    {
        final int maxValue = longs * 64 - (longs > 0 ? 1 : 0);
        final EnhancedIntBitSet result = new EnhancedIntBitSet(maxValue);
        final Random random = new Random(seed);

        for (int i = 0; i < longs * 8; i++)
        {
            result.add(random.nextInt(maxValue));
        }

        return result;
    }
}
