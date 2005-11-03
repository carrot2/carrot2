package carrot2.demo.settings;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JLabel;

public class IntScale {

    public final int minInt;
    public final int maxInt;
    public final int intStep;

    private double scaling;

    public IntScale(double min, int max, double step) {
        intStep = 1;
        this.scaling = 1.0d / step;
        minInt = (int) (min * scaling);
        maxInt = (int) (max * scaling);
    }

    public int toInt(double value) {
        return Math.max(minInt, Math.min(maxInt, (int)(value * scaling)));
    }

    public double fromInt(int value) {
        return value / scaling;
    }

    public Dictionary createLabels(int divs) {
        int interval = (maxInt - minInt) / divs;
        if (interval == 0) {
            interval = 1;
        }
        int current = minInt;
        Dictionary d = new Hashtable();
        while (current < maxInt) {
            d.put(new Integer(current), new JLabel(Double.toString(fromInt(current))));
            current += interval;
        }

        return d;
    }
}
