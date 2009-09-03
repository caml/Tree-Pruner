package com.lanl.application.treeDecorator.applet.ui.frames;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;


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
	private JRadioButton noStyleRadioButton;
	ArrayList<JCheckBox> charValueCheckBoxes = new ArrayList<JCheckBox>();
	private JPanel characterValueLegendPanel,decorationStyleOptionsPanel;
	private JButton applyButton, closeButton,resetButton,defaultButton;
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
		_frame = MakeFrame.getFrame( DecoratorUIConstants.TREE_DECORATOR_FRAME_HEADER.getName(), 800, 500, 300, 300);
		_frame.addWindowListener(new WindowAdapter(){
			public void windowClosing( final WindowEvent e ) {
				semiDecorateFrame.backButton.setEnabled(true);
				semiDecorateFrame.closeButton.setEnabled(true);
				semiDecorateFrame.manualCheckBox.setSelected(false);
				semiDecorateFrame.applyButton.setEnabled(true);
				semiDecorateFrame.defaultButton.setEnabled(true);
				semiDecorateFrame.setCloseWindowListners(semiDecorateFrame._frame);
				dispose();
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
		gbContraints.gridwidth = 6;
		gbContraints.weightx = 0;
		JLabel title = MakeJLabel.getJLabel(DecoratorUIConstants.MANUAL_DECORATION_TITLE.getName(), false, "north");
		title.setAlignmentX(50);
		content.add( title, gbContraints);
		
		gbContraints = new GridBagConstraints();
		gbContraints.anchor = GridBagConstraints.NORTHWEST;
		gbContraints.insets = new Insets(5,5,5,5);
		gbContraints.gridwidth = 3;
		gbContraints.gridx = 0;
		gbContraints.gridy = 2;
		characterValueLegendPanel = new MakeCharValueLegendPanel();
		
		JScrollPane scrollPaneForcharacterValueLegendPanel = new JScrollPane(characterValueLegendPanel);
		scrollPaneForcharacterValueLegendPanel.setPreferredSize(new Dimension(350,350));
		content.add(scrollPaneForcharacterValueLegendPanel, gbContraints);
		
		gbContraints.insets = new Insets(5,20,5,5);
		gbContraints.gridwidth = 3;
		gbContraints.gridx = 3;
		gbContraints.gridy = 2;
		decorationStyleOptionsPanel = new MakeDecorationStyleOptionsPanel();
		
		JScrollPane scrollPaneForDecorationStyleOptionsPanel = new JScrollPane(decorationStyleOptionsPanel);
		scrollPaneForDecorationStyleOptionsPanel.setPreferredSize(new Dimension(350,350));
		content.add(scrollPaneForDecorationStyleOptionsPanel, gbContraints);
		
		JPanel applyClosePanel = MakeJPanel.getGridLayoutPanel(1,3);
		applyButton = MakeButton.getButton(DecoratorUIConstants.APPLY.getName());
		closeButton = MakeButton.getButton(DecoratorUIConstants.CLOSE.getName());
		resetButton = MakeButton.getButton(DecoratorUIConstants.RESET.getName());
		defaultButton = MakeButton.getButton(DecoratorUIConstants.DEFAULT.getName());
		defaultButton.addActionListener(this);
		resetButton.addActionListener(this);
		applyButton.addActionListener( this);
		closeButton.addActionListener( this);
		resetButton.setEnabled(false);
		applyButton.setEnabled(false);
		defaultButton.setEnabled(false);
		
		applyClosePanel.add( resetButton);
		applyClosePanel.add( defaultButton);
		applyClosePanel.add( applyButton);
		applyClosePanel.add( closeButton);
		gbContraints.anchor = GridBagConstraints.SOUTHWEST;
		gbContraints.insets = new Insets(0,0,0,0);
		gbContraints.ipady = 0;
		gbContraints.gridx = 2;
		gbContraints.gridy = 4;
		gbContraints.gridwidth = 4;
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
			noStyleRadioButton.setSelected(true);
			for(JCheckBox jb : charValueCheckBoxes){
				jb.setSelected(false);
			}
			resetButton.setEnabled(false);
			defaultButton.setEnabled(false);
		}
		else if(e.getSource() == applyButton){
			handleManualDecoration();
			characterValueLegendPanel.repaint();
			for(JRadioButton srb: styleRadioButtons){
				srb.setEnabled(false);
			}
			noStyleRadioButton.setSelected(true);
			for(JCheckBox jb : charValueCheckBoxes){
				jb.setSelected(false);
			}
			resetButton.setEnabled(false);
			applyButton.setEnabled(false);
			defaultButton.setEnabled(false);
			
			
		}
		
		else if(e.getSource() == defaultButton){
			handleRevertToDefaultDecorations();
			characterValueLegendPanel.repaint();
			for(JRadioButton srb: styleRadioButtons){
				srb.setEnabled(false);
			}
			noStyleRadioButton.setSelected(true);
			for(JCheckBox jb : charValueCheckBoxes){
				jb.setSelected(false);
			}
			resetButton.setEnabled(false);
			applyButton.setEnabled(false);
			defaultButton.setEnabled(false);
			
			
		}
		else if(e.getSource() == closeButton){
			semiDecorateFrame.backButton.setEnabled(true);
			semiDecorateFrame.closeButton.setEnabled(true);
			semiDecorateFrame.manualCheckBox.setSelected(false);
			semiDecorateFrame.applyButton.setEnabled(true);
			semiDecorateFrame.defaultButton.setEnabled(true);
			semiDecorateFrame.setCloseWindowListners(semiDecorateFrame._frame);
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
		if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.STRAIN_COLOR){
			DecorationEnumHelper.populateBranchColorNodes();
		}
		DecoratorTable.updateLegend();
		// repaint all
		SubTreePanel.refreshAllWindows();
	}
	private void handleRevertToDefaultDecorations(){
		boolean allSelected = false;
		for(JCheckBox jb:charValueCheckBoxes){
			if(jb.isSelected()){
				allSelected = true;
			}
			else {
				allSelected = false;
				break;
			}
		}
		if(allSelected){
			DecoratorTable.styleCharacteristicMapping.remove(semiDecorateFrame.selectedDecorationSyle);
			for(String charValue : DecoratorTable.decoratorTable.get(semiDecorateFrame.selectedCharacteristic).keySet()){
				DecoratorTable.decoratorTable.get(semiDecorateFrame.selectedCharacteristic).
					get(charValue).setAnyDecorationStyle(
							semiDecorateFrame.selectedDecorationSyle, DecorationEnumHelper.
							getDefaultDecorationStyles(semiDecorateFrame.selectedDecorationSyle));
			}
		}
		else{
			for(String charValue : selectedCharValues){
				DecoratorTable.decoratorTable.get(semiDecorateFrame.selectedCharacteristic).
				get(charValue).setAnyDecorationStyle(
						semiDecorateFrame.selectedDecorationSyle, DecorationEnumHelper.
						getDefaultDecorationStyles(semiDecorateFrame.selectedDecorationSyle));
			}
		}
		if(semiDecorateFrame.selectedDecorationSyle == DecoratorUIConstants.STRAIN_COLOR){
			DecorationEnumHelper.populateBranchColorNodes();
		}
		DecoratorTable.updateLegend();
		// repaint all
		SubTreePanel.refreshAllWindows();
	}
	class MakeDecorationStyleOptionsPanel extends JPanel implements ItemListener{
		MakeDecorationStyleOptionsPanel(){
			this.setBackground(DecoratorColorSet.getBackgroundColor());
			BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(boxLayout);
			styleRadioButtons.clear();
			noStyleRadioButton = MakeRadioButton.getRadioButton("");
			styleButtonGroup.add(noStyleRadioButton);
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
		Set<String> charValueSet = charMap.keySet();
		String[] charValueArray = (String []) charValueSet.toArray (new String [charValueSet.size ()]);
		MakeCharValueLegendPanel(){
			Arrays.sort(charValueArray, String.CASE_INSENSITIVE_ORDER);
			this.setBackground(DecoratorColorSet.getBackgroundColor());
			
			BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(boxLayout);			
			
			JLabel charNameLabel = new JLabel(semiDecorateFrame.selectedCharacteristic.getName());
			this.add(charNameLabel);
			charValueCheckBoxes.clear();	
			for(int count = 0 ; count<charValueArray.length;count++){
				charValueCheckBox = new JCheckBox();
				charValueCheckBox.addItemListener(this);
				charValueCheckBox.setName(charValueArray[count]);
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
				defaultButton.setEnabled(false);
				for(JRadioButton srb: styleRadioButtons){
					srb.setEnabled(false);
				}
				noStyleRadioButton.setSelected(true);
			}
			else{
				for(JRadioButton srb: styleRadioButtons){
					srb.setEnabled(true);
				}
				resetButton.setEnabled(true);
				defaultButton.setEnabled(true);
			}
		}
		
		public void paint(Graphics g){
			super.paint(g);
			int i = 1;
			boolean allDefault = false;
			if(semiDecorateFrame.selectedToDecorate == DecoratorUIConstants.STRAIN){
				for(String charValue : charMap.keySet()){
					if(DecorationEnumHelper.isStyleValueDefault(DecoratorUIConstants.SHAPES,
							charMap.get(charValue).getNodeShape())&&
							DecorationEnumHelper.isStyleValueDefault(DecoratorUIConstants.NODE_COLOR,
							charMap.get(charValue).getNodeColor())){
						allDefault = true;
					}
					else{
						allDefault = false;
						break;
					}
				}
			}
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
				
				if(allDefault == false){
					DecorationEnumHelper.drawShapesWithColor
					(charMap.get(charValueArray[count]).getNodeShape(),
							g, new Point((x1+30),(y1+11) ),	10, 10,
							charMap.get(charValueArray[count]).getNodeColor());
				}
			}
		}
	}
}

	
