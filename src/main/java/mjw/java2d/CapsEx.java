package mjw.java2d;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Dec 2023, 5:06 PM
 */
class CapsSurface extends JPanel {

    private void doDrawing(Graphics g) {
        Graphics2D gd = (Graphics2D) g.create();

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        gd.setRenderingHints(rh);

        BasicStroke bs1 = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        gd.setStroke(bs1);
        gd.drawLine(20, 30, 250, 30);


        BasicStroke bs2 = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        gd.setStroke(bs2);
        gd.drawLine(20, 80, 250, 80);

        BasicStroke bs3 = new BasicStroke(20, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
        gd.setStroke(bs3);
        gd.drawLine(20, 130, 250, 130);

        BasicStroke bs4 = new BasicStroke();
        gd.setStroke(bs4);
        gd.drawLine(20, 10, 20, 150);
        gd.drawLine(250, 10, 250, 150);
        gd.drawLine(260, 10, 260, 150);

        gd.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class CapsEx extends JFrame {

    public CapsEx() {
        initUI();
    }

    private void initUI() {
        add(new CapsSurface());

        setTitle("Caps");
        setSize(280, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            CapsEx ex = new CapsEx();
            ex.setVisible(true);
        });
    }
}
