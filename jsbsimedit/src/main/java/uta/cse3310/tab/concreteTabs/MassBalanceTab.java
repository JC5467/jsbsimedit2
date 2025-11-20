package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class MassBalanceTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab

    public MassBalanceTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);

        // what is unique about this tab....
        TF = tf;
        panel.setLayout(new GridLayout(2, 1)); // Use GridLayout for panel3
        panel.add(new JButton("Click Me!"));
        panel.add(new JCheckBox("Enable Feature"));
        panel.setBackground(new Color(220, 255, 200)); // Light green background
    }

}
