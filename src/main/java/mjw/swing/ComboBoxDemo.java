package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 27 Dec 2023, 8:39 PM
 */
public class ComboBoxDemo extends JPanel implements ActionListener {

    JLabel picture;

    public ComboBoxDemo() {
        super(new BorderLayout());
        String[] petStrings = {"Bird", "Cat", "Dog", "Rabbit", "Pig"};

        // 创建 combo-box，选择 index 4 的 Pig
        JComboBox<String> petList = new JComboBox<>(petStrings);
        petList.setSelectedIndex(4);
        petList.addActionListener(this);

        // 配置图片
        picture = new JLabel();
        picture.setFont(picture.getFont().deriveFont(Font.ITALIC));
        picture.setHorizontalAlignment(JLabel.CENTER);
        updateLabel(petStrings[petList.getSelectedIndex()]);
        picture.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 将 JLabel 的首选大小硬编码为最宽图像的宽度和最高图像的高度+边框
        // 真实程序应该根据需要计算
        picture.setPreferredSize(new Dimension(177, 122 + 10));

        add(petList, BorderLayout.PAGE_START);
        add(picture, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    protected void updateLabel(String name) {
        ImageIcon imageIcon = createImageIcon("images/" + name + ".gif");
        picture.setIcon(imageIcon);
        picture.setToolTipText("A drawing of a " + name.toLowerCase());
        if (imageIcon != null) {
            picture.setText(null);
        } else {
            picture.setText("Image not found");
        }
    }

    protected static ImageIcon createImageIcon(String path) {
        URL imgURL = ComboBoxDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> cb = (JComboBox) e.getSource();
        String petName = (String) cb.getSelectedItem();
        updateLabel(petName);
    }

    protected static void createAndShowGUI() {
        JFrame frame = new JFrame("ComboBoxDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JComponent contentPane = new ComboBoxDemo();
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ComboBoxDemo::createAndShowGUI);
    }
}
