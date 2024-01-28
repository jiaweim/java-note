package mjw.swing.j2d.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 12 Jan 2024, 12:36 PM
 */
public class ClippingTest extends JPanel
{
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        FontRenderContext context = g2.getFontRenderContext();

        Font font = g2.getFont();

        TextLayout layout = new TextLayout("Hello", font, context);
        AffineTransform transform = AffineTransform.getTranslateInstance(0, 100);
        Shape outline = layout.getOutline(transform);

        GeneralPath clipShape = new GeneralPath();
        clipShape.append(outline, false);
        g2.setClip(clipShape);
    }
}
