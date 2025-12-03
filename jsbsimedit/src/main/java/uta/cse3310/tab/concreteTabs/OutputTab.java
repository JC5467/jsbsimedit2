
package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import generated.Output;
import uta.cse3310.tab.concreteTabs.PropertyBrowser;

public class OutputTab extends simpleTab {  
    // ui elements for basic output settings
    private JTextField nameField;        // output file name
    private JComboBox<String> typeBox;   // file type: csv or tabular
    private JTextField rateField;        // sampling rate in hz
    
    // properties management
    private DefaultListModel<String> propList;    // list of selected properties
    private JList<String> propDisplay;           // display for properties list
    private JTextField propInput;                // input for manual property entry
    
    // checkboxes for output categories
    private JCheckBox simCheck;        // simulation data
    private JCheckBox ratesCheck;      // angular rates
    private JCheckBox posCheck;        // position data
    private JCheckBox propCheck;       // propulsion data
    private JCheckBox atmCheck;        // atmosphere data
    private JCheckBox velCheck;        // velocity data
    private JCheckBox coeffCheck;      // aerodynamic coefficients
    private JCheckBox massCheck;       // mass properties
    private JCheckBox forceCheck;      // forces
    private JCheckBox groundCheck;     // ground reactions
    private JCheckBox aeroCheck;       // aerosurfaces
    private JCheckBox momentCheck;     // moments
    private JCheckBox fcsCheck;        // flight control system
    
    // data management
    private Output current;            // currently edited output object
    private boolean loading;           // flag to prevent save loops during loading
    private List<String> props = new ArrayList<>();  // local copy of properties

