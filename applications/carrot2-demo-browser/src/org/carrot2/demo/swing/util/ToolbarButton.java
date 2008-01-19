
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.swing.util;

import java.awt.event.*;

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
