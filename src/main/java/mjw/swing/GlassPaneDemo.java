package mjw.swing;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 28 Dec 2023, 5:00 PM
 */
public class GlassPaneDemo {

    static private MyGlassPane myGlassPane;

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JCheckBox changeButton = new JCheckBox("Glass pane \"visible\"");
        changeButton.setSelected(false);

        //Set up the content pane, where the "main GUI" lives.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());
        contentPane.add(changeButton);
        contentPane.add(new JButton("Button 1"));
        contentPane.add(new JButton("Button 2"));

        //Set up the menu bar, which appears above the content pane.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.add(new JMenuItem("Do nothing"));
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // 设置 glass-pane，它在 menu-bar 和 content-pane 上方，item-listener
        myGlassPane = new MyGlassPane(changeButton, menuBar, frame.getContentPane());
        changeButton.addItemListener(myGlassPane);
        frame.setGlassPane(myGlassPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GlassPaneDemo::createAndShowGUI);
    }
}

// 自定义 glass-pane，以实现自定义绘制
class MyGlassPane extends JComponent implements ItemListener {

    Point point;

    /**
     * @param aButton     check-box
     * @param menuBar     menu-bar
     * @param contentPane content-pane
     */
    public MyGlassPane(JCheckBox aButton, JMenuBar menuBar, Container contentPane) {
        CBListener listener = new CBListener(aButton, menuBar, this, contentPane);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    // 响应点击 check-box
    public void itemStateChanged(ItemEvent e) {
        setVisible(e.getStateChange() == ItemEvent.SELECTED);
    }

    // 在 glass-pane 上画个圆
    protected void paintComponent(Graphics g) {
        if (point != null) {
            g.setColor(Color.red);
            g.fillOval(point.x - 10, point.y - 10, 20, 20);
        }
    }

    // 画圆的位置
    public void setPoint(Point p) {
        point = p;
    }
}

// 监听所有 check-box 可能感兴趣的事件，并分配给 check-box
class CBListener extends MouseInputAdapter {

    Toolkit toolkit;
    JCheckBox liveButton;
    JMenuBar menuBar;
    MyGlassPane glassPane;
    Container contentPane;

    public CBListener(JCheckBox liveButton, JMenuBar menuBar,
            MyGlassPane glassPane, Container contentPane) {
        toolkit = Toolkit.getDefaultToolkit();
        this.liveButton = liveButton;
        this.menuBar = menuBar;
        this.glassPane = glassPane;
        this.contentPane = contentPane;
    }

    public void mouseMoved(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseDragged(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseClicked(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseEntered(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseExited(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mousePressed(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }

    public void mouseReleased(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }

    //A basic implementation of redispatching events.
    private void redispatchMouseEvent(MouseEvent e, boolean repaint) {
        Point glassPanePoint = e.getPoint();
        Container container = contentPane;
        Point containerPoint = SwingUtilities.convertPoint(
                glassPane,
                glassPanePoint,
                contentPane);
        if (containerPoint.y < 0) { // 不在 content-pane 里面
            if (containerPoint.y + menuBar.getHeight() >= 0) {
                // 鼠标在菜单栏
            } else {
                // 鼠标在非系统窗口装饰，如 Java Laf提供的装饰
            }
        } else {
            // 鼠标可能在 content-pane 上面，找到它下面的组件
            Component component = SwingUtilities.getDeepestComponentAt(
                    container,
                    containerPoint.x,
                    containerPoint.y);

            if ((component != null) && (component.equals(liveButton))) {
                // 转给 check box
                Point componentPoint = SwingUtilities.convertPoint(
                        glassPane,
                        glassPanePoint,
                        component);
                component.dispatchEvent(new MouseEvent(component,
                        e.getID(),
                        e.getWhen(),
                        e.getModifiers(),
                        componentPoint.x,
                        componentPoint.y,
                        e.getClickCount(),
                        e.isPopupTrigger()));
            }
        }

        //Update the glass pane if requested.
        if (repaint) {
            glassPane.setPoint(glassPanePoint);
            glassPane.repaint();
        }
    }
}