    // constructor
    public OutputTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        setupUI();
    }

    //load data from xml into ui
    @Override
    public void loadData() {
        loading = true;  // prevent auto-save during loading
        
        // clear old data
        if (propList != null) {
            propList.clear();
        }
        props.clear();
        
        // reset all checkboxes to unchecked
        resetChecks();
        
        // load from config file
        if (DS.cfg != null) {
            List<Output> outputs = DS.cfg.getOutput();
            if (outputs != null && !outputs.isEmpty()) {
                current = outputs.get(0);  // get first output
                
                // set basic fields
                nameField.setText(current.getName());
                typeBox.setSelectedItem(current.getType());
                rateField.setText(String.valueOf(current.getRate()));
                
                // load properties if they exist
                loadProps();
                
                // load checkbox states
                loadChecks();
                
            } else {
                // no output exists, create default
                setDefaults();
                makeDefault();
            }
        } else {
            // no configuration loaded, use defaults
            setDefaults();
            makeDefault();
        }
        
        //update ui with loaded properties
        updatePropUI();
        
        loading = false;
        panel.revalidate();
        panel.repaint();
    }
    
    // try to load properties from output object
    private void loadProps() {
        // use reflection for compatibility with old xml
        try {
            java.lang.reflect.Method getProp = current.getClass().getMethod("getProperty");
            Object propObj = getProp.invoke(current);
            if (propObj != null && propObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> loaded = (List<String>) propObj;
                props.addAll(loaded);
            }
        } catch (NoSuchMethodException e) {
            // ok if method doesn't exist (new xml format)
        } catch (Exception e) {
            System.err.println("error loading props: " + e.getMessage());
        }
    }
    
    // update ui list from local properties
    private void updatePropUI() {
        if (propList != null) {
            propList.clear();
            for (String prop : props) {
                propList.addElement(prop);
            }
        }
    }
    
    // reset all checkboxes tounchecked
    private void resetChecks() {
        if (simCheck != null) simCheck.setSelected(false);
        if (ratesCheck != null) ratesCheck.setSelected(false);
        if (posCheck != null) posCheck.setSelected(false);
        if (propCheck != null) propCheck.setSelected(false);
        if (atmCheck != null) atmCheck.setSelected(false);
        if (velCheck != null) velCheck.setSelected(false);
        if (coeffCheck != null) coeffCheck.setSelected(false);
        if (massCheck != null) massCheck.setSelected(false);
        if (forceCheck != null) forceCheck.setSelected(false);
        if (groundCheck != null) groundCheck.setSelected(false);
        if (aeroCheck != null) aeroCheck.setSelected(false);
        if (momentCheck != null) momentCheck.setSelected(false);
        if (fcsCheck != null) fcsCheck.setSelected(false);
    }
    
    // load checkbox states from output object
    private void loadChecks() {
        // category names that might exist in old xml
        String[] cats = {"simulation", "rates", "position", "propulsion", "atmosphere", 
                        "velocities", "coefficients", "massprops", "forces", 
                        "ground_reactions", "aerosurfaces", "moments", "fcs"};
        
        for (String cat : cats) {
            try {
                // build method name like "getSimulation"
                String methodName = "get" + cap(cat);
                java.lang.reflect.Method method = current.getClass().getMethod(methodName);
                Object value = method.invoke(current);
                if (value != null && value instanceof String) {
                    String state = ((String) value).trim();
                    setCheck(cat, "ON".equalsIgnoreCase(state));  // set checkbox based on "ON"/"OFF"
                }
            } catch (Exception e) {
                // method doesn't exist - ok for new xml
            }
        }
    }
    
    // set a specific checkbox state
    private void setCheck(String cat, boolean on) {
        switch (cat) {
            case "simulation": if (simCheck != null) simCheck.setSelected(on); break;
            case "rates": if (ratesCheck != null) ratesCheck.setSelected(on); break;
            case "position": if (posCheck != null) posCheck.setSelected(on); break;
            case "propulsion": if (propCheck != null) propCheck.setSelected(on); break;
            case "atmosphere": if (atmCheck != null) atmCheck.setSelected(on); break;
            case "velocities": if (velCheck != null) velCheck.setSelected(on); break;
            case "coefficients": if (coeffCheck != null) coeffCheck.setSelected(on); break;
            case "massprops": if (massCheck != null) massCheck.setSelected(on); break;
            case "forces": if (forceCheck != null) forceCheck.setSelected(on); break;
            case "ground_reactions": if (groundCheck != null) groundCheck.setSelected(on); break;
            case "aerosurfaces": if (aeroCheck != null) aeroCheck.setSelected(on); break;
            case "moments": if (momentCheck != null) momentCheck.setSelected(on); break;
            case "fcs": if (fcsCheck != null) fcsCheck.setSelected(on); break;
        }
    }
    
    // capitalize string for method names (ground_reactions-> groundreactions)
    private String cap(String str) {
        if (str == null || str.isEmpty()) return str;
        str = str.replace("_reactions", "Reactions");  // special case
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // create default output object
    private void makeDefault() {
        current = new Output();
        current.setName("JSBout.csv");
        current.setType("CSV");
        current.setRate(100.0);
        
        List<Output> outputs = DS.cfg.getOutput();
        if (outputs != null && outputs.isEmpty()) {
            outputs.add(current);
        }
    }

    //set default values in ui fields
    private void setDefaults() {
        nameField.setText("JSBout.csv");
        typeBox.setSelectedItem("CSV");
        rateField.setText("100");
        resetChecks();
        props.clear();
        if (propList != null) {
            propList.clear();
        }
    }

    // create and arrange all ui components
    private void setupUI() {
        panel.removeAll();
        panel.setLayout(new BorderLayout());

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        
        // add all panels with spacing
        main.add(makeConfigPanel());
        main.add(Box.createVerticalStrut(10));
        main.add(makePropsPanel());
        main.add(Box.createVerticalStrut(10));
        main.add(makeChecksPanel());
        
        main.add(makeOutputButtonsPanel());

        panel.add(main, BorderLayout.NORTH);
        panel.revalidate();
        panel.repaint();
    }

    // create configuration panel (name, type,rate)
    private JPanel makeConfigPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Output"));
        
        // file name
        p.add(new JLabel("Name:"));
        nameField = new JTextField("JSBout.csv", 15);
        nameField.getDocument().addDocumentListener(new ChangeListener());  // auto-save
        p.add(nameField);
        
        // file type dropdown
        p.add(new JLabel("Type:"));
        typeBox = new JComboBox<>(new String[]{"CSV", "TABULAR"});
        typeBox.setSelectedItem("CSV");
        typeBox.addActionListener(e -> save());  // save on change
        p.add(typeBox);
        
        // sampling rate
        p.add(new JLabel("Rate:"));
        rateField = new JTextField("100", 5);
        rateField.getDocument().addDocumentListener(new ChangeListener());  // auto-save
        p.add(rateField);
        
        return p;
    }

    // create properties management panel
    private JPanel makePropsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Properties"));
        
        // top part - selected properties
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createTitledBorder("Selected"));
        
        // button bar with browse button and count
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton browse = new JButton("Choose");
        browse.addActionListener(e -> showBrowser());
        buttons.add(browse);
        
        JLabel count = new JLabel("Count: 0");
        buttons.add(Box.createHorizontalStrut(20));
        buttons.add(count);
        top.add(buttons, BorderLayout.NORTH);
        
        // list display for properties
        propList = new DefaultListModel<>();
        propDisplay = new JList<>(propList);
        propDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //update count when list changes
        propList.addListDataListener(new javax.swing.event.ListDataListener() {
            public void contentsChanged(javax.swing.event.ListDataEvent e) {
                count.setText("Count: " + propList.getSize());
                syncProps();  // keep local list in sync
            }
            public void intervalAdded(javax.swing.event.ListDataEvent e) {
                count.setText("Count: " + propList.getSize());
                syncProps();
            }
            public void intervalRemoved(javax.swing.event.ListDataEvent e) {
                count.setText("Count: " + propList.getSize());
                syncProps();
            }
        });
        
        JScrollPane scroll = new JScrollPane(propDisplay);
        scroll.setPreferredSize(new Dimension(400, 120));
        top.add(scroll, BorderLayout.CENTER);
        
        // manual property input area
        JPanel input = new JPanel(new BorderLayout());
        input.add(new JLabel("Enter property:"), BorderLayout.WEST);
        propInput = new JTextField();
        propInput.addActionListener(e -> addProp());  // add on enter key
        input.add(propInput, BorderLayout.CENTER);
        
        // add and delete buttons
        JPanel inpButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton del = new JButton("Delete");
        
        add.addActionListener(e -> addProp());
        del.addActionListener(e -> delProp());
        
        inpButtons.add(add);
        inpButtons.add(del);
        input.add(inpButtons, BorderLayout.EAST);
        
        top.add(input, BorderLayout.SOUTH);
        p.add(top, BorderLayout.CENTER);
        
        return p;
    }
    
    // sync local props list with ui list
    private void syncProps() {
        props.clear();
        if (propList != null) {
            for (int i = 0; i < propList.getSize(); i++) {
                props.add(propList.getElementAt(i));
            }
        }
    }
    
    // create checkboxes panel for output categories
    private JPanel makeChecksPanel() {
        JPanel p = new JPanel(new GridLayout(4, 4, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Categories"));
        
        // create all checkboxes
        simCheck = new JCheckBox("simulation");
        ratesCheck = new JCheckBox("rates");
        posCheck = new JCheckBox("position");
        propCheck = new JCheckBox("propulsion");
        atmCheck = new JCheckBox("atmosphere");
        velCheck = new JCheckBox("velocities");
        coeffCheck = new JCheckBox("coefficients");
        massCheck = new JCheckBox("massprops");
        forceCheck = new JCheckBox("forces");
        groundCheck = new JCheckBox("ground reactions");
        aeroCheck = new JCheckBox("aerosurfaces");
        momentCheck = new JCheckBox("moments");
        fcsCheck = new JCheckBox("FCS");
        
        // add to 4x4 grid
        p.add(simCheck);
        p.add(ratesCheck);
        p.add(posCheck);
        p.add(propCheck);
        p.add(atmCheck);
        p.add(velCheck);
        p.add(coeffCheck);
        p.add(massCheck);
        p.add(forceCheck);
        p.add(groundCheck);
        p.add(aeroCheck);
        p.add(momentCheck);
        p.add(fcsCheck);
        
        // fill empty cells in grid
        p.add(new JLabel(""));
        p.add(new JLabel(""));
        p.add(new JLabel(""));
        
        return p;
    }

    // create output add and delete buttons panel
    private JPanel makeOutputButtonsPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton addBtn = new JButton("Add Output");
        JButton delBtn = new JButton("Delete Output");
        
        addBtn.addActionListener(e -> addOutput());
        delBtn.addActionListener(e -> delOutput());
        
        p.add(addBtn);
        p.add(delBtn);
        
        return p;
    }

    //add new output configuration
    private void addOutput() {
        // create new output with defaults
        Output newOut = new Output();
        newOut.setName("NewOutput.csv");
        newOut.setType("CSV");
        newOut.setRate(100.0);
        
        if (DS.cfg != null) {
            List<Output> outputs = DS.cfg.getOutput();
            if (outputs != null) {
                outputs.add(newOut);
                current = newOut;
                loadData();  //refresh ui
                DS.setDirty();  // mark as changed
                JOptionPane.showMessageDialog(panel, "new output added.", "add", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // delete current output
    private void delOutput() {
        if (current != null && DS.cfg != null) {
            List<Output> outputs = DS.cfg.getOutput();
            if (outputs != null && !outputs.isEmpty()) {
                // ask for confirmation
                int ok = JOptionPane.showConfirmDialog(panel, 
                    "delete current output?", 
                    "delete", 
                    JOptionPane.YES_NO_OPTION);
                
                if (ok == JOptionPane.YES_OPTION) {
                    outputs.remove(current);
                    DS.setDirty();  // mark as changed
                    
                    // get next output or create default
                    if (!outputs.isEmpty()) {
                        current = outputs.get(0);
                    } else {
                        makeDefault();
                    }
                    loadData(); //refresh ui
                    JOptionPane.showMessageDialog(panel, "output deleted.", "delete", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "no output to delete.", "delete", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(panel, "no output to delete.", "delete", JOptionPane.WARNING_MESSAGE);
        }
    }

    // add property from manual input
    private void addProp() {
        String prop = propInput.getText().trim();
        if (prop.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "enter property name.", "empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (propList.contains(prop)) {
            JOptionPane.showMessageDialog(panel, "already in list.", "duplicate", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        propList.addElement(prop);
        propInput.setText(""); // clear input field
    }

    // delete selected property
    private void delProp() {
        int idx = propDisplay.getSelectedIndex();
        if (idx != -1) {
            propList.remove(idx);
        } else {
            JOptionPane.showMessageDialog(panel, "select property first.", "no selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    // show property browser dialog
    private void showBrowser() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(panel);
        PropertyBrowser browser = new PropertyBrowser(parent);
        browser.setVisible(true);
        
        // add selected properties to list
        List<String> selected = browser.getSelectedProperties();
        if (selected != null) {
            for (String prop : selected) {
                if (!propList.contains(prop)) {
                    propList.addElement(prop);
                }
            }
        }
    }

    // save current settings to output object
    private void save() {
        if (current == null) {
            //create new output if none exists
            current = new Output();
            List<Output> outputs = DS.cfg.getOutput();
            if (outputs != null) {
                outputs.add(current);
            }
        }
        
        // update output with current ui values
        current.setName(nameField.getText());
        current.setType((String) typeBox.getSelectedItem());
        
        try {
            current.setRate(Double.parseDouble(rateField.getText()));
        } catch (NumberFormatException e) {
            // invalid number, reset to default
            rateField.setText("100");
            current.setRate(100.0);
        }
        
        DS.setDirty();  //mark data as changed for auto-save
    }

    //listener for auto-saving when fields change
    private class ChangeListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) { if (!loading) save(); }
        public void insertUpdate(DocumentEvent e) { if (!loading) save(); }
        public void removeUpdate(DocumentEvent e) { if (!loading) save(); }
    }
}
