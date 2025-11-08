package uta.cse3310.tab.concreteTabs;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;

public class HookSystemTab extends simpleTab {
    public HookSystemTab(dataStore ds, String label) {
        super(ds, label);
        System.out.println("In HookSystem constructor");
    }
}
