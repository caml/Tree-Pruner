package gov.lanl.application.treeDecorator.applet.ui.frames;




import gov.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolTip;



public class MakeButton {	
	
	
	protected static JButton getButton( String title){
		JButton button = new JButton( title);
		button.setBackground( DecoratorColorSet.getBackgroundColor());
		return button;
	}
	
	protected static JButton getToolTipButton( String title, String toolTipText){
		JButton button = getButton( title);
		button.setToolTipText( toolTipText);
		return button;
	}
	
	//Toggle buttons
	protected static JToggleButton getJToggleButton( String title){
		JToggleButton button = new JToggleButton( title);
		button.setBackground(  DecoratorColorSet.getBackgroundColor());
		return button;
	}
}
