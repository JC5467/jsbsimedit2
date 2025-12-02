package uta.cse3310;

import uta.cse3310.tabFrame;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.UnmarshalException;

import generated.FdmConfig;

import java.io.File;
import java.io.FileNotFoundException; // Import must be present

public class dataStore {
    public Boolean valid; // true if the file is usable

    public String fileName; // the name of the file in memory

    public Integer version;
    // incremented each time a file is read. users should
    // check the version and decide if they need to refresh
    // the data they have displayed to the user

    public FdmConfig cfg;
    public tabFrame tf;

    // the datastore is dirty when it has been changed by a tab, and
    // possibly should be written to the disk
    public boolean dirty;

    public void setDirty() {
        dirty = true;
    }

    /**
     * Save current cfg to the specified file using JAXB marshalling.
     */
    public void saveToFile(File f) throws JAXBException {
        if (cfg == null)
            throw new JAXBException("No configuration loaded to save.");
        JAXBContext jc = JAXBContext.newInstance("generated");
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(cfg, f);
        fileName = f.getPath();
        clearDirty();
    }

    public void clearDirty() {
        dirty = false;
    }

    public dataStore(tabFrame TF) {
        valid = false;
        fileName = "";
        version = 0;
        tf = TF;
        dirty = false;
    }

    public void openFile(File f) {
        // right now, there is a defect in that the fileBrowser only
        // processes one event. so you can only open one file, you cannot open another
        // one. i am leaving that defect to someone else.
        fileName = f.getPath();

        valid = false;
        FdmConfig tempCfg = null;
        String errorMessage = null;

        // read it in, convert to java
        try {
            // This is the line that creates the file object, which is fine,
            // but the unmarshal call below is what throws the underlying exception.
            File file = new File(fileName);

            JAXBContext jc = JAXBContext.newInstance("generated");
            Unmarshaller um = jc.createUnmarshaller();

            // This is the file access operation, which can cause FileNotFoundException
            tempCfg = (FdmConfig) um.unmarshal(file);

            // --- SUCCESS PATH ---
            // If unmarshalling succeeds, update the main state variables
            cfg = tempCfg;
            version = version + 1;
            valid = true;
            dirty = false;

            if (tf != null) {
                tf.dataLoaded();
            }

        } catch (UnmarshalException e) {
            // CATCH 2: Catches the specific XML structure/parsing error (e.g., malformed
            // XML)
            errorMessage = "XML Parsing Error: The file " + f.getName() + " is malformed or invalid.";
            System.err.println("JAXB PARSE ERROR: " + errorMessage);

            // Print the linked SAX exception message for more detail
            if (e.getLinkedException() != null) {
                System.err.println("Linked Exception Detail: " + e.getLinkedException().getMessage());
            }

        } catch (JAXBException e) {
            // CATCH 3: Catches other JAXB issues (e.g., context setup errors)
            errorMessage = "A general JAXB error occurred during loading: " + e.getMessage();
            System.err.println("JAXB ERROR: " + errorMessage);

        } catch (Exception e) {
            // CATCH 4: Catches any other runtime exceptions that weren't expected
            errorMessage = "An unexpected error occurred during file processing: " + e.getClass().getSimpleName() + ": "
                    + e.getMessage();
            System.err.println("UNEXPECTED ERROR: " + errorMessage);

        } finally {
            // --- Post-Error Handling ---
            if (errorMessage != null) {
                // If any error occurred, notify the UI (safely checking for null tf)
                if (tf != null) {
                    tf.showError(errorMessage);
                }
            }
        }
    }

}