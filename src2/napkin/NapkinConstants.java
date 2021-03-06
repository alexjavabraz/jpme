
package napkin;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.plaf.ColorUIResource;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String IS_PAPER = "napkin.isPaper";
    String BG_COMPONENT = "napkin.bgComponent";
    String PENDING_BG_COMPONENT = "napkin.pendingBGComponent";

    //!! ColorUIResource can't seem to deal with the alpha channel -- file a bug
//    ColorUIResource CLEAR = new ColorUIResource(new Color(0, 0, 0, 0));
    Color CLEAR = new Color(0, 0, 0, 0);
    ColorUIResource BLACK = new ColorUIResource(Color.black);

    int NO_SIDE = -1;
}