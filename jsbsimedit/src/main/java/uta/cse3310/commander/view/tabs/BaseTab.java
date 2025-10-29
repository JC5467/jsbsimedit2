package uta.cse3310.commander.view.tabs;

public abstract class BaseTab {
    // Base class for all tabs
    protected String tabName;

    public abstract void initializeTab();
    public abstract void refreshData();
}
