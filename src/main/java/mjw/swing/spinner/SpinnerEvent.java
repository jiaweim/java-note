package mjw.swing.spinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Calendar;

/**
 * 可以在 Spinner 或其 model 注册监听器，监听值的变化。
 *
 * @author JiaweiMao
 * @version 1.0.0
 * @since 25 Nov 2021, 1:42 PM
 */
public class SpinnerEvent extends JPanel implements ChangeListener
{
    protected Calendar calendar;
    protected JSpinner dateSpinner;

    protected Color SPRING_COLOR = new Color(0, 204, 51);
    protected Color SUMMER_COLOR = Color.RED;
    protected Color FALL_COLOR = new Color(255, 153, 0);
    protected Color WINTER_COLOR = Color.CYAN;

    public SpinnerEvent()
    {

    }

    @Override
    public void stateChanged(ChangeEvent e)
    {

    }
}
