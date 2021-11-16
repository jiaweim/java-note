package mjw.study.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 15 Nov 2021, 11:11 PM
 */
public class FocusEventDemo1 extends JFrame implements FocusListener
{
    List info = new List(10);
    JTextField tf = new JTextField("");
    JButton button = new JButton("确认");

    public FocusEventDemo1(String title)
    {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(info, "North");
        add(tf, "Center");
        add(button, "South");
        tf.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        if (e.isTemporary())//将焦点更改事件的标识为暂时性或者永久性
            info.add("暂时性获得");
        else info.add("长久性获得");
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        if (e.isTemporary())//将焦点更改事件的标识为暂时性或者永久性
            info.add("暂时性失去");
        else info.add("长久性失去");
    }

    public static void main(String[] args)
    {
        FocusEventDemo1 t = new FocusEventDemo1("测试窗口");
        t.pack();
        t.setVisible(true);
    }
}
