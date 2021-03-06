// $Id: NapkinFileChooserUI.java,v 1.11 2004/05/30 06:07:41 kcrca Exp $

package napkin;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

// It seems as if the BasicFileChooserUI is not yet well formed, so we're just
// borrowing the metal chooser for now.

public class NapkinFileChooserUI extends MetalFileChooserUI {
    private static final ComponentWalker.Visitor NO_BORDER_VISITOR =
            new ComponentWalker.Visitor() {
                public boolean visit(Component c, int depth) {
                    if (c instanceof JButton) {
                        JButton button = (JButton) c;
                        button.setBorderPainted(false);
                    }
                    return true;
                }
            };

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinFileChooserUI((JFileChooser) c));
    }

    private NapkinFileChooserUI(JFileChooser c) {
        super(c);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void installComponents(JFileChooser fc) {
        super.installComponents(fc);
        Component[] comps = fc.getComponents();
        JPanel topPanel = (JPanel) comps[0];
        new ComponentWalker(topPanel, NO_BORDER_VISITOR);
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

