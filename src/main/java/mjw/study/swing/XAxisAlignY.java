package mjw.study.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 24 Nov 2021, 11:29 AM
 */
public class XAxisAlignY
{
    private static Container makeIt(String title, float alignment)
    {
        String[] labels = {"-", "-", "-"};
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(title));
        BoxLayout layout = new BoxLayout(container, BoxLayout.X_AXIS);
        container.setLayout(layout);

        for (String label : labels) {
            JButton button = new JButton(label);
            button.setAlignmentY(alignment);
            container.add(button);
        }
        return container;
    }

    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Alignment Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Container panel1 = makeIt("Top", Component.TOP_ALIGNMENT);
            Container panel2 = makeIt("Center", Component.CENTER_ALIGNMENT);
            Container panel3 = makeIt("Bottom", Component.BOTTOM_ALIGNMENT);
            frame.setLayout(new GridLayout(1, 3));
            frame.add(panel1);
            frame.add(panel2);
            frame.add(panel3);
            frame.setSize(423, 171);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
