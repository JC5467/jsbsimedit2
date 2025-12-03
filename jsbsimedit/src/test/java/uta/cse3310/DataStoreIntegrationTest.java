package uta.cse3310;

// Import the classes we are testing or mocking
//import uta.cse3310.dataStore;
//import uta.cse3310.tabFrame;

// JAXB, IO, and JUnit imports
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

// Mockito imports
import org.mockito.Mockito;

public class DataStoreIntegrationTest {

    private static File testXmlFile;
    private static File malformedXmlFile;

    /**
     * Sets up the environment by creating two temporary XML files:
     * 1. A well-formed file for successful load testing.
     * 2. A malformed file for failure/robustness testing.
     */
    @BeforeClass
    public static void setupTestFiles() throws IOException {
        // --- 1. SETUP FOR SUCCESS TEST (Well-formed XML) ---
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fdm_config name=\"test_aircraft\">\n" +
                " <fileheader version=\"1.0\"/>\n" +
                " <aerodynamics>\n" +
                " <axis name=\"ROLL\"/>\n" +
                " </aerodynamics>\n" +
                "</fdm_config>";

        // Create the file in the system temp directory
        testXmlFile = File.createTempFile("jsbsim_test", ".xml");
        try (FileWriter writer = new FileWriter(testXmlFile)) {
            writer.write(xmlContent);
        }
        testXmlFile.deleteOnExit();

        // --- 2. SETUP FOR FAILURE TEST (Malformed XML: missing closing tag) ---
        String invalidContent = "<fdm_config name=\"malformed\">" +
        // Missing the closing tag for fdm_config or fileheader
                "<fileheader version=\"1.0\">" +
                "<aerodynamics/>";

        malformedXmlFile = File.createTempFile("jsbsim_invalid", ".xml");
        try (FileWriter writer = new FileWriter(malformedXmlFile)) {
            writer.write(invalidContent);
        }
        malformedXmlFile.deleteOnExit();
    }

    /**
     * Test case SYS-MEN-001: Verifies successful loading of a valid XML file.
     * Checks data store state changes and UI notification.
     */
    @Test
    public void testOpenFile_Success() {
        // Create a mock dependency for the tabFrame
        tabFrame mockTabFrame = Mockito.mock(tabFrame.class);
        dataStore ds = new dataStore(mockTabFrame);

        // EXECUTE: Load the temporary, well-formed file
        ds.openFile(testXmlFile);

        // VERIFY STATE CHANGES
        assertTrue("SYS-MEN-001: DataStore should be valid after successful load", ds.valid);
        assertFalse("SYS-MEN-001: DataStore should not be dirty after load", ds.dirty);
        assertTrue("SYS-MEN-001: Version should be incremented to 1", ds.version == 1);
        assertNotNull("SYS-MEN-001: FdmConfig object (ds.cfg) should not be null", ds.cfg);

        // VERIFY INTERACTION (ensures UI was notified)
        Mockito.verify(mockTabFrame, Mockito.times(1)).dataLoaded();
    }

    /**
     * Test case SYS-MEN-002: Verifies failure when trying to load a malformed XML
     * file.
     * Checks that the data store state remains clean and valid data isn't
     * overwritten.
     */
    @Test
    public void testOpenFile_FailureOnMalformedXML() {
        tabFrame mockTabFrame = Mockito.mock(tabFrame.class);
        dataStore ds = new dataStore(mockTabFrame);

        // Ensure initial state is clean
        assertFalse(ds.valid);
        assertTrue(ds.version == 0);

        // EXECUTE: Attempt to load the malformed file. This should trigger the
        // JAXBException
        ds.openFile(malformedXmlFile);

        // VERIFY STATE CHANGES (should be unchanged from initial state)
        assertFalse("SYS-MEN-002: DataStore should NOT be valid after failed load", ds.valid);
        assertTrue("SYS-MEN-002: Version should remain 0 after failed load", ds.version == 0);
        assertNull("SYS-MEN-002: FdmConfig object should be null after failed load", ds.cfg);

        // VERIFY INTERACTION (ensures UI was NOT notified)
        Mockito.verify(mockTabFrame, Mockito.never()).dataLoaded();
    }
}