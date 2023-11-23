package mjw.swing.button;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 25 Nov 2021, 2:57 PM
 */
public class ImagePanel extends JPanel
{
    private Image img;

    public ImagePanel(Image img)
    {
        this.img = img;

        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));

        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLayout(null); // 使用 AbsoluteLayout
    }
}
