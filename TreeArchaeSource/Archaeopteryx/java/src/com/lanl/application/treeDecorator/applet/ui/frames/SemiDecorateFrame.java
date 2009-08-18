package com.lanl.application.treeDecorator.applet.ui.frames;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import com.lanl.application.TPTD.applet.SubTreePanel;
import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;




public class SemiDecorateFrame implements ActionListener{
	private JRadioButton countryRadioButton, yearRadioButon, ahaSerotypeRadioButton, anaSerotypeRadioButton, hostRadioButton;
	private JRadioButton strainRadioButton, nodeRadioButton; 
	private JRadioButton nodeColorRadioButton, shapesRadioButton;
	private JRadioButton strainColorRadioButton, caseRadioButton, fontRadioButton, styleRadioButton, sizeRadioButton; 
	protected JButton applyButton, closeButton,backButton,defaultButton;
	protected JLabel selectedPathLabel;
	protected JLabel toSelectLabel;
	private JPanel infoPanel;
	protected JCheckBox manualCheckBox;
	GridBagConstraints gbContraints;
	JFrame _frame;
	ManualDecorateFrame manualDecorateFrame;
	ButtonGroup characteristicButtonGroup,toDecorateButtonGroup,decorateStrainButtonGroup, decorateNodeButtonGroup;
	public DecoratorUIConstants selectedToDecorate = DecoratorUIConstants.NULL;  //make instance
	public DecoratorUIConstants selectedCharacteristic = DecoratorUIConstants.NULL; //make instance
	public DecoratorUIConstants selectedDecorationSyle = DecoratorUIConstants.NULL;  //make instance
	
	public SemiDecorateFrame(){
	//	gbContraints = new GridBagConstraints();
		try{
			String laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel( laf);
		}
		catch (Exception e){
			System.err.println( "SemiDecorateFrame(): Error setting LookAndFeel - using default" );
			e.printStackTrace();
		}
		makeSemiDecoratorFrame();
		_frame.setVisible(true);
	}
	
	public void makeSemiDecoratorFrame(){
		_frame = MakeFrame.getFrame( DecoratorUIConstants.TREE_DECORATOR_FRAME_HEADER.getName(), 750, 600, 300, 300);
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
		JLabel title = MakeJLabel.getJLabel(DecoratorUIConstants.SEMI_AUTOMATIC_DECORATION_TITLE.getName(), false, "north");
		title.setAlignmentX(50);
		content.add( title, gbContraints);
		
		selectedPathLabel = MakeJLabel.getInfoJLabel("",false);
		toSelectLabel = MakeJLabel.getInfoJLabel(DecoratorUIConstants.TO_SELECT_CHARACTERISTIC_LABEL_TEXT.getName(),true);
		infoPanel = MakeJPanel.getInfoGridLayoutPanel(2, 1, true);
		infoPanel.add(selectedPathLabel);
		infoPanel.add(toSelectLabel);
		gbContraints.gridx = 0;
		gbContraints.gridy = 1;
		
		content.add( infoPanel, gbContraints);
		
		gbContraints = new GridBagConstraints();
		gbContraints.anchor = GridBagConstraints.NORTHWEST;
		gbContraints.insets = new Insets(5,5,5,5);
		gbContraints.gridwidth = 1;
		gbContraints.gridx = 0;
		gbContraints.gridy = 3;
		content.add( makeCharectisticPanel(), gbContraints);
		
		gbContraints.gridx = 1;
		gbContraints.gridy = 3;
		content.add( makeToDecoratePanel(), gbContraints);
		
		gbContraints.gridx = 2;
		gbContraints.gridy = 3;
		content.add( makeDecorationStylesPanel(), gbContraints);
		
		manualCheckBox = new JCheckBox(DecoratorUIConstants.MANUAL.getName());
		manualCheckBox.addActionListener( this);
		manualCheckBox.setSelected(false);
		manualCheckBox.setEnabled(false);
		
		gbContraints.anchor = GridBagConstraints.EAST;
		gbContraints.gridx = 3;
		gbContraints.gridy = 3;
		
		content.add( manualCheckBox, gbContraints);
		
		
		JPanel applyClosePanel = MakeJPanel.getGridLayoutPanel(1,3);
		applyButton = MakeButton.getButton(DecoratorUIConstants.APPLY.getName());
		closeButton = MakeButton.getButton(DecoratorUIConstants.CLOSE.getName());
		backButton = MakeButton.getButton(DecoratorUIConstants.BACK.getName());
		defaultButton = MakeButton.getButton(DecoratorUIConstants.DEFAULT.getName());
		defaultButton.addActionListener(this);
		backButton.addActionListener(this);
		applyButton.addActionListener( this);
		closeButton.addActionListener( this);
		defaultButton.setEnabled(false);
		backButton.setEnabled(false);
		applyButton.setEnabled(false);
		
		applyClosePanel.add( backButton);
		applyClosePanel.add( defaultButton);
		applyClosePanel.add( applyButton);
		applyClosePanel.add( closeButton);
		gbContraints.anchor = GridBagConstraints.SOUTHWEST;
		gbContraints.insets = new Insets(50,0,0,0);
		gbContraints.ipady = 2;
		gbContraints.gridx = 1;
		gbContraints.gridy = 4;
		gbContraints.gridwidth = 4;
		content.add( applyClosePanel, gbContraints);
	}
	
