package mjw.java2d;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Dec 2023, 4:44 PM
 */
class BasicStrokesSurface extends JPanel {

    private void doDrawing(Graphics g) {
        Graphics2D gd = (Graphics2D) g.create();

        float[] dash1 = {2f, 0f, 2f};
        float[] dash2 = {1f, 1f, 1f};
        float[] dash3 = {4f, 0f, 2f};
        float[] dash4 = {4f, 4f, 1f};

        gd.drawLine(20, 40, 250, 40);

        BasicStroke bs1 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash1, 2f);
        BasicStroke bs2 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash2, 2f);
        BasicStroke bs3 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash3, 2f);
        BasicStroke bs4 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash4, 2f);

        gd.setStroke(bs1);
        gd.drawLine(20, 80, 250, 80);

        gd.setStroke(bs2);
        gd.drawLine(20, 120, 250, 120);

        gd.setStroke(bs3);
        gd.drawLine(20, 160, 250, 160);

        gd.setStroke(bs4);
        gd.drawLine(20, 200, 250, 200);

        gd.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class BasicStrokesEx extends JFrame {

    public BasicStrokesEx() {
        initUI();
    }

    private void initUI() {
        add(new BasicStrokesSurface());
        setTitle("Basic strokes");
        setSize(280, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            BasicStrokesEx ex = new BasicStrokesEx();
            ex.setVisible(true);
        });
    }
}
