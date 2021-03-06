
package napkin;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

class ScribbleHolder extends ShapeHolder implements NapkinConstants {
    private Rectangle size;
    private Insets insets;
    private int orientation;
    private int shown;
    private boolean backwards;

    private static final float LINE_WIDTH = 3;

    ScribbleHolder() {
        super(new ScribbleGenerator(LINE_WIDTH), LINE_WIDTH);
    }

    void shapeUpToDate(Component c, Rectangle sz, int orient, int shn,
            boolean bwrds) {
        Insets in = (c instanceof JComponent ?
                ((JComponent) c).getInsets() : DrawnBorder.DEFAULT_INSETS);

        if (size != null && bwrds == backwards && insets.equals(in) &&
                orientation == orient && shown == shn &&
                size.width == sz.width && size.height == sz.height) {

            return;
        }

        size = (Rectangle) sz.clone();
        insets = (Insets) in.clone();
        orientation = orient;
        shown = shn;
        backwards = bwrds;

        int cornerX = in.top / 2 + 1;
        int cornerY = in.left / 2 + 1;

        double innerWidth = sz.getWidth() - (in.left + in.right);
        double innerHeight = sz.getHeight() - (in.top + in.bottom);

        ScribbleGenerator gen = (ScribbleGenerator) this.gen;
        gen.setShown(shown);
        gen.setOrientation(orientation);
        gen.setRange(orientation == HORIZONTAL ? innerHeight : innerWidth);
        gen.setMax(orientation == HORIZONTAL ?  innerWidth: innerHeight);

        AffineTransform matrix = new AffineTransform();
        matrix.translate(cornerY, cornerX);
        if (backwards) {
            matrix.translate(innerWidth, innerHeight);
            matrix.scale(-1, -1);
        }

        shape = gen.generate(matrix);
    }
}