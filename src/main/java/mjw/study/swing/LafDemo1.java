package mjw.study.swing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 16 Nov 2021, 9:35 AM
 */
public class LafDemo1
{
    public LafDemo1()
    {
        JFrame f = new JFrame("学校名录");
        Container contentPane = f.getContentPane();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("学校名录");//使用DefaultMutableTreeNode的构造器创建根节点
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("教导处");//使用DefaultMutableTreeNode的构造器创建四个枝节点
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("一年级");
        DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("二年级");
        DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("三年级");
        root.add(node1); //将四个枝节点添加到根节点中
        root.add(node2);
        root.add(node3);
        root.add(node4);
        DefaultMutableTreeNode leafnode = new DefaultMutableTreeNode("王成");//利用DefaultMutableTreeNode的构造器构造器创建出叶节点，再将页节点分别添加到不同的枝节点上
        node1.add(leafnode);
        leafnode = new DefaultMutableTreeNode("赵薇");
        node1.add(leafnode);
        leafnode = new DefaultMutableTreeNode("李明");
        node1.add(leafnode);
        leafnode = new DefaultMutableTreeNode("宋大兵");
        node2.add(leafnode);
        leafnode = new DefaultMutableTreeNode("雷宝");
        node2.add(leafnode);
        leafnode = new DefaultMutableTreeNode("赵月");
        node2.add(leafnode);

        leafnode = new DefaultMutableTreeNode("潘良");
        node3.add(leafnode);

        leafnode = new DefaultMutableTreeNode("严康");
        node3.add(leafnode);
        leafnode = new DefaultMutableTreeNode("王鹏");
        node3.add(leafnode);
        leafnode = new DefaultMutableTreeNode("刘华");
        node3.add(leafnode);

        leafnode = new DefaultMutableTreeNode("朱卫");
        node4.add(leafnode);

        leafnode = new DefaultMutableTreeNode("谭豪");
        node4.add(leafnode);
        leafnode = new DefaultMutableTreeNode("叶婷");
        node4.add(leafnode);
        leafnode = new DefaultMutableTreeNode("陈浩");
        node4.add(leafnode);

        JTree tree = new JTree(root);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tree);

        contentPane.add(scrollPane);
        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        new LafDemo1();

    }
}
