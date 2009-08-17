package com.lanl.application.treeDecorator.applet.ui.frames;


import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.Style;

import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;

public class MakeJLabel {
	
	protected static JLabel getJLabel( String text, boolean withBorder) {
	    JLabel label = new JLabel( text);
	    Font currentFont = label.getFont();
	    float newSize = 18.f;
	    Font newFont = currentFont.deriveFont(newSize);
	    label.setFont(newFont);
	    
	    if( withBorder){
	    	Border border = LineBorder.createGrayLineBorder();
	    	label.setBorder(border);
	    }
	    
	    return label;
	}
	
	protected static JLabel getJLabel( String text, boolean withBorder, String center) {
	    JLabel label = new JLabel( text, JLabel.CENTER);
	    Font currentFont = label.getFont();
	    float newSize = 22.f;
	    Font newFont = currentFont.deriveFont(newSize);
	    label.setFont(newFont);
	    
	    if( withBorder){
	    	Border border = LineBorder.createGrayLineBorder();
	    	label.setBorder(border);
	    }
	    
	    return label;
	}
	
	protected static JLabel getInfoJLabel( String text,boolean isColorRed) {
	    JLabel label = new JLabel( text, JLabel.CENTER);
	    Font currentFont = label.getFont();
	    float newSize = 12.f;
	    Font newFont = currentFont.deriveFont(newSize);
	    label.setFont(newFont);
	    if(isColorRed){
	    	label.setForeground(DecoratorColorSet.getRed());
	    }
	    else label.setForeground(DecoratorColorSet.getForestGreen());
	    return label;
	}

	
}
