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

import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;
import com.lanl.application.treeDecorator.dataStructures.DecorateObject;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

public class LegendFrame implements ItemListener{

	int height = 120;
	int width = 250;
	public JFrame _frame;
	GridBagConstraints gbContraints;
	public JPanel countryJPanel,yearJPanel,ahaJPanel,anaJPanel,hostJPanel;
	JScrollPane countryScrollPanel,yearScrollPanel,ahaScrollPanel,anaScrollPanel,hostScrollPanel;
	JCheckBox countryCheckBox,yearCheckBox,ahaCheckBox,anaCheckBox,hostCheckBox;
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
		_frame = MakeFrame.getFrame( DecoratorUIConstants.TREE_DECORATOR_FRAME_HEADER.getName(), 400, 900, 400, 0);
		
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
		
		countryCheckBox = new JCheckBox(DecoratorUIConstants.SHOW_COUNTRY.getName());
		countryCheckBox.addItemListener(this);
		countryCheckBox.setName(DecoratorUIConstants.SHOW_COUNTRY.getName());
		content.add(countryCheckBox, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 2;
		countryJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.COUNTRY);
		countryScrollPanel = new JScrollPane(countryJPanel);
		countryScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(countryScrollPanel, gbContraints);
		countryCheckBox.setSelected(true);
		
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 3;
		
		yearCheckBox = new JCheckBox(DecoratorUIConstants.SHOW_YEAR.getName());
		yearCheckBox.addItemListener(this);
		yearCheckBox.setName(DecoratorUIConstants.SHOW_YEAR.getName());
		content.add(yearCheckBox, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 4;
		yearJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.YEAR);
		yearScrollPanel = new JScrollPane(yearJPanel);
		yearScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(yearScrollPanel, gbContraints);
		yearCheckBox.setSelected(true);
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 5;
		
		ahaCheckBox = new JCheckBox(DecoratorUIConstants.SHOW_A_HA_SUBTYPE.getName());
		ahaCheckBox.addItemListener(this);
		ahaCheckBox.setName(DecoratorUIConstants.SHOW_A_HA_SUBTYPE.getName());
		content.add(ahaCheckBox, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 6;
		ahaJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.A_HA_SUBTYPE);
		ahaScrollPanel = new JScrollPane(ahaJPanel);
		ahaScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(ahaScrollPanel, gbContraints);
		ahaCheckBox.setSelected(true);
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 7;
		
		anaCheckBox = new JCheckBox(DecoratorUIConstants.SHOW_A_NA_SUBTYPE.getName());
		anaCheckBox.addItemListener(this);
		anaCheckBox.setName(DecoratorUIConstants.SHOW_A_NA_SUBTYPE.getName());
		content.add(anaCheckBox, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 8;
		anaJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.A_NA_SYBTYPE);
		anaScrollPanel = new JScrollPane(anaJPanel);
		anaScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(anaScrollPanel, gbContraints);
		anaCheckBox.setSelected(true);
		
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 9;
		
		hostCheckBox = new JCheckBox(DecoratorUIConstants.SHOW_HOST_SPECIES.getName());
		hostCheckBox.addItemListener(this);
		hostCheckBox.setName(DecoratorUIConstants.SHOW_HOST_SPECIES.getName());
		content.add(hostCheckBox, gbContraints);
		gbContraints.insets = new Insets(0,0,10,0);
		gbContraints.gridx = 0;
		gbContraints.gridy = 10;
		hostJPanel = new MakeCharNameLegendPanel(DecoratorUIConstants.HOST_SPECIES);
		hostScrollPanel = new JScrollPane(hostJPanel);
		hostScrollPanel.setPreferredSize(new Dimension(width,height));
		content.add(hostScrollPanel, gbContraints);
		hostCheckBox.setSelected(true);
		
		
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
						new Point((x1+30),y1+16), charMap.get(charValueArray[count]).getStrainStyle(),
						charMap.get(charValueArray[count]).getStrainColor());
				
				DecorationEnumHelper.drawShapesWithColor
				(charMap.get(charValueArray[count]).getNodeShape(),
						g, new Point((x1+100),(y1+11) ),	10, 10,
						charMap.get(charValueArray[count]).getNodeColor());
			}
		}
	}

	public void dispose(){
		if(_frame!=null){
			_frame.setVisible(false);
			_frame.dispose();
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		JCheckBox selectedCheckBox = (JCheckBox)e.getSource();
		if(selectedCheckBox.getName().equals(DecoratorUIConstants.SHOW_COUNTRY.getName())){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				countryScrollPanel.setVisible(true);
				_frame.pack();
			}
			else{
				countryScrollPanel.setVisible(false);
				_frame.pack();
				
			}
		}
		else if(selectedCheckBox.getName().equals(DecoratorUIConstants.SHOW_YEAR.getName())){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				yearScrollPanel.setVisible(true);
				_frame.pack();
			}
			else{
				yearScrollPanel.setVisible(false);
				_frame.pack();
				
			}
		}
		else if(selectedCheckBox.getName().equals(DecoratorUIConstants.SHOW_A_HA_SUBTYPE.getName())){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				ahaScrollPanel.setVisible(true);
				_frame.pack();
			}
			else{
				ahaScrollPanel.setVisible(false);
				_frame.pack();
				
			}
		}
		else if(selectedCheckBox.getName().equals(DecoratorUIConstants.SHOW_A_NA_SUBTYPE.getName())){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				anaScrollPanel.setVisible(true);
				_frame.pack();
			}
			else{
				anaScrollPanel.setVisible(false);
				_frame.pack();
				
			}
		}
		else if(selectedCheckBox.getName().equals(DecoratorUIConstants.SHOW_HOST_SPECIES.getName())){
			if (e.getStateChange() == ItemEvent.SELECTED) {
				hostScrollPanel.setVisible(true);
				_frame.pack();
			}
			else{
				hostScrollPanel.setVisible(false);
				_frame.pack();
				
			}
		}
		
	}
}
