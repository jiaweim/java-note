/*
 * Created by JFormDesigner on Tue Nov 16 15:49:01 CST 2021
 */

package mjw.study.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author unknown
 */
public class JFormDesignerDemo extends JPanel
{
    public JFormDesignerDemo()
    {
        initComponents();
    }

    private void chooseMzML(ActionEvent e)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose mzML file");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("MzML file", "mzML"));

        int openDialog = fileChooser.showOpenDialog(this);
        if (openDialog == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            mzMLField.setText(file.getAbsolutePath());
        }
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        var label1 = new JLabel();
        panel1 = new JPanel();
        mzMLField = new JTextField();
        chooseMzMLButton = new JButton();
        var label2 = new JLabel();
        precursorToleranceBox = new JComboBox();
        var label3 = new JLabel();
        fragmentToleranceBox = new JComboBox();
        var label4 = new JLabel();
        minMethylBox = new JComboBox();
        maxMethylBox = new JComboBox();
        var label5 = new JLabel();
        minMetBox = new JComboBox();
        maxMetBox = new JComboBox();
        var label6 = new JLabel();
        comboBox1 = new JComboBox();
        comboBox2 = new JComboBox();
        var label7 = new JLabel();
        comboBox3 = new JComboBox();
        comboBox4 = new JComboBox();

        //======== this ========
         addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override
        public void propertyChange(java.beans.PropertyChangeEvent e){if("borde\u0072".equals(e.getPropertyName(
        )))throw new RuntimeException();}});
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 107, 100, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //---- label1 ----
        label1.setText("MzML file");
        add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE,
            new Insets(0, 0, 7, 7), 0, 0));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
            panel1.add(mzMLField);

            //---- chooseMzMLButton ----
            chooseMzMLButton.setText("...");
            chooseMzMLButton.addActionListener(e -> chooseMzML(e));
            panel1.add(chooseMzMLButton);
        }
        add(panel1, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
            GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 7, 0), 0, 0));

        //---- label2 ----
        label2.setText("Precursor tolerance");
        add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE,
            new Insets(0, 0, 7, 7), 0, 0));
        add(precursorToleranceBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 7, 7), 0, 0));

        //---- label3 ----
        label3.setText("Fragment tolerance");
        add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE,
            new Insets(0, 0, 7, 7), 0, 0));
        add(fragmentToleranceBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 7, 7), 0, 0));

        //---- label4 ----
        label4.setText("Methyl count");
        add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 7, 7), 0, 0));
        add(minMethylBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 7, 7), 0, 0));
        add(maxMethylBox, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 7, 0), 0, 0));

        //---- label5 ----
        label5.setText("Methionine count");
        add(label5, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 7, 7), 0, 0));
        add(minMetBox, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 7, 7), 0, 0));
        add(maxMetBox, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 7, 0), 0, 0));

        //---- label6 ----
        label6.setText("Retention time");
        add(label6, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 7, 7), 0, 0));
        add(comboBox1, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 7, 7), 0, 0));
        add(comboBox2, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 7, 0), 0, 0));

        //---- label7 ----
        label7.setText("Intensity");
        add(label7, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
            new Insets(0, 0, 0, 7), 0, 0));
        add(comboBox3, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 7), 0, 0));
        add(comboBox4, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel panel1;
    private JTextField mzMLField;
    private JButton chooseMzMLButton;
    private JComboBox precursorToleranceBox;
    private JComboBox fragmentToleranceBox;
    private JComboBox minMethylBox;
    private JComboBox maxMethylBox;
    private JComboBox minMetBox;
    private JComboBox maxMetBox;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JComboBox comboBox4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
