package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

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

    @Override
    public void loadData() {

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

        var axisList = aero.getAxis();
        if (axisList == null || axisList.isEmpty()) {
            panel.add(new JLabel("No <axis> entries found.", SwingConstants.CENTER));
            panel.revalidate();
            panel.repaint();
            return;
        }

        for (Axis a : axisList) {
            JPanel axisPanel = createAxisPanel(a);
            axisTabs.addTab(a.getName(), axisPanel);
        }

        panel.add(axisTabs, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createAxisPanel(Axis axis) {

        JPanel container = new JPanel(new BorderLayout());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(content);

        JLabel title = new JLabel(axis.getName() + " Axis");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(10));

        for (Object obj : axis.getDocumentationOrFunction()) {

            if (obj instanceof String s) {
                JLabel doc = new JLabel("Note: " + s);
                doc.setFont(new Font("Arial", Font.ITALIC, 11));
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

    private JPanel createFunctionPanel(Axis.Function f) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(
                f.getName() == null ? "Function" : f.getName()));

        if (f.getDescription() != null) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Description:"));

            JTextField text = new JTextField(f.getDescription(), 28);
            text.getDocument().addDocumentListener(new SimpleDocListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    f.setDescription(text.getText());
                    DS.setDirty();
                }
            });

            row.add(text);
            p.add(row);
        }

        if (f.getValue() != null) {

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel("Value:"));

            JTextField vField = new JTextField(f.getValue().toString(), 8);

            vField.addActionListener(e -> updateValue(f, vField));
            vField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    updateValue(f, vField);
                }
            });

            row.add(vField);
            p.add(row);
        }

        if (f.isApplyAtCg() != null) {
            JCheckBox cg = new JCheckBox("Apply at CG", f.isApplyAtCg());
            cg.addActionListener(e -> {
                f.setApplyAtCg(cg.isSelected());
                DS.setDirty();
            });
            p.add(cg);
        }

        return p;
    }

    private void updateValue(Function f, JTextField txt) {
        try {
            double v = Double.parseDouble(txt.getText());
            f.setValue(v);
            DS.setDirty();
        } catch (Exception ex) {
            txt.setText(f.getValue().toString());
        }
    }

    private abstract static class SimpleDocListener implements DocumentListener {
        @Override public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
        @Override public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
        public abstract void changedUpdate(DocumentEvent e);
    }
}
