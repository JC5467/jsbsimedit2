package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import generated.Aerodynamics;
import generated.Axis;
import generated.Function;

import uta.cse3310.dataStore;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.tabFrame;


public class AerodynamicsTab extends simpleTab {

    private JTabbedPane axisTabs;

    public AerodynamicsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    /**
     * Called when new data is loaded 
     */
    @Override
    public void loadData() {

        System.out.println("in loadData() for Aerodynamics");
        System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);

        panel.removeAll();

        Aerodynamics aero = DS.cfg.getAerodynamics();
        if (aero == null) {
            panel.add(new JLabel("No <aerodynamics> section found.", SwingConstants.CENTER));
            panel.revalidate();
            panel.repaint();
            return;
        }

        axisTabs = new JTabbedPane();
        axisTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        List<Axis> axisList = aero.getAxis();
        if (axisList == null || axisList.isEmpty()) {
            panel.add(new JLabel("No <axis> entries found.", SwingConstants.CENTER));
            panel.revalidate();
            panel.repaint();
            return;
        }

        // a tab for each axis (DRAG, LIFT, SIDE, ROLL, etc.)
        for (Axis a : axisList) {
            JPanel axisPanel = createAxisPanel(a);
            axisTabs.addTab(a.getName(), axisPanel);
        }

        panel.add(axisTabs, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Build UI for one AXIS block.
     */
    private JPanel createAxisPanel(Axis axis) {

        JPanel container = new JPanel(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Axis name label
        JLabel title = new JLabel(axis.getName() + " Axis", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(8));

        // Go through list: contains documentation strings and Axis.Function objects
        for (Object obj : axis.getDocumentationOrFunction()) {

            if (obj instanceof String) {
                // documentation entry
                JLabel doc = new JLabel("Note: " + obj);
                doc.setFont(new Font("Arial", Font.ITALIC, 11));
                doc.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(doc);
                content.add(Box.createVerticalStrut(5));
            }

            else if (obj instanceof Axis.Function f) {
                content.add(createFunctionPanel(f));
                content.add(Box.createVerticalStrut(12));
            }
        }

        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    /**
     * Build UI for a single Function block inside an axis.
     */
    private JPanel createFunctionPanel(Axis.Function f) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(f.getName() == null ? "Function" : f.getName()));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description if present
        if (f.getDescription() != null) {
            JLabel desc = new JLabel("Description: " + f.getDescription());
            desc.setFont(new Font("Arial", Font.ITALIC, 11));
            p.add(desc);
        }

        
        if (f.getValue() != null) {

            JPanel valueRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            valueRow.add(new JLabel("Value:"));

            JTextField valueField = new JTextField(f.getValue().toString(), 8);

            valueField.addActionListener(e -> {
                try {
                    double newVal = Double.parseDouble(valueField.getText());
                    f.setValue(newVal);
                    DS.setDirty();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid number.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    valueField.setText(f.getValue().toString());
                }
            });

            valueRow.add(valueField);
            p.add(valueRow);
        }

        // Table present?
        if (f.getTable() != null) {
            JLabel tableLabel = new JLabel("Contains table data (editing not implemented)");
            tableLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            p.add(tableLabel);
        }

        // Apply_at_cg boolean
        if (f.isApplyAtCg() != null) {
            JCheckBox cgCheck = new JCheckBox("Apply at CG", f.isApplyAtCg());
            cgCheck.addActionListener(e -> {
                f.setApplyAtCg(cgCheck.isSelected());
                DS.setDirty();
            });
            p.add(cgCheck);
        }

        return p;
    }
}
