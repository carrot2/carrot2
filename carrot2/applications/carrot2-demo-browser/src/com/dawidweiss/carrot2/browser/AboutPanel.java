package com.dawidweiss.carrot2.browser;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * About panel.
 * @author Dawid Weiss
 */
public class AboutPanel extends JPanel {
    
    public AboutPanel() {
        setLayout(new BorderLayout());
        
        JTextArea ta = new JTextArea();
        ta.setBorder(BorderFactory.createEmptyBorder());
        ta.setBackground(this.getBackground());
        ta.setLineWrap(false);
        
        ta.setText("Carrot2 Local interfaces demo.\n\n"
        		+ "This is a technology showcase ONLY. Not for real use.\n\n"
                + "Includes icons from the Eclipse project.\n");
        this.add(ta);
    }
}
