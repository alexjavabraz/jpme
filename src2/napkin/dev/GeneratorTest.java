// $Id: GeneratorTest.java,v 1.1 2004/03/18 04:19:30 kcrca Exp $

package napkin.dev;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import napkin.CubicGenerator;
import napkin.NapkinConstants;
import napkin.NapkinUtil;
import napkin.QuadGenerator;
import napkin.ShapeGenerator;
import napkin.Value;
import napkin.ValueSource;

public class GeneratorTest extends NapkinUtil implements NapkinConstants {

    private static final double MARK_SIZE = 3;

    // Subclass of this that implement Generator will have a symbol conflict
    // if we just call this "length"
    private static final int STD_LENGTH = ShapeGenerator.LENGTH;
    static final int SPACE = STD_LENGTH / 2;
    static final int MIN_HEIGHT = STD_LENGTH * 2;

    private static final Rectangle2D mark = new Rectangle2D.Double();

    static final DecimalFormat DECIMAL = new DecimalFormat("#0.09");

    private static Drawer currentDrawer;
    private static JCheckBox showControlPoints;
    private static JButton randomize;

    private static final Drawer[] drawers;

    static final ValueSource ZERO = new Value(0);

    static final ChangeListener REPAINT = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            if (currentDrawer != null) {
                currentDrawer.getDrawing().repaint();
            }
        }
    };
    static final ChangeListener NEWPOINTS = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            if (currentDrawer != null) {
                currentDrawer.rebuild();
                REPAINT.stateChanged(null);
            }
        }
    };
    static final ActionListener REPAINT_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            REPAINT.stateChanged(null);
        }
    };
    protected final Value width;
    protected final ValueSpinner widthSpin;

    static {
        CubicTest cubic = new CubicTest();
        QuadTest quad = new QuadTest();
        BoxTest box = new BoxTest((CubicGenerator) cubic.getGenerator(),
                (QuadGenerator) quad.getGenerator());
        CheckBoxTest checkBox = new CheckBoxTest();
        drawers = new Drawer[]{cubic, quad, box, checkBox};
    }

    interface Drawer {
        JComponent getDrawing();

        JComponent getControls();

        ShapeGenerator getGenerator();

        ValueSource[] getSpinners();

        String getName();

        void rebuild();
    }

    public GeneratorTest() {
        width = new Value(1);
        widthSpin = new ValueSpinner("w", width, 0, 3, 20);
    }

    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        final JTabbedPane tabs = new JTabbedPane();
        for (int i = 0; i < drawers.length; i++) {
            Drawer drawer = drawers[i];
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(drawer.getControls(), BorderLayout.CENTER);
            panel.add(drawer.getDrawing(), BorderLayout.WEST);
            tabs.addTab(drawer.getName(), panel);
        }
        currentDrawer = drawers[0];

        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                currentDrawer = drawers[tabs.getSelectedIndex()];
            }
        });
        tabs.setSelectedIndex(0);

        JFrame top = new JFrame("Drawing Test");
        top.getContentPane().add(tabs, BorderLayout.CENTER);
        top.getContentPane().add(displayControls(), BorderLayout.SOUTH);

        top.pack();
        top.show();
    }

    private static JPanel displayControls() {
        showControlPoints = new JCheckBox("Show control points", false);
        randomize = new JButton("Randomize");
        randomize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentDrawer != null) {
                    ValueSource[] sources = currentDrawer.getSpinners();
                    for (int i = 0; i < sources.length; i++) {
                        sources[i].randomize();
                    }
                    currentDrawer.rebuild();
                }
            }
        });
        randomize.addChangeListener(REPAINT);

        JPanel displayControls =
                controlSet("Display", showControlPoints, randomize);
        return displayControls;
    }

    static JPanel controlSet(String label, JComponent v1) {
        return controlSet(label, new JComponent[]{v1});

    }

    static JPanel controlSet(String label, JComponent v1, JComponent v2) {
        return controlSet(label, new JComponent[]{v1, v2});

    }

    static JPanel controlSet(String label, JComponent v1, JComponent v2,
            JComponent v3) {
        return controlSet(label, new JComponent[]{v1, v2, v3});

    }

    private static JPanel controlSet(String label, JComponent[] cs) {
        JPanel controls = new JPanel();
        controls.setBorder(new TitledBorder(label));
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        for (int i = 0; i < cs.length; i++) {
            JComponent c = cs[i];
            if (c == null)
                continue;
            controls.add(c);
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                button.addChangeListener(REPAINT);
            }
        }
        return controls;
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        return NapkinUtil.lineGraphics(orig, w);
    }

    static Graphics2D markGraphics(Graphics2D orig) {
        if (!showControlPoints.isSelected())
            return null;
        Graphics2D markG = (Graphics2D) orig.create();
        markG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        markG.setColor(Color.blue);
        markG.setStroke(new BasicStroke(0.25f));
        return markG;
    }

    static void
            mark(Graphics2D g, ValueSource vx, ValueSource vy, boolean left) {

        if (g == null)
            return;

        double d = MARK_SIZE / 2;
        double x = NapkinUtil.leftRight(vx.get(), left);
        double y = vy.get();
        mark.setRect(x - d, y - d, MARK_SIZE, MARK_SIZE);
        g.setColor(Color.blue);
        g.fill(mark);

        double xMid = NapkinUtil.leftRight(vx.getMid(), left);
        double xRange = vx.getRange();
        double yMid = vy.getMid();
        double yRange = vy.getRange();
        mark.setRect(xMid - xRange, yMid - yRange, xRange * 2, yRange * 2);
        g.setColor(Color.red);
        g.draw(mark);
    }

    static void mark(Graphics2D g, ValueSource vx, ValueSource vy,
            ValueSource vw, ValueSource vh) {
        if (g == null)
            return;

        g.draw(new Rectangle2D.Double(vx.getMid() - vx.getRange(),
                vy.getMid() - vy.getRange(), vw.getMid() + vw.getRange(),
                vh.getMid() + vh.getRange()));
        g.draw(new Rectangle2D.Double(vx.getMid() + vx.getRange(),
                vy.getMid() + vy.getRange(), vw.getMid() - vw.getRange(),
                vh.getMid() - vh.getRange()));
    }
}

