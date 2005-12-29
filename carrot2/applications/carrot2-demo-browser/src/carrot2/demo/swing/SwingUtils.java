package carrot2.demo.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Various Swing utilities.
 * 
 * @author Dawid Weiss
 */
public final class SwingUtils {
    private SwingUtils() {
        // No instances.
    }

    /**
     * Displays a message dialog with exception information.
     */
    public static void showExceptionDialog(Component component, String message, Throwable t) {
        final JPanel panel = new JPanel(new BorderLayout(5, 5));

        final JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Dialog", Font.BOLD, messageLabel.getFont().getSize()));

        final JTextArea stacktraceArea = new JTextArea(20, 60);
        stacktraceArea.setFont(new Font("Monospaced", Font.PLAIN, stacktraceArea.getFont().getSize()));
        stacktraceArea.setWrapStyleWord(false);
        stacktraceArea.setLineWrap(false);
        stacktraceArea.setText(
                  "Exception: " + t.getClass().getName() + "\n\n"
                + "Exception message: " + t.getMessage() + "\n\n"
                + "Stack trace:\n" + getStackTrace(t));
        stacktraceArea.setEditable(false);
        final JScrollPane stacktraceAreaScroller = new JScrollPane(stacktraceArea);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(stacktraceAreaScroller, BorderLayout.CENTER);

        // Adjust stack trace dimensions
        final Dimension stacktraceDimension = stacktraceArea.getPreferredScrollableViewportSize();
        final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        screenDimension.setSize(screenDimension.getWidth() * 0.7, screenDimension.getHeight() * 0.7);
        final Dimension maxStackTraceDimension = new Dimension(500, 500);
        maxStackTraceDimension.setSize(
                Math.min(maxStackTraceDimension.getWidth(), screenDimension.getWidth()), 
                Math.min(maxStackTraceDimension.getHeight(), screenDimension.getHeight()));
        stacktraceDimension.setSize(
                Math.min(stacktraceDimension.getWidth(), maxStackTraceDimension.getWidth()), 
                Math.min(stacktraceDimension.getHeight(), maxStackTraceDimension.getHeight()));
        stacktraceAreaScroller.setPreferredSize(stacktraceDimension);

        JOptionPane.showMessageDialog(component, panel, "Exception occurred.", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Dumps a {@link Throwable}'s stack trace to a string.
     */
     private static String getStackTrace(Throwable e) {
         final StringWriter sw = new StringWriter();
         final PrintWriter  pw = new PrintWriter(sw);
         e.printStackTrace(pw);
         pw.close();
         return sw.toString();
     }
}
