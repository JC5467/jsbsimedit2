package uta.cse3310;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit4 tests for verifying that the General Information
 * section loads correctly from XML.
 */
public class GeneralInformationSubsystemTest {

    @Test
    public void loadValidXml_populatesFieldsCorrectly() {
        // Arrange
        GeneralInformationSubsystem subsystem = new GeneralInformationSubsystem();

        // Act
        // adjust the path to wherever your test XML lives
        subsystem.loadFromXml("src/test/resources/test_data/general_info_valid.xml");

        // Assert – these should match the values inside your XML
        assertEquals("2024-11-17", subsystem.getCreationDate());   // example
        assertEquals("1.0",          subsystem.getVersion());
        assertEquals("UTASTUDENTS",  subsystem.getCopyright());
        assertEquals("UNCLASSIFIED", subsystem.getSensitivity());
        assertEquals("Models an F-16A Block-32 (Basic US configuration)",
                     subsystem.getDescription());
    }

    @Test(expected = RuntimeException.class)
    public void loadInvalidXml_throwsRuntimeException() {
        // Arrange
        GeneralInformationSubsystem subsystem = new GeneralInformationSubsystem();

        // Act + Assert (JUnit4 style)
        subsystem.loadFromXml("src/test/resources/test_data/invalid.xml");
    }
}
