package mjw.swing;

import javax.swing.*;
import java.awt.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 17 Nov 2021, 12:26 PM
 */
public class JComboBoxDemo2 {
    String[] s = {"王鹏", "朱雪莲", "王宸博", "朱广兴", "朱广莲", "马力", "欧海", "黎明"};

    public JComboBoxDemo2() {
        JFrame f = new JFrame("JComboBox");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = f.getContentPane();
        ComboBoxModel mode = new UserDefineComboBoxModel();//创建一个UserDefineComboBoxModel对象
        JComboBox combo = new JComboBox(mode);//通过UserDefineComboBoxModel对象来创建一个下拉列表框
        combo.setBorder(BorderFactory.createTitledBorder("你的好朋友是谁?"));
        contentPane.add(combo);
        f.pack();
        f.setVisible(true);
    }

    public static void main(String[] args) {
        new JComboBoxDemo2();
    }

    //创建一个继承AbstractListModel 同时实现ComboBoxModel这个接口的类UserDefineComboBoxModel
    class UserDefineComboBoxModel extends AbstractListModel implements ComboBoxModel {
        String item = null;

        public Object getElementAt(int index) //由于继承AbstractListModel抽象类。因此我们分别在程序中实作了getElementAt()与getSize()方法。
        {
            return s[index++];
        }

        public int getSize() {
            return s.length;
        }

        public void setSelectedItem(Object anItem) //由于我们实现了ComboBoxModel interface.因此我们必须在程序中实作setSelectedItem()与getSelectedItem()方法.
        {
            item = (String) anItem;
        }

        public Object getSelectedItem() {
            return item;
        }
    }
}

