package carrot2.demo.swing.util;

import java.text.MessageFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;


/**
 * A threshold slider.
 * 
 * @author Dawid Weiss
 */
public class JDoubleSlider extends JSlider {
    private final MessageFormat labelFormat = new MessageFormat("{0,number,#.##}");
    private RangeScaler scaler;

    public JDoubleSlider(double min, double max, double minorTicks, double majorTicks) {
        this.scaler = new RangeScaler(min, max, 0, 1000);

        super.setMinimum(scaler.getIntMin());
        super.setMaximum(scaler.getIntMax());

        setMinorTickSpacing(scaler.scale(minorTicks));
        setMajorTickSpacing(scaler.scale(majorTicks));
        setPaintLabels(true);
        setPaintTicks(true);
        setSnapToTicks(false);

        setLabelTable(createLabels(scaler.getIntMin(), scaler.getIntMax(), scaler.scale(majorTicks)));
    }

    public void setValue(double value) {
        super.setValue(scaler.to(value));
    }

    public double getDoubleValue() {
        return scaler.from(super.getValue());
    }

    private Hashtable createLabels(int min, int max, int step) {
        final Hashtable table = new Hashtable();

        
        for (int i = min; i <= max; i = i + step) {
            final String label = labelFormat.format(new Object [] {new Double(scaler.from(i))});
            table.put(new Integer(i), createLabel(label));
        }

        String label;

        label = labelFormat.format(new Object [] {new Double(scaler.from(min))});
        table.put(new Integer(min), createLabel(label));

        label = labelFormat.format(new Object [] {new Double(scaler.from(max))});
        table.put(new Integer(max), createLabel(label));

        return table;
    }

    private JLabel createLabel(String label) {
        return new JLabel(label);
    }
}
