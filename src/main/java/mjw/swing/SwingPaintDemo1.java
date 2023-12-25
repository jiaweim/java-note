package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 25 Dec 2023, 8:39 AM
 */
public class SwingPaintDemo1 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? " +
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.add(new MyPanel());
        f.setSize(250, 250);
        f.setVisible(true);
    }
}

class MyPanel extends JPanel {

    RedSquare redSquare = new RedSquare();

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });
    }

    private void moveSquare(int x, int y) {
        // 当前 square 状态
        final int CURR_X = redSquare.getX();
        final int CURR_Y = redSquare.getY();
        final int CURR_W = redSquare.getWidth();
        final int CURR_H = redSquare.getHeight();
        final int OFFSET = 1;

        if ((CURR_X != x) || (CURR_Y != y)) {
            // 将原始位置填充背景
            repaint(CURR_X, CURR_Y, CURR_W + OFFSET, CURR_H + OFFSET);

            // 更新坐标
            redSquare.setX(x);
            redSquare.setY(y);

            // 重绘新位置的 square
            repaint(redSquare.getX(), redSquare.getY(),
                    redSquare.getWidth() + OFFSET,
                    redSquare.getHeight() + OFFSET);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(250, 200);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("This is my custom Panel!", 10, 20);
        redSquare.paintSquare(g);
    }
}

class RedSquare {

    private int xPos = 50;
    private int yPos = 50;
    private int width = 20;
    private int height = 20;

    public void setX(int xPos) {
        this.xPos = xPos;
    }

    public int getX() {
        return xPos;
    }

    public void setY(int yPos) {
        this.yPos = yPos;
    }

    public int getY() {
        return yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void paintSquare(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(xPos, yPos, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(xPos, yPos, width, height);
    }
}