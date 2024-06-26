package mjw.swing.event;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 10 Apr 2024, 1:58 PM
 */
public class VerifierSample {

    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Verifier Sample");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JTextField textField1 = new JTextField();
                JTextField textField2 = new JTextField();
                JTextField textField3 = new JTextField();

                InputVerifier verifier = new InputVerifier() {
                    public boolean verify(JComponent comp) {
                        boolean returnValue;
                        JTextField textField = (JTextField) comp;
                        try {
                            Integer.parseInt(textField.getText());
                            returnValue = true;
                        } catch (NumberFormatException e) {
                            returnValue = false;
                        }
                        return returnValue;
                    }
                };

                // 设置 InputVerifier
                textField1.setInputVerifier(verifier);
                textField3.setInputVerifier(verifier);

                frame.add(textField1, BorderLayout.NORTH);
                frame.add(textField2, BorderLayout.CENTER);
                frame.add(textField3, BorderLayout.SOUTH);
                frame.setSize(300, 100);
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
