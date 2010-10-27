package com.lanl.application.treeDecorator.dataStructures;


import java.util.HashMap;
import java.util.Map;

import com.lanl.application.treeDecorator.applet.communication.DecoratorJSONHandler;
import com.lanl.application.treeDecorator.applet.ui.frames.LegendFrame;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

public class DecoratorTable {
	
	// (CharName => (CharValue->DecorateObject(node,strain))
	public static Map<DecoratorUIConstants, CharValueAndDecorationMap<String, DecorateObject>> decoratorTable = new HashMap<DecoratorUIConstants, CharValueAndDecorationMap<String, DecorateObject>>();
	//StyleName => charName
	public static Map<DecoratorUIConstants,DecoratorUIConstants> styleCharacteristicMapping
		= new HashMap<DecoratorUIConstants,DecoratorUIConstants>();
	
	public static Map<DecoratorUIConstants, CharValueAndDecorationMap<String, DecorateObject>> savedDecoratorTable = new HashMap<DecoratorUIConstants, CharValueAndDecorationMap<String, DecorateObject>>();
	
	public static Map<DecoratorUIConstants,DecoratorUIConstants> savedStyleCharacteristicMapping
		= new HashMap<DecoratorUIConstants,DecoratorUIConstants>();
	
	public static Map<Integer,DecorationStyles> nodeIDStyleValuesForBranchColoring 
										= new HashMap<Integer,DecorationStyles>();
	
	public static LegendFrame legendFrame;
	public static void decoratorTableInit(String[] countryNames, String[] year, String[] aha,
			String[] ana, String[] host) {
		clearAllTreeDecoratorDataStructures();
		CharValueAndDecorationMap<String, DecorateObject> tempMap;
		
		tempMap = new CharValueAndDecorationMap<String, DecorateObject>();
		for (int i = 0; i < countryNames.length; i++) {
			tempMap.put(countryNames[i], new DecorateObject());
		}
		decoratorTable.put(DecoratorUIConstants.COUNTRY, tempMap);
		
		tempMap = new CharValueAndDecorationMap<String, DecorateObject>();
		for (int i = 0; i < year.length; i++) {
			tempMap.put(year[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.YEAR, tempMap);
		
		tempMap = new CharValueAndDecorationMap<String, DecorateObject>();
		for (int i = 0; i < aha.length; i++) {
			tempMap.put(aha[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.A_HA_SUBTYPE, tempMap);
		
		tempMap = new CharValueAndDecorationMap<String, DecorateObject>();
		for (int i = 0; i < ana.length; i++) {
			tempMap.put(ana[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.A_NA_SYBTYPE, tempMap);
		tempMap = new CharValueAndDecorationMap<String, DecorateObject>();

		for (int i = 0; i < host.length; i++) {
			tempMap.put(host[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.HOST_SPECIES, tempMap);
		legendFrame = new LegendFrame();
	}
	
	public static void clearAllTreeDecoratorDataStructures(){
		decoratorTable.clear();
		styleCharacteristicMapping.clear();
		savedDecoratorTable.clear();
		savedStyleCharacteristicMapping.clear();
		clearBranchColoring();
		if(legendFrame!=null){
			legendFrame.dispose();
		}
	}
	
	public static void resetDecorations(){
		if(DecoratorJSONHandler.getSequenceDetailsJSON()!=null){
			DecoratorJSONHandler.storeCharacteristicValues(DecoratorJSONHandler.getSequenceDetailsJSON());
		}
	}
	
	public static void copyStuffToSavedStuff(){
		savedDecoratorTable.clear();
		savedStyleCharacteristicMapping.clear();
		CharValueAndDecorationMap<String, DecorateObject> tempMap;
		for(DecoratorUIConstants charName: decoratorTable.keySet()){
			tempMap = new CharValueAndDecorationMap<String, DecorateObject>();
			for(String charValue: decoratorTable.get(charName).keySet()){
				tempMap.put(charValue, new DecorateObject(
						decoratorTable.get(charName).get(charValue).getNodeShape(),
						decoratorTable.get(charName).get(charValue).getNodeColor(),
						decoratorTable.get(charName).get(charValue).getStrainColor(),
						decoratorTable.get(charName).get(charValue).getStrainFont(),
						decoratorTable.get(charName).get(charValue).getStrainCase(),
						decoratorTable.get(charName).get(charValue).getStrainStyle(),
						decoratorTable.get(charName).get(charValue).getStrainSize()));
				savedDecoratorTable.put(charName, tempMap);
			}
		}
		for(DecoratorUIConstants styleName:styleCharacteristicMapping.keySet()){
			savedStyleCharacteristicMapping.put(styleName, styleCharacteristicMapping.get(styleName));
		}
	}
	
	public static void copySavedStuffToStuff(){
		if(!savedDecoratorTable.isEmpty()){
			decoratorTable.clear();
			styleCharacteristicMapping.clear();
			clearBranchColoring();
			CharValueAndDecorationMap<String, DecorateObject> tempMap;
			for(DecoratorUIConstants charName: savedDecoratorTable.keySet()){
				tempMap = new CharValueAndDecorationMap<String, DecorateObject>();
				for(String charValue: savedDecoratorTable.get(charName).keySet()){
					tempMap.put(charValue, new DecorateObject(
							savedDecoratorTable.get(charName).get(charValue).getNodeShape(),
							savedDecoratorTable.get(charName).get(charValue).getNodeColor(),
							savedDecoratorTable.get(charName).get(charValue).getStrainColor(),
							savedDecoratorTable.get(charName).get(charValue).getStrainFont(),
							savedDecoratorTable.get(charName).get(charValue).getStrainCase(),
							savedDecoratorTable.get(charName).get(charValue).getStrainStyle(),
							savedDecoratorTable.get(charName).get(charValue).getStrainSize()));
					decoratorTable.put(charName, tempMap);
				}
			}
			for(DecoratorUIConstants styleName:savedStyleCharacteristicMapping.keySet()){
				styleCharacteristicMapping.put(styleName, savedStyleCharacteristicMapping.get(styleName));
			}
			DecorationEnumHelper.populateBranchColorNodes();
			updateLegend();
		}
	
		
		
	}
	
	public static boolean toSave(){
		boolean toSave = false;
		if(savedDecoratorTable.isEmpty()){
			return true;
		}
		for(DecoratorUIConstants charName: decoratorTable.keySet()){
			for(String charValue: decoratorTable.get(charName).keySet()){
				if(decoratorTable.get(charName).get(charValue).isEqual(savedDecoratorTable.get(charName).get(charValue))){
					toSave = false;
				}
				else{
					toSave = true;
					break;
				}
			}
			if(toSave == true){
				break;
			}
		}
		return toSave;
	}
	
	public static void clearBranchColoring(){
		nodeIDStyleValuesForBranchColoring.clear();
	}
	
	public static void updateLegend(){
		legendFrame.countryJPanel.repaint();
		legendFrame.yearJPanel.repaint();
		legendFrame.ahaJPanel.repaint();
		legendFrame.anaJPanel.repaint();
		legendFrame.hostJPanel.repaint();
	}
	
	public static String getFormattedDecoratorTable(){
		String s ="\n\n\nDecorator Table:";
		for (DecoratorUIConstants dui: decoratorTable.keySet()){
			s+= "\n\nCharacteristic Name: "+dui.getName();
			for (String m: decoratorTable.get(dui).keySet()){
				s+= "\nCharacteristic Value: "+m;
				s+= "\nDecoration Style Values:";
				s+= decoratorTable.get(dui).get(m).toString();
			}
		}
		return s;
	}
}
