package mjw.swing.pane;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 07 Apr 2024, 3:14 PM
 */
public class MoveViewSample {

    public static final int INCREASE = 0; // direction
    public static final int DECREASE = 1; // direction
    public static final int X_AXIS = 0; // axis
    public static final int Y_AXIS = 1; // axis
    public static final int UNIT = 0; // type
    public static final int BLOCK = 1; // type

    static class MoveAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

}
