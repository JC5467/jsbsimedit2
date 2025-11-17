package uta.cse3310.tab.concreteTabs;

import javax.swing.JPanel;
import uta.cse3310.dataStore;
import uta.cse3310.tab.baseTab;

public class BuoyantForcesTab extends baseTab {

    public BuoyantForcesTab(dataStore DS, String label) {
        super(DS, label);
        panel = new JPanel();    // simple blank panel for now
    }

    public void loadData() {
        // TODO: read buoyant forces data from XML when schema is ready
    }

    public void saveData() {
        // TODO: write buoyant forces data to XML when schema is ready
    }
}
