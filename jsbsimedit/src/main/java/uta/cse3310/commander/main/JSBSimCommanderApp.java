package uta.cse3310.commander.main;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import uta.cse3310.commander.controller.*;

public class JSBSimCommanderApp implements ActionListener{
	JButton Open_XML;
	JButton Exit;
	JFrame frame;
	
	public JSBSimCommanderApp(){
	//Frame
		frame = new JFrame("JSBsim Command");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,150);
	//Buttons
		Open_XML = new JButton("Open");
		Exit = new JButton("Exit");
	//Toolbar
		JToolBar toolbar = new JToolBar();
		toolbar.add(Open_XML);
		toolbar.add(Exit);
		toolbar.setFloatable(false);
		frame.setLayout(new BorderLayout());
		frame.add(toolbar, BorderLayout.NORTH);
	//Button ToolTips
		Open_XML.setToolTipText("Open XML file");
		Exit.setToolTipText("Exit Program");
	//Button ActionListeners
		Open_XML.addActionListener(this);
		Exit.addActionListener(this);
		
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == Open_XML) {
			FileBrowser fb = new FileBrowser();
			if(fb.File_Found == true) { 
				new AirCraftModel(fb.Chosen_file); //Close JSBSimcommand if file found and open AirCraft Command
				frame.dispose(); 
			}
		}
		if(e.getSource() == Exit) {
			System.exit(0); //Sometimes you just gotta give up
		}
		
	}

}