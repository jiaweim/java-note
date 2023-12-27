package mjw.java2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 26 Dec 2023, 3:20 PM
 */

class PointSurface extends JPanel implements ActionListener {

    private final int DELAY = 150;
    private Timer timer;

    public PointSurface() {
        initTimer();
    }

    private void initTimer() {
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public Timer getTimer() {
        return timer;
    }

    private void doDrawing(Graphics g) {
        Graphics2D gd = (Graphics2D) g;

        gd.setPaint(Color.blue);
        int w = getWidth();
        int h = getHeight();

        Random r = new Random();
        for (int i = 0; i < 2000; i++) {
            int x = Math.abs(r.nextInt()) % w;
            int y = Math.abs(r.nextInt()) % h;
            gd.drawLine(x, y, x, y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}

public class PointsEx extends JFrame {

    public PointsEx() {
        initUI();
    }

    private void initUI() {
        PointSurface surface = new PointSurface();
        add(surface);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Timer timer = surface.getTimer();
                timer.stop();
            }
        });

        setTitle("Points");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            PointsEx ex = new PointsEx();
            ex.setVisible(true);
        });
    }
}
