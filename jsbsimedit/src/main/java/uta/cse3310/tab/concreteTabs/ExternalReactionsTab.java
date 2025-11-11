package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;


 //ExternalReactionsTab - Handles externally applied forces on the aircraft
public class ExternalReactionsTab extends simpleTab {

    private JTable table;
    private DefaultTableModel tableModel;
    private tabFrame TF;
    private int lastVersion = -1;

    public ExternalReactionsTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        this.TF = tf;
        System.out.println("in ExternalReactions constructor");

        panel.setLayout(new BorderLayout());

        // Columns for the table
        String[] columns = {"Force Name", "Frame", "Unit", "Location (x y z)", "Direction (x y z)"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons 
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Force");
        JButton deleteButton = new JButton("Delete Force");
        JButton clearButton = new JButton("Clear All");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addButton.addActionListener(e -> {
            tableModel.addRow(new Object[]{"new_force", "BODY", "IN", "0 0 0", "0 0 0"});
            saveToDataStore();
        });
        
        deleteButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected != -1) {
                tableModel.removeRow(selected);
                saveToDataStore();
            }
        });
        
        clearButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            saveToDataStore();
        });

        // Listen for table edits
        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                saveToDataStore();
            }
        });

        // Start a timer to check for dataStore updates
        Timer timer = new Timer(500, e -> checkForUpdates());
        timer.start();
    }

    /**
     * Check if dataStore has been updated (new file loaded)
     * Auto-load when version changes
     */
    private void checkForUpdates() {
        if (DS.version != lastVersion) {
            lastVersion = DS.version;
            if (DS.valid && DS.cfg != null) {
                loadFromDataStore();
            }
        }
    }

    
    private void loadFromDataStore() {
        if (DS.fileName == null || DS.fileName.isEmpty()) {
            return;
        }

        try {
            java.io.File xmlFile = new java.io.File(DS.fileName);
            if (!xmlFile.exists()) {
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Find the <external_reactions> element
            NodeList extList = doc.getElementsByTagName("external_reactions");
            if (extList.getLength() == 0) {
                System.out.println("No <external_reactions> section found in XML");
                tableModel.setRowCount(0);
                return;
            }

            Element externalReactions = (Element) extList.item(0);
            
            // Parse each <force> element
            NodeList forces = externalReactions.getElementsByTagName("force");

            tableModel.setRowCount(0); // Clear existing data

            for (int i = 0; i < forces.getLength(); i++) {
                Element force = (Element) forces.item(i);
                
                //  Get name and frame attributes
                String name = force.getAttribute("name");
                String frame = force.getAttribute("frame");

                // Parse <location> child
                Element location = (Element) force.getElementsByTagName("location").item(0);
                String unit = location != null ? location.getAttribute("unit") : "IN";
                String loc = getXYZ(location);

                // Parse <direction> child
                Element direction = (Element) force.getElementsByTagName("direction").item(0);
                String dir = getXYZ(direction);

                // Instantiate runtime object (table row represents force object)
                tableModel.addRow(new Object[]{name, frame, unit, loc, dir});
                
                // Identify special forces
                if (name.equals("pushback")) {
                    System.out.println("Loaded pushback force for ground operations");
                } else if (name.equals("hook")) {
                    System.out.println("Loaded hook force for arrested landings");
                }
            }

            System.out.println("Auto-loaded " + forces.getLength() + " external forces from XML");

        } catch (Exception e) {
            System.err.println("Error loading external reactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * get "x y z" text from XML element
     * Extract coordinate values
     */
    private String getXYZ(Element parent) {
        if (parent == null) return "0 0 0";
        String x = getTagText(parent, "x");
        String y = getTagText(parent, "y");
        String z = getTagText(parent, "z");
        return x + " " + y + " " + z;
    }

    private String getTagText(Element parent, String tag) {
        if (parent == null) return "0";
        NodeList list = parent.getElementsByTagName(tag);
        return (list.getLength() > 0) ? list.item(0).getTextContent().trim() : "0";
    }

    /**
     * Save changes back to dataStore 
     */
    private void saveToDataStore() {
        if (DS.fileName == null || DS.fileName.isEmpty()) {
            return;
        }

        try {
            java.io.File xmlFile = new java.io.File(DS.fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList extList = doc.getElementsByTagName("external_reactions");
            Element externalReactions;
            
            if (extList.getLength() == 0) {
                // Create <external_reactions> section if it doesn't exist
                externalReactions = doc.createElement("external_reactions");
                doc.getDocumentElement().appendChild(externalReactions);
            } else {
                externalReactions = (Element) extList.item(0);
                
                // Remove old forces
                NodeList oldForces = externalReactions.getElementsByTagName("force");
                for (int i = oldForces.getLength() - 1; i >= 0; i--) {
                    externalReactions.removeChild(oldForces.item(i));
                }
            }

            // Add new forces from table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Element force = doc.createElement("force");
                force.setAttribute("name", tableModel.getValueAt(i, 0).toString());
                force.setAttribute("frame", tableModel.getValueAt(i, 1).toString());

                Element location = doc.createElement("location");
                location.setAttribute("unit", tableModel.getValueAt(i, 2).toString());
                String[] locVals = tableModel.getValueAt(i, 3).toString().split(" ");
                addXYZ(doc, location, locVals);
                force.appendChild(location);

                Element direction = doc.createElement("direction");
                String[] dirVals = tableModel.getValueAt(i, 4).toString().split(" ");
                addXYZ(doc, direction, dirVals);
                force.appendChild(direction);

                externalReactions.appendChild(force);
            }

            // Write back to file
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(xmlFile);
            transformer.transform(source, result);

            // Mark dataStore as dirty so it knows changes were made
            DS.dirty = true;
            
            System.out.println("Saved external reactions to dataStore");

        } catch (Exception e) {
            System.err.println("Error saving external reactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addXYZ(Document doc, Element parent, String[] vals) {
        String[] tags = {"x", "y", "z"};
        for (int i = 0; i < 3; i++) {
            Element e = doc.createElement(tags[i]);
            e.setTextContent((i < vals.length) ? vals[i] : "0");
            parent.appendChild(e);
        }
    }
}