package com.lanl.application.treeDecorator.applet.ui.frames;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.forester.archaeopteryx.MainFrame;

import com.lanl.application.TPTD.applet.SubTreePanel;
import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;
import com.lanl.application.treeDecorator.dataStructures.DecorateObject;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;




public class ManualDecorateFrame implements ActionListener{
	
	ArrayList<String> selectedCharValues = new ArrayList<String>();
	String selectedStyle = "";
	ArrayList<JRadioButton> styleRadioButtons = new ArrayList<JRadioButton>();
	ArrayList<JCheckBox> charValueCheckBoxes = new ArrayList<JCheckBox>();
	private JPanel characterValueLegendPanel,decorationStyleOptionsPanel;
	private JButton applyButton, closeButton,resetButton;
	private JCheckBox charValueCheckBox;
	JRadioButton styleRadioButton;
	ButtonGroup styleButtonGroup = new ButtonGroup(); 
	GridBagConstraints gbContraints;
	JFrame _frame;
	SemiDecorateFrame semiDecorateFrame;
	
	
	public ManualDecorateFrame(SemiDecorateFrame _semiDecorateFrame){
		this.semiDecorateFrame = _semiDecorateFrame;
		try{
			String laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel( laf);
		}
		catch (Exception e){
			System.err.println( "ManualDecorateFrame(): Error setting LookAndFeel - using default" );
			e.printStackTrace();
		}
		makeManalDecoratorFrame();
		_frame.setResizable(false);
		_frame.setVisible(true);
		
	}
	
	public void makeManalDecoratorFrame(){
		_frame = MakeFrame.getFrame( DecoratorUIConstants.TREE_DECORATOR_FRAME_HEADER.getName(), 650, 500, 300, 300);
		
		Container content = _frame.getContentPane();
		content.setLayout( new GridBagLayout());
		content.setBackground(DecoratorColorSet.getBackgroundColor());
	
		gbContraints = new GridBagConstraints();
		
		gbContraints.anchor = GridBagConstraints.NORTH;
		gbContraints.insets = new Insets(10,10,10,10);
		gbContraints.gridx = 0;
		gbContraints.gridy = 0;
		gbContraints.gridwidth = 5;
		gbContraints.weightx = 0;
		JLabel title = MakeJLabel.getJLabel(DecoratorUIConstants.MANUAL_DECORATION_TITLE.getName(), false, "north");
		title.setAlignmentX(50);
		content.add( title, gbContraints);
		
		gbContraints = new GridBagConstraints();
		gbContraints.anchor = GridBagConstraints.NORTHWEST;
		gbContraints.insets = new Insets(5,5,5,5);
		gbContraints.gridwidth = 2;
		gbContraints.gridx = 1;
		gbContraints.gridy = 2;
		characterValueLegendPanel = new MakeCharValueLegendPanel();
		
		JScrollPane scrollPaneForcharacterValueLegendPanel = new JScrollPane(characterValueLegendPanel);
		scrollPaneForcharacterValueLegendPanel.setPreferredSize(new Dimension(150,350));
		content.add(scrollPaneForcharacterValueLegendPanel, gbContraints);
		
		gbContraints.insets = new Insets(5,20,5,5);
		gbContraints.gridwidth = 2;
		gbContraints.gridx = 3;
		gbContraints.gridy = 2;
		decorationStyleOptionsPanel = new MakeDecorationStyleOptionsPanel();
		
		JScrollPane scrollPaneForDecorationStyleOptionsPanel = new JScrollPane(decorationStyleOptionsPanel);
		scrollPaneForDecorationStyleOptionsPanel.setPreferredSize(new Dimension(150,350));
		content.add(scrollPaneForDecorationStyleOptionsPanel, gbContraints);
		
		JPanel applyClosePanel = MakeJPanel.getGridLayoutPanel(1,3);
		applyButton = MakeButton.getButton(DecoratorUIConstants.APPLY.getName());
		closeButton = MakeButton.getButton(DecoratorUIConstants.CLOSE.getName());
		resetButton = MakeButton.getButton(DecoratorUIConstants.RESET.getName());
		resetButton.addActionListener(this);
		applyButton.addActionListener( this);
		closeButton.addActionListener( this);
		resetButton.setEnabled(false);
		applyButton.setEnabled(false);
		
		applyClosePanel.add( resetButton);
		applyClosePanel.add( applyButton);
		applyClosePanel.add( closeButton);
		gbContraints.anchor = GridBagConstraints.SOUTHWEST;
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.ipady = 2;
		gbContraints.gridx = 3;
		gbContraints.gridy = 3;
		gbContraints.gridwidth = 2;
		content.add( applyClosePanel, gbContraints);
	
		
	}
	public void dispose(){
		if(_frame!=null){
			_frame.setVisible(false);
			_frame.dispose();
		}
	}
	
