package org.carrot2.source.microsoft;

import com.microsoft.msnsearch.SafeSearchOptions;

/**
 * Safe search modes for Live API.
 */
public enum SafeSearch
{
    OFF, MODERATE, STRICT;

    /**
     * Converts enum constant to a {@link SafeSearchOptions}.
     */
    SafeSearchOptions getSafeSearchOption()
    {
        switch (this)
        {
            case OFF:
                return SafeSearchOptions.Off;
            case MODERATE:
                return SafeSearchOptions.Moderate;
            case STRICT:
                return SafeSearchOptions.Strict;
            default:
                throw new RuntimeException("Unhandled constant: " + this);
        }
    }
}
