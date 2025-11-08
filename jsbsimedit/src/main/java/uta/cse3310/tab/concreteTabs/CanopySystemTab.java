package uta.cse3310.tab.concreteTabs;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;

public class CanopySystemTab extends simpleTab {
    public CanopySystemTab(dataStore ds, String label) {
        super(ds, label);
        System.out.println("in CanopySystem constructor");
    }
}
