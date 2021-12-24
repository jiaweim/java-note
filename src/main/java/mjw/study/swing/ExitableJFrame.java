package mjw.study.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 24 Nov 2021, 8:07 PM
 */
public class ExitableJFrame extends JFrame
{
    public ExitableJFrame() throws HeadlessException
    {}

    public ExitableJFrame(String title) throws HeadlessException
    {
        super(title);
    }

    @Override
    protected void frameInit()
    {
        super.frameInit();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
