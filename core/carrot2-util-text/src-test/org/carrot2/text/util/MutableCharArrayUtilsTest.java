
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.util;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * Test cases for {@link MutableCharArrayUtils}.
 */
public class MutableCharArrayUtilsTest extends CarrotTestCase
{
    @Test
    public void toLowerCaseNoReallocation()
    {
        final MutableCharArray source = new MutableCharArray("ŁÓdŹ");
        final MutableCharArray result = new MutableCharArray("    z");
        
        assertThat(MutableCharArrayUtils.toLowerCase(source, result)).isTrue();
        assertThat(result.getBuffer()).isEqualTo("łódźz".toCharArray());
    }
    
    @Test
    public void toLowerCaseNoWithReallocation()
    {
        final MutableCharArray source = new MutableCharArray("ŁÓdŹ");
        final MutableCharArray result = new MutableCharArray("abc");
        
        assertThat(MutableCharArrayUtils.toLowerCase(source, result)).isTrue();
        assertThat(result.getBuffer()).isEqualTo("łódź".toCharArray());
    }
}
