package carrot2.demo.swing.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A two-state toolbar icon button class.
 */
public class ToolbarButton extends JButton {
    private final Icon iconEnabled;
    private final Icon iconDisabled;

    /**
     * Draws border only on mouse flyover.
     */
    private final MouseListener rolloverMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            ((JButton) e.getSource()).setBorderPainted(false);
            ((JButton) e.getSource()).setIcon(iconEnabled);
        }

        public void mouseExited(MouseEvent e) {
            ((JButton) e.getSource()).setBorderPainted(false);
            ((JButton) e.getSource()).setIcon(iconDisabled);
        }
    };

    /**
     * Constructs a toolbar button.
     */
    public ToolbarButton(Icon iconEnabled, Icon iconDisabled) {
        super(iconDisabled);

        this.iconEnabled = iconEnabled;
        this.iconDisabled = iconDisabled;

        setFocusPainted(false);
        setBorderPainted(false);
        addMouseListener(rolloverMouseListener);
        this.setContentAreaFilled(false);
    }
}
