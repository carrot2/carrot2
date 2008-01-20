
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

package org.carrot2.demo.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.carrot2.util.StringUtils;

/**
 * Various Swing utilities.
 * 
 * @author Dawid Weiss
 */
public final class SwingUtils
{
    private SwingUtils()
    {
        // No instances.
    }

    /**
     * Displays a message dialog with exception information.
     */
    public static void showExceptionDialog(Component component, String message,
        Throwable t)
    {
        final JPanel panel = new JPanel(new BorderLayout(5, 5));

        final JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Dialog", Font.BOLD, messageLabel
            .getFont().getSize()));

        final JTextArea stacktraceArea = new JTextArea(20, 60);
        stacktraceArea.setFont(new Font("Monospaced", Font.PLAIN,
            stacktraceArea.getFont().getSize()));
        stacktraceArea.setWrapStyleWord(false);
        stacktraceArea.setLineWrap(false);
        stacktraceArea.setText("Exception: " + t.getClass().getName() + "\n\n"
            + "Exception message: " + t.getMessage() + "\n\n"
            + "Stack trace:\n" + StringUtils.getStackTrace(t));
        stacktraceArea.setEditable(false);
        final JScrollPane stacktraceAreaScroller = new JScrollPane(
            stacktraceArea);

        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(stacktraceAreaScroller, BorderLayout.CENTER);

        // Adjust stack trace dimensions
        final Dimension stacktraceDimension = stacktraceArea
            .getPreferredScrollableViewportSize();
        final Dimension screenDimension = Toolkit.getDefaultToolkit()
            .getScreenSize();
        screenDimension.setSize(screenDimension.getWidth() * 0.7,
            screenDimension.getHeight() * 0.7);
        final Dimension maxStackTraceDimension = new Dimension(500, 500);
        maxStackTraceDimension.setSize(Math.min(maxStackTraceDimension
            .getWidth(), screenDimension.getWidth()), Math.min(
            maxStackTraceDimension.getHeight(), screenDimension.getHeight()));
        stacktraceDimension.setSize(Math.min(stacktraceDimension.getWidth(),
            maxStackTraceDimension.getWidth()), Math.min(stacktraceDimension
            .getHeight(), maxStackTraceDimension.getHeight()));
        stacktraceAreaScroller.setPreferredSize(stacktraceDimension);

        JOptionPane.showMessageDialog(component, panel, "Exception occurred.",
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Centers a {@link JFrame} on screen.
     */
    public static void centerFrameOnScreen(JFrame frame)
    {
        final Dimension position = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((position.width - frame.getWidth()) / 2,
            (position.height - frame.getHeight()) / 2);
    }

    /**
     * Adds to the dialog a key listener that makes the dialog invisible.
     * 
     * @param dialog
     */
    public static void addEscapeKeyCloseAction(final JDialog dialog)
    {
        dialog.getRootPane().registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}
