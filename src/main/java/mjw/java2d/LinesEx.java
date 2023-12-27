package mjw.java2d;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Dec 2023, 3:37 PM
 */
class LinesSurface extends JPanel {

    private void doDrawing(Graphics g) {
        Graphics2D gd = (Graphics2D) g;

        gd.drawLine(30, 30, 200, 30);
        gd.drawLine(200, 30, 30, 200);
        gd.drawLine(30, 200, 200, 200);
        gd.drawLine(200, 200, 30, 30);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class LinesEx extends JFrame {

    public LinesEx() {
        initUI();
    }

    private void initUI() {
        add(new LinesSurface());

        setTitle("Lines");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            LinesEx ex = new LinesEx();
            ex.setVisible(true);
        });
    }
}
