package mjw.swing.table;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 19 Jan 2024, 12:35 PM
 */
public class ScrollTableDemo1 extends JFrame
{
    public ScrollTableDemo1() throws HeadlessException {
        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(new JTable(10, 10));
        contentPane.add(new JScrollPane(new JTable(10, 10)));
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ScrollTableDemo1 demo1 = new ScrollTableDemo1();
            demo1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            demo1.setVisible(true);
        });
    }
}
