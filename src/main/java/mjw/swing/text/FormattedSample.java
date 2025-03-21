package mjw.swing.text;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 11 Mar 2025, 10:28 AM
 */
public class FormattedSample {
    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Formatted Example");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JPanel datePanel = new JPanel(new BorderLayout());
                JLabel dateLabel = new JLabel("Date: ");
                dateLabel.setDisplayedMnemonic(KeyEvent.VK_D);
                DateFormat format = new SimpleDateFormat("yyyy--MMMM--dd");
                JFormattedTextField dateTextField = new JFormattedTextField(format);
                dateLabel.setLabelFor(dateTextField);
                datePanel.add(dateLabel, BorderLayout.WEST);
                datePanel.add(dateTextField, BorderLayout.CENTER);
                frame.add(datePanel, BorderLayout.NORTH);

                JPanel date2Panel = new JPanel(new BorderLayout());
                JLabel date2Label = new JLabel("Date 2: ");
                date2Label.setDisplayedMnemonic(KeyEvent.VK_A);
                DateFormat displayFormat = new SimpleDateFormat("yyyy--MMMM--dd");
                DateFormatter displayFormatter = new DateFormatter(displayFormat);
                DateFormat editFormat = new SimpleDateFormat("MM//dd//yy");
                DateFormatter editFormatter = new DateFormatter(editFormat);
                DefaultFormatterFactory factory = new DefaultFormatterFactory(displayFormatter, displayFormatter, editFormatter);
                JFormattedTextField date2TextField = new JFormattedTextField(factory, new Date());
                date2Label.setLabelFor(date2TextField);
                date2Panel.add(date2Label, BorderLayout.WEST);
                date2Panel.add(date2TextField, BorderLayout.CENTER);
                frame.add(date2Panel, BorderLayout.SOUTH);

                ActionListener actionListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFormattedTextField source = (JFormattedTextField) e.getSource();
                        Object value = source.getValue();
                        System.out.println("Class: " + value.getClass());
                        System.out.println("Value: " + value);
                    }
                };
                dateTextField.addActionListener(actionListener);
                date2TextField.addActionListener(actionListener);

                frame.setSize(250, 100);
                frame.setVisible(true);
            }
        };
        EventQueue.invokeLater(runner);
    }
}
