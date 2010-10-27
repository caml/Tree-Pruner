package com.lanl.application.treeDecorator.applet.ui.frames;



import javax.swing.JButton;
import javax.swing.JRadioButton;

import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;


public class MakeRadioButton {
	protected static JRadioButton getRadioButton( String string){
		JRadioButton button = new JRadioButton( string);
		button.setBackground(  DecoratorColorSet.getBackgroundColor());
		return button;
	}
}
