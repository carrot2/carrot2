
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

package carrot2.demo;

import carrot2.demo.swing.SwingDemoGui;

/**
 * Carrot2 demo in Swing.
 * 
 * @author Dawid Weiss
 */
public class DemoSwing {
    public static void main(String[] args) {
        final DemoContext carrotDemo = new DemoContext();
        final SwingDemoGui demoGui = new SwingDemoGui(carrotDemo); 
        demoGui.display();
    }
}
