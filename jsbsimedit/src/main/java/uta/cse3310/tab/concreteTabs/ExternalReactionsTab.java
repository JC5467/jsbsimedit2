package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import generated.ExternalReactions;
import generated.Force;
import generated.Location;
import generated.Frame;
import generated.LengthUnit;

//ExternalReactionsTab - Handles externally applied forces on the aircraft
 
public class ExternalReactionsTab extends simpleTab {

    private JTable table;
    private DefaultTableModel tableModel;

    public ExternalReactionsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        System.out.println("in ExternalReactions constructor");
        
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
    }


     
    public void loadData() {
        System.out.println("this is in loadData() for ExternalReactions");
        System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);

        // Remove all existing widgets
        panel.removeAll();

        // Get external_reactions section from DS.cfg
        ExternalReactions externalReactions = DS.cfg.getExternalReactions();
        
        if (externalReactions == null) {
            panel.add(new JLabel("No <external_reactions> section found in this aircraft file.", 
                                SwingConstants.CENTER), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }

        //  UI setup
        panel.setLayout(new BorderLayout());

        // Create table
        String[] columns = {"Force Name", "Frame", "Unit", "Loc X", "Loc Y", "Loc Z", 
                          "Dir X", "Dir Y", "Dir Z"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // All cells editable
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // center alignment + spacing

        JButton addButton = new JButton("Add Force");
        JButton deleteButton = new JButton("Delete Force");
        JButton saveButton = new JButton("Save Force"); // renamed for clarity

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        // add the centered panel to the bottom
        panel.add(buttonPanel, BorderLayout.SOUTH);


        // Button actions
        addButton.addActionListener(e -> {
            tableModel.addRow(new Object[]{"new_force", "BODY", "IN", "0", "0", "0", "0", "0", "0"});
        });

        deleteButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                tableModel.removeRow(selected);
            }
        });

        saveButton.addActionListener(e -> saveToDataStore());

        // Get the mixed list and filter for Force objects
        List<Object> mixedList = externalReactions.getDocumentationOrPropertyOrFunction();
        
        if (mixedList != null) {
            for (Object item : mixedList) {
                // Check if this item is a Force
                if (item instanceof Force) {
                    Force force = (Force) item;
                    
                    // Get name and frame
                    String name = force.getName();
                    String frameStr = force.getFrame() != null ? force.getFrame().value() : "BODY";
                    
                    // Get location
                    Location location = force.getLocation();
                    String unit = "";
                    String locX = "0", locY = "0", locZ = "0";
                    if (location != null) {
                        unit = location.getUnit() != null ? location.getUnit().value() : "IN";
                        locX = String.valueOf(location.getX());
                        locY = String.valueOf(location.getY());
                        locZ = String.valueOf(location.getZ());
                    }
                    
                    // Get direction
                    Location direction = force.getDirection();
                    String dirX = "0", dirY = "0", dirZ = "0";
                    if (direction != null) {
                        dirX = String.valueOf(direction.getX());
                        dirY = String.valueOf(direction.getY());
                        dirZ = String.valueOf(direction.getZ());
                    }

                    // Add to table 
                    tableModel.addRow(new Object[]{name, frameStr, unit, locX, locY, locZ, dirX, dirY, dirZ});
                    
                    // Identify special forces
                    if ("pushback".equals(name)) {
                        System.out.println("Loaded pushback force for ground operations");
                    } else if ("hook".equals(name)) {
                        System.out.println("Loaded hook force for arrested landings");
                    }
                }
            }
        }

        System.out.println("Loaded external reactions from dataStore");

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Save table data back to DS.cfg
     */
    private void saveToDataStore() {
        if (!DS.valid || DS.cfg == null) {
            JOptionPane.showMessageDialog(panel, "No valid data to save !");
            return;
        }

        try {
            ExternalReactions externalReactions = DS.cfg.getExternalReactions();
            
            if (externalReactions == null) {
                externalReactions = new ExternalReactions();
                DS.cfg.setExternalReactions(externalReactions);
            }

            // Get the mixed list
            List<Object> mixedList = externalReactions.getDocumentationOrPropertyOrFunction();
            
            // Remove all Force objects from the list
            mixedList.removeIf(item -> item instanceof Force);

            // Add new forces from table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Force force = new Force();
                
                force.setName(tableModel.getValueAt(i, 0).toString());
                
                // Convert string to Frame enum
                String frameStr = tableModel.getValueAt(i, 1).toString();
                force.setFrame(Frame.fromValue(frameStr));

                // Location
                Location location = new Location();
                location.setUnit(LengthUnit.fromValue(tableModel.getValueAt(i, 2).toString()));
                location.setX(Double.parseDouble(tableModel.getValueAt(i, 3).toString()));
                location.setY(Double.parseDouble(tableModel.getValueAt(i, 4).toString()));
                location.setZ(Double.parseDouble(tableModel.getValueAt(i, 5).toString()));
                force.setLocation(location);

                // Direction 
                Location direction = new Location();
                direction.setX(Double.parseDouble(tableModel.getValueAt(i, 6).toString()));
                direction.setY(Double.parseDouble(tableModel.getValueAt(i, 7).toString()));
                direction.setZ(Double.parseDouble(tableModel.getValueAt(i, 8).toString()));
                force.setDirection(direction);

                mixedList.add(force);
            }

            // Mark dirty 
            DS.setDirty();
            
            JOptionPane.showMessageDialog(panel, "Saved " + tableModel.getRowCount() + " forces to dataStore");
            System.out.println("Saved " + tableModel.getRowCount() + " forces to dataStore");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error saving: " + e.getMessage());
        }
    }
}