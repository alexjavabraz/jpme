
package napkin;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

class BoxHolder extends ShapeHolder implements NapkinConstants {
    private Rectangle size;
    private int breakSide;
    private final Point2D begBreak, endBreak;

    BoxHolder(BoxGenerator gen) {
        super(gen);
        breakSide = NO_SIDE;
        begBreak = new Point2D.Double();
        endBreak = new Point2D.Double();
    }

    BoxHolder() {
        this(new BoxGenerator());
    }

    void shapeUpToDate(Component c, Rectangle sz) {
        shapeUpToDate(c, sz, -1, 0, 0, 0, 0);
    }

    void shapeUpToDate(Component c, Rectangle sz, int bSide, double begX,
            double begY, double endX, double endY) {

        if (size != null && size.width == sz.width && size.height == sz.height
                && bSide == breakSide &&
                begBreak.getX() == begX && begBreak.getY() == begY &&
                endBreak.getX() == endX && endBreak.getY() == endY) {

            return;
        }

        size = (Rectangle) sz.clone();
        breakSide = bSide;
        begBreak.setLocation(begX, begY);
        endBreak.setLocation(endX, endY);

        Insets in = DrawnBorder.DEFAULT_INSETS;

        double innerWidth = sz.getWidth() - (in.left + in.right);
        double innerHeight = sz.getHeight() - (in.top + in.bottom);
        double borderWidth = innerWidth + in.right - 1;
        double borderHeight = innerHeight + in.bottom - 1;

        int cornerX = in.top / 2 + 1;
        int cornerY = in.left / 2 + 1;

        BoxGenerator gen = (BoxGenerator) this.gen;
        gen.getSizeX().setMid(borderWidth);
        gen.getSizeY().setMid(borderHeight);
        gen.getBegX().setMid(cornerX);
        gen.getEndY().setMid(cornerY);

        AffineTransform matrix = new AffineTransform();
        matrix.translate(cornerX, cornerY);

        if (bSide == NO_SIDE)
            gen.setNoBreak();
        else
            gen.setBreak(bSide, begX, begY, endX, endY);

        shape = gen.generate(matrix);
    }
}