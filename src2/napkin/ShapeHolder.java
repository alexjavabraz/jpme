
package napkin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

abstract class ShapeHolder {
    ShapeGenerator gen;
    Shape shape;
    float width;

    interface Factory {
        ShapeHolder create();
    }

    ShapeHolder(ShapeGenerator gen) {
        this(gen, 1);
    }

    ShapeHolder(ShapeGenerator gen, float width) {
        this.gen = gen;
        this.width = width;
    }

    void draw(Graphics g) {
        Graphics2D lineG = NapkinUtil.lineGraphics(g, width);
        lineG.draw(shape);
    }

    void fill(Graphics g) {
        Graphics2D fillG = NapkinUtil.lineGraphics(g, 1);
        fillG.fill(shape);
    }
}