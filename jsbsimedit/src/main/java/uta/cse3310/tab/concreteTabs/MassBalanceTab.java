package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

import generated.MassBalance;
import generated.PointMass;


public class MassBalanceTab extends simpleTab {

    // UI fields
    private JTextField tfAircraftMass;
    private JTextField tfMassUnits;

    private JTextField tfComX;
    private JTextField tfComY;
    private JTextField tfComZ;
    private JTextField tfComUnits;

    private JTable pointMassTable;
    private DefaultTableModel pointMassModel;

    public MassBalanceTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in MassBalance constructor");

        panel.setLayout(new BorderLayout());

        JPanel topPanel = buildTopPanel();
        JPanel tablePanel = buildTablePanel();

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        // initial load from DS.cfg
        reloadFromModel();
    }

    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfAircraftMass = new JTextField(10);
        tfMassUnits = new JTextField(6);

        tfComX = new JTextField(8);
        tfComY = new JTextField(8);
        tfComZ = new JTextField(8);
        tfComUnits = new JTextField(6);

        int row = 0;
        addRow(top, gbc, row++, "Aircraft mass:", tfAircraftMass, "Units:", tfMassUnits);
        addRow(top, gbc, row++, "Center of mass X:", tfComX, "Units:", tfComUnits);
        addRow(top, gbc, row++, "Center of mass Y:", tfComY, null, null);
        addRow(top, gbc, row++, "Center of mass Z:", tfComZ, null, null);

        // lamba style and dirtying
        addDocListener(tfAircraftMass, () -> {
            MassBalance mb = DS.cfg.getMassBalance();
            try {
                double v = Double.parseDouble(tfAircraftMass.getText().trim());
                mb.getAircraftMass().setValue(v);
                DS.setDirty();
            } catch (NumberFormatException ignored) {}
        });

        addDocListener(tfMassUnits, () -> {
            MassBalance mb = DS.cfg.getMassBalance();
            mb.getAircraftMass().setUnits(tfMassUnits.getText().trim());
            DS.setDirty();
        });

        addDocListener(tfComX, () -> {
            MassBalance mb = DS.cfg.getMassBalance();
            try {
                double v = Double.parseDouble(tfComX.getText().trim());
                mb.getCenterOfMass().setX(v);
                DS.setDirty();
            } catch (NumberFormatException ignored) {}
        });

        addDocListener(tfComY, () -> {
            MassBalance mb = DS.cfg.getMassBalance();
            try {
                double v = Double.parseDouble(tfComY.getText().trim());
                mb.getCenterOfMass().setY(v);
                DS.setDirty();
            } catch (NumberFormatException ignored) {}
        });

        addDocListener(tfComZ, () -> {
            MassBalance mb = DS.cfg.getMassBalance();
            try {
                double v = Double.parseDouble(tfComZ.getText().trim());
                mb.getCenterOfMass().setZ(v);
                DS.setDirty();
            } catch (NumberFormatException ignored) {}
        });

        addDocListener(tfComUnits, () -> {
            MassBalance mb = DS.cfg.getMassBalance();
            mb.getCenterOfMass().setUnits(tfComUnits.getText().trim());
            DS.setDirty();
        });

        return top;
    }

    private JPanel buildTablePanel() {
        JPanel panelMid = new JPanel(new BorderLayout());
        String[] cols = {"name", "mass", "units", "x", "y", "z", "units"};
        pointMassModel = new DefaultTableModel(cols, 0);
        pointMassTable = new JTable(pointMassModel);
        panelMid.add(new JScrollPane(pointMassTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add point mass");
        JButton btnDelete = new JButton("Delete selected");
        JButton btnReload = new JButton("Reload from XML");
        btns.add(btnAdd);
        btns.add(btnDelete);
        btns.add(btnReload);
        panelMid.add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(this::onAddPointMass);
        btnDelete.addActionListener(this::onDeletePointMass);
        btnReload.addActionListener(e -> reloadFromModel());

        return panelMid;
    }

    private void onAddPointMass(ActionEvent evt) {
        // add new blank row
        pointMassModel.addRow(new Object[]{"", "", "", "", "", "", ""});

        MassBalance mb = DS.cfg.getMassBalance();
        PointMass pm = new PointMass();
        pm.setName("");
        pm.getMass().setValue(0.0);
        pm.getMass().setUnits("");
        pm.getLocation().setX(0.0);
        pm.getLocation().setY(0.0);
        pm.getLocation().setZ(0.0);
        pm.getLocation().setUnits("");

        mb.getPointMass().add(pm);
        DS.setDirty();
    }

    private void onDeletePointMass(ActionEvent evt) {
        int row = pointMassTable.getSelectedRow();
        if (row >= 0) {
            pointMassModel.removeRow(row);
            MassBalance mb = DS.cfg.getMassBalance();
            if (row < mb.getPointMass().size()) {
                mb.getPointMass().remove(row);
                DS.setDirty();
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Select a point mass row to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void reloadFromModel() {
        MassBalance mb = DS.cfg.getMassBalance();

        tfAircraftMass.setText(String.valueOf(mb.getAircraftMass().getValue()));
        tfMassUnits.setText(String.valueOf(mb.getAircraftMass().getUnits()));

        tfComX.setText(String.valueOf(mb.getCenterOfMass().getX()));
        tfComY.setText(String.valueOf(mb.getCenterOfMass().getY()));
        tfComZ.setText(String.valueOf(mb.getCenterOfMass().getZ()));
        tfComUnits.setText(String.valueOf(mb.getCenterOfMass().getUnits()));

        pointMassModel.setRowCount(0);
        for (PointMass pm : mb.getPointMass()) {
            pointMassModel.addRow(new Object[]{
                    pm.getName(),
                    pm.getMass().getValue(),
                    pm.getMass().getUnits(),
                    pm.getLocation().getX(),
                    pm.getLocation().getY(),
                    pm.getLocation().getZ(),
                    pm.getLocation().getUnits()
            });
        }
    }

    private static void addDocListener(JTextField tf, Runnable onChange) {
        tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onChange.run(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange.run(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange.run(); }
        });
    }

    /** help construct of grid */
    private static void addRow(JPanel parent, GridBagConstraints gbc, int row,
                               String l1, JComponent c1, String l2, JComponent c2) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        parent.add(new JLabel(l1), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        parent.add(c1, gbc);
        if (l2 != null && c2 != null) {
            gbc.gridx = 2; gbc.weightx = 0;
            parent.add(new JLabel(l2), gbc);
            gbc.gridx = 3; gbc.weightx = 1;
            parent.add(c2, gbc);
        }
    }
}
