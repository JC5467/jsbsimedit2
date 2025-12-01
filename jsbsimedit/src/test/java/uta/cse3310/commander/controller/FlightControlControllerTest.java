package uta.cse3310.commander.controller;

import org.junit.Test;
import static org.junit.Assert.*;

import uta.cse3310.commander.model.FlightControlModel.NodeType;

/**
 * Unit tests for the core connection validation logic in
 * FlightControlController, using JUnit 4.
 * *
 */
public class FlightControlControllerTest {

    // --- Valid Connections ---

    @Test
    public void testValidConnection_SourceToGain() {
        // A block with outputs (Source) to a block with inputs (Gain).
        assertTrue("Should allow connection from SOURCE to GAIN.",
                FlightControlController.isValidConnection(NodeType.SOURCE, NodeType.GAIN));
    }

    @Test
    public void testValidConnection_SummerToFilter() {
        // A standard valid connection between two processing blocks.
        assertTrue("Should allow connection from SUMMER to FILTER.",
                FlightControlController.isValidConnection(NodeType.SUMMER, NodeType.FILTER));
    }

    @Test
    public void testValidConnection_GainToDestination() {
        // Connecting a processing block to the terminal point.
        assertTrue("Should allow connection from GAIN to DESTINATION.",
                FlightControlController.isValidConnection(NodeType.GAIN, NodeType.DESTINATION));
    }

    // --- Invalid Connections (General Rules) ---
    // --- SYS-FLC-080---
    @Test
    public void testInvalidConnection_BlockToSelf() {
        // Connecting any block to itself should fail.
        assertFalse("Should prevent connecting a block to itself.",
                FlightControlController.isValidConnection(NodeType.PID, NodeType.PID));
    }

    // --- SYS-FLC-081---
    @Test
    public void testInvalidConnection_DestinationAsSource() {
        // DESTINATION blocks cannot output/be the source of a connection.
        assertFalse("Should prevent DESTINATION from being a source.",
                FlightControlController.isValidConnection(NodeType.DESTINATION, NodeType.GAIN));
    }

    @Test
    public void testInvalidConnection_SourceAsDestination() {
        // SOURCE blocks cannot receive inputs/be the destination of a connection.
        assertFalse("Should prevent SOURCE from being a destination.",
                FlightControlController.isValidConnection(NodeType.GAIN, NodeType.SOURCE));
    }

    // --- Invalid Connections (Business Rule) ---

    @Test
    public void testInvalidConnection_FilterToDestination() {
        // Specific business rule: FILTER cannot connect directly to DESTINATION.
        assertFalse("Should prevent FILTER from connecting directly to DESTINATION (business rule).",
                FlightControlController.isValidConnection(NodeType.FILTER, NodeType.DESTINATION));
    }
}