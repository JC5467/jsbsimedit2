package uta.cse3310.tab.concreteTabs;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class CanopySystemTab extends simpleTab {
    public CanopySystemTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        System.out.println("in CanopySystem constructor");
    }
}
