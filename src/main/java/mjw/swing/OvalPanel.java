package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 31 Mar 2024, 10:29
 */
public class OvalPanel extends JPanel {
    Color color;

    public OvalPanel() {
        this(Color.BLACK);
    }

    public OvalPanel(Color color) {
        this.color = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.setColor(color);
        g.drawOval(0, 0, width, height);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Oval Sample");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setLayout(new GridLayout(2, 2));
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
            for (int i = 0; i < 4; i++) {
                OvalPanel panel = new OvalPanel(colors[i]);
                frame.add(panel);
            }
            frame.setSize(300, 200);
            frame.setVisible(true);
        });
    }
}
