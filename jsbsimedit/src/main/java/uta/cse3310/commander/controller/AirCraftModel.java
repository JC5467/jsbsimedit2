package uta.cse3310.commander.controller;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AirCraftModel implements ActionListener {

	JFrame frame;
	JButton Save_XML;
	JButton SaveAs_XML;
	JButton Open_XML;
	JButton New_XML;

	public AirCraftModel(File XML_File) {
		// Frame name and look and feel
		frame = new JFrame("Aircraft Command");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
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
		// TabSubSystem TESTING ONLY NOT FINAL MAKE SURE YOU UPDATE THIS WHEN THE
		// TABSUBSYSTEM IS FINISHED IM TALKING TO YO, YEAH YOU DAVID, YOU LAZY PUNK YOU
		// ARE GONNA FORGET TO CHANGE THIS AND THEN GET CONFUSED WHY NOTHING WORKS I
		// SWEAR
		// TabController Test = new TabControler(XML_File);
		// frame.add(Test, BorderLayout.CENTER);

		// Action listeners
		Open_XML.addActionListener(this);

		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Save_XML) {

		}
		if (e.getSource() == SaveAs_XML) {

		}
		if (e.getSource() == Open_XML) {
			FileBrowser fb = new FileBrowser();
			if (fb.File_Found == true) {
				new AirCraftModel(fb.Chosen_file);
				frame.dispose(); // Close JSBSimcommand if file found
			}
		}
		if (e.getSource() == New_XML) {

		}
	}

}
