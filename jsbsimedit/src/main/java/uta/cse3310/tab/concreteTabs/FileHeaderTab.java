package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import java.awt.*;

import generated.Fileheader;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.File;

public class FileHeaderTab extends simpleTab {

    private JTextField dateField;
    private JTextField versionField;
    private JTextField copyrightField;
    private JTextField sensitivityField;
    private JTextArea descriptionArea;

    public FileHeaderTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        TF = tf;
        panel.add(new JLabel("Loading File Header...", SwingConstants.CENTER), BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
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

        JLabel dateLbl = new JLabel("Creation Date (yyyy-mm-dd):");
        dateLbl.setBounds(20, 60, 200, 20);
        panel.add(dateLbl);

        String dateText = fh.getFilecreationdate() != null ? fh.getFilecreationdate().toString() : "";
        dateField = new JTextField(dateText);
        dateField.setBounds(230, 60, 200, 22);
        panel.add(dateField);

        JLabel versionLbl = new JLabel("Version:");
        versionLbl.setBounds(20, 100, 200, 20);
        panel.add(versionLbl);

        versionField = new JTextField(fh.getVersion() != null ? fh.getVersion() : "");
        versionField.setBounds(230, 100, 200, 22);
        panel.add(versionField);

        JLabel copyrightLbl = new JLabel("Copyright:");
        copyrightLbl.setBounds(20, 140, 200, 20);
        panel.add(copyrightLbl);

        String copyrightText = fh.getCopyright();
        if (copyrightText == null || copyrightText.trim().isEmpty()) {
            try {
                File xmlFile = new File(DS.fileName);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(xmlFile);
                NodeList n = doc.getElementsByTagName("author");
                if (n != null && n.getLength() > 0 && n.item(0).getFirstChild() != null) {
                    copyrightText = n.item(0).getTextContent().trim();
                }
            } catch (Exception e) {
                copyrightText = "";
            }
        }

        copyrightField = new JTextField(copyrightText);
        copyrightField.setBounds(230, 140, 380, 22);
        panel.add(copyrightField);

        JLabel sensLbl = new JLabel("Sensitivity:");
        sensLbl.setBounds(20, 180, 200, 20);
        panel.add(sensLbl);

        sensitivityField = new JTextField(fh.getSensitivity() != null ? fh.getSensitivity() : "");
        sensitivityField.setBounds(230, 180, 200, 22);
        panel.add(sensitivityField);

        JLabel descLbl = new JLabel("Description:");
        descLbl.setBounds(20, 220, 200, 20);
        panel.add(descLbl);

        descriptionArea = new JTextArea(fh.getDescription() != null ? fh.getDescription() : "");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setBounds(230, 220, 380, 120);
        panel.add(scroll);

        panel.revalidate();
        panel.repaint();
    }
}
