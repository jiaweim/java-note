package mjw.study.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 11:02 AM
 */
public class FreezeEDT extends JFrame implements ActionListener
{
    public FreezeEDT()
    {
        super("Freeze");
        JButton freezer = new JButton("Freeze");
        freezer.addActionListener(this);
        add(freezer);
        pack();
    }

    public void actionPerformed(ActionEvent e)
    {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException evt) {
        }
    }

    public static void main(String... args)
    {
        FreezeEDT edt = new FreezeEDT();
        edt.setVisible(true);
    }
}
