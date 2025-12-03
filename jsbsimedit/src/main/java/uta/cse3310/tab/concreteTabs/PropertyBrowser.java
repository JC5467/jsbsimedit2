

package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyBrowser extends JDialog {
    //ui components
    private JTable propertyTable;          // table showing all properties
    private DefaultTableModel tableModel;  //data model for the table
    private TableRowSorter<DefaultTableModel> rowSorter;  // for sorting and filtering
    private JTextField filterField;        // text field for filtering
    private JTextField currentSelectionField;  // shows currently selected property
    
    //data
    private List<String> selectedProps;    //list of selected properties to return
    private Object[][] propData;     // property data - more properties will be added
    
    //constructor
    public PropertyBrowser(JFrame parent) {
        super(parent, "Properties", true);
        selectedProps = new ArrayList<>();
        initPropertyData();  //load the property data
        setupUI();      // build the user interface
        setSize(1100, 750);
        setLocationRelativeTo(parent);
        
        // show count in window title
        setTitle("Properties (" + propData.length + " properties)");
    }

    // initialize the property data
    // note: more properties will be added later
    private void initPropertyData() {
        // more properties will be added
        propData = new Object[][] {
            
            // simulation properties
            {"sim-time-sec", "time", "SEC", "RO", ""},
            {"output_delay", "go on output after optimize", "NORM", "RO", "default value is zero"},
            {"output_date_rate", "dat file output rate", "NORM", "RO", "output a record every X times. default value is 5."},
            
            // signal properties
            {"signal/aeroelastic", "aeroelasticity", "NORM", "R/W", "aeroelastic enable signal(0-false, 1-true)"},
            {"signal/custom1", "custom signal 1", "NORM", "R/W", "custom enable signal(0-false, 1-true)"},
            {"signal/custom2", "custom signal 2", "NORM", "R/W", "custom enable signal(0-false, 1-true)"},
            {"signal/custom3", "custom signal 3", "NORM", "R/W", "custom enable signal(0-false, 1-true)"},
            {"signal/custom4", "custom signal 4", "NORM", "R/W", "custom enable signal(0-false, 1-true)"},
            {"signal/custom5", "custom signal 5", "NORM", "R/W", "custom enable signal(0-false, 1-true)"},
            
            //metrics properties
            {"metrics/Sw-sqft", "wing area", "SQFT", "RO", ""},
            {"metrics/Sw-sqm", "wing area", "SQM", "RO", ""},
            {"metrics/bw-ft", "wing span", "FT", "RO", ""},
            {"metrics/bw-m", "wing span", "M", "RO", ""},
            {"metrics/charw-ft", "chord bar", "FT", "RO", ""},
            {"metrics/charw-m", "chord bar", "M", "RO", ""},
            {"metrics/hv-deg", "wing incidence angle", "DEG", "RO", "aero/alpha-wing-rad = aero/alpha-rad + metrics/lw-deg * deg2rad"},
            {"metrics/sh-sqft", "Horizontal Tail Area", "SQFT", "RO", "deprecated"},
            {"metrics/sh-sqm", "horizontal tail area", "SQM", "RO", "deprecated"},
            {"metrics/h-ft", "horizontal tail arm", "FT", "RO", "deprecated"},
            {"metrics/h-m", "horizontal tail arm", "M", "RO", "deprecated"},
            {"metrics/h-norm", "horizontal tail arm", "norm", "RO", "deprecated, HTailArm / char"},
            {"metrics/vbarh-norm", "H. Tail Volume", "norm", "RO", "vbarh = HTailArm*HTailArea / (char*WingArea)"},
            {"metrics/Sv-sqft", "vertical tail area", "SQFT", "RO", "deprecated"},
            {"metrics/Sv-sqm", "vertical tail area", "SQM", "RO", ""},
            {"metrics/lv-ft", "vertical tail arm", "FT", "RO", ""},
            {"metrics/lv-m", "vertical tail arm", "FT", "RO", ""},
            {"metrics/lv-norm", "vertical tail arm", "norm", "RO", ""},
            {"metrics/vbarv-norm", "V. Tail Volume", "norm", "RO", "vbarv = VTailArm*VTailArea / (char*WingArea)"},
            {"metrics/aero-rp-x-in", "X coordinate for ARP", "IN", "RO", "Aero Reference Point in the structural frame"},
            {"metrics/aero-rp-x-m", "X coordinate for ARP", "M", "RO", "Aero Reference Point in the structural frame"},
            {"metrics/aero-rp-y-in", "Y coordinate for ARP", "IN", "RO", "Aero Reference Point in the structural frame"},
            {"metrics/aero-rp-y-m", "Y coordinate for ARP", "M", "RO", "Aero Reference Point in the structural frame"},
            {"metrics/aero-rp-z-in", "Z coordinate for ARP", "IN", "RO", "Aero Reference Point in the structural frame"},
            {"metrics/aero-rp-z-m", "Z coordinate for ARP", "M", "RO", "Aero Reference Point in the structural frame"},
            {"metrics/eyepoint-x-in", "X coordinate for EP", "IN", "RO", "Eye Point in the structural frame"},
            {"metrics/eyepoint-x-m", "X coordinate for EP", "M", "RO", "Eye Point in the structural frame"},
            {"metrics/eyepoint-y-in", "Y coordinate for EP", "IN", "RO", "Eye Point in the structural frame"},
            {"metrics/eyepoint-y-m", "Y coordinate for EP", "M", "RO", "Eye Point in the structural frame"},
            {"metrics/eyepoint-z-in", "Z coordinate for EP", "IN", "RO", "Eye Point in the structural frame"},
            {"metrics/eyepoint-z-m", "Z coordinate for EP", "M", "RO", "Eye Point in the structural frame"},
            {"metrics/visualrefpoint-x-in", "X coordinate for VRP", "IN", "RO", "Visual Reference Point in the structural frame"},
            {"metrics/visualrefpoint-x-m", "X coordinate for VRP", "M", "RO", "Visual Reference Point in the structural frame"},
            {"metrics/visualrefpoint-y-in", "Y coordinate for VRP", "IN", "RO", "Visual Reference Point in the structural frame"},
            {"metrics/visualrefpoint-y-m", "Y coordinate for VRP", "M", "RO", "Visual Reference Point in the structural frame"},
            {"metrics/visualrefpoint-z-in", "Z coordinate for VRP", "IN", "RO", "Visual Reference Point in the structural frame"},
            {"metrics/visualrefpoint-z-m", "Z coordinate for VRP", "M", "RO", "Visual Reference Point in the structural frame"},
        };
    }

    // set up the user interface
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        // top panel with controls
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // property count label
        JLabel countLabel = new JLabel("Showing " + propData.length + " properties");
        countLabel.setFont(new Font("Arial", Font.BOLD, 12));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(countLabel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // current selection display
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionPanel.add(new JLabel("Current Selection: "));
        currentSelectionField = new JTextField(30);
        currentSelectionField.setEditable(false);
        currentSelectionField.setBackground(Color.WHITE);
        currentSelectionField.setText("(click on a property to select)");
        selectionPanel.add(currentSelectionField);
        topPanel.add(selectionPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(new JLabel("Filter: "));
        filterField = new JTextField(25);
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
        });
        filterPanel.add(filterField);
        
        JButton clearFilterBtn = new JButton("Clear");
        clearFilterBtn.addActionListener(e -> {
            filterField.setText("");
            applyFilter();
        });
        filterPanel.add(clearFilterBtn);
        
        topPanel.add(filterPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // show all button
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton showAllBtn = new JButton("Show All Properties");
        showAllBtn.addActionListener(e -> showAllProps());
        topButtonPanel.add(showAllBtn);
        topPanel.add(topButtonPanel);
        
        add(topPanel, BorderLayout.NORTH);

        // property table setup
        String[] columnNames = {"#", "Property Name", "Description", "Unit", "Access", "Comment"};
        
        //create table model with row numbers
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // populate table with data
        for (int i = 0; i < propData.length; i++) {
            Object[] rowData = new Object[6];
            rowData[0] = String.valueOf(i + 1); // row number
            System.arraycopy(propData[i], 0, rowData, 1, 5); // copy property data
            tableModel.addRow(rowData);
        }
        
        propertyTable = new JTable(tableModel);
        propertyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propertyTable.getTableHeader().setReorderingAllowed(false);
        
        // selection listener - update display when row is selected
        propertyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionDisplay();
            }
        });
        
        //double-click to select property
        propertyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    updateSelectionDisplay();
                }
                if (e.getClickCount() == 2) {
                    int selectedRow = propertyTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectAndClose();
                    }
                }
            }
        });
        
        // set column widths
        propertyTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // row number
        propertyTable.getColumnModel().getColumn(1).setPreferredWidth(200); // property name
        propertyTable.getColumnModel().getColumn(2).setPreferredWidth(250); // description
        propertyTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // unit
        propertyTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // access
        propertyTable.getColumnModel().getColumn(5).setPreferredWidth(250); // comment
        
        // set up row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        propertyTable.setRowSorter(rowSorter);
        rowSorter.setSortable(0, false); //don't sort row number column
        
        // add table to scroll pane
        JScrollPane tableScroll = new JScrollPane(propertyTable);
        tableScroll.setPreferredSize(new Dimension(950, 450));
        add(tableScroll, BorderLayout.CENTER);

        // bottom buttons panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton selectBtn = new JButton("Select Property");
        selectBtn.addActionListener(e -> selectAndClose());
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> cancelSelection());
        
        bottomButtonPanel.add(selectBtn);
        bottomButtonPanel.add(cancelBtn);
        
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    // update the selection display field
    private void updateSelectionDisplay() {
        int selectedRow = propertyTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = propertyTable.convertRowIndexToModel(selectedRow);
            String propName = (String) tableModel.getValueAt(modelRow, 1);
            String propDesc = (String) tableModel.getValueAt(modelRow, 2);
            currentSelectionField.setText(propName + " - " + propDesc);
        } else {
            currentSelectionField.setText("(click on a property to select)");
        }
    }

    //apply filter based on text in filter field
    private void applyFilter() {
        String filterText = filterField.getText().trim();
        if (filterText.isEmpty()) {
            rowSorter.setRowFilter(null);
            setTitle("Properties (" + propData.length + " properties)");
        } else {
            //filter on name and description columns
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText, 1, 2));
            int visibleCount = propertyTable.getRowCount();
            setTitle("Properties (" + visibleCount + " of " + propData.length + " properties)");
        }
    }

    // clear filter and show all properties
    private void showAllProps() {
        filterField.setText("");
        rowSorter.setRowFilter(null);
        setTitle("Properties (" + propData.length + " properties)");
    }

    // select current property and close dialog
    private void selectAndClose() {
        int selectedRow = propertyTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = propertyTable.convertRowIndexToModel(selectedRow);
            String propName = (String) tableModel.getValueAt(modelRow, 1);
            selectedProps.add(propName);
        }
        dispose();
    }

    // cancel selection and close dialog
    private void cancelSelection() {
        selectedProps.clear();
        dispose();
    }

    //get the list of selected properties
    public List<String> getSelectedProperties() {
        return selectedProps;
    }
}
