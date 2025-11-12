package uta.cse3310;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.io.File;

/**
 * Simple smoke test to verify that DataStore can create, save, and reload XML successfully.
 * This ensures the JAXB setup and file I/O logic both work.
 */
public class DataStoreSmokeTest {

    @Test
    void create_save_and_reload_xml_should_work() throws Exception {
        // Create a new DataStore instance
        DataStore ds = new DataStore();
        ds.createNew();  // make a minimal test configuration

        // Save it to a temporary XML file
        File tmp = ds.saveToTemp();

        // Check that file was written and is readable
        assertThat(tmp)
                .exists()
                .isFile()
                .canRead()
                .hasExtension("xml");

        // Open the saved file in a new DataStore instance
        DataStore ds2 = new DataStore();
        ds2.openFile(tmp.getAbsolutePath());

        // Assert that it was loaded correctly
        assertThat(ds2.valid).isTrue();
        assertThat(ds2.cfg).isNotNull();
        assertThat(ds2.cfg.getName()).isEqualTo("TestAircraft");

        // Clean up
        tmp.delete();
    }
}
