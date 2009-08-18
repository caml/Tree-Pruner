package com.lanl.application.treeDecorator.applet;

import java.awt.Graphics;
import java.awt.Point;

import org.forester.archaeopteryx.TreePanel;
import org.forester.phylogeny.PhylogenyNode;

import com.lanl.application.treeDecorator.applet.ui.drawDecoration.DecoratorColorSet;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

public class TreeDecoratorPaint {
	public TreePanel treePanel;
	private static int height = 5;
	private static int width = 5;

	public TreeDecoratorPaint(TreePanel tp) {
		this.treePanel = tp;
	}
	
	public void decorateNodeBox(Graphics g,int x,int y, PhylogenyNode node) {
		DecorationStyles shape,color;
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.NODE_COLOR)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.NODE_COLOR));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					color = DecoratorTable.decoratorTable.get(charName).get(charValue).getNodeColor();
				}
				else{
					color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.NODE_COLOR);
				}
			}
			else{
				color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.NODE_COLOR);
			}
		}
		else{
			color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.NODE_COLOR);
		}
		
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.SHAPES)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.SHAPES));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					shape = DecoratorTable.decoratorTable.get(charName).get(charValue).getNodeShape();
				}
				else{
					shape = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SHAPES);
				}
			}
			else{
				shape = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SHAPES);
			}
		}
		else{
			shape = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SHAPES);
		}
		
		DecorationEnumHelper.drawShapesWithColor(shape, g, new Point(x,y), height, width, color);
	}
	
	public void decorateStrain(Graphics g,int x, int y,String s, PhylogenyNode node) {
		DecorationStyles color,_case,font, style,size;
		
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.STRAIN_COLOR)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.STRAIN_COLOR));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					color = DecoratorTable.decoratorTable.get(charName).get(charValue).getStrainColor();
				}
				else{
					color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR);
				}
			}
			else{
				color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR);
			}
		}
		else{
			color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR);
		}
		
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.CASE)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.CASE));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					_case = DecoratorTable.decoratorTable.get(charName).get(charValue).getStrainCase();
				}
				else{
					_case = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.CASE);
				}
			}
			else{
				_case = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.CASE);
			}
		}
		else{
			_case = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.CASE);
		}
		
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.FONT)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.FONT));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					font = DecoratorTable.decoratorTable.get(charName).get(charValue).getStrainFont();
				}
				else{
					font = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.FONT);
				}
			}
			else{
				font = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.FONT);
			}
		}
		else{
			font = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.FONT);
		}
		
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.STYLE)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.STYLE));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					style = DecoratorTable.decoratorTable.get(charName).get(charValue).getStrainStyle();
				}
				else{
					style = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STYLE);
				}
			}
			else{
				style = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STYLE);
			}
		}
		else{
			style = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STYLE);
		}
		
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.SIZE)){
			DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get((DecoratorUIConstants.SIZE));
			String charValue = DecorationEnumHelper.getCharValueForCharNameFromNode(charName, node);
			if(charValue!=null ){
				if(!charValue.equals("null")){
					size = DecoratorTable.decoratorTable.get(charName).get(charValue).getStrainSize();
				}
				else{
					size = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SIZE);
				}
			}
			else{
				size = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SIZE);
			}
		}
		else{
			size = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.SIZE);
		}
		
		DecorationEnumHelper.drawStrainWithColorFontCase(g, DecorationEnumHelper.getStringWithCase(s, _case),
				DecorationEnumHelper.getFont(font, style, size),
				new Point(x,y), style, color);
	}
	
	public void decorateBranch(Graphics g, PhylogenyNode node) {
		DecorationStyles color;
		if(DecoratorTable.styleCharacteristicMapping.containsKey(DecoratorUIConstants.STRAIN_COLOR)){
			if(!DecoratorTable.nodeIDStyleValuesForBranchColoring.isEmpty()){
				if(DecoratorTable.nodeIDStyleValuesForBranchColoring.containsKey(node.getNodeId())){
					color = DecoratorTable.nodeIDStyleValuesForBranchColoring.get(node.getNodeId());
				}
				else{
					color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR);
				}
			}
			else{
				color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR);
			}
		}
		else{
			color = DecorationEnumHelper.getDefaultDecorationStyles(DecoratorUIConstants.STRAIN_COLOR);
		}
		
		g.setColor(DecorationEnumHelper.getColor(color));   
	}
	
}
