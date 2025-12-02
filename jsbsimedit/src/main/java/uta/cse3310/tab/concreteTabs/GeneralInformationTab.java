package uta.cse3310.tab.concreteTabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import generated.FdmConfig;
import generated.Fileheader;
import generated.Reference;
import jakarta.xml.bind.JAXBElement;
import uta.cse3310.dataStore;
import uta.cse3310.tab.simpleTab;
import uta.cse3310.tabFrame;

public class GeneralInformationTab extends simpleTab {

    private tabFrame TF;

    public GeneralInformationTab(tabFrame tf, dataStore ds, String label) {
        super(ds, label);
        this.TF = tf;

        // placeholder
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel("General Information (no file loaded yet)",
                SwingConstants.CENTER), BorderLayout.CENTER);
    }

    @Override
    public void loadData() {
        panel.removeAll();
        panel.setLayout(new BorderLayout());

        if (DS == null || DS.cfg == null) {
            panel.add(new JLabel("No aircraft file loaded.", SwingConstants.CENTER),
                    BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }

        FdmConfig cfg = DS.cfg;
        Fileheader fh = cfg.getFileheader();

        if (fh == null) {
            panel.add(new JLabel("No <fileheader> section found in XML.",
                    SwingConstants.CENTER), BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            return;
        }

        // Extract data from FdmConfig / Fileheader

        // Aircraft name
        String aircraftName = cfg.getName() != null ? cfg.getName() : "";

        // File path
        String filePath = DS.fileName != null ? DS.fileName : "";

        // Creation date / version / copyright / sensitivity / description
        String creationDate = (fh.getFilecreationdate() != null)
                ? fh.getFilecreationdate().toString()
                : "";

        String versionText = fh.getVersion() != null ? fh.getVersion() : "";
        String copyrightText = fh.getCopyright() != null ? fh.getCopyright() : "";
        String sensitivityText = fh.getSensitivity() != null ? fh.getSensitivity() : "";
        String descriptionText = fh.getDescription() != null ? fh.getDescription() : "";

        // Author / email / organization are mixed together as a list of
        // JAXBElement<String>
        String author = "";
        String email = "";
        String organization = "";
        if (fh.getAuthorOrEmailOrOrganization() != null) {
            for (JAXBElement<String> el : fh.getAuthorOrEmailOrOrganization()) {
                if (el == null || el.getName() == null)
                    continue;
                String local = el.getName().getLocalPart();
                String value = el.getValue() != null ? el.getValue() : "";
                if ("author".equalsIgnoreCase(local) && author.isEmpty()) {
                    author = value;
                } else if ("email".equalsIgnoreCase(local) && email.isEmpty()) {
                    email = value;
                } else if ("organization".equalsIgnoreCase(local) && organization.isEmpty()) {
                    organization = value;
                }
            }
        }

        // Documentation / notes / limitations + references
        StringBuilder notesBuf = new StringBuilder();
        StringBuilder docsBuf = new StringBuilder();
        StringBuilder limitsBuf = new StringBuilder();
        List<Reference> references = new ArrayList<>();

        if (fh.getNoteOrDocumentationOrLimitation() != null) {
            for (Object o : fh.getNoteOrDocumentationOrLimitation()) {
                if (o instanceof JAXBElement) {
                    JAXBElement<?> je = (JAXBElement<?>) o;
                    String local = je.getName().getLocalPart();
                    Object val = je.getValue();
                    String text = val != null ? val.toString() : "";

                    if ("note".equalsIgnoreCase(local)) {
                        if (!text.isBlank()) {
                            notesBuf.append(text).append("\n");
                        }
                    } else if ("documentation".equalsIgnoreCase(local)) {
                        if (!text.isBlank()) {
                            docsBuf.append(text).append("\n");
                        }
                    } else if ("limitation".equalsIgnoreCase(local)) {
                        if (!text.isBlank()) {
                            limitsBuf.append(text).append("\n");
                        }
                    }
                } else if (o instanceof Reference) {
                    references.add((Reference) o);
                }
            }
        }

        // Build UI

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        java.util.function.BiConsumer<String, JComponent> addRow = (labelText, comp) -> {
            // Label
            gbc.gridx = 0;
            gbc.weightx = 0;
            JLabel lbl = new JLabel(labelText);
            content.add(lbl, gbc);

            // Component
            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            content.add(comp, gbc);

            gbc.gridy++;
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.NONE;
        };

        // Aircraft name
        JTextField aircraftField = new JTextField(aircraftName, 25);
        addRow.accept("Aircraft Name:", aircraftField);

        // File path
        JTextField filePathField = new JTextField(filePath, 40);
        filePathField.setEditable(false);
        addRow.accept("File Path:", filePathField);

        // Creation date
        JTextField dateField = new JTextField(creationDate, 15);
        addRow.accept("File Date:", dateField);

        // Version
        JTextField versionField = new JTextField(versionText, 10);
        addRow.accept("Version:", versionField);

        // Copyright
        JTextField copyrightField = new JTextField(copyrightText, 30);
        addRow.accept("Copyright:", copyrightField);

        // Sensitivity
        JTextField sensitivityField = new JTextField(sensitivityText, 15);
        addRow.accept("Sensitivity:", sensitivityField);

        // Release level
        JComboBox<String> releaseLevelCombo = new JComboBox<>(new String[] { "ALPHA", "BETA", "PRODUCTION" });
        addRow.accept("Release Level:", releaseLevelCombo);

        // Author
        JTextField authorField = new JTextField(author, 25);
        addRow.accept("Author:", authorField);

        // Email
        JTextField emailField = new JTextField(email, 25);
        addRow.accept("Email:", emailField);

        // Organization
        JTextField orgField = new JTextField(organization, 25);
        addRow.accept("Organization:", orgField);

        // Description
        JTextArea descArea = new JTextArea(descriptionText, 4, 40);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        gbc.gridx = 0;
        gbc.weightx = 0;
        content.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(descScroll, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Documentation
        JTextArea docsArea = new JTextArea(docsBuf.toString(), 3, 40);
        docsArea.setLineWrap(true);
        docsArea.setWrapStyleWord(true);
        JScrollPane docsScroll = new JScrollPane(docsArea);
        gbc.gridx = 0;
        content.add(new JLabel("Documentation:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(docsScroll, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Notes
        JTextArea notesArea = new JTextArea(notesBuf.toString(), 3, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 0;
        content.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(notesScroll, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Limitations
        JTextArea limitsArea = new JTextArea(limitsBuf.toString(), 3, 40);
        limitsArea.setLineWrap(true);
        limitsArea.setWrapStyleWord(true);
        JScrollPane limitsScroll = new JScrollPane(limitsArea);
        gbc.gridx = 0;
        content.add(new JLabel("Limitations:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(limitsScroll, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // References table
        String[] cols = { "Ref ID.", "Author", "Title", "Date" };
        DefaultTableModel refModel = new DefaultTableModel(cols, 0);
        for (Reference r : references) {
            String refId = r.getRefID() != null ? r.getRefID() : "";
            String rAuthor = r.getAuthor() != null ? r.getAuthor() : "";
            String rTitle = r.getTitle() != null ? r.getTitle().toString() : "";
            String rDate = r.getDate() != null ? r.getDate().toString() : "";
            refModel.addRow(new Object[] { refId, rAuthor, rTitle, rDate });
        }
        JTable refsTable = new JTable(refModel);
        JScrollPane refsScroll = new JScrollPane(refsTable);

        gbc.gridx = 0;
        content.add(new JLabel("References:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; // give table some vertical stretch
        content.add(refsScroll, gbc);
        gbc.gridy++;

        // page scroll
        JScrollPane outerScroll = new JScrollPane(content);
        panel.add(outerScroll, BorderLayout.CENTER);

        panel.revalidate();
        panel.repaint();
    }
}
