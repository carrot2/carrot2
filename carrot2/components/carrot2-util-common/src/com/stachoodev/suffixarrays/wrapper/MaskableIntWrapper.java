/*
 * MaskableIntWrapper.java Created on 2004-06-21
 */
package com.stachoodev.suffixarrays.wrapper;

/**
 * An integer wrapper that is capable of representing groups of symbols. The
 * {@link #SECONDARY_BITS}lowest order bits represent different types of
 * symbols within one group (e.g. variants of the same stem), while the
 * remaining bits represent distinct groups.
 * 
 * @author stachoo
 */
public interface MaskableIntWrapper extends IntWrapper
{
    /**
     * The number of lowest order bits reserved for representing different
     * symbols within a group.
     */
    public static final int SECONDARY_BITS = 5;
    public static final int SECONDARY_OFFSET = (1 << SECONDARY_BITS);
    public static final int SECONDARY_MASK = ~(SECONDARY_OFFSET - 1);
}