package uta.cse3310.tab;

import uta.cse3310.dataStore;

import javax.swing.*;
import java.awt.*;

/*
The object that will be used to hold the XML information is in App.java line 43
- cfg is the name of said object




 */

public class baseTab {
    // attributes common to all tabs
    public JPanel panel;
    public String label;

    public dataStore DS; // the data store that contains the information from the xml file
    public Integer version;

    public void loadData() {
        System.out.println("i am in basetab loadData");
    }

    public baseTab(dataStore ds, String LABEL) {

        System.out.println("in base Tab constructor");
        // this is common to all tabs
        DS = ds; // save off a pointer to the data store
        version = DS.version;

        label = LABEL;
        panel = new JPanel();
        panel.setLayout(new BorderLayout()); // Use BorderLayout for panel1

        panel.setBackground(new Color(200, 220, 255)); // Light blue background
    }
}
