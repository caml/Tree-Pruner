package gov.lanl.application.treeDecorator.applet.ui.frames;



import gov.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;

import javax.swing.JButton;
import javax.swing.JRadioButton;



public class MakeRadioButton {
	protected static JRadioButton getRadioButton( String string){
		JRadioButton button = new JRadioButton( string);
		button.setBackground(  DecoratorColorSet.getBackgroundColor());
		return button;
	}
}
