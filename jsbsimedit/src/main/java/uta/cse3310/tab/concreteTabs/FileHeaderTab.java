package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import generated.Fileheader;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

public class FileHeaderTab extends simpleTab {

    private JTextField dateField;
    private JTextField versionField;
    private JTextField copyrightField;
    private JTextField sensitivityField;
    private JTextArea  descriptionArea;

    public FileHeaderTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;

        // Placeholder until data is loaded
        panel.add(new JLabel("Loading File Header...", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
        System.out.println("this is in loadData() for fileHeader");

        panel.removeAll();
        panel.setLayout(null);

        Fileheader fh = DS.cfg.getFileheader();
        if (fh == null) {
            panel.add(new JLabel("No <fileheader> section found.", SwingConstants.CENTER));
            panel.revalidate();
            panel.repaint();
            return;
        }

        JLabel title = new JLabel("General Information");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(20, 10, 300, 25);
        panel.add(title);

        // ===== Creation Date =====
        JLabel dateLbl = new JLabel("Creation Date (yyyy-mm-dd):");
        dateLbl.setBounds(20, 60, 200, 20);
        panel.add(dateLbl);

        String dateText = fh.getFilecreationdate() != null ? fh.getFilecreationdate().toString() : "";
        dateField = new JTextField(dateText);
        dateField.setBounds(230, 60, 200, 22);
        panel.add(dateField);

        // ===== Version =====
        JLabel versionLbl = new JLabel("Version:");
        versionLbl.setBounds(20, 100, 200, 20);
        panel.add(versionLbl);

        versionField = new JTextField(fh.getVersion() != null ? fh.getVersion() : "");
        versionField.setBounds(230, 100, 200, 22);
        panel.add(versionField);

        // ===== Copyright =====
        JLabel copyrightLbl = new JLabel("Copyright:");
        copyrightLbl.setBounds(20, 140, 200, 20);
        panel.add(copyrightLbl);

        copyrightField = new JTextField(fh.getCopyright() != null ? fh.getCopyright() : "");
        copyrightField.setBounds(230, 140, 380, 22);
        panel.add(copyrightField);

        // ===== Sensitivity =====
        JLabel sensLbl = new JLabel("Sensitivity:");
        sensLbl.setBounds(20, 180, 200, 20);
        panel.add(sensLbl);

        sensitivityField = new JTextField(fh.getSensitivity() != null ? fh.getSensitivity() : "");
        sensitivityField.setBounds(230, 180, 200, 22);
        panel.add(sensitivityField);

        // ===== Description =====
        JLabel descLbl = new JLabel("Description:");
        descLbl.setBounds(20, 220, 200, 20);
        panel.add(descLbl);

        descriptionArea = new JTextArea(fh.getDescription() != null ? fh.getDescription() : "");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setBounds(230, 220, 380, 120);
        panel.add(scroll);

        // ===== Save Button =====
        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(530, 360, 100, 28);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> saveToXML());

        panel.revalidate();
        panel.repaint();
    }

    private void saveToXML() {
        Fileheader fh = DS.cfg.getFileheader();
        if (fh == null) return;

        try {
            String dateStr = dateField.getText().trim();
            if (!dateStr.isEmpty()) {
                javax.xml.datatype.DatatypeFactory df = javax.xml.datatype.DatatypeFactory.newInstance();
                fh.setFilecreationdate(df.newXMLGregorianCalendar(dateStr));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "Invalid date format. Use yyyy-mm-dd.");
            return;
        }

        fh.setVersion(versionField.getText());
        fh.setCopyright(copyrightField.getText());
        fh.setSensitivity(sensitivityField.getText());
        fh.setDescription(descriptionArea.getText());

        DS.setDirty();
        JOptionPane.showMessageDialog(panel, "File header saved.");
    }
}

