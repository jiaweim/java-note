package mjw.swing.j2d.graphics;

import javax.swing.*;
import java.awt.*;

public class FillDraw extends JComponent
{
    /**
     * Fill first, then draw the boundary
     */
    private void fillDraw(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    /**
     * Draw the boundary, then fill
     */
    private void drawFill(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, w, h);
    }

    /**
     * Fill the area only inside the boundary (no overlap)
     */
    private void fillInsideDraw(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + 1, y + 1, w - 1, h - 1);
    }

    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Perform three different versions for visual comparison
        // (you'll need a magnifier to see the differences)
        fillDraw(g, 50, 10, 3, 3);
        drawFill(g, 60, 10, 3, 3);
        fillInsideDraw(g, 70, 10, 3, 3);
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(150, 100);
        JComponent test = new FillDraw();
        f.add(test);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable doCreateAndShowGUI = FillDraw::createAndShowGUI;
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}