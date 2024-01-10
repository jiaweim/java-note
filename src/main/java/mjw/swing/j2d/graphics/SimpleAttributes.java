package mjw.swing.j2d.graphics;

import javax.swing.*;
import java.awt.*;

public class SimpleAttributes extends JComponent
{

    public void paintComponent(Graphics g) {
        // 创建临时 Graphics2D
        Graphics2D g2d = (Graphics2D) g.create();

        // 设置背景，并清除背景
        g2d.setBackground(Color.GRAY);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        // 使用默认 font 和 color 画字符串
        g2d.drawString("Default Font", 10, 20);

        // 使用默认 color 和 stroke 画直线
        g2d.drawLine(10, 22, 80, 22);

        // 修改字体
        g2d.setFont(g.getFont().deriveFont(Font.BOLD | Font.ITALIC, 24f));
        // 修改 color
        g2d.setColor(Color.WHITE);
        // 修改 stroke
        g2d.setStroke(new BasicStroke(10f,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        // 使用新的 font 和 color 画字符串和直线
        g2d.drawString("New Font", 10, 50);
        g2d.drawLine(10, 57, 120, 57);
        g2d.dispose();
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(150, 100);
        JComponent test = new SimpleAttributes();
        f.add(test);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Runnable doCreateAndShowGUI = SimpleAttributes::createAndShowGUI;
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}
