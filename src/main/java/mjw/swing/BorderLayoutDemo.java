package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 13 Nov 2021, 9:35 PM
 */
public class BorderLayoutDemo {


    public static void main(String[] args) {
        JFrame jf = new JFrame("测试程序");
        jf.setSize(300, 200);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setVisible(true);
        JPanel contentPane = new JPanel();
        jf.setContentPane(contentPane);

        JButton b1 = new JButton("生活");
        JButton b2 = new JButton("工作");
        JButton b3 = new JButton("睡觉");
        JButton b4 = new JButton("购物");
        JButton b5 = new JButton("饮食");

        //创建一个布局管理器对象，将中间容器设置为此布局管理
        BorderLayout lay = new BorderLayout();
        jf.setLayout(lay);

        contentPane.add(b1, "North");
        contentPane.add(b2, "South");
        contentPane.add(b3, "East");
        contentPane.add(b4, "West");
        contentPane.add(b5, "Center");
    }
}
