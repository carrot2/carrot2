
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package carrot2.demo.swing.util;

import junit.framework.TestCase;

public class RangeScalerTest extends TestCase {

    public void testTo() {
        RangeScaler scaler = new RangeScaler(1.0, 10.0, 0, 100);
        assertEquals(0, scaler.to(1.0));
        assertEquals(100, scaler.to(10.0));
    }

    public void testScale() {
        RangeScaler scaler = new RangeScaler(100, 200, 1000, 2000);
        assertEquals(10, scaler.scale(1));
    }

    public void testFrom() {
        RangeScaler scaler = new RangeScaler(1.0, 10.0, 0, 100);
        assertEquals(scaler.from(0), 1.0, 0.0001);
        assertEquals(scaler.from(100), 10.0, 0.0001);
    }
}
