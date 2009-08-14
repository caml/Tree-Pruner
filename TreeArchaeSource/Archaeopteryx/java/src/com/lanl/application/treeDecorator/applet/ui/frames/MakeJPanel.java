package com.lanl.application.treeDecorator.applet.ui.frames;



import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;


public class MakeJPanel {
	
	protected static JPanel getFlowLayoutPanel(){
		JPanel p = new JPanel();
		p.setBackground( DecoratorColorSet.getBackgroundColor());
		p.setLayout( new FlowLayout());
		return p;
	}
	
	protected static JPanel getBorderLayoutPanel(){
		JPanel p = new JPanel();
		p.setBackground( DecoratorColorSet.getBackgroundColor());
		p.setLayout( new BorderLayout());
		return p;
	}
	
	protected static JPanel getGridLayoutPanel(int row, int col){
		JPanel p = new JPanel();
		p.setBackground( DecoratorColorSet.getBackgroundColor());
		p.setLayout( new GridLayout( row, col, 5, 5));
		return p;
	}
	
	protected static JPanel getGridBagLayoutPanel(){
		JPanel p = new JPanel();
		p.setBackground( DecoratorColorSet.getBackgroundColor());
		p.setLayout( new GridBagLayout());
		return p;
	}
}
