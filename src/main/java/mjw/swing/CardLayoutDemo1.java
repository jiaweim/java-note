package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 10:11 PM
 */
public class CardLayoutDemo1 extends JFrame {

    public CardLayoutDemo1() {
        super("CardLayout Test");
        try {
            // 将LookAndFeel设置成Windows样式
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CardLayout card = new CardLayout(5, 5); //创建一个具有指定的水平和垂直间隙的新卡片布局
        // 主要的JPanel，该JPanel的布局管理将被设置成CardLayout
        JPanel mainPane = new JPanel(card); // JPanel的布局管理将被设置成CardLayout

        // 放按钮的 JPanel
        JPanel p = new JPanel(); // 构造放按钮的JPanel

        JButton button_1 = new JButton("< 上一步");
        JButton button_2 = new JButton("下一步 >");
        // 三个可直接翻转到JPanel组件的按钮
        JButton b_1 = new JButton("1");
        JButton b_2 = new JButton("2");
        JButton b_3 = new JButton("3");
        b_1.setMargin(new Insets(2, 2, 2, 2));
        b_2.setMargin(new Insets(2, 2, 2, 2));
        b_3.setMargin(new Insets(2, 2, 2, 2));
        p.add(button_1);
        p.add(b_1);
        p.add(b_2);
        p.add(b_3);
        p.add(button_2);

        // 要切换的三个 JPanel，设置为不同颜色
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.setBackground(Color.RED);
        p2.setBackground(Color.BLUE);
        p3.setBackground(Color.GREEN);
        p1.add(new JLabel("JPanel-1"));
        p2.add(new JLabel("JPanel-2"));
        p3.add(new JLabel("JPanel-3"));
        mainPane.add(p1, "p1");
        mainPane.add(p2, "p2");
        mainPane.add(p3, "p3");

        //下面是翻转到卡片布局的某个组件的动作事件处理，当单击按钮，就会触发出现下一个组件
        /// 上一步的按钮动作
        button_1.addActionListener(e -> card.previous(mainPane));
        // 下一步的按钮动作
        button_2.addActionListener(e -> card.next(mainPane));
        // 直接翻转到p_1
        b_1.addActionListener(e -> card.show(mainPane, "p1"));
        // 直接翻转到p_2
        b_2.addActionListener(e -> card.show(mainPane, "p2"));
        // 直接翻转到p_3
        b_3.addActionListener(e -> card.show(mainPane, "p3"));

        getContentPane().add(mainPane);
        getContentPane().add(p, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setVisible(true);
    }

    public static void main(String[] args) {
        new CardLayoutDemo1();
    }
}
