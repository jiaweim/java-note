package mjw.study.swing;

import javax.swing.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 11:08 PM
 */
public class ActionEventDemo1 extends JFrame
{
    JButton b;

    public ActionEventDemo1(String str)
    {
        super(str);
        b = new JButton("确认");
        add(b);
        b.addActionListener(e -> b.setText("取消"));
    }

    public static void main(String[] args)
    {
        ActionEventDemo1 me = new ActionEventDemo1("动作事件测试窗口");
        me.pack();
        me.setVisible(true);
    }
}
