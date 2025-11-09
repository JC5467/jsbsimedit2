package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class MetricsTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab

    public MetricsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in Metrics constructor");

        // this is what is unique about 1 tab
        TF = tf;
        panel.add(new JLabel("Content of Tab 1", SwingConstants.CENTER), BorderLayout.CENTER);

    }

}
