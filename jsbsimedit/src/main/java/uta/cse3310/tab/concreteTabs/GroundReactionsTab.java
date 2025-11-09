package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class GroundReactionsTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab
    // Following UIID094 - UIID098

    public GroundReactionsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in GroundReactions constructor");

        TF = tf;
        // Plan: Implement an editable table tab using JTable
        // Data will be passed from JAXB parsing of the XML file
        // TODO: automate the data (by parsing the XML) instead of hardcoding an array
        // TODO: allow the user to edit the XML in the GUI

        String[] columnNames = { "Contact Name", "Contact Type", "Brake Group" };
        Object[][] data = {
                { "NOSE_LG", "BOGEY", "NOSE" },
                { "LEFT_MLG", "BOGEY", "LEFT" },
                { "RIGHT_MLG", "BOGEY", "RIGHT" }
        };
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(new JTable(data, columnNames));
        panel.add(scrollPane);

        // what is unique about this tab
        // panel.setLayout(new FlowLayout()); // Use FlowLayout for panel2
        // panel.add(new JLabel("Enter your name:"));
        // panel.add(new JTextField(15));
        // panel.setBackground(new Color(255, 220, 200)); // Light orange background

    }

}
