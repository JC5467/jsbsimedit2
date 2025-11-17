package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;
import generated.Contact;
import generated.GroundReactions;
import generated.Location;
import generated.LengthUnit;
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
        // TODO: FIX SAVE DETAIL
        // TODO: LET USER SAVE TO XML
    
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
            Contact newContact = new Contact();
            newContact.setName("New Contact");
            newContact.setType("BOGEY");
            
            Location location = new Location();
            location.setX(0.0);
            location.setY(0.0);
            location.setZ(0.0);
            location.setUnit(LengthUnit.IN);
            newContact.setLocation(location);
            
            SpringForce spring = new SpringForce();
            spring.setValue(0.0);
            spring.setUnit(SpringForceUnit.LBS_FT);
            newContact.setSpringCoeff(spring);
            
            DampForce damp = new DampForce();
            damp.setValue(0.0);
            damp.setUnit(DampForceUnit.LBS_FT_SEC);
            newContact.setDampingCoeff(damp);
            
            newContact.setStaticFriction(0.0);
            newContact.setDynamicFriction(0.0);
            newContact.setRollingFriction(0.0);
            
            Angle steer = new Angle();
            steer.setValue(0.0);
            steer.setUnit(AngleUnit.DEG);
            newContact.setMaxSteer(steer);
            
            newContact.setBrakeGroup("NONE");
            newContact.setRetractable(BigInteger.ZERO);
            DS.cfg.getGroundReactions().getContent().add(newContact);
            listModel.addElement(newContact);
            detailWindow(newContact);
        });
            //delete button logic
        deleteButton.addActionListener(e -> {
        selected = list.getSelectedIndex();
        Object index = listModel.getElementAt(selected);
        if (selected != -1){
            Contact member = (Contact) index; 
            listModel.remove(selected);
            DS.cfg.getGroundReactions().getContent().remove(member);
            panel.repaint();
        }
        });
            //detail button logic
        detailButton.addActionListener(e -> {
            selected = list.getSelectedIndex();
            if (selected != -1) {
                Contact contact = (Contact) list.getSelectedValue();
                detailWindow(contact);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a contact first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void detailWindow(Contact contact) {
                Location location = contact.getLocation();
                SpringForce spring = contact.getSpringCoeff();
                DampForce damp = contact.getDampingCoeff();
                Angle steer = contact.getMaxSteer();
                
                //Create detail dialog with this data
                detail = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel), "Landing Gear Setup");
                detail.setSize(500, 600);
                detail.setLocationRelativeTo(scrollPane);
                detail.setModal(true);
                
                //Form panel
                JPanel formPanel = new JPanel(new GridLayout(17, 2, 3, 5));
                //Detail button panel
                JPanel detailButtonPanel = new JPanel();
                JButton detailOkButton = new JButton("Ok");
                JButton detailCancelButton = new JButton("Cancel");

                //detail cancel Button logic
                detailCancelButton.addActionListener(e2 -> {
                    detail.dispose();
                });

                //Fields to contact data
                nameField = new JTextField(contact.getName());
                typeField = new JTextField(contact.getType());
                formPanel.add(new JLabel("Name:"));
                formPanel.add(nameField);

                formPanel.add(new JLabel("Type:"));
                formPanel.add(typeField);

                if (location != null) {
                    xField = new JTextField(String.valueOf(location.getX()));
                    yField = new JTextField(String.valueOf(location.getY()));
                    zField = new JTextField(String.valueOf(location.getZ()));
                    locUnitField = new JTextField(String.valueOf(location.getUnit()));
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
                    springCoeffField = new JTextField(String.valueOf(spring.getValue()));
                    springUnitField = new JTextField(spring.getUnit().value());
                    formPanel.add(new JLabel("Spring Coefficient ="));
                    formPanel.add(springCoeffField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(springUnitField);
                }

                if (damp != null) {
                    dampCoeffField = new JTextField(String.valueOf(damp.getValue()));
                    dampUnitField = new JTextField(damp.getUnit().value());
                    formPanel.add(new JLabel("Damping Coefficient ="));
                    formPanel.add(dampCoeffField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(dampUnitField);
                }

                staticField = new JTextField(String.valueOf(contact.getStaticFriction()));
                dyanmicField = new JTextField(String.valueOf(contact.getDynamicFriction()));
                rollField = new JTextField(String.valueOf(contact.getRollingFriction()));
                formPanel.add(new JLabel("Static Friction ="));
                formPanel.add(staticField);

                formPanel.add(new JLabel("Dynamic Friction ="));
                formPanel.add(dyanmicField);

                formPanel.add(new JLabel("Rolling Friction ="));
                formPanel.add(rollField);

                if (steer != null) {
                    steerField = new JTextField(String.valueOf(steer.getValue()));
                    steerUnitField = new JTextField(steer.getUnit().value());
                    formPanel.add(new JLabel("Max Steer ="));
                    formPanel.add(steerField);

                    formPanel.add(new JLabel("Unit ="));
                    formPanel.add(steerUnitField);
                }

                if (contact.getBrakeGroup() != null) {
                    brakeField = new JTextField(contact.getBrakeGroup());
                    formPanel.add(new JLabel("Brake Group ="));
                    formPanel.add(brakeField);
                }

                if (contact.getRetractable() != null) {
                    retractField = new JTextField(String.valueOf(contact.getRetractable()));
                    formPanel.add(new JLabel("Retractable ="));
                    formPanel.add(retractField);
                }

                //detail save button logic
                detailOkButton.addActionListener(e2 -> {
                    //update fields in contact
                    contact.setName(nameField.getText());
                    contact.setType(typeField.getText());
                    if (location != null) {
                    location.setX(Double.parseDouble(xField.getText()));
                    location.setY(Double.parseDouble(yField.getText()));
                    location.setZ(Double.parseDouble(zField.getText()));
                    location.setUnit(LengthUnit.fromValue(locUnitField.getText()));
                    }
                    if (spring != null) {
                        spring.setValue(Double.parseDouble(springCoeffField.getText()));
                        spring.setUnit(SpringForceUnit.fromValue(springUnitField.getText()));
                    }
                    if (damp != null) {
                        damp.setValue(Double.parseDouble(dampCoeffField.getText()));
                        damp.setUnit(DampForceUnit.fromValue(dampUnitField.getText()));
                    }
                    contact.setStaticFriction(Double.parseDouble(staticField.getText()));
                    contact.setDynamicFriction(Double.parseDouble(dyanmicField.getText()));
                    contact.setRollingFriction(Double.parseDouble(rollField.getText()));
                    if (steer != null) {
                        steer.setValue(Double.parseDouble(steerField.getText()));
                        steer.setUnit(AngleUnit.fromValue(steerUnitField.getText()));
                    }
                    if (contact.getBrakeGroup() != null) {
                        contact.setBrakeGroup(brakeField.getText());
                    }
                    if (contact.getRetractable() != null) {
                        contact.setRetractable(new BigInteger(retractField.getText()));
                    }
                    detail.dispose();
                    panel.repaint();
                });

                detailButtonPanel.add(detailOkButton);
                detailButtonPanel.add(detailCancelButton);
                detail.add(formPanel, BorderLayout.CENTER);
                detail.add(detailButtonPanel, BorderLayout.SOUTH);
                detail.setVisible(true);
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
    private JTextField nameField;
    private JTextField typeField;
    private JTextField xField;
    private JTextField yField;
    private JTextField zField;
    private JTextField locUnitField;
    private JTextField springCoeffField;
    private JTextField springUnitField;
    private JTextField dampCoeffField;
    private JTextField dampUnitField;
    private JTextField staticField;
    private JTextField dyanmicField;
    private JTextField rollField;
    private JTextField steerField;
    private JTextField steerUnitField;
    private JTextField brakeField;
    private JTextField retractField;
}
