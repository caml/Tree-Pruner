package com.lanl.application.treePruner.applet;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

import javax.swing.*;

/**
 *
 *
 * @version 
 */
public class TreePrunerCommunicationMessageWarningWindow 
{
    private JFrame      jf;
   
//---------------------------------------------------------------------//      
    /** Creates new PriorityHelpGui */
    public TreePrunerCommunicationMessageWarningWindow() 
    {
    	String title = "Your Action is being performed Please wait...";
    	String msg = "                                                                                                                ";
    	JLabel label = new JLabel(msg);
        Container rx = makeFrame(title, 40, 100, 300, 200);
        rx.add(label,BorderLayout.SOUTH);
       
        jf.pack();
        jf.setVisible(true);                
    }
    
    public void close(){
    	jf.setVisible(false);
    	jf.dispose();
    }
    
//---------------------------------------------------------------------//         
	public Container makeFrame( String title, int width, int height, int xPos, int yPos){
	    jf = new JFrame( title);
//		jf.setSize(570, 440
//	    jf.setLocation( xPos, yPos);
//	    jf.setLocationRelativeTo(null);
	    centerFrame();
		jf.addWindowListener( new WindowAdapter() {
				// use System.exit(0) to exit the entire application - instead of f.dispose()
				public void windowClosing( WindowEvent e) { System.exit( 0); } 
		});
	    
		jf.addWindowListener( new WindowAdapter() {
			// use System.exit(0) to exit the entire application - instead of f.dispose()
			public void windowClosing( WindowEvent e) { jf.dispose(); } 
		});
		
		return jf.getContentPane();
	}

	public void  centerFrame(){
		 Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		    
		    
		    int w = jf.getSize().width;
		    int h = jf.getSize().height;
		    int x = (dim.width-w)/2;
		    int y = (dim.height-h)/2;
		    
		    
		    jf.setLocation((x-200), (y-15));
	}
//---------------------------------------------------------------------//
} 