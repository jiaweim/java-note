package mjw.study.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class SnapFrame extends JFrame
{

    private JPanel contentPane;
    private JButton btnNewButton;
    private JLabel lblNewLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try {
                    SnapFrame frame = new SnapFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public SnapFrame()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new FormLayout(new ColumnSpec[] {
        		ColumnSpec.decode("99px"),
        		ColumnSpec.decode("96px"),
        		FormSpecs.UNRELATED_GAP_COLSPEC,
        		ColumnSpec.decode("54px"),},
        	new RowSpec[] {
        		RowSpec.decode("53px"),
        		RowSpec.decode("26px"),
        		RowSpec.decode("35px"),
        		RowSpec.decode("16px"),}));
        
        btnNewButton = new JButton("New button");
        contentPane.add(btnNewButton, "2, 1, left, top");
        
        lblNewLabel = new JLabel("New label");
        contentPane.add(lblNewLabel, "4, 4, left, top");
    }
}
