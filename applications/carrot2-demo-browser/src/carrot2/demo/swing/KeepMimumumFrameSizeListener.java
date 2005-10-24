package carrot2.demo.swing;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

/**
 * A component listener which, when attached to a {@link javax.swing.JFrame}, prevents
 * from resizing it below a given value.
 */
public class KeepMimumumFrameSizeListener extends ComponentAdapter {
    private final Dimension minSize;

    public KeepMimumumFrameSizeListener(Dimension minSize) {
        this.minSize = new Dimension(minSize);
    }

    public void componentResized(ComponentEvent e) {
        if (e.getSource() instanceof JFrame) {
            final JFrame frame = (JFrame) e.getSource();
            if (frame.getWidth() < minSize.width
                    || frame.getHeight() < minSize.getHeight()) {
                frame.setSize(
                        Math.max(frame.getWidth(), minSize.width),
                        Math.max(frame.getHeight(), minSize.height));
            }
        }
    }
}
