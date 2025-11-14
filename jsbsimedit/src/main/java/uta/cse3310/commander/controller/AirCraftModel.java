package uta.cse3310.commander.controller;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import uta.cse3310.dataStore;

public class AirCraftModel implements ActionListener {

	JFrame frame;
	JButton Save_XML;
	JButton SaveAs_XML;
	JButton Open_XML;
	JButton New_XML;

	dataStore DS;

	public AirCraftModel(dataStore ds) {
		DS = ds;

		// Frame name and look and feel
		frame = new JFrame("Aircraft Commander");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		// Buttons
		Save_XML = new JButton("Save");
		SaveAs_XML = new JButton("Save As...");
		Open_XML = new JButton("Open");
		New_XML = new JButton("New");
		// Toolbar and Button adds
		JToolBar toolbar = new JToolBar();
		toolbar.add(Save_XML);
		toolbar.add(SaveAs_XML);
		toolbar.add(Open_XML);
		toolbar.add(New_XML);
		toolbar.setFloatable(false);
		frame.setLayout(new BorderLayout());
		frame.add(toolbar, BorderLayout.NORTH);

		// Action listeners
		Save_XML.addActionListener(this);
		SaveAs_XML.addActionListener(this);
		Open_XML.addActionListener(this);
		New_XML.addActionListener(this);

		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Save_XML) {
			// Save to current file
			if (DS == null || DS.cfg == null) {
				JOptionPane.showMessageDialog(frame, "No data loaded to save.");
				return;
			}
			if (DS.fileName != null && !DS.fileName.isEmpty()) {
				try {
					DS.saveToFile(new File(DS.fileName));
					JOptionPane.showMessageDialog(frame, "Saved to " + DS.fileName);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, "Save failed: " + ex.getMessage());
				}
			} else {
				saveAsDialog();
			}
		}
		if (e.getSource() == SaveAs_XML) {
			saveAsDialog();
		}
		if (e.getSource() == Open_XML) {
			FileBrowser fb = new FileBrowser();
			if (fb.File_Found == true) {
				// load new file into data store
				DS.openFile(fb.Chosen_file);
				// reopen Aircraft Commander to refresh state
				new AirCraftModel(DS);
				frame.dispose(); // Close current Aircraft Commander
			}
		}
		if (e.getSource() == New_XML) {
			try {
				generated.FdmConfig cfg = new generated.FdmConfig();
				// initialize required sections...
				cfg.setFileheader(new generated.Fileheader());
				cfg.setMetrics(new generated.Metrics());
				cfg.setMassBalance(new generated.MassBalance());
				cfg.setGroundReactions(new generated.GroundReactions());
				cfg.setExternalReactions(new generated.ExternalReactions());
				cfg.setPropulsion(new generated.Propulsion());
				cfg.setAerodynamics(new generated.Aerodynamics());
				cfg.setName("untitled");
				cfg.setVersion("0.1");

				DS.cfg = cfg;
				DS.fileName = "";
				DS.setDirty();

				new AirCraftModel(DS);
				frame.dispose();

				JOptionPane.showMessageDialog(null,
						"New aircraft created. Use Save As to write to disk.");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(frame,
						"Failed to create new aircraft: " + ex.getMessage());
			}
		}

	}

	private void saveAsDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save XML As");
		chooser.setSelectedFile(new File(DS.fileName != null && !DS.fileName.isEmpty() ? DS.fileName : "untitled.xml"));
		int userSelection = chooser.showSaveDialog(frame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = chooser.getSelectedFile();
			// ensure .xml extension
			if (!fileToSave.getName().toLowerCase().endsWith(".xml")) {
				fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xml");
			}
			try {
				DS.saveToFile(fileToSave);
				DS.fileName = fileToSave.getPath();
				JOptionPane.showMessageDialog(frame, "Saved to " + DS.fileName);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(frame, "Save As failed: " + ex.getMessage());
			}
		}
	}

}
