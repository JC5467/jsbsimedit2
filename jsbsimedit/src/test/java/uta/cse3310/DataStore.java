package uta.cse3310;

import generated.FdmConfig;
import generated.ExternalReactions;

import javax.xml.bind.*;
import java.io.*;
import java.nio.file.Files;

/**
 * Lightweight XML DataStore used by the testing group to load/save aircraft XML files.
 * This version is safe to live under src/test/java and can be used by AssertJ or smoke tests.
 */
public class DataStore {

    public String fileName = null;  // Path of currently loaded file
    public FdmConfig cfg = null;    // JAXB root object (XML model)
    public boolean valid = false;   // True if XML is valid and loaded
    public int version = 0;         // Increment when a new file is opened
    public boolean dirty = false;   // Set when changes occur

    private JAXBContext jaxbContext;

    public DataStore() {
        try {
            // Ensure the context points to the JAXB-generated classes under "generated"
            jaxbContext = JAXBContext.newInstance("generated");
        } catch (JAXBException e) {
            System.err.println("Error initializing JAXB context: " + e.getMessage());
        }
    }

    /**
     * Opens an XML file and loads it into cfg.
     */
    public void openFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.err.println("File not found: " + path);
                valid = false;
                return;
            }

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object root = unmarshaller.unmarshal(file);

            if (root instanceof FdmConfig) {
                cfg = (FdmConfig) root;
                fileName = path;
                valid = true;
                dirty = false;
                version++;
                System.out.println("Loaded aircraft XML successfully: " + path);
            } else {
                System.err.println("Unexpected root type: " + root.getClass().getName());
                valid = false;
            }

        } catch (Exception e) {
            System.err.println("Error opening XML: " + e.getMessage());
            valid = false;
        }
    }

    /**
     * Saves the current cfg to fileName or given path.
     */
    public void saveFile(String targetPath) {
        if (cfg == null) {
            System.err.println("No configuration loaded to save.");
            return;
        }

        File file = (targetPath != null) ? new File(targetPath) : new File(fileName != null ? fileName : "output.xml");

        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(cfg, file);

            fileName = file.getAbsolutePath();
            dirty = false;
            System.out.println("Saved aircraft XML successfully: " + fileName);

        } catch (Exception e) {
            System.err.println("Error saving XML: " + e.getMessage());
        }
    }

    /**
     * Creates a new, minimal configuration for testing.
     */
    public void createNew() {
        cfg = new FdmConfig();
        cfg.setName("TestAircraft");
        cfg.setVersion("1.0");

        // Add at least one section so tabs/tests have something to load
        cfg.setExternalReactions(new ExternalReactions());

        valid = true;
        dirty = true;
        version++;
        System.out.println("Created new test configuration.");
    }

    /**
     * Quick utility for test cases to write XML to a temp file.
     */
    public File saveToTemp() throws Exception {
        File temp = Files.createTempFile("jsbsim-", ".xml").toFile();
        saveFile(temp.getAbsolutePath());
        return temp;
    }

    public void setDirty() {
        this.dirty = true;
    }
}
