package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 13 Nov 2021, 9:48 PM
 */
public class GridLayoutDemo {

    public static void main(String[] args) {
        JFrame jf = new JFrame("测试程序");
        jf.setSize(300, 200);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);

        JPanel contentPane = new JPanel();
        jf.setContentPane(contentPane);

        JButton b1 = new JButton("港币");
        JButton b2 = new JButton("人民币");
        JButton b3 = new JButton("美元");
        JButton b4 = new JButton("欧元");
        JButton b5 = new JButton("英镑");
        JButton b6 = new JButton("主板");
        JButton b7 = new JButton("内存");
        JButton b8 = new JButton("硬盘");
        JButton b9 = new JButton("显示器");

        //创建一个 GridLayout，3 行 3 列
        GridLayout gird = new GridLayout(3, 3);
        contentPane.setLayout(gird);

        contentPane.add(b1);
        contentPane.add(b2);
        contentPane.add(b3);
        contentPane.add(b4);
        contentPane.add(b5);
        contentPane.add(b6);
        contentPane.add(b7);
        contentPane.add(b8);
        contentPane.add(b9);

        jf.pack();
    }
}
