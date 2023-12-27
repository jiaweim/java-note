package mjw.java2d;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Dec 2023, 5:24 PM
 */
class JoinsSurface extends JPanel {

    private void doDrawing(Graphics g) {
        Graphics2D gd = (Graphics2D) g.create();

        BasicStroke bs1 = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        gd.setStroke(bs1);
        gd.drawRect(15, 15, 80, 50);

        BasicStroke bs2 = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        gd.setStroke(bs2);
        gd.drawRect(125, 15, 80, 50);

        BasicStroke bs3 = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        gd.setStroke(bs3);
        gd.drawRect(235, 15, 80, 50);

        gd.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class JoinsEx extends JFrame {

    public JoinsEx() {
        initUI();
    }

    private void initUI() {
        add(new JoinsSurface());

        setTitle("Joins");
        setSize(340, 110);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JoinsEx ex = new JoinsEx();
            ex.setVisible(true);
        });
    }
}
