package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

import generated.Contact;
import generated.GroundReactions;
import generated.Location;
import uta.cse3310.tab.widgets.textFieldWLabel;

public class GroundReactionsTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab
    // Following UIID094 - UIID098
    
        public JTable table;
        public DefaultTableModel tableModel;

    public GroundReactionsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in GroundReactions constructor");

        TF = tf;
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
        // TODO: allow the user to edit the XML in the GUI
        // TODO: implement DETAIL WINDOW
        // TODO: DO NOT ALLOW USER TO EDIT TABLE FROM TAB, ONLY DETAIL WINDOW

    }

    //data fields to be included:
    //C.name, C.type, Location.coords, Stat-fcoef, dyam-fcoef, rolling, spring, damping, steer, brake group, retractable

    public void loadData() {

        panel.removeAll();
        //Initialize table when data is loaded
        String[] columnNames = {"Contact Name", "Location (X,Y,Z)", "UNIT", "Brake Group"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        //Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton detailButton = new JButton("Detail");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> {
        tableModel.addRow(new Object[]{"new_contact", "", "", ""});
        });

        deleteButton.addActionListener(e -> {
        int selected = table.getSelectedRow();
        if (selected != -1){tableModel.removeRow(selected);}
        });

        for (Object obj : DS.cfg.getGroundReactions().getContent()) {
            if (obj instanceof Contact) {
                Contact contact = (Contact) obj;
                String name = contact.getName();

                Location location = contact.getLocation();
                String unit = "IN";
                String locX = "0", locY = "0", locZ = "0";
                String locCombined = "0";
                if (location != null) {
                    unit = location.getUnit() != null ? location.getUnit().value() : "IN";
                    locX = String.valueOf(location.getX());
                    locY = String.valueOf(location.getY());
                    locZ = String.valueOf(location.getZ());
                    locCombined = String.format("%s, %s, %s", locX, locY, locZ);

                }

                String bGroup = contact.getBrakeGroup() != null ? contact.getBrakeGroup() : "NONE";

                tableModel.addRow(new Object[]{name, locCombined, unit, bGroup});
            }
        }

    }
    




}