	public void actionPerformed( ActionEvent e){
		if(e.getSource() == resetButton){
			applyButton.setEnabled(false);
			for(JRadioButton srb: styleRadioButtons){
				srb.setEnabled(false);
			}
			for(JCheckBox jb : charValueCheckBoxes){
				jb.setSelected(false);
			}
			resetButton.setEnabled(false);
		}
		else if(e.getSource() == applyButton){
			handleManualDecoration();
			characterValueLegendPanel.repaint();
			for(JRadioButton srb: styleRadioButtons){
				srb.setEnabled(false);
			}
			for(JCheckBox jb : charValueCheckBoxes){
				jb.setSelected(false);
			}
			resetButton.setEnabled(false);
			applyButton.setEnabled(false);
			
			
		}
		else if(e.getSource() == closeButton){
			semiDecorateFrame.backButton.setEnabled(true);
			semiDecorateFrame.closeButton.setEnabled(true);
			semiDecorateFrame.manualCheckBox.setSelected(false);
			this.dispose();
		}
	}
	
	private void handleManualDecoration(){
		DecoratorTable.styleCharacteristicMapping.put(semiDecorateFrame.selectedDecorationSyle,
						semiDecorateFrame.selectedCharacteristic);
		for(String charValue : selectedCharValues){
			DecoratorTable.decoratorTable.get(semiDecorateFrame.selectedCharacteristic).
				get(charValue).setAnyDecorationStyle(semiDecorateFrame.selectedDecorationSyle,
						DecorationEnumHelper.getDecorationStylesObject(selectedStyle));
		}
		// repaint all
		SubTreePanel.refreshAllWindows();
	}
	class MakeDecorationStyleOptionsPanel extends JPanel implements ItemListener{
		MakeDecorationStyleOptionsPanel(){
			this.setBackground(DecoratorColorSet.getBackgroundColor());
			BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(boxLayout);
			styleRadioButtons.clear();
			if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.NODE_COLOR){
				JLabel styleNameLabel = new JLabel(semiDecorateFrame.selectedCharacteristic.getName());
				this.add(styleNameLabel);
				for(int i=0;i< DecorationEnumHelper.allColors.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton("");
					this.add(styleRadioButton);
					
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allColors[i].getName());
					
				}
				
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.SHAPES){
				JLabel styleNameLabel = new JLabel(semiDecorateFrame.selectedCharacteristic.getName());
				this.add(styleNameLabel);
				
				for(int i=0;i< DecorationEnumHelper.allShapes.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton("");
					this.add(styleRadioButton);
					
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allShapes[i].getName());
				}
				
				
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.STRAIN_COLOR){
				JLabel styleNameLabel = new JLabel(DecoratorUIConstants.STRAIN_COLOR.getName());
				this.add(styleNameLabel);
				
				for(int i=0;i< DecorationEnumHelper.allColors.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton(semiDecorateFrame.selectedCharacteristic.getName());
					styleRadioButton.setForeground(DecorationEnumHelper.getColor(DecorationEnumHelper.allColors[i]));
					this.add(styleRadioButton);
				
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allColors[i].getName());
					
				}
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.CASE){
				JLabel styleNameLabel = new JLabel(DecoratorUIConstants.CASE.getName());
				this.add(styleNameLabel);
				for(int i=0;i< DecorationEnumHelper.allCases.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton(
							DecorationEnumHelper.getStringWithCase(semiDecorateFrame.selectedCharacteristic.getName()
									, DecorationEnumHelper.allCases[i]));
					this.add(styleRadioButton);
					
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allCases[i].getName());
				}
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.FONT){
				JLabel styleNameLabel = new JLabel(DecoratorUIConstants.FONT.getName());
				this.add(styleNameLabel);
				for(int i=0;i< DecorationEnumHelper.allFonts.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton(semiDecorateFrame.selectedCharacteristic.getName());
					this.add(styleRadioButton);
					styleRadioButton.setFont(DecorationEnumHelper.getFont(DecorationEnumHelper.allFonts[i]
								, DecorationStyles.REGULAR, DecorationStyles._12));
					
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allFonts[i].getName());
				}
				
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.STYLE){
				JLabel styleNameLabel = new JLabel(DecoratorUIConstants.STYLE.getName());
				this.add(styleNameLabel);
				for(int i=0;i< DecorationEnumHelper.allStyles.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton
					(DecorationEnumHelper.doUnderline(semiDecorateFrame.selectedCharacteristic.getName()
							, DecorationEnumHelper.allStyles[i]));
					this.add(styleRadioButton);
					styleRadioButton.setFont(DecorationEnumHelper.getFont(DecorationStyles.ARIAL
					         , DecorationEnumHelper.allStyles[i], DecorationStyles._12));
					
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allStyles[i].getName());
					
				}
				
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.SIZE){
				JLabel styleNameLabel = new JLabel(DecoratorUIConstants.SIZE.getName());
				this.add(styleNameLabel);
				for(int i=0;i< DecorationEnumHelper.allSizes.length;i++){
					styleRadioButton = MakeRadioButton.getRadioButton(semiDecorateFrame.selectedCharacteristic.getName());
					this.add(styleRadioButton);
					styleRadioButton.setFont(DecorationEnumHelper.getFont(DecorationStyles.ARIAL
					         , DecorationStyles.REGULAR,DecorationEnumHelper.allSizes[i]));
					
					styleRadioButton.addItemListener(this);
					
					styleRadioButtons.add(styleRadioButton);
					
					styleRadioButton.setEnabled(false);
					styleButtonGroup.add(styleRadioButton);
					styleRadioButton.setName(DecorationEnumHelper.allSizes[i].getName());
					
				}
				
			}
		}

		public void itemStateChanged(ItemEvent e) {
			JRadioButton selectedRadioButton = (JRadioButton) e.getSource();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				applyButton.setEnabled(true);
				selectedStyle = selectedRadioButton.getName();
			}
		}
			
			
			
		

		public void paint(Graphics g){
			super.paint(g);
			if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.SHAPES){
				for(int i=0;i< DecorationEnumHelper.allShapes.length;i++){
					int x = this.getComponent(i+1).getX();
					int y = this.getComponent(i+1).getY();
					DecorationEnumHelper.drawShapesWithColor(DecorationEnumHelper.allShapes[i],
							g, new Point((x+30),(y+12) ), 10, 10, DecorationStyles.BLACK);
					
				}
			}
			else if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.NODE_COLOR){
				for(int i=0;i< DecorationEnumHelper.allColors.length;i++){
					int x = this.getComponent(i+1).getX();
					int y = this.getComponent(i+1).getY();
					DecorationEnumHelper.drawShapesWithColor(DecorationStyles.FILLED_SQUARE,
					g, new Point((x+30),(y+12) ), 10, 10, DecorationEnumHelper.allColors[i]);
				}
			}
		}
	}
	
	class  MakeCharValueLegendPanel extends JPanel implements ItemListener{
		Map<String, DecorateObject> charMap = DecoratorTable.decoratorTable.get(semiDecorateFrame.selectedCharacteristic);
		Set<String> charValues = charMap.keySet();
		MakeCharValueLegendPanel(){
			this.setBackground(DecoratorColorSet.getBackgroundColor());
			
			BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(boxLayout);			
			
			JLabel charNameLabel = new JLabel(semiDecorateFrame.selectedCharacteristic.getName());
			this.add(charNameLabel);
			charValueCheckBoxes.clear();	
			for(String charValue:charValues){
				charValueCheckBox = new JCheckBox();
				charValueCheckBox.addItemListener(this);
				charValueCheckBox.setName(charValue);
				charValueCheckBoxes.add(charValueCheckBox);
				this.add(charValueCheckBox);
				
			}
			
				
		}
		public void itemStateChanged(ItemEvent e) {
			JCheckBox selectedCheckBox = (JCheckBox)e.getSource();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectedCharValues.add(selectedCheckBox.getName());
				
		    } else {
		    	selectedCharValues.remove(selectedCharValues.indexOf(selectedCheckBox.getName()));
		    	
		    }
			if(selectedCharValues.isEmpty()){
				applyButton.setEnabled(false);
				resetButton.setEnabled(false);
				for(JRadioButton srb: styleRadioButtons){
					srb.setEnabled(false);
				}
			}
			else{
				for(JRadioButton srb: styleRadioButtons){
					srb.setEnabled(true);
				}
				resetButton.setEnabled(true);
			}
		}
		
		public void paint(Graphics g){
			super.paint(g);
			int i = 1;
			for(String charValue:charValues){
				int x1 = this.getComponent(i).getX(); 
				int y1 = this.getComponent(i).getY();
				i++;
				
				Font charValueFont = DecorationEnumHelper.
				getFont(charMap.get(charValue).getStrainFont(), charMap.get(charValue).getStrainStyle(),
						charMap.get(charValue).getStrainSize());
				
				DecorationEnumHelper.drawStrainWithColorFontCase(g, DecorationEnumHelper.getStringWithCase(charValue, 
						charMap.get(charValue).getStrainCase()), charValueFont, 
						new Point((x1+30),y1+16), charMap.get(charValue).getStrainStyle(),
						charMap.get(charValue).getStrainColor());
				
				DecorationEnumHelper.drawShapesWithColor
				(charMap.get(charValue).getNodeShape(),
						g, new Point((x1+100),(y1+11) ),	10, 10,
						charMap.get(charValue).getNodeColor());
			}
		}
	}
}

	