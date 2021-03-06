// $Id: NapkinProgressBarUI.java,v 1.9 2004/05/30 06:07:41 kcrca Exp $

package napkin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class NapkinProgressBarUI extends BasicProgressBarUI {
    private final ScribbleHolder scribble = new ScribbleHolder();
    private final Rectangle sz = new Rectangle(0, 0, 0, 0);
    private Rectangle boxRect;
    private BoxHolder box;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinProgressBarUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        c.setForeground(Color.black);
        super.paint(g, c);
    }

    protected void paintIndeterminate(Graphics g1, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        Graphics2D g = (Graphics2D) g1;

        // Paint the bouncing box.
        boxRect = getBox(boxRect);
        if (boxRect == null)
            return;

        if (box == null) {
            box = new BoxHolder();
            box.width = 2;
        }
        box.shapeUpToDate(null, boxRect);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(NapkinIconFactory.CheckBoxIcon.MARK_COLOR);
        lineG.translate(boxRect.x, boxRect.y);
        box.draw(lineG);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                        boxRect.width, b);
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                        boxRect.height, b);
            }
        }
    }

    protected void paintDeterminate(Graphics g, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        // amount of progress to draw
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        c.getBounds(sz);
        int orientation = progressBar.getOrientation();
        boolean backwards = !NapkinUtil.isLeftToRight(c);
        scribble.shapeUpToDate(c, sz, orientation, amountFull, backwards);

        g.setColor(NapkinIconFactory.CheckBoxIcon.MARK_COLOR);
        scribble.draw(g);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                    amountFull, b);
        }
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

