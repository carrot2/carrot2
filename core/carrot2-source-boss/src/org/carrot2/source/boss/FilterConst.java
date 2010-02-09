
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
