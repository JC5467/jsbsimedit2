package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import generated.*;

public class MassBalanceTab extends simpleTab {
    
    private DefaultListModel<Pointmass> listModel;
    private JList<Pointmass> list;
    private JScrollPane scrollPane;
    
    // Inertia fields
    private JTextField ixxField, iyyField, izzField;
    private JComboBox<InertiaUnit> ixxUnitField, iyyUnitField, izzUnitField;
    private JTextField ixyField, ixzField, iyzField;
    private JComboBox<InertiaUnit> ixyUnitField, ixzUnitField, iyzUnitField;
    
    // Empty weight fields
    private JTextField emptyWtField;
    private JComboBox<MassUnit> emptyWtUnitField;
    
    // CG location fields
    private JTextField cgXField, cgYField, cgZField;
    private JComboBox<LengthUnit> cgUnitField;
    
    public MassBalanceTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
    }
    
    private void loadUI() {
        panel.removeAll();
        
        // Main split: top panel for inertia/weight/CG, bottom for point masses
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(500);
        
        // Top panel: Inertia, Empty Weight, and CG
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Inertia section
        JPanel inertiaPanel = createInertiaPanel();
        topPanel.add(inertiaPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // Empty weight and CG section
        JPanel weightCGPanel = createWeightCGPanel();
        topPanel.add(weightCGPanel);
        
        JScrollPane topScrollPane = new JScrollPane(topPanel);
        splitPane.setTopComponent(topScrollPane);
        
        // Bottom panel: Point masses list
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Point Masses"));
        
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pointmass) {
                    Pointmass pm = (Pointmass) value;
                    String text = pm.getName() + " - " + pm.getWeight().getValue() + " " + pm.getWeight().getUnit();
                    setText(text);
                }
                return this;
            }
        });
        
        // Double click to edit
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Pointmass pm = listModel.getElementAt(index);
                        editPointmass(pm);
                    }
                }
            }
        });
        
        scrollPane = new JScrollPane(list);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons for point masses
        JPanel pmButtonPanel = new JPanel();
        JButton addPMButton = new JButton("Add");
        JButton deletePMButton = new JButton("Delete");
        JButton editPMButton = new JButton("Edit");
        
        addPMButton.addActionListener(e -> addPointmass());
        deletePMButton.addActionListener(e -> deletePointmass());
        editPMButton.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index != -1) {
                editPointmass(listModel.getElementAt(index));
            }
        });
        
        pmButtonPanel.add(addPMButton);
        pmButtonPanel.add(deletePMButton);
        pmButtonPanel.add(editPMButton);
        bottomPanel.add(pmButtonPanel, BorderLayout.SOUTH);
        
        splitPane.setBottomComponent(bottomPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Save button at the bottom
        JPanel savePanel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        savePanel.add(saveButton);
        panel.add(savePanel, BorderLayout.SOUTH);
        
        panel.revalidate();
        panel.repaint();
    }
    
    private JPanel createInertiaPanel() {
        JPanel inertiaPanel = new JPanel();
        inertiaPanel.setLayout(new GridBagLayout());
        inertiaPanel.setBorder(BorderFactory.createTitledBorder("Moments of Inertia"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Column headers
        gbc.gridx = 0; gbc.gridy = 0;
        inertiaPanel.add(new JLabel("Parameter"), gbc);
        gbc.gridx = 1;
        inertiaPanel.add(new JLabel("Value"), gbc);
        gbc.gridx = 2;
        inertiaPanel.add(new JLabel("Unit"), gbc);
        
        // Ixx
        gbc.gridx = 0; gbc.gridy = 1;
        inertiaPanel.add(new JLabel("Ixx:"), gbc);
        gbc.gridx = 1;
        ixxField = new JTextField(10);
        inertiaPanel.add(ixxField, gbc);
        gbc.gridx = 2;
        ixxUnitField = new JComboBox<>(InertiaUnit.values());
        inertiaPanel.add(ixxUnitField, gbc);
        
        // Iyy
        gbc.gridx = 0; gbc.gridy = 2;
        inertiaPanel.add(new JLabel("Iyy:"), gbc);
        gbc.gridx = 1;
        iyyField = new JTextField(10);
        inertiaPanel.add(iyyField, gbc);
        gbc.gridx = 2;
        iyyUnitField = new JComboBox<>(InertiaUnit.values());
        inertiaPanel.add(iyyUnitField, gbc);
        
        // Izz
        gbc.gridx = 0; gbc.gridy = 3;
        inertiaPanel.add(new JLabel("Izz:"), gbc);
        gbc.gridx = 1;
        izzField = new JTextField(10);
        inertiaPanel.add(izzField, gbc);
        gbc.gridx = 2;
        izzUnitField = new JComboBox<>(InertiaUnit.values());
        inertiaPanel.add(izzUnitField, gbc);
        
        // Ixy
        gbc.gridx = 0; gbc.gridy = 4;
        inertiaPanel.add(new JLabel("Ixy:"), gbc);
        gbc.gridx = 1;
        ixyField = new JTextField(10);
        inertiaPanel.add(ixyField, gbc);
        gbc.gridx = 2;
        ixyUnitField = new JComboBox<>(InertiaUnit.values());
        inertiaPanel.add(ixyUnitField, gbc);
        
        // Ixz
        gbc.gridx = 0; gbc.gridy = 5;
        inertiaPanel.add(new JLabel("Ixz:"), gbc);
        gbc.gridx = 1;
        ixzField = new JTextField(10);
        inertiaPanel.add(ixzField, gbc);
        gbc.gridx = 2;
        ixzUnitField = new JComboBox<>(InertiaUnit.values());
        inertiaPanel.add(ixzUnitField, gbc);
        
        // Iyz
        gbc.gridx = 0; gbc.gridy = 6;
        inertiaPanel.add(new JLabel("Iyz:"), gbc);
        gbc.gridx = 1;
        iyzField = new JTextField(10);
        inertiaPanel.add(iyzField, gbc);
        gbc.gridx = 2;
        iyzUnitField = new JComboBox<>(InertiaUnit.values());
        inertiaPanel.add(iyzUnitField, gbc);
        
        return inertiaPanel;
    }
    
    private JPanel createWeightCGPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Empty weight panel
        JPanel wtPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wtPanel.setBorder(BorderFactory.createTitledBorder("Empty Weight"));
        wtPanel.add(new JLabel("Weight:"));
        emptyWtField = new JTextField(10);
        wtPanel.add(emptyWtField);
        emptyWtUnitField = new JComboBox<>(MassUnit.values());
        wtPanel.add(emptyWtUnitField);
        panel.add(wtPanel);
        
        // CG location panel
        JPanel cgPanel = new JPanel();
        cgPanel.setLayout(new GridBagLayout());
        cgPanel.setBorder(BorderFactory.createTitledBorder("Center of Gravity Location"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        cgPanel.add(new JLabel("X:"), gbc);
        gbc.gridx = 1;
        cgXField = new JTextField(10);
        cgPanel.add(cgXField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        cgPanel.add(new JLabel("Y:"), gbc);
        gbc.gridx = 1;
        cgYField = new JTextField(10);
        cgPanel.add(cgYField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        cgPanel.add(new JLabel("Z:"), gbc);
        gbc.gridx = 1;
        cgZField = new JTextField(10);
        cgPanel.add(cgZField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        cgPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        cgUnitField = new JComboBox<>(LengthUnit.values());
        cgPanel.add(cgUnitField, gbc);
        
        panel.add(cgPanel);
        
        return panel;
    }
    
    private void addPointmass() {
        Pointmass pm = new Pointmass();
        pm.setName("New Point Mass");
        
        Mass weight = new Mass();
        weight.setValue(100.0);
        weight.setUnit(MassUnit.LBS);
        pm.setWeight(weight);
        
        Location loc = new Location();
        loc.setX(0.0);
        loc.setY(0.0);
        loc.setZ(0.0);
        loc.setUnit(LengthUnit.IN);
        pm.setLocation(loc);
        
        if (editPointmass(pm)) {
            DS.cfg.getMassBalance().getPointmass().add(pm);
            listModel.addElement(pm);
        }
    }
    
    private void deletePointmass() {
        int index = list.getSelectedIndex();
        if (index != -1) {
            int result = JOptionPane.showConfirmDialog(panel,
                "Delete selected point mass?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                Pointmass pm = listModel.getElementAt(index);
                DS.cfg.getMassBalance().getPointmass().remove(pm);
                listModel.remove(index);
            }
        }
    }
    
    private boolean editPointmass(Pointmass pm) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Point Mass");
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(panel);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        JTextField nameField = new JTextField(pm.getName(), 15);
        formPanel.add(nameField, gbc);
        
        // Weight
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Weight:"), gbc);
        gbc.gridx = 1;
        JTextField weightField = new JTextField(String.valueOf(pm.getWeight().getValue()), 10);
        formPanel.add(weightField, gbc);
        gbc.gridx = 2;
        JComboBox<MassUnit> weightUnitField = new JComboBox<>(MassUnit.values());
        weightUnitField.setSelectedItem(pm.getWeight().getUnit());
        formPanel.add(weightUnitField, gbc);
        
        // Location X
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Location X:"), gbc);
        gbc.gridx = 1;
        JTextField xField = new JTextField(String.valueOf(pm.getLocation().getX()), 10);
        formPanel.add(xField, gbc);
        
        // Location Y
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Location Y:"), gbc);
        gbc.gridx = 1;
        JTextField yField = new JTextField(String.valueOf(pm.getLocation().getY()), 10);
        formPanel.add(yField, gbc);
        
        // Location Z
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Location Z:"), gbc);
        gbc.gridx = 1;
        JTextField zField = new JTextField(String.valueOf(pm.getLocation().getZ()), 10);
        formPanel.add(zField, gbc);
        
        // Location Unit
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        JComboBox<LengthUnit> locUnitField = new JComboBox<>(LengthUnit.values());
        locUnitField.setSelectedItem(pm.getLocation().getUnit());
        formPanel.add(locUnitField, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        final boolean[] result = {false};
        
        okButton.addActionListener(e -> {
            try {
                pm.setName(nameField.getText());
                pm.getWeight().setValue(Double.parseDouble(weightField.getText()));
                pm.getWeight().setUnit((MassUnit) weightUnitField.getSelectedItem());
                pm.getLocation().setX(Double.parseDouble(xField.getText()));
                pm.getLocation().setY(Double.parseDouble(yField.getText()));
                pm.getLocation().setZ(Double.parseDouble(zField.getText()));
                pm.getLocation().setUnit((LengthUnit) locUnitField.getSelectedItem());
                result[0] = true;
                dialog.dispose();
                list.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
        return result[0];
    }
    
    private void saveChanges() {
        try {
            MassBalance mb = DS.cfg.getMassBalance();
            
            // Save inertia values
            if (!ixxField.getText().isEmpty()) {
                if (mb.getIxx() == null) mb.setIxx(new Inertia());
                mb.getIxx().setValue(Double.parseDouble(ixxField.getText()));
                mb.getIxx().setUnit((InertiaUnit) ixxUnitField.getSelectedItem());
            }
            
            if (!iyyField.getText().isEmpty()) {
                if (mb.getIyy() == null) mb.setIyy(new Inertia());
                mb.getIyy().setValue(Double.parseDouble(iyyField.getText()));
                mb.getIyy().setUnit((InertiaUnit) iyyUnitField.getSelectedItem());
            }
            
            if (!izzField.getText().isEmpty()) {
                if (mb.getIzz() == null) mb.setIzz(new Inertia());
                mb.getIzz().setValue(Double.parseDouble(izzField.getText()));
                mb.getIzz().setUnit((InertiaUnit) izzUnitField.getSelectedItem());
            }
            
            if (!ixyField.getText().isEmpty()) {
                if (mb.getIxy() == null) mb.setIxy(new CrossProductInertia());
                mb.getIxy().setValue(Double.parseDouble(ixyField.getText()));
                mb.getIxy().setUnit((InertiaUnit) ixyUnitField.getSelectedItem());
            }
            
            if (!ixzField.getText().isEmpty()) {
                if (mb.getIxz() == null) mb.setIxz(new CrossProductInertia());
                mb.getIxz().setValue(Double.parseDouble(ixzField.getText()));
                mb.getIxz().setUnit((InertiaUnit) ixzUnitField.getSelectedItem());
            }
            
            if (!iyzField.getText().isEmpty()) {
                if (mb.getIyz() == null) mb.setIyz(new CrossProductInertia());
                mb.getIyz().setValue(Double.parseDouble(iyzField.getText()));
                mb.getIyz().setUnit((InertiaUnit) iyzUnitField.getSelectedItem());
            }
            
            // Save empty weight
            if (!emptyWtField.getText().isEmpty()) {
                if (mb.getEmptywt() == null) mb.setEmptywt(new Mass());
                mb.getEmptywt().setValue(Double.parseDouble(emptyWtField.getText()));
                mb.getEmptywt().setUnit((MassUnit) emptyWtUnitField.getSelectedItem());
            }
            
            // Save CG location
            if (!cgXField.getText().isEmpty() || !cgYField.getText().isEmpty() || !cgZField.getText().isEmpty()) {
                if (mb.getLocation() == null) mb.setLocation(new MassBalance.Location());
                if (!cgXField.getText().isEmpty()) 
                    mb.getLocation().setX(Double.parseDouble(cgXField.getText()));
                if (!cgYField.getText().isEmpty()) 
                    mb.getLocation().setY(Double.parseDouble(cgYField.getText()));
                if (!cgZField.getText().isEmpty()) 
                    mb.getLocation().setZ(Double.parseDouble(cgZField.getText()));
                mb.getLocation().setUnit((LengthUnit) cgUnitField.getSelectedItem());
            }
            
            JOptionPane.showMessageDialog(panel, "Changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid number format: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void loadData() {
        if (DS == null || DS.cfg == null || DS.cfg.getMassBalance() == null) {
            panel.removeAll();
            panel.setLayout(new BorderLayout());
            panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }
        
        loadUI();
        
        MassBalance mb = DS.cfg.getMassBalance();
        
        // Load inertia values
        if (mb.getIxx() != null) {
            ixxField.setText(String.valueOf(mb.getIxx().getValue()));
            ixxUnitField.setSelectedItem(mb.getIxx().getUnit());
        }
        if (mb.getIyy() != null) {
            iyyField.setText(String.valueOf(mb.getIyy().getValue()));
            iyyUnitField.setSelectedItem(mb.getIyy().getUnit());
        }
        if (mb.getIzz() != null) {
            izzField.setText(String.valueOf(mb.getIzz().getValue()));
            izzUnitField.setSelectedItem(mb.getIzz().getUnit());
        }
        if (mb.getIxy() != null) {
            ixyField.setText(String.valueOf(mb.getIxy().getValue()));
            ixyUnitField.setSelectedItem(mb.getIxy().getUnit());
        }
        if (mb.getIxz() != null) {
            ixzField.setText(String.valueOf(mb.getIxz().getValue()));
            ixzUnitField.setSelectedItem(mb.getIxz().getUnit());
        }
        if (mb.getIyz() != null) {
            iyzField.setText(String.valueOf(mb.getIyz().getValue()));
            iyzUnitField.setSelectedItem(mb.getIyz().getUnit());
        }
        
        // Load empty weight
        if (mb.getEmptywt() != null) {
            emptyWtField.setText(String.valueOf(mb.getEmptywt().getValue()));
            emptyWtUnitField.setSelectedItem(mb.getEmptywt().getUnit());
        }
        
        // Load CG location
        if (mb.getLocation() != null) {
            cgXField.setText(String.valueOf(mb.getLocation().getX()));
            cgYField.setText(String.valueOf(mb.getLocation().getY()));
            cgZField.setText(String.valueOf(mb.getLocation().getZ()));
            cgUnitField.setSelectedItem(mb.getLocation().getUnit());
        }
        
        // Load point masses
        listModel.clear();
        for (Pointmass pm : mb.getPointmass()) {
            listModel.addElement(pm);
        }
        
        panel.revalidate();
        panel.repaint();
    }
}
