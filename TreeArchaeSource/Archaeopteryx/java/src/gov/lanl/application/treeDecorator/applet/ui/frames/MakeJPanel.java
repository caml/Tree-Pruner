package gov.lanl.application.treeDecorator.applet.ui.frames;



import gov.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;
import gov.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;



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
	
	protected static JPanel getInfoGridLayoutPanel(int row, int col,boolean withBorder){
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground( DecoratorColorSet.getBackgroundColor());
		infoPanel.setLayout( new GridLayout( row, col, 5, 5));
		
		if(withBorder){
			infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
							BorderFactory.createLoweredBevelBorder()),DecoratorUIConstants.INFORMATION.getName()));
		}
		return infoPanel;
	}
}
