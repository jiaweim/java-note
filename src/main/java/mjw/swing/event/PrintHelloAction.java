package mjw.swing.event;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 09 Apr 2024, 11:23 AM
 */
public class PrintHelloAction extends AbstractAction {

    private static final Icon printIcon = FontIcon.of(FontAwesomeSolid.PRINT, 24);

    public PrintHelloAction() {
        super("Print", printIcon);
        putValue(Action.SHORT_DESCRIPTION, "Hello, World");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Hello, World");
    }
}
