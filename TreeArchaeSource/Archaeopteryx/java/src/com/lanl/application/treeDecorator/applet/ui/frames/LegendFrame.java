package com.lanl.application.treeDecorator.applet.ui.frames;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.lanl.application.TPTD.applet.AppletParams;
import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;
import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorShapes;
import com.lanl.application.treeDecorator.dataStructures.DecorateObject;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

public class LegendFrame {

	int height = 120;
	int width = 360;
	public JFrame _frame;
	GridBagConstraints gbContraints;
	public JPanel countryJPanel,yearJPanel,ahaJPanel,anaJPanel,hostJPanel;
	JScrollPane countryScrollPanel,yearScrollPanel,ahaScrollPanel,anaScrollPanel,hostScrollPanel;
	JPanel countryShowHidePanel,yearShowHidePanel,ahaShowHidePanel,anaShowHidePanel,hostShowHidePanel; 
	boolean showingCountry= false,showingYear= false,showingAHA= false,showingANA= false,showingHost = false;
	public LegendFrame(){
		try{
			String laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel( laf);
		}
		catch (Exception e){
			System.err.println( "ManualDecorateFrame(): Error setting LookAndFeel - using default" );
			e.printStackTrace();
		}
		makeLegendFrame();
		_frame.setResizable(false);
		_frame.setVisible(false);
		_frame.pack();
	}
	
