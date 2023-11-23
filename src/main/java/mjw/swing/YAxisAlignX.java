package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 24 Nov 2021, 11:23 AM
 */
public class YAxisAlignX
{
    private static Container makeIt(String title, float alignment)
    {
        String[] labels = {"--", "----", "--------", "------------"};
        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder(title));

        BoxLayout layout = new BoxLayout(container, BoxLayout.Y_AXIS);
        container.setLayout(layout);

        for (String label : labels) {
            JButton button = new JButton(label);
            button.setAlignmentX(alignment);
            container.add(button);
        }
        return container;
    }

    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Alignment Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Container panel1 = makeIt("Left", Component.LEFT_ALIGNMENT);
            Container panel2 = makeIt("Center", Component.CENTER_ALIGNMENT);
            Container panel3 = makeIt("Right", Component.RIGHT_ALIGNMENT);

            frame.setLayout(new FlowLayout());
            frame.add(panel1);
            frame.add(panel2);
            frame.add(panel3);
            frame.pack();
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
