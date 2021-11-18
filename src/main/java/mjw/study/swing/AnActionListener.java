package mjw.study.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 18 Nov 2021, 11:33 AM
 */
public class AnActionListener implements ActionListener
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("I was selected.");
    }
}
