package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 19 Nov 2021, 11:14 AM
 */
public class ButtonPopupSample
{
    static final Random random = new Random();

    // 定义监听器
    static class ButtonActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("Selected: " + e.getActionCommand());
        }
    }

    // 定义弹窗监听器
    static class ShowPopupActionListener implements ActionListener
    {
        // owner 组件
        private final Component component;

        public ShowPopupActionListener(Component component)
        {
            this.component = component;
        }

        @Override
        public synchronized void actionPerformed(ActionEvent e)
        {
            JButton button = new JButton("Hello, World!");
            button.addActionListener(new ButtonActionListener());

            PopupFactory factory = PopupFactory.getSharedInstance();
            int x = random.nextInt(200);
            int y = random.nextInt(200);
            Popup popup = factory.getPopup(component, button, x, y);
            popup.show();

            // 3 秒后自动关闭
            Timer timer = new Timer(3000, e1 -> popup.hide());
            timer.start();
        }
    }

    public static void main(String[] args)
    {
        Runnable runner = () -> {
            JFrame frame = new JFrame("Button Popup Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // 定义弹窗监听器，在 frame 上显示
            ActionListener actionListener = new ShowPopupActionListener(frame);

            JButton start = new JButton("Pick me for Popup");
            start.addActionListener(actionListener);
            frame.add(start);
            frame.setSize(350, 250);
            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }
}
