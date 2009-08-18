package com.lanl.application.treeDecorator.applet.ui.frames;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class MakeFrame {
	protected static JFrame getFrame( String title, int width, int height, int xPos, int yPos){
	  final JFrame  f = new JFrame( title);
	    f.setSize(width, height);
		f.setLocation( xPos, yPos);
	    
	    /*f.addWindowListener( new WindowAdapter() {
			
			public void windowClosing( WindowEvent e) { 
				f.dispose(); 
			} 
		});*/
		
		return f;
	}
}