	public void makeLegendFrame(){
		_frame = MakeFrame.getFrame( DecoratorUIConstants.TREE_DECORATOR_FRAME_HEADER.getName(), 360, 900, 400, 0);
		
		_frame.addWindowListener(new WindowAdapter(){
			public void windowClosing( final WindowEvent e ) {
				_frame.setVisible(false);
			}
		});
		Container content = _frame.getContentPane();
		content.setLayout( new GridBagLayout());
		content.setBackground(DecoratorColorSet.getBackgroundColor());
		
		gbContraints = new GridBagConstraints();
		gbContraints.anchor = GridBagConstraints.NORTH;
		gbContraints.insets = new Insets(10,10,10,10);
		gbContraints.gridx = 0;
		gbContraints.gridy = 0;
		gbContraints.gridwidth = 3;
		gbContraints.weightx = 0;
		JLabel title = MakeJLabel.getJLabel(DecoratorUIConstants.LEGEND_TITLE.getName(), false, "north");
		title.setAlignmentX(50);
		content.add( title, gbContraints);
		
		gbContraints.anchor = GridBagConstraints.NORTHWEST;
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 1;
		
		
		countryJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.COUNTRY);
		countryShowHidePanel = new MakeShowHidePanel(DecoratorUIConstants.SHOW_COUNTRY);
		content.add(countryShowHidePanel, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 2;
		countryScrollPanel = new JScrollPane(countryJPanel);
		countryScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(countryScrollPanel, gbContraints);
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 3;
		
		yearJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.YEAR);
		yearShowHidePanel = new MakeShowHidePanel(DecoratorUIConstants.SHOW_YEAR);
		content.add(yearShowHidePanel, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 4;
		yearScrollPanel = new JScrollPane(yearJPanel);
		yearScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(yearScrollPanel, gbContraints);
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 5;
		
		hostJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.HOST_SPECIES);
		hostShowHidePanel = new MakeShowHidePanel(DecoratorUIConstants.SHOW_HOST_SPECIES);
		content.add(hostShowHidePanel, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 6;
		hostScrollPanel = new JScrollPane(hostJPanel);
		hostScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(hostScrollPanel, gbContraints);
		
		
		ahaJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.A_HA_SUBTYPE);
		anaJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.A_NA_SYBTYPE);
		
		if(AppletParams.isFluTypeA){
			gbContraints.insets = new Insets(0,0,0,0);
			gbContraints.gridx = 0;
			gbContraints.gridy = 7;
			
			ahaShowHidePanel = new MakeShowHidePanel(DecoratorUIConstants.SHOW_A_HA_SUBTYPE);
			content.add(ahaShowHidePanel, gbContraints);
			gbContraints.insets = new Insets(0,0,10,0);
			gbContraints.gridx = 0;
			gbContraints.gridy = 8;
			ahaScrollPanel = new JScrollPane(ahaJPanel);
			ahaScrollPanel.setPreferredSize(new Dimension(width,height));
			content.add(ahaScrollPanel, gbContraints);
			
			
			gbContraints.insets = new Insets(0,0,0,0);
			gbContraints.gridx = 0;
			gbContraints.gridy = 9;
			
			anaShowHidePanel = new MakeShowHidePanel(DecoratorUIConstants.SHOW_A_NA_SUBTYPE);
			content.add(anaShowHidePanel, gbContraints);
			gbContraints.insets = new Insets(0,0,10,0);
			gbContraints.gridx = 0;
			gbContraints.gridy = 10;
			anaScrollPanel = new JScrollPane(anaJPanel);
			anaScrollPanel.setPreferredSize(new Dimension(width,height));
			content.add(anaScrollPanel, gbContraints);
		}		
	}
	
	public void dispose(){
		if(_frame!=null){
			_frame.setVisible(false);
			_frame.dispose();
		}
	}
	
	class MakeShowHidePanel extends JPanel implements MouseListener{
		DecoratorUIConstants charName = DecoratorUIConstants.NULL;
		boolean showing = false;
		MakeShowHidePanel(DecoratorUIConstants charName1){
			this.charName = charName1;
			this.setBackground(DecoratorColorSet.getBackgroundColor());
			JLabel tempLabel = new JLabel("     "+charName.getName());
		    Font currentFont = tempLabel.getFont();
		    float newSize = 12.f;
		    Font newFont = currentFont.deriveFont(newSize);
		    tempLabel.setFont(newFont);
			
			tempLabel.addMouseListener(this);
			this.add(tempLabel);
		}
		public void paint(Graphics g){
			super.paint(g);
			int x1 = this.getComponent(0).getX(); 
			int y1 = this.getComponent(0).getY();
			int height=10,width = 10;
			if(!showing){
				DecoratorShapes.drawInvertedTriangleFilled(g, new Point(x1+10,y1+8), height, width, DecoratorColorSet.getBlack());
			}
			else{
				DecoratorShapes.drawTriangleFilledpPointingToRight
					(g, new Point(x1+10,y1+8), height, width, DecoratorColorSet.getBlack());
			}
			
			
		}
		
		public void mouseClicked(MouseEvent arg0) {
			if(charName.getName().equals(DecoratorUIConstants.SHOW_COUNTRY.getName())){
				if (!showingCountry) {
					showing = false;
					this.repaint();
					countryScrollPanel.setVisible(true);
					_frame.pack();
					showingCountry = true;
				}
				else if (showingCountry){
					showing = true;
					this.repaint();
					countryScrollPanel.setVisible(false);
					_frame.pack();
					showingCountry = false;
				}
			}
			else if(charName.getName().equals(DecoratorUIConstants.SHOW_YEAR.getName())){
				if (!showingYear) {
					showing = false;
					this.repaint();
					yearScrollPanel.setVisible(true);
					_frame.pack();
					showingYear = true;
				}
				else if (showingYear){
					showing = true;
					this.repaint();
					yearScrollPanel.setVisible(false);
					_frame.pack();
					showingYear = false;
					
				}
			}
			else if(charName.getName().equals(DecoratorUIConstants.SHOW_A_HA_SUBTYPE.getName())){
				if (!showingAHA) {
					showing = false;
					this.repaint();
					ahaScrollPanel.setVisible(true);
					_frame.pack();
					showingAHA = true;
				}
				else if (showingAHA){
					showing = true;
					this.repaint();
					ahaScrollPanel.setVisible(false);
					_frame.pack();
					showingAHA = false;
					
				}
			}
			else if(charName.getName().equals(DecoratorUIConstants.SHOW_A_NA_SUBTYPE.getName())){
				if (!showingANA) {
					showing = false;
					this.repaint();
					anaScrollPanel.setVisible(true);
					_frame.pack();
					showingANA = true;
				}
				else if (showingANA){
					showing = true;
					this.repaint();
					anaScrollPanel.setVisible(false);
					_frame.pack();
					showingANA = false;
					
				}
			}
			else if(charName.getName().equals(DecoratorUIConstants.SHOW_HOST_SPECIES.getName())){
				if (!showingHost) {
					showing = false;
					this.repaint();
					hostScrollPanel.setVisible(true);
					_frame.pack();
					showingHost = true;
				}
				else if (showingHost){
					showing = true;
					this.repaint();
					hostScrollPanel.setVisible(false);
					_frame.pack();
					showingHost = false;
				}
			}
		}

		public void mouseEntered(MouseEvent arg0) {}
		

		public void mouseExited(MouseEvent arg0) {}
		

		public void mousePressed(MouseEvent arg0) {}
		
		
		public void mouseReleased(MouseEvent arg0) {}
		
	}
	class  MakeCharNameLegendPanel extends JPanel {
		Map<String, DecorateObject> charMap;
		MakeCharNameLegendPanel(DecoratorUIConstants charName){
			charMap = DecoratorTable.decoratorTable.get(charName);
			int number = charMap.size();
			JLabel  tempLabel;	
			this.setBackground(DecoratorColorSet.getBackgroundColor());
			
			BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(boxLayout);			
			
			for(int i = 0 ;i<number;i++){
				tempLabel = MakeJLabel.getJLabel("<html></html>", false);
				this.add(tempLabel);
				
			}
			
				
		}
		public void paint(Graphics g){
			super.paint(g);
			int i = 0;
			
			Set<String> charValueSet = charMap.keySet();
			String[] charValueArray = (String []) charValueSet.toArray (new String [charValueSet.size ()]);
			Arrays.sort(charValueArray, String.CASE_INSENSITIVE_ORDER);
			for(int count = 0 ; count<charValueArray.length;count++){
				
				int x1 = this.getComponent(i).getX(); 
				int y1 = this.getComponent(i).getY();
				i++;
				Font charValueFont = DecorationEnumHelper.
				getFont(charMap.get(charValueArray[count]).getStrainFont(), charMap.get(charValueArray[count]).getStrainStyle(),
						charMap.get(charValueArray[count]).getStrainSize());
						
				
				DecorationEnumHelper.drawStrainWithColorFontCase(g, DecorationEnumHelper.getStringWithCase(charValueArray[count], 
						charMap.get(charValueArray[count]).getStrainCase()), charValueFont, 
						new Point((x1+50),y1+16), charMap.get(charValueArray[count]).getStrainStyle(),
						charMap.get(charValueArray[count]).getStrainColor());
				
				DecorationEnumHelper.drawShapesWithColor
				(charMap.get(charValueArray[count]).getNodeShape(),
						g, new Point((x1+30),(y1+11) ),	10, 10,
						charMap.get(charValueArray[count]).getNodeColor());
			}
		}
	}
}
