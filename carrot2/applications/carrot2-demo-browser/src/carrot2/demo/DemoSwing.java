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
