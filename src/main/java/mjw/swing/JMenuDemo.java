package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 25 Dec 2023, 4:46 PM
 */
public class JMenuDemo extends JFrame {

    public JMenuDemo() throws HeadlessException {
        super("菜单测试");
        JRootPane rp = new JRootPane();
        setContentPane(rp);

        JMenuBar menuBar = new JMenuBar();
        rp.setJMenuBar(menuBar);

        JMenu menu1 = new JMenu("文件");
        JMenu menu2 = new JMenu("编辑");
        JMenu menu3 = new JMenu("视图");
        JMenu menu4 = new JMenu("运行");
        JMenu menu5 = new JMenu("工具");
        JMenu menu6 = new JMenu("帮助");

        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu3);
        menuBar.add(menu4);
        menuBar.add(menu5);
        menuBar.add(menu6);

        setVisible(true);
    }

    public static void main(String[] args) {
        new JMenuDemo();
    }
}
