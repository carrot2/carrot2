package carrot2.demo.swing;

import javax.swing.JPanel;

/**
 * All the functionality we need for the demo
 * browser is in this interface. We may then
 * use either Swing's {@link javax.swing.JEditorPane}
 * or JDIC's {@link org.jdesktop.jdic.browser.WebBrowser}
 * class to provide the implementation.
 * 
 * @author Dawid Weiss
 */
public abstract class HtmlDisplay extends JPanel {
    public abstract void setContent(String htmlContent);
}
