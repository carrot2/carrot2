package carrot2.demo.swing.util;


/**
 * A helper class to proportionally translate
 * values expressed on one range to another range.
 * 
 * @author Dawid Weiss
 */
public class RangeScaler {
    private final double min;
    private final double max;
    private final int minInt;
    private final int maxInt;

    private final double drange;
    private final int irange;

    public RangeScaler(double min, double max, int minInt, int maxInt) {
        this.min = min;
        this.max = max;
        this.minInt = minInt;
        this.maxInt = maxInt;

        this.irange = maxInt - minInt;
        this.drange = max - min;
    }

    public int to(double value) {
        final double tmp = ((value - min) / drange) * irange + minInt;
        return (int) Math.round(tmp);
    }

    public double from(int value) {
        final double tmp = ((value - minInt) / (double) irange) * drange + min;
        return tmp;
    }

    public int scale(double unit) {
        final double tmp = (unit / drange) * irange;
        return (int) Math.round(tmp);
    }

    public int getIntMin() {
        return minInt;
    }

    public int getIntMax() {
        return maxInt;
    }
}
