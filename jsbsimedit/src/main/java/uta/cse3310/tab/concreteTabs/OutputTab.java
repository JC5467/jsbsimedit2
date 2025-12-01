
package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class OutputTab extends simpleTab {

    // for the property lists
    private DefaultListModel<String> availMod;
    private DefaultListModel<String> selMod;
    private JList<String> availL;
    private JList<String> selL;

    public OutputTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;

        // build the ui
        setupUI();
    }

    @Override
    public void loadData() {
        // just print when data loads
        System.out.println("in loadData() for Output");
    }

    private void setupUI() {
        // clear and setup main panel
        panel.removeAll();
        panel.setLayout(new BorderLayout());

        // main container
        JPanel mainP = new JPanel();
        mainP.setLayout(new BoxLayout(mainP, BoxLayout.Y_AXIS));

        // add all sections
        mainP.add(makeConfig());
        mainP.add(Box.createVerticalStrut(10));
        mainP.add(makeProps());
        mainP.add(Box.createVerticalStrut(10));
        mainP.add(makeCats());
        mainP.add(Box.createVerticalStrut(10));
        mainP.add(makeBtns());

        panel.add(mainP, BorderLayout.NORTH);
        panel.revalidate();
        panel.repaint();
    }

    private JPanel makeConfig() {
        // basic output settings
        JPanel P = new JPanel(new GridLayout(3, 2, 10, 10));
        P.setBorder(BorderFactory.createTitledBorder("Output Configuration"));

        // output file name
        P.add(new JLabel("Name(*):"));
        P.add(new JTextField("f16_datalog.csv"));

        // file type - csv for excel
        P.add(new JLabel("Type(*):"));
        P.add(new JComboBox<>(new String[] { "CSV" }));

        // how often to record data
        P.add(new JLabel("Rate(*):"));
        P.add(new JTextField("1"));

        return P;
    }

    private JPanel makeProps() {
        // pick which data to record
        JPanel P = new JPanel(new BorderLayout(10, 10));
        P.setBorder(BorderFactory.createTitledBorder("Properties"));

        // setup lists for properties
        availMod = new DefaultListModel<>();
        selMod = new DefaultListModel<>();

        // left side - available data
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available"));
        availL = new JList<>(availMod);
        JScrollPane availS = new JScrollPane(availL);
        leftPanel.add(availS, BorderLayout.CENTER);

        // right side - selected data
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Selected Properties"));
        selL = new JList<>(selMod);
        JScrollPane selS = new JScrollPane(selL);
        rightPanel.add(selS, BorderLayout.CENTER);

        // buttons in middle
        JPanel btnP = new JPanel();
        btnP.setLayout(new BoxLayout(btnP, BoxLayout.Y_AXIS));

        // add button - pick data to record
        JButton addB = new JButton("Add");
        addB.addActionListener(e -> {
            // button clicked - nothing happens yet
        });

        // delete button - remove from recording
        JButton delB = new JButton("Delete");
        delB.addActionListener(e -> {
            // button clicked - nothing happens yet
        });

        // layout buttons
        btnP.add(Box.createVerticalStrut(20));
        btnP.add(addB);
        btnP.add(Box.createVerticalStrut(10));
        btnP.add(delB);
        btnP.add(Box.createVerticalGlue());

        // put lists and buttons together
        JPanel listsP = new JPanel(new GridLayout(1, 3, 10, 0));
        listsP.add(leftPanel);
        listsP.add(btnP);
        listsP.add(rightPanel);

        P.add(listsP, BorderLayout.CENTER);
        return P;
    }

    private JPanel makeCats() {
        // groups of related data
        JPanel P = new JPanel(new GridLayout(4, 3, 10, 5));
        P.setBorder(BorderFactory.createTitledBorder("Output Categories"));

        // checkboxes for data groups
        P.add(new JCheckBox("simulation")); // basic sim info
        P.add(new JCheckBox("rates")); // rotation speeds
        P.add(new JCheckBox("position")); // where plane is
        P.add(new JCheckBox("propulsion")); // engine stuff
        P.add(new JCheckBox("atmosphere")); // weather/air
        P.add(new JCheckBox("velocities")); // movement speeds
        P.add(new JCheckBox("coefficients")); // aero numbers
        P.add(new JCheckBox("massprops")); // weight info
        P.add(new JCheckBox("forces")); // physical forces
        P.add(new JCheckBox("ground reactions")); // landing gear
        P.add(new JCheckBox("aerosurfaces")); // control surfaces
        P.add(new JCheckBox("moments")); // turning forces
        P.add(new JCheckBox("FCS")); // flight controls

        return P;
    }

    private JPanel makeBtns() {
        // action buttons at bottom
        JPanel P = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // pick where to save file
        JButton chooseB = new JButton("Choose");
        chooseB.addActionListener(e -> {
            // button clicked-nothing happens yet
        });

        // save these settings
        JButton addB = new JButton("Add Output");
        addB.addActionListener(e -> {
            // button clicked - nothing happens yet
        });

        // remove output settings
        JButton delB = new JButton("Delete Output");
        delB.addActionListener(e -> {
            // button clicked -nothing happens yet
        });

        P.add(chooseB);
        P.add(addB);
        P.add(delB);

        return P;
    }
}
