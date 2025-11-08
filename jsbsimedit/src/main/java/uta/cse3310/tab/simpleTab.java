package uta.cse3310.tab;

import uta.cse3310.dataStore;

public class simpleTab extends baseTab {
    // attributes common to simpleTabs

    public simpleTab(dataStore ds, String label) {

        super(ds, label);
        System.out.println("in simpleTab constructor");
        // i expect some code will accumulate here.
        // a 'peer' of this class would be "dragAndDropTab" which would be inherited by
        // a few concrete implementations.

    }

}
