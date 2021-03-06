
package napkin;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

public abstract class NapkinBorder extends AbstractBorder {
    private final Border formalBorder;
    private boolean recentlyFormal;

    public NapkinBorder(Border formalBorder) {
        this.formalBorder = formalBorder;
    }

    public Insets getBorderInsets(Component c) {
        if (isFormal(c))
            return formalBorder.getBorderInsets(c);
        else
            return doGetBorderInsets(c);
    }

    public boolean isBorderOpaque() {
        if (recentlyFormal)
            return formalBorder.isBorderOpaque();
        else
            return doIsBorderOpaque();
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {

        if (isFormal(c))
            formalBorder.paintBorder(c, g, x, y, width, height);
        else
            doPaintBorder(c, g, x, y, width, height);
    }

    protected boolean isFormal(Component c) {
        return (recentlyFormal = NapkinUtil.isFormal(c));
    }

    protected abstract Insets doGetBorderInsets(Component c);

    protected abstract boolean doIsBorderOpaque();

    protected abstract void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height);
}