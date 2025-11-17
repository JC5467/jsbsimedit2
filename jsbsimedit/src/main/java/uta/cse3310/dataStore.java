package uta.cse3310;

import uta.cse3310.tabFrame;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Marshaller;

import generated.FdmConfig;

import java.io.File;

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
        System.out.println("setDirty");
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
        System.out.println("setUndirty");
        dirty = false;
    }

    public dataStore(tabFrame TF) {
        System.out.println("in the constructor for dataStore");
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
        System.out.println("in openFile");
        System.out.println("the file name is " + f);
        fileName = f.getPath();

        // read it in, convert to java
        try {

            File file = new File(fileName);
            // JAXBContext jaxbContext = JAXBContext.newInstance(FdmConfig.class);

            // Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            // FdmConfig cfg = (FdmConfig) jaxbUnmarshaller.unmarshal(file);

            // System.out.println(cfg.getName());

            JAXBContext jc = JAXBContext.newInstance("generated");

            Unmarshaller um = jc.createUnmarshaller();
            cfg = (FdmConfig) um.unmarshal(file);

            /*
             * THIS WAS MOVED inside the try scope.
             * no need to set outside of try because it was
             * set at the begining of method.
             */
            // set flags so the using tabs can know if the data has changed
            version = version + 1;
            valid = true;
            dirty = false;
            tf.dataLoaded();

            /*
             * eventually, delete this stuff. just not now
             * System.out.println(cfg);
             * System.out.println(cfg.getFileheader().getCopyright());
             * System.out.println(cfg.getFileheader().getVersion());
             * System.out.println(cfg.getAerodynamics().getAxis().get(0).getName());
             * System.out.println(cfg.getAerodynamics().getAxis().get(0).
             * getDocumentationOrFunction());
             * System.out.println(cfg.getAerodynamics().getAxis().get(0).getClass());
             */

            // Marshaller m = jc.createMarshaller();
            // m.setProperty("jaxb.formatted.output", true);
            // m.marshal(cfg, System.out);

        } catch (JAXBException e) {
            System.err.println("ERROR: Failed to load or parse XML file");
            e.printStackTrace();
        }
    }

}
