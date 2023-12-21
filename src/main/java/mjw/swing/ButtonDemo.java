package mjw.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 9:00 PM
 */
public class ButtonDemo extends JPanel {

    public ButtonDemo() {
        JFrame frame = new JFrame("按钮测试");
        frame.setSize(300, 200);
        frame.setContentPane(this);

        JLabel name = new JLabel("李磊");
        JRadioButton b1 = new JRadioButton("男");
        JRadioButton b2 = new JRadioButton("女");
        add(name);
        add(b1);
        add(b2);

        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(b1);
        bg1.add(b2);

        JLabel interesting = new JLabel("兴趣爱好");
        JCheckBox cb1 = new JCheckBox("羽毛球");
        JCheckBox cb2 = new JCheckBox("足球");
        JCheckBox cb3 = new JCheckBox("电脑");
        JCheckBox cb4 = new JCheckBox("数学");
        JCheckBox cb5 = new JCheckBox("电影");
        JCheckBox cb6 = new JCheckBox("录像");


        add(interesting);
        add(cb1);
        add(cb2);
        add(cb3);
        add(cb4);
        add(cb5);
        add(cb6);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ButtonDemo();
    }
}