	private JPanel makeDecorationStylesPanel(){
		nodeColorRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.NODE_COLOR.getName());
		shapesRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.SHAPES.getName());
		strainColorRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.STRAIN_COLOR.getName());
		caseRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.CASE.getName());
		fontRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.FONT.getName());
		styleRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.STYLE.getName());
		sizeRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.SIZE.getName());

		nodeColorRadioButton.setEnabled(false);
		shapesRadioButton.setEnabled(false);
		strainColorRadioButton.setEnabled(false);
		caseRadioButton.setEnabled(false);
		fontRadioButton.setEnabled(false);
		styleRadioButton.setEnabled(false);
		sizeRadioButton.setEnabled(false);

		decorateNodeButtonGroup = new ButtonGroup();
		decorateNodeButtonGroup.add(nodeColorRadioButton);
		decorateNodeButtonGroup.add(shapesRadioButton);

		decorateStrainButtonGroup = new ButtonGroup();
		decorateStrainButtonGroup.add(strainColorRadioButton);
		decorateStrainButtonGroup.add(caseRadioButton);
		decorateStrainButtonGroup.add(fontRadioButton);
		decorateStrainButtonGroup.add(styleRadioButton);
		decorateStrainButtonGroup.add(sizeRadioButton);

		nodeColorRadioButton.addActionListener(this);
		shapesRadioButton.addActionListener(this);
		strainColorRadioButton.addActionListener(this);
		caseRadioButton.addActionListener(this);
		fontRadioButton.addActionListener(this);
		styleRadioButton.addActionListener(this);
		sizeRadioButton.addActionListener(this);

		JPanel decoratorStylePanel = MakeJPanel.getGridLayoutPanel(10, 1);
		JLabel strainLabel = new JLabel(DecoratorUIConstants.STRAIN_DECORATION.getName());
		decoratorStylePanel.add(strainLabel);
		decoratorStylePanel.add(strainColorRadioButton);
		decoratorStylePanel.add(caseRadioButton);
		decoratorStylePanel.add(fontRadioButton);
		decoratorStylePanel.add(styleRadioButton);
		decoratorStylePanel.add(sizeRadioButton);
		decoratorStylePanel.add(new JLabel(""));
		JLabel nodeLabel = new JLabel(DecoratorUIConstants.NODE_DECORATION.getName());
		decoratorStylePanel.add(nodeLabel);
		decoratorStylePanel.add(nodeColorRadioButton);
		decoratorStylePanel.add(shapesRadioButton);
		decoratorStylePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
						BorderFactory.createLoweredBevelBorder()),DecoratorUIConstants.DECORATION_OPTIONS.getName()));
		
		return decoratorStylePanel;
	}

	private JPanel makeToDecoratePanel(){
		strainRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.STRAIN.getName()+"                   ");
		nodeRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.NODE.getName());
		
		strainRadioButton.setEnabled(false);
		nodeRadioButton.setEnabled(false);
		
		toDecorateButtonGroup = new ButtonGroup();
		toDecorateButtonGroup.add(strainRadioButton);
		toDecorateButtonGroup.add(nodeRadioButton);
		
		strainRadioButton.addActionListener( this);
		nodeRadioButton.addActionListener( this);
		
		JPanel toDecoratePanel = MakeJPanel.getGridLayoutPanel(5,1);
		toDecoratePanel.add(strainRadioButton);
		toDecoratePanel.add(nodeRadioButton);
		toDecoratePanel.setBorder( BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()), DecoratorUIConstants.TO_DECORATE.getName()));
				
		return toDecoratePanel;
	}
	
	private JPanel makeCharectisticPanel(){
		countryRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.COUNTRY.getName());
		yearRadioButon = MakeRadioButton.getRadioButton(DecoratorUIConstants.YEAR.getName());
		ahaSerotypeRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.A_HA_SUBTYPE.getName());
		anaSerotypeRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.A_NA_SYBTYPE.getName());
		hostRadioButton = MakeRadioButton.getRadioButton(DecoratorUIConstants.HOST_SPECIES.getName());
		
		characteristicButtonGroup = new ButtonGroup();
		characteristicButtonGroup.add(countryRadioButton);
		characteristicButtonGroup.add(yearRadioButon);
		characteristicButtonGroup.add(ahaSerotypeRadioButton);
		characteristicButtonGroup.add(anaSerotypeRadioButton);
		characteristicButtonGroup.add(hostRadioButton);
		
		countryRadioButton.addActionListener( this);
		yearRadioButon.addActionListener( this);
		ahaSerotypeRadioButton.addActionListener( this);
		anaSerotypeRadioButton.addActionListener( this);
		hostRadioButton.addActionListener( this);
		
		JPanel charPanel = MakeJPanel.getGridLayoutPanel(5,1);
		charPanel.add(countryRadioButton);
		charPanel.add(yearRadioButon);
		charPanel.add(ahaSerotypeRadioButton);
		charPanel.add(anaSerotypeRadioButton);
		charPanel.add(hostRadioButton);
		charPanel.setBorder( BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.
				createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()), DecoratorUIConstants.CHARACTERISTIC.getName()));
		
		return charPanel;
	}
	public void actionPerformed( ActionEvent ae){
		if (ae.getSource() == countryRadioButton ){
			if(selectedCharacteristic == DecoratorUIConstants.NULL){
				selectedCharacteristic = DecoratorUIConstants.COUNTRY;
				handleSelectUnselectCharacteristicRadioButtons(false);
				
				handleSelectUnselectToDecorateRadioButtons(true);
			}
		}
		else if (ae.getSource() == yearRadioButon ){
			if(selectedCharacteristic == DecoratorUIConstants.NULL){
				selectedCharacteristic = DecoratorUIConstants.YEAR;
				handleSelectUnselectCharacteristicRadioButtons(false);
				
				handleSelectUnselectToDecorateRadioButtons(true);
			}
		}
		else if (ae.getSource() == ahaSerotypeRadioButton ){
			if(selectedCharacteristic == DecoratorUIConstants.NULL){
				selectedCharacteristic = DecoratorUIConstants.A_HA_SUBTYPE;
				handleSelectUnselectCharacteristicRadioButtons(false);
				
				handleSelectUnselectToDecorateRadioButtons(true);
			}
		}
		else if (ae.getSource() == anaSerotypeRadioButton ){
			if(selectedCharacteristic == DecoratorUIConstants.NULL){
				selectedCharacteristic = DecoratorUIConstants.A_NA_SYBTYPE;
				handleSelectUnselectCharacteristicRadioButtons(false);
				
				handleSelectUnselectToDecorateRadioButtons(true);
			}
		}
		else if (ae.getSource() == hostRadioButton ){
			if(selectedCharacteristic == DecoratorUIConstants.NULL){
				selectedCharacteristic = DecoratorUIConstants.HOST_SPECIES;
				handleSelectUnselectCharacteristicRadioButtons(false);
				
				handleSelectUnselectToDecorateRadioButtons(true);
			}
		}
		else if(ae.getSource() == nodeRadioButton){
			if(selectedToDecorate == DecoratorUIConstants.NULL){
				selectedToDecorate = DecoratorUIConstants.NODE;
				handleSelectUnselectToDecorateRadioButtons(false);
				
				handleSelectUnselectDecorationStyleRadioButton(true);
			}
		}
		else if(ae.getSource() == strainRadioButton){
			if(selectedToDecorate == DecoratorUIConstants.NULL){
				selectedToDecorate = DecoratorUIConstants.STRAIN;
				handleSelectUnselectToDecorateRadioButtons(false);
				
				handleSelectUnselectDecorationStyleRadioButton(true);
			}
		}
		else if(ae.getSource() == nodeColorRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.NODE_COLOR;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		else if(ae.getSource() == shapesRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.SHAPES;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		else if(ae.getSource() == strainColorRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.STRAIN_COLOR;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		else if(ae.getSource() == caseRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.CASE;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		else if(ae.getSource() == fontRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.FONT;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		else if(ae.getSource() == styleRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.STYLE;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		else if(ae.getSource() == sizeRadioButton){
			if(selectedDecorationSyle == DecoratorUIConstants.NULL){
				selectedDecorationSyle = DecoratorUIConstants.SIZE;
				handleSelectUnselectDecorationStyleRadioButton(false);
			}
		}
		
		else if(ae.getSource() == backButton){
			handleClickToBack();
		}
		
		else if(ae.getSource() == closeButton){
			this.dispose();
		}
		
		else if(ae.getSource() == applyButton){
			//handle painting the panel and other operations here
			
			handleSemiDecoration();
			
			selectedDecorationSyle =  DecoratorUIConstants.NULL;
			handleSelectUnselectDecorationStyleRadioButton(false);
			selectedToDecorate =  DecoratorUIConstants.NULL;
			handleSelectUnselectToDecorateRadioButtons(false);
			selectedCharacteristic =  DecoratorUIConstants.NULL;
			handleSelectUnselectCharacteristicRadioButtons(true);
		}
		
		else if(ae.getSource() == defaultButton){
			//handle painting the panel and other operations here
			
			handleRevertToDefaultDecorations();
			
			selectedDecorationSyle =  DecoratorUIConstants.NULL;
			handleSelectUnselectDecorationStyleRadioButton(false);
			selectedToDecorate =  DecoratorUIConstants.NULL;
			handleSelectUnselectToDecorateRadioButtons(false);
			selectedCharacteristic =  DecoratorUIConstants.NULL;
			handleSelectUnselectCharacteristicRadioButtons(true);
		}
		
		
		else if(ae.getSource() == manualCheckBox){
			
			if(manualCheckBox.isSelected()){
				manualDecorateFrame = new ManualDecorateFrame(this);
				defaultButton.setEnabled(false);
				applyButton.setEnabled(false);
				closeButton.setEnabled(false);
				backButton.setEnabled(false);
			}
			else{
				//handle closing manual frame here
				if(manualDecorateFrame!=null){
					manualDecorateFrame.dispose();
				}
				defaultButton.setEnabled(true);
				applyButton.setEnabled(true);
				closeButton.setEnabled(true);
				backButton.setEnabled(true);
			}
		}
	}
	
	private void handleRevertToDefaultDecorations(){
		DecoratorTable.styleCharacteristicMapping.remove(selectedDecorationSyle);
		for(String charValue : DecoratorTable.decoratorTable.get(selectedCharacteristic).keySet()){
			DecoratorTable.decoratorTable.get(selectedCharacteristic).
				get(charValue).setAnyDecorationStyle(
						selectedDecorationSyle, DecorationEnumHelper.getDefaultDecorationStyles(selectedDecorationSyle));
		}
		if(selectedDecorationSyle == DecoratorUIConstants.STRAIN_COLOR){
			DecorationEnumHelper.populateBranchColorNodes();
		}
		// repaint all
		SubTreePanel.refreshAllWindows();
	}
	private void handleSemiDecoration(){
		DecoratorTable.styleCharacteristicMapping.put(selectedDecorationSyle, selectedCharacteristic);
		DecorationStyles[] decorationStylesValues = DecorationEnumHelper.getAllStyleValues(selectedDecorationSyle);
		int styleValuePos = 0;
		for(String charValue : DecoratorTable.decoratorTable.get(selectedCharacteristic).keySet()){
			if(styleValuePos == decorationStylesValues.length){
				styleValuePos = 0;
			}
			DecoratorTable.decoratorTable.get(selectedCharacteristic).
				get(charValue).setAnyDecorationStyle(selectedDecorationSyle, decorationStylesValues[styleValuePos]);
			styleValuePos++;
		}
		if(selectedDecorationSyle == DecoratorUIConstants.STRAIN_COLOR){
			DecorationEnumHelper.populateBranchColorNodes();
		}
		// repaint all
		SubTreePanel.refreshAllWindows();
	}
	public void dispose(){
		if(_frame!=null){
			_frame.setVisible(false);
			_frame.dispose();
		}
	}
	private void handleClickToBack(){
		if(selectedDecorationSyle != DecoratorUIConstants.NULL){
			
			selectedDecorationSyle = DecoratorUIConstants.NULL;
			handleSelectUnselectDecorationStyleRadioButton(true);
	//		selectedToDecorate = DecoratorUIConstants.NULL;
	//		handleSelectUnselectToDecorateRadioButtons(true);
		}
		else if(selectedToDecorate!=DecoratorUIConstants.NULL){
			handleSelectUnselectDecorationStyleRadioButton(false);
			selectedToDecorate = DecoratorUIConstants.NULL;
			handleSelectUnselectToDecorateRadioButtons(true);
	//		selectedCharacteristic = DecoratorUIConstants.NULL;
	//		handleSelectUnselectCharacteristicRadioButtons(true);
		}
		else if(selectedCharacteristic != DecoratorUIConstants.NULL){
			handleSelectUnselectToDecorateRadioButtons(false);
			selectedCharacteristic = DecoratorUIConstants.NULL;
			handleSelectUnselectCharacteristicRadioButtons(true);
		}
		
	}
	
	private void handleSelectUnselectCharacteristicRadioButtons(boolean enable){
		handleInfoLabels();
		if(enable){
			countryRadioButton.setEnabled(enable);
			yearRadioButon.setEnabled(enable);
			ahaSerotypeRadioButton.setEnabled(enable);
			anaSerotypeRadioButton.setEnabled(enable);
			hostRadioButton.setEnabled(enable);
			backButton.setEnabled(false);
		}
		else{ //disable
			if(selectedCharacteristic == DecoratorUIConstants.COUNTRY){
				yearRadioButon.setEnabled(enable);
				ahaSerotypeRadioButton.setEnabled(enable);
				anaSerotypeRadioButton.setEnabled(enable);
				hostRadioButton.setEnabled(enable);
				backButton.setEnabled(true);
			}
			else if(selectedCharacteristic == DecoratorUIConstants.YEAR){
				countryRadioButton.setEnabled(enable);
				ahaSerotypeRadioButton.setEnabled(enable);
				anaSerotypeRadioButton.setEnabled(enable);
				hostRadioButton.setEnabled(enable);
				backButton.setEnabled(true);
			}
			else if(selectedCharacteristic == DecoratorUIConstants.A_HA_SUBTYPE){
				countryRadioButton.setEnabled(enable);
				yearRadioButon.setEnabled(enable);
				anaSerotypeRadioButton.setEnabled(enable);
				hostRadioButton.setEnabled(enable);
				backButton.setEnabled(true);
			}
			else if(selectedCharacteristic == DecoratorUIConstants.A_NA_SYBTYPE){
				countryRadioButton.setEnabled(enable);
				yearRadioButon.setEnabled(enable);
				ahaSerotypeRadioButton.setEnabled(enable);
				hostRadioButton.setEnabled(enable);
				backButton.setEnabled(true);
			}
			else if(selectedCharacteristic == DecoratorUIConstants.HOST_SPECIES){
				countryRadioButton.setEnabled(enable);
				yearRadioButon.setEnabled(enable);
				ahaSerotypeRadioButton.setEnabled(enable);
				anaSerotypeRadioButton.setEnabled(enable);
				backButton.setEnabled(true);
			}
		}
	}
	
	private void handleSelectUnselectToDecorateRadioButtons(boolean enable){
		handleInfoLabels();
		if(enable){
			nodeRadioButton.setEnabled(enable);
			strainRadioButton.setEnabled(enable);
		}
		else{  //disable
			if(selectedToDecorate == DecoratorUIConstants.NODE){
				strainRadioButton.setEnabled(enable);
			}
			else if(selectedToDecorate == DecoratorUIConstants.STRAIN){
				nodeRadioButton.setEnabled(enable);
			}
			else if(selectedToDecorate == DecoratorUIConstants.NULL){
				strainRadioButton.setEnabled(enable);
				nodeRadioButton.setEnabled(enable);
			}
		}
	}
	
	private void handleSelectUnselectDecorationStyleRadioButton(boolean enable){
		handleInfoLabels();
		if(enable){
			if(selectedToDecorate == DecoratorUIConstants.STRAIN){
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.STRAIN_COLOR)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.STRAIN_COLOR) == selectedCharacteristic){
				strainColorRadioButton.setEnabled(enable);
					}
				}
				else strainColorRadioButton.setEnabled(enable);
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.CASE)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.CASE) == selectedCharacteristic){
						caseRadioButton.setEnabled(enable);
					}
				}
				else caseRadioButton.setEnabled(enable);
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.FONT)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.FONT) == selectedCharacteristic){
						fontRadioButton.setEnabled(enable);
					}
				}
				else fontRadioButton.setEnabled(enable);
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.STYLE)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.STYLE) == selectedCharacteristic){
						styleRadioButton.setEnabled(enable);
					}
				}
				else styleRadioButton.setEnabled(enable);
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.SIZE)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.SIZE) == selectedCharacteristic){
						sizeRadioButton.setEnabled(enable);
					}
				}
				else sizeRadioButton.setEnabled(enable);
			}
			else if(selectedToDecorate == DecoratorUIConstants.NODE){
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.NODE_COLOR)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.NODE_COLOR) == selectedCharacteristic){
						nodeColorRadioButton.setEnabled(enable);
					}
				}
				else nodeColorRadioButton.setEnabled(enable);
				if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.SHAPES)){
					if(DecoratorTable.styleCharacteristicMapping.get(DecoratorUIConstants.SHAPES) == selectedCharacteristic){
						shapesRadioButton.setEnabled(enable);
					}
				}
				else shapesRadioButton.setEnabled(enable);
			}
		}
		else{ //disable
			if(selectedToDecorate == DecoratorUIConstants.STRAIN){
				if(selectedDecorationSyle == DecoratorUIConstants.STRAIN_COLOR){
					caseRadioButton.setEnabled(enable);
					fontRadioButton.setEnabled(enable);
					styleRadioButton.setEnabled(enable);
					sizeRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
					
				}
				else if(selectedDecorationSyle == DecoratorUIConstants.CASE){
					strainColorRadioButton.setEnabled(enable);
					fontRadioButton.setEnabled(enable);
					styleRadioButton.setEnabled(enable);
					sizeRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
				}
				else if(selectedDecorationSyle == DecoratorUIConstants.FONT){
					strainColorRadioButton.setEnabled(enable);
					caseRadioButton.setEnabled(enable);
					styleRadioButton.setEnabled(enable);
					sizeRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
				}
				else if(selectedDecorationSyle == DecoratorUIConstants.STYLE){
					strainColorRadioButton.setEnabled(enable);
					caseRadioButton.setEnabled(enable);
					fontRadioButton.setEnabled(enable);
					sizeRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
				}
				else if(selectedDecorationSyle == DecoratorUIConstants.SIZE){
					strainColorRadioButton.setEnabled(enable);
					caseRadioButton.setEnabled(enable);
					fontRadioButton.setEnabled(enable);
					styleRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
				}
				else if(selectedDecorationSyle == DecoratorUIConstants.NULL){
					strainColorRadioButton.setEnabled(enable);
					caseRadioButton.setEnabled(enable);
					fontRadioButton.setEnabled(enable);
					styleRadioButton.setEnabled(enable);
					sizeRadioButton.setEnabled(enable);
					
					manualCheckBox.setEnabled(enable);
					applyButton.setEnabled(enable);
					defaultButton.setEnabled(enable);
				}
			}
			else if(selectedToDecorate == DecoratorUIConstants.NODE){
				 if(selectedDecorationSyle == DecoratorUIConstants.NODE_COLOR){
					shapesRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
				 }
				 else if(selectedDecorationSyle == DecoratorUIConstants.SHAPES){
					nodeColorRadioButton.setEnabled(enable);
					manualCheckBox.setEnabled(true);
					applyButton.setEnabled(true);
					defaultButton.setEnabled(true);
				 }
				 else if(selectedDecorationSyle == DecoratorUIConstants.NULL){
					nodeColorRadioButton.setEnabled(enable);
					shapesRadioButton.setEnabled(enable);
					
					manualCheckBox.setEnabled(enable);
					applyButton.setEnabled(enable);
					defaultButton.setEnabled(enable);
				 }
			}
		}
	}
	
	private void handleInfoLabels(){
		if(selectedCharacteristic == DecoratorUIConstants.NULL){
			selectedPathLabel.setText("");
			toSelectLabel.setText(DecoratorUIConstants.TO_SELECT_CHARACTERISTIC_LABEL_TEXT.getName());
		}
		else if(selectedToDecorate == DecoratorUIConstants.NULL){
			selectedPathLabel.setText(getHtmlTagString(DecoratorUIConstants.SELECTED_CHARACTERISTIC_LABEL_TEXT.getName()
					+ getBoldTagString(selectedCharacteristic.getName())));
			toSelectLabel.setText(DecoratorUIConstants.TO_SELECT_TO_DECORATE_LABEL_TEXT.getName());
		}
		else if(selectedDecorationSyle == DecoratorUIConstants.NULL){
			selectedPathLabel.setText(getHtmlTagString(DecoratorUIConstants.SELECTED_CHARACTERISTIC_LABEL_TEXT.getName() 
					+ getBoldTagString(selectedCharacteristic.getName())
					+ DecoratorUIConstants.SELECTED_TO_DECORATE_LABEL_TEXT.getName()
					+ getBoldTagString(selectedToDecorate.getName())));
			toSelectLabel.setText(DecoratorUIConstants.TO_SELECT_DECORATION_STYLE_LABEL_TEXT.getName()
					+selectedToDecorate.getName());
		}
		else {
			selectedPathLabel.setText(getHtmlTagString(DecoratorUIConstants.SELECTED_CHARACTERISTIC_LABEL_TEXT.getName() 
					+ getBoldTagString(selectedCharacteristic.getName())
					+ DecoratorUIConstants.SELECTED_TO_DECORATE_LABEL_TEXT.getName()
					+ getBoldTagString(selectedToDecorate.getName())
					+ DecoratorUIConstants.SELECTED_DECORATION_STYLE_LABEL_TEXT.getName()
					+ getBoldTagString(selectedDecorationSyle.getName())));
			toSelectLabel.setText(DecoratorUIConstants.TO_SELECT_MANUAL_APPLY_STYLE_LABEL_TEXT.getName());
		}
	}
	
	private String getBoldTagString(String s){
		return ("<B>"+s+"</B>");
	}
	
	private String getHtmlTagString(String s){
		return ("<html>"+s+"</html>");
	}
	
	public static void main (String[] args){
		SemiDecorateFrame sd = new SemiDecorateFrame();
	}
}
