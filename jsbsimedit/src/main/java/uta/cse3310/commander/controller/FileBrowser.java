package uta.cse3310.commander.controller;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;

public class FileBrowser {

	JFileChooser fc;
	public File Chosen_file;
	public boolean File_Found;

	public File getFileFound() {
		return Chosen_file;
	}

	public FileBrowser() {

		try {
			// Set the look and feel to the system's default
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JButton Open = new JButton();
		fc = new JFileChooser();
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setDialogTitle("Open XML");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files (*.xml)", "xml");
		fc.setFileFilter(filter);

		File_Found = false;
		if (fc.showOpenDialog(Open) == JFileChooser.APPROVE_OPTION) {
			Chosen_file = fc.getSelectedFile();
			File_Found = true;
		}
	}
}
