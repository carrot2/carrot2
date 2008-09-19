package org.carrot2.source.boss;

/**
 * Content filtering constants for {@link BossWebSearchService#filter}.
 */
public enum FilterConst
{
    PORN, HATE;

    public String toString()
    {
        switch (this)
        {
            case PORN:
                return "-port";
            case HATE:
                return "-hate";
        }

        throw new RuntimeException();
    }
}