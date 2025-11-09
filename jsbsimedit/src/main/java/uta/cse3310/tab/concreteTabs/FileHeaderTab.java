package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class FileHeaderTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab

    public FileHeaderTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in FileHeader constructor");

        // this is what is unique about 1 tab
        TF = tf;
        panel.add(new JLabel("Content of Tab 1", SwingConstants.CENTER), BorderLayout.CENTER);
        System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);
    }

    public void loadData() {
        System.out.println("this is in loadData() for fileHeader");
        System.out.println("data structure is " + DS.valid + " and the version is " + DS.version);

        // here is the data to put into widgets.....
        System.out.println(DS.cfg.getFileheader().getCopyright());
        System.out.println(DS.cfg.getFileheader().getVersion());
        // the can be lists and individual items
    }
}
