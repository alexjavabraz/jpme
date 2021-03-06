// $Id: Value.java,v 1.3 2004/05/12 02:52:24 kcrca Exp $

package napkin;

public class Value implements ValueSource {

    private double adjust;
    private double mid;
    private double range;

    public Value(double val) {
        this(val, 0);
    }

    public Value(double val, double range) {
        this.mid = val;
        this.range = range;
    }

    public void randomize() {
        double factor = getRange();
        if (factor == 0)
            adjust = 0;
        else
            adjust = NapkinUtil.random.nextGaussian() * factor;
    }

    public double get() {
        return getMid() + adjust;
    }

    public double generate() {
        randomize();
        return get();
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getAdjust() {
        return adjust;
    }

    public double min() {
        return mid - range;
    }

    public double max() {
        return mid + range;
    }
}

