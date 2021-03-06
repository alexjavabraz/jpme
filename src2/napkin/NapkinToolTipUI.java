// $Id: NapkinToolTipUI.java,v 1.7 2004/05/30 06:07:41 kcrca Exp $

package napkin;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

public class NapkinToolTipUI extends BasicToolTipUI implements NapkinConstants {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinToolTipUI());
    }

    public void installUI(JComponent c) {
        Color bg = c.getBackground();
        NapkinUtil.installUI(c);
        super.installUI(c);
        c.setOpaque(true);
        c.setBackground(bg);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

