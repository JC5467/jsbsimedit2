package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import generated.Contact;
import generated.GroundReactions;
import generated.Location;
import generated.Angle;
import generated.AngleUnit;
import generated.SpringForce;
import generated.SpringForceUnit;
import generated.DampForce;
import generated.DampForceUnit;
import uta.cse3310.tab.widgets.textFieldWLabel;

public class GroundReactionsTab extends simpleTab {
    // attributes that only pertain to the 'concrete' tab called oneTab
    // Following UIID094 - UIID098
        // TODO: allow the user to edit the XML in the GUI
        // TODO: implement DETAIL WINDOW
        // TODO: DO NOT ALLOW USER TO EDIT TABLE FROM TAB, ONLY DETAIL WINDOW
        // TODO: ADD SAVE + CANCEL BUTTON TO DETAIL
        // TODO: FIX DOUBLE EXIT DETAIL
    
    public GroundReactionsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        System.out.println("in GroundReactions constructor");

        TF = tf;
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("No aircraft file read.", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    //data fields to be included:
    //C.name, C.type, Location.coords, Stat-fcoef, dyam-fcoef, rolling, spring, damping, steer, brake group, retractable

    private void loadUI() {
        //JList
        listModel = new DefaultListModel<>();
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellRender();
        scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);

        //Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton detailButton = new JButton("Detail");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        //Button actions
            //add button logic
        addButton.addActionListener(e -> {
        listModel.addElement(new Object[]{"new_contact", "", "", ""});
        });
            //delete button logic
        deleteButton.addActionListener(e -> {
        selected = list.getSelectedIndex();
        if (selected != -1){list.remove(selected);}
        });
            //detail button logic
        detailButton.addActionListener(e -> {
            selected = list.getSelectedIndex();
            if (selected != -1) {
                Contact contact = (Contact) list.getSelectedValue();
                Location location = contact.getLocation();
                SpringForce spring = contact.getSpringCoeff();
                DampForce damp = contact.getDampingCoeff();
                Angle steer = contact.getMaxSteer();
                
                //Create detail dialog with this data
                detail = new JDialog((JFrame) SwingUtilities.getWindowAncestor(detailButton), "Landing Gear Setup");
                detail.setSize(500, 600);
                detail.setLocationRelativeTo(scrollPane);
                detail.setModal(true);
                
                //Form panel
                JPanel formPanel = new JPanel(new GridLayout(17, 2, 3, 5));

                //Fields to contact data
                JTextField nameField = new JTextField(contact.getName());
                JTextField typeField = new JTextField(contact.getType());
                formPanel.add(new JLabel("Name:"));
                formPanel.add(nameField);

                formPanel.add(new JLabel("Type:"));
                formPanel.add(typeField);

                if (location != null) {
                    JTextField xField = new JTextField(String.valueOf(location.getX()));
                    JTextField yField = new JTextField(String.valueOf(location.getY()));
                    JTextField zField = new JTextField(String.valueOf(location.getZ()));
                    JTextField locUnitField = new JTextField(String.valueOf(location.getUnit()));
                    formPanel.add(new JLabel("X ="));
                    formPanel.add(xField);

                    formPanel.add(new JLabel("Y ="));
                    formPanel.add(yField);

                    formPanel.add(new JLabel("Z ="));
                    formPanel.add(zField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(locUnitField);
                }

                if (spring != null) {
                    JTextField springCoeffField = new JTextField(String.valueOf(spring.getValue()));
                    JTextField springUnitField = new JTextField(String.valueOf(spring.getUnit()));
                    formPanel.add(new JLabel("Spring Coefficient ="));
                    formPanel.add(springCoeffField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(springUnitField);
                }

                if (damp != null) {
                    JTextField dampCoeffField = new JTextField(String.valueOf(damp.getValue()));
                    JTextField dampUnitField = new JTextField(String.valueOf(damp.getUnit()));
                    formPanel.add(new JLabel("Damping Coefficient ="));
                    formPanel.add(dampCoeffField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(dampUnitField);
                }

                JTextField staticField = new JTextField(String.valueOf(contact.getStaticFriction()));
                JTextField dyanmicField = new JTextField(String.valueOf(contact.getDynamicFriction()));
                JTextField rollField = new JTextField(String.valueOf(contact.getRollingFriction()));
                formPanel.add(new JLabel("Static Friction ="));
                formPanel.add(staticField);

                formPanel.add(new JLabel("Dynamic Friction ="));
                formPanel.add(dyanmicField);

                formPanel.add(new JLabel("Rolling Friction ="));
                formPanel.add(rollField);

                if (steer != null) {
                    JTextField steerField = new JTextField(String.valueOf(steer.getValue()));
                    JTextField steerUnitField = new JTextField(String.valueOf(steer.getUnit()));
                    formPanel.add(new JLabel("Max Steer ="));
                    formPanel.add(steerField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(steerUnitField);
                }

                if (contact.getBrakeGroup() != null) {
                    JTextField brakeField = new JTextField(contact.getBrakeGroup());
                    formPanel.add(new JLabel("Brake Group ="));
                    formPanel.add(brakeField);
                }

                if (contact.getRetractable() != null) {
                    JTextField retractField = new JTextField(String.valueOf(contact.getRetractable()));
                    formPanel.add(new JLabel("Retractable ="));
                    formPanel.add(retractField);
                }
                detail.add(formPanel);
                detail.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a contact first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
            detail.setVisible(true);
        });
    }
    public void loadData() {
        panel.removeAll();
        loadUI();

        for (Object obj : DS.cfg.getGroundReactions().getContent()) {
            if (obj instanceof Contact) {
                Contact contact = (Contact) obj;
                listModel.addElement(contact);
            }
        }
    }
    private void cellRender() {
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object value, 
                                                        int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Contact) {
                    Contact contact = (Contact) value;
                    Location loc = contact.getLocation();
                    String display = String.format("%s at point [%s, %s, %s] in %s (in %s brake group)", 
                    contact.getName(), 
                    loc != null ? loc.getX() : 0,
                    loc != null ? loc.getY() : 0,
                    loc != null ? loc.getZ() : 0,
                    loc != null && loc.getUnit() != null ? loc.getUnit().value() : "IN",
                    contact.getBrakeGroup() != null ? contact.getBrakeGroup() : "NONE");
                    return super.getListCellRendererComponent(jlist, display, index, isSelected, cellHasFocus);
                }
                return super.getListCellRendererComponent(jlist, value, index, isSelected, cellHasFocus);
            }
        });
    }   
    private JList list;
    private DefaultListModel listModel;
    private JScrollPane scrollPane;
    private JDialog detail;
    private int selected;
}
