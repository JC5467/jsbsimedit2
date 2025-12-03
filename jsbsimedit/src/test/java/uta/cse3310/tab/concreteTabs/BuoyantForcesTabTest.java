package uta.cse3310.tab.concreteTabs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uta.cse3310.dataStore;
import uta.cse3310.tabFrame;

// JAXB Generated Mocks
import generated.BuoyantForces;
import generated.FdmConfig;
import generated.GasCell;
import generated.Length;
import generated.Ballonet;
import generated.Location;
import generated.LengthUnit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Use MockitoJUnitRunner for automatic mock creation
@RunWith(MockitoJUnitRunner.class)
public class BuoyantForcesTabTest {

    private BuoyantForcesTab buoyantForcesTab;

    // Core Dependencies (Real DataStore for dirty flag checking)
    private dataStore realDataStore;
    @Mock private tabFrame mockTabFrame; 

    // JAXB Structure Mocks
    @Mock private FdmConfig mockFdmConfig;
    @Mock private BuoyantForces mockBuoyantForces;
    @Mock private GasCell mockGasCell;
    @Mock private Ballonet mockBallonet;
    @Mock private Location mockLocation;
    @Mock private LengthUnit mockLengthUnit;

    @Before
    public void setUp() {
        // 1. Initialize Real DataStore with Mock FdmConfig
        realDataStore = new dataStore(mockTabFrame);
        realDataStore.cfg = mockFdmConfig;
        
        // 2. Mock the deep structure access
        when(mockFdmConfig.getBuoyantForces()).thenReturn(mockBuoyantForces);
        
        // 3. Initialize the Tab
        buoyantForcesTab = new BuoyantForcesTab(mockTabFrame, realDataStore, "Buoyant Forces");
    }

    // ********************T1: Load Data Structure Tests***********************

    @Test
    public void testLoadDataNoBuoyantForcesSection() {
        // Arrange: Simulate FdmConfig having no <buoyant_forces> section
        when(mockFdmConfig.getBuoyantForces()).thenReturn(null);

        buoyantForcesTab.loadData();

        // Assert 1: Verify a new BuoyantForces object was created and set
        verify(mockFdmConfig, times(1)).setBuoyantForces(any(BuoyantForces.class));

        // Assert 2: Verify the panel now contains the main layout component
        Component mainPanel = buoyantForcesTab.panel.getComponent(0);
        assertTrue("Panel should contain the main JPanel after loading.", mainPanel instanceof JPanel);
    }
    
    @Test
    public void testLoadDataPopulatesGasCellList() {
        // Arrange: Mock two GasCell objects
        GasCell mockCell2 = mock(GasCell.class);
        when(mockGasCell.getType()).thenReturn("HELIUM");
        when(mockCell2.getType()).thenReturn("HYDROGEN");

        List<GasCell> gasCells = Arrays.asList(mockGasCell, mockCell2);
        when(mockBuoyantForces.getGasCell()).thenReturn(gasCells);
        
        buoyantForcesTab.loadData();
        
        // Assert 1: GasCell list model is populated correctly
        DefaultListModel<String> gasCellModel = getGasCellModel(buoyantForcesTab);
        assertEquals("Should find 2 gas cells.", 2, gasCellModel.getSize());
        assertTrue(gasCellModel.getElementAt(0).contains("HELIUM"));
        assertTrue(gasCellModel.getElementAt(1).contains("HYDROGEN"));

        // Assert 2: First gas cell should be selected
        JList<String> gasCellList = getGasCellList(buoyantForcesTab);
        assertEquals("The first gas cell should be selected by default.", 0, gasCellList.getSelectedIndex());
    }
    
    // T2: ****************** Add/Delete Tests ***********************

    @Test
    public void testAddGasCellUpdatesModelAndSetsDirty() {
        // Arrange
        List<GasCell> mockList = new ArrayList<>();
        when(mockBuoyantForces.getGasCell()).thenReturn(mockList);

        buoyantForcesTab.addGasCell(); 

        // Assert 1: Verify a new GasCell was added to the mock list
        assertEquals("One GasCell should be added to the mock list.", 1, mockList.size());
        
        // Assert 2: Verify dirty flag is set
        assertTrue("Data store must be marked dirty after addition.", realDataStore.dirty);
    
    }
    
    // ****************************T3: Calculation Test ********************************
    
    @Test
    public void testMakeDetailsCalculatesVolumeAndBuoyancy() {
    // Arrange: Mock the object and its radius properties
    GasCell mockCell = mock(GasCell.class);

    // 1. Mock the JAXB Value Holder objects (the ones with the getValue() method)
    // We assume the object returned by getXRadius() is the one with getValue().
    // We will call this object 'mockRadiusValueHolder' to distinguish it.
    
    // NOTE: If LengthUnit is the *Value Holder* class (not the enum), then your original
    // stubbing syntax is correct, but the issue is simply that you forgot to stub xRad.
    
    // Let's stick to the simplest interpretation that works:
    
    // Mock the radius value holders and explicitly set their values
    // We use the mock(LengthUnit.class) because that's what getXRadius() returns.
    Length xRadHolder = mock(Length.class);
    when(xRadHolder.getValue()).thenReturn(2.0); // <<<<<< ADDED THE STUB
    
    Length yRadHolder = mock(Length.class);
    when(yRadHolder.getValue()).thenReturn(3.0); 
    
    Length zRadHolder = mock(Length.class);
    when(zRadHolder.getValue()).thenReturn(4.0); 

    // 2. Stub the GasCell to return the mock holders
    when(mockCell.getType()).thenReturn("AIR");
    when(mockCell.getFullness()).thenReturn(0.5);
    when(mockCell.getLocation()).thenReturn(mock(Location.class));
    
    when(mockCell.getXRadius()).thenReturn(xRadHolder);
    when(mockCell.getYRadius()).thenReturn(yRadHolder);
    when(mockCell.getZRadius()).thenReturn(zRadHolder);
    
    // 3. Calculation
    double expectedVolume = 4.0 / 3.0 * Math.PI * 2.0 * 3.0 * 4.0;
    double expectedBuoyancy = expectedVolume * 0.5;

    // Act
    String details = buoyantForcesTab.makeDetails("Test Gas Cell", mockCell);

    // Assert 1: Verify Volume calculation is present and correct
    assertTrue(details.contains("Approx volume: " + String.format("%.5f", expectedVolume)));
    
    // Assert 2: Verify Buoyancy calculation is present and correct
    assertTrue(details.contains("Buoyancy: " + String.format("%.5f", expectedBuoyancy)));
}
    
    // Helper Methods to access private fields for testing

    @SuppressWarnings("unchecked")
    private DefaultListModel<String> getGasCellModel(BuoyantForcesTab tab) {
        
        try {
            java.lang.reflect.Field field = BuoyantForcesTab.class.getDeclaredField("gasCellModel");
            field.setAccessible(true);
            return (DefaultListModel<String>) field.get(tab);
        } catch (Exception e) {
            throw new RuntimeException("Could not access gasCellModel field.", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private JList<String> getGasCellList(BuoyantForcesTab tab) {
        try {
            java.lang.reflect.Field field = BuoyantForcesTab.class.getDeclaredField("gasCellList");
            field.setAccessible(true);
            return (JList<String>) field.get(tab);
        } catch (Exception e) {
            throw new RuntimeException("Could not access gasCellList field.", e);
        }
    }
}

