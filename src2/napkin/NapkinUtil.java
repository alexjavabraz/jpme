// $Id: NapkinUtil.java,v 1.17 2004/05/30 06:07:41 kcrca Exp $

package napkin;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

public class NapkinUtil implements NapkinConstants {
    private static final Set printed = new HashSet();

    private static final Integer BOTTOM_LAYER = new Integer(Integer.MIN_VALUE);

    private static final Logger logger = Logger.getLogger("NapkinUtil");
    public static final Random random = new Random();
    public static final Icon EMPTY_ICON = new Icon() {
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }

        public int getIconWidth() {
            return 0;
        }

        public int getIconHeight() {
            return 0;
        }
    };

    private static Map strokes = new WeakHashMap();
    private static Map fieldsForType = new WeakHashMap();

    public static class DumpListener implements FocusListener {
        private Timer timer;

        public void focusGained(final FocusEvent ev) {
            if (timer != null)
                timer.stop();
            int delay = 1000; //milliseconds
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    NapkinLookAndFeel laf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
                    laf.dumpFormality(
                            ((JComponent) ev.getSource()).getTopLevelAncestor(),
                            System.out);
                }
            };
            timer = new Timer(delay, taskPerformer);
            timer.start();
        }

        public void focusLost(FocusEvent e) {
            if (timer != null)
                timer.stop();
        }
    }

    static boolean isFormal(JComponent l) {
        NapkinLookAndFeel nlaf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
        return nlaf.isFormal(l);
    }

    public static boolean isFormal(Component c) {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf instanceof NapkinLookAndFeel) {
            NapkinLookAndFeel nlaf = (NapkinLookAndFeel) laf;
            return nlaf.isFormal(c);
        }
        return true;
    }

    static ComponentUI uiFor(JComponent c, ComponentUI napkinUI) {
        NapkinLookAndFeel nlaf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
        ComponentUI ui;
        if (nlaf.isFormal(c))
            ui = nlaf.getFormal().getDefaults().getUI(c);
        else
            ui = napkinUI;
        if (logger.isLoggable(Level.FINER) && !printed.contains(c.getClass())) {
            logger.finer(c.getUIClassID() + "\n  " + napkinUI.getClass() +
                    "\n  " + c.getClass());
            printed.add(c.getClass());
        }
        return ui;
    }

    static String prBool(boolean bool, String name) {
        if (bool)
            return name;
        else
            return '!' + name;
    }

    static String descFor(Object obj) {
        if (obj instanceof Component)
            return descFor((Component) obj);
        else
            return obj.getClass().getName();
    }

    static String descFor(Component c) {
        if (c == null)
            return "null";
        String desc;
        if ((desc = c.getName()) != null)
            return desc;
        desc = c.getClass().getName();
        int dot = desc.lastIndexOf('.');
        if (dot > 0)
            desc = desc.substring(dot + 1);

        if (c instanceof JLabel)
            desc += ": " + ((JLabel) c).getText();
        if (c instanceof AbstractButton)
            return desc + ": " + ((AbstractButton) c).getText();
        if (c instanceof JTextField)
            return desc + ": " + ((JTextField) c).getText();
        if (c instanceof JPopupMenu)
            return desc + ": " + ((JPopupMenu) c).getLabel();
        if (c instanceof Label)
            return desc + ": " + ((Label) c).getText();
        if (c instanceof TextField)
            return desc + ": " + ((TextField) c).getText();
        if (c instanceof Button)
            return desc + ": " + ((Button) c).getLabel();
        if (c instanceof Checkbox)
            return desc + ": " + ((Checkbox) c).getLabel();
        if (c instanceof Dialog)
            return desc + ": " + ((Dialog) c).getTitle();
        if (c instanceof Frame)
            return desc + ": " + ((Frame) c).getTitle();
        if (c instanceof JInternalFrame)
            return desc + ": " + ((JInternalFrame) c).getTitle();

        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Border border = jc.getBorder();
            if (border instanceof TitledBorder)
                return desc + ": " + ((TitledBorder) border).getTitle();
        }

        return desc;
    }

    public static void installUI(JComponent c) {
        c.setOpaque(false);
        c.setBackground(CLEAR);
    }

    public static void uninstallUI(JComponent c) {
        c.setOpaque(true);
        unsetupBorder(c);
    }

    static void setBackground(Component child, NapkinBackground bg) {
        setBackground(layeredPane(child), bg);
    }

    static JLayeredPane layeredPane(Component child) {
        for (Component c = child; c != null; c = c.getParent()) {
            if (c instanceof JLayeredPane)
                return (JLayeredPane) c;
        }
        return null;
    }

    static void setBackground(JLayeredPane lp, NapkinBackground bg) {
        if (lp == null)
            return;
        removeBackground(lp);
        Component cur = new NapkinBackgroundLabel(bg);
        lp.add(cur, BOTTOM_LAYER);
        lp.putClientProperty(BG_COMPONENT, cur);
    }

    static void removeBackground(Component child) {
        for (Component c = child; c != null; c = c.getParent()) {
            if (c instanceof JLayeredPane) {
                JLayeredPane lp = (JLayeredPane) c;
                removeBackground(lp);
                return;
            }
        }
        // removing is OK even if nothing is there
    }

    static void removeBackground(JLayeredPane lp) {
        Component cur = (Component) lp.getClientProperty(BG_COMPONENT);
        if (cur != null)
            lp.remove(cur);
    }

    static void uninstallLayeredPane(JLayeredPane lp) {
        removeBackground(lp);
    }

    public static double leftRight(double x, boolean left) {
        return (left ? x : ShapeGenerator.LENGTH - x);
    }

    public static AffineTransform copy(AffineTransform matrix) {
        if (matrix == null)
            return new AffineTransform();
        else
            return (AffineTransform) matrix.clone();
    }

    public static Graphics2D copy(Graphics g) {
        return (Graphics2D) g.create();
    }

    public static Graphics2D lineGraphics(Graphics orig, float w) {
        return lineGraphics((Graphics2D) orig, w);
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        Graphics2D lineG = copy(orig);

        Float key = new Float(w);
        Stroke stroke = (Stroke) strokes.get(key);
        if (stroke == null) {
            stroke = new BasicStroke(w, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);
            strokes.put(key, stroke);
        }
        lineG.setStroke(stroke);
        lineG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return lineG;
    }

    public static Graphics2D defaultGraphics(Graphics g1, Component c) {
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        setupBorder(c);
        return g;
    }

    private static void setupBorder(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Border b = jc.getBorder();
            if (b instanceof UIResource)
                jc.setBorder(new NapkinWrappedBorder(b));
        }
    }

    private static void unsetupBorder(JComponent c) {
        Border b = c.getBorder();
        if (b instanceof NapkinWrappedBorder) {
            NapkinWrappedBorder nb = (NapkinWrappedBorder) b;
            c.setBorder(nb.getFormalBorder());
        }
    }

    boolean isPressed(Component c) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            return model.isPressed() && model.isArmed();
        }
        return false;
    }

    boolean isDefault(Component c) {
        if (c instanceof JButton)
            return ((JButton) c).isDefaultButton();
        return false;
    }

    static AffineTransform scaleMat(double scale) {
        AffineTransform mat = new AffineTransform();
        mat.scale(scale, scale);
        return mat;
    }

    static void transform(AffineTransform matrix, double[] points) {
        if (matrix != null)
            matrix.transform(points, 0, points, 0, points.length / 2);
    }

    static JButton createArrowButton(int pointTowards) {
        int size = NapkinIconFactory.ArrowIcon.DEFAULT_SIZE;
        return createArrowButton(pointTowards, size);
    }

    static JButton createArrowButton(int pointTowards, int size) {
        JButton button = new JButton(
                NapkinIconFactory.createArrowIcon(pointTowards, size));
        button.setBorderPainted(false);
        return button;
    }

    static void applyPendingBackground(JComponent c) {
        Object pending = c.getClientProperty(PENDING_BG_COMPONENT);
        if (pending != null) {
            c.putClientProperty(PENDING_BG_COMPONENT, null);
            setBackground(c, (NapkinBackground) pending);
        }
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    public static LineHolder paintLine(Graphics g, boolean vertical,
            LineHolder holder, Rectangle bounds) {
        if (holder == null)
            holder = new LineHolder(CubicGenerator.INSTANCE, vertical);
        holder.shapeUpToDate(bounds, null);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(Color.black);
        if (vertical)
            lineG.translate(bounds.x + bounds.width / 2, 0);
        else
            lineG.translate(0, bounds.y + bounds.height / 2);
        holder.draw(lineG);
        return holder;
    }

    static void dumpTo(String file, JComponent c) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new FileWriter(file)));
            Set dumped = new HashSet();
            dumpTo(out, c, c.getClass(), 0, dumped);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void dumpTo(PrintWriter out, Object obj, Class cl, int level,
            Set dumped)
            throws IllegalAccessException {
        if (cl == null)
            return;
        dumpTo(out, obj, cl.getSuperclass(), level, dumped);
        Field[] fields = cl.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object val = field.get(obj);
            for (int l = 0; l < level; l++)
                out.print("    ");
            out.println(field.getName() + ": " + val);
            if (val != null && !dumped.contains(obj) &&
                    !field.getType().isPrimitive()) {
                dumpTo(out, val, val.getClass(), level + 1, dumped);
                dumped.add(obj);
            }
        }
    }

    public static Object[] reallocate(Object[] orig, int count) {
        if (count == orig.length)
            return orig;
        Class componentType = orig.getClass().getComponentType();
        Object[] next = (Object[]) Array.newInstance(componentType, count);
        System.arraycopy(orig, 0, next, 0, Math.min(orig.length, next.length));
        return next;
    }

    public static void printPair(String label, double x, double y) {
        System.out.println(label + ": " + x + ", " + y);
    }

    public static void background(Graphics g, Component c) {
        String name = c.getName();
        if (name != null && name.endsWith("glassPane"))
            return;

        JLayeredPane lp = layeredPane(c);
        if (lp == null)
            return;
        NapkinBackgroundLabel lab = (NapkinBackgroundLabel)
                lp.getClientProperty(BG_COMPONENT);

        NapkinBackground bg = null;

        if(lab != null){
          bg = lab.getNapkinBackground();
        }

        if(bg == null){
         bg = new NapkinBackground("resources/napkin.jpg");
        }

        boolean print = c.getClass() == JLabel.class;
        print = false;
        Point start = getStart(c, print);
        bg.paint(c, g, -start.x, -start.y, lp.getWidth(), lp.getHeight());
    }

    private static Point getStart(Component c, boolean print) {
        Point start = new Point(0, 0);
        while (c != null && !(c instanceof JRootPane)) {
            if (print)
                System.out.println(
                        "(" + c.getX() + ", " + c.getY() + "): " + descFor(c));
            start.x += c.getX();
            start.y += c.getY();
            if (print)
                System.out.println("start = " + start);
            c = c.getParent();
        }
        return start;
    }

    public static void dumpObject(Object obj, String fileName) {
        PrintStream out = null;
        try {
            out =
                    new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(fileName)));
            dumpObject(obj, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static void dumpObject(Object obj, PrintStream out) {
        Map known = new HashMap();
        dumpObject(obj, out, 0, known);
    }

    static Set skip;

    static {
        skip = new HashSet();
        skip.add("source");
        skip.add("mostRecentKeyValue");
    }

    private static void
            dumpObject(Object obj, PrintStream out, int depth, Map known) {

        Object id = known.get(obj);
        if (id != null) {
            out.println("<known: " + id + ">");
            return;
        }
        id = new Integer(known.size());
        known.put(obj, id);

        out.println(descFor(obj) + "<" + id + ">");

        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i <= depth; i++)
                sb.append(i % 2 == 0 ? '.' : '|').append(' ');

            Field[] fields = getFields(obj);
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (skip.contains(field.getName()))
                    continue;
                Class type = field.getType();
                out.print(sb);
                out.print(
                        field.getName() + " [" +
                        field.getDeclaringClass().getName() +
                        "]: ");
                Object val = field.get(obj);
                if (type.isPrimitive())
                    out.println(val);
                else {
                    if (val == null || type == String.class)
                        out.println(val);
                    else if (type.isArray()) {
                        Class aType = type.getComponentType();
                        out.println(" " + aType.getName() + "[" +
                                Array.getLength(val) + "]");
                    } else {
                        dumpObject(val, out, depth + 1, known);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Field[] getFields(Object obj) {
        Class type = obj.getClass();
        Field[] fields = (Field[]) fieldsForType.get(type);
        if (fields != null)
            return fields;

        Set fSet = new HashSet();
        int skip = Modifier.STATIC | Modifier.FINAL;
        while (type != Object.class) {
            Field[] declaredFields = type.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                int mods = field.getModifiers();
                if (!field.getDeclaringClass().isAssignableFrom(obj.getClass()))
                    fSet.size();
                if ((mods & skip) == 0)
                    fSet.add(field);
            }
            type = type.getSuperclass();
        }
        fields = (Field[]) fSet.toArray(new Field[fSet.size()]);
        Arrays.sort(fields, new Comparator() {
            public int compare(Object o1, Object o2) {
                Field f1 = (Field) o1;
                Field f2 = (Field) o2;
                int d = f1.getName().compareTo(f2.getName());
                if (d != 0)
                    return d;
                Class c1 = f1.getDeclaringClass();
                Class c2 = f2.getDeclaringClass();
                return c1.getName().compareTo(c2.getName());
            }
        });
        AccessibleObject.setAccessible(fields, true);
        fieldsForType.put(obj.getClass(), fields);
        return fields;
    }
}
