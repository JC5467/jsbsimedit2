package uta.cse3310.tab;

import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class simpleTab extends baseTab {
    // attributes common to simpleTabs
    public tabFrame TF; // this is the top level tab frame, the pointer is needed in order to accept
                        // messages

    public simpleTab(dataStore ds, String label) {

        super(ds, label);
        // i expect some code will accumulate here.
        // a 'peer' of this class would be "dragAndDropTab" which would be inherited by
        // a few concrete implementations.

    }

}
