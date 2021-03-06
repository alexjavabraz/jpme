
package napkin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class NapkinInternalFrameTitlePane extends BasicInternalFrameTitlePane {
    private LineHolder line;
    private Rectangle bounds;

    public NapkinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected void paintTitleBackground(Graphics g) {
        NapkinUtil.background(g, this);

        if (line == null)
            line = new LineHolder(new CubicGenerator());
        Graphics2D ulG = NapkinUtil.copy(g);
        bounds = getBounds(bounds);
        bounds.x = bounds.y = 0;
        line.shapeUpToDate(bounds, null);
        ulG.translate(0, bounds.height - 2);
        line.draw(ulG);
    }

    public void paint(Graphics g) {
        NapkinUtil.defaultGraphics(g, this);
        super.paint(g);
    }
}