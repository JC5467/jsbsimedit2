package uta.cse3310.commander.view.tabs;
import javax.swing.*
public class MetricsTab extends BaseTab {
    @Override
    public void initializeTab() {
// TODO: Implement metrics tab initialization
    String wingarea;
    String wingspan;
    String chord;
    String htailarea;
    String htailarm;
    String vtailarea;
    String vtailarm;   

        JLabel wingareaLabel = new JLabel("Wingarea: " + wingspan);
        JLabel wingspanLabel = new JLabel("Wingspan: " + wingarea);
        JLabel chordLabel = new JLabel("Chord: " + chord);
        JLabel htailareaLabel = new JLabel("HTailArea: + htailarea);
        JLabel htailarmLabel = new JLabel("HTailArm: + htailarm);
        JLabel vtailareaLabel = new JLabel("VTailArea: + vtailarea);
        JLabel vtailarmLabel = new JLabel("HTailArea: + vtailarm);
    }
    
    @Override
    public void refreshData() {
        // TODO: Implement metrics data refresh
    }
}
