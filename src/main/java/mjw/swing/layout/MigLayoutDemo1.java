package mjw.swing.layout;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MigLayoutDemo1 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the panel.
	 */
	public MigLayoutDemo1() {
		setLayout(new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]"));
		
		JLabel lblNewLabel = new JLabel("Enter Size:");
		add(lblNewLabel, "");
		
		textField = new JTextField();
		add(textField, "wrap");
		
		JLabel lblNewLabel_1 = new JLabel("Enter weight:");
		add(lblNewLabel_1, "");
		
		textField_1 = new JTextField();
		add(textField_1, "");
	}

}
