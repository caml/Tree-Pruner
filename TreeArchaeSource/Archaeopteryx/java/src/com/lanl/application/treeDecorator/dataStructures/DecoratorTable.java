package com.lanl.application.treeDecorator.dataStructures;


import java.util.HashMap;
import java.util.Map;

import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

public class DecoratorTable {

	// (CharName => (CharValue->DecorateObject(node,strain))
	public static Map<DecoratorUIConstants, Map<String, DecorateObject>> decoratorTable = new HashMap<DecoratorUIConstants, Map<String, DecorateObject>>();
	//StyleName => charName
	public static Map<DecoratorUIConstants,DecoratorUIConstants> styleCharacteristicMapping
		= new HashMap<DecoratorUIConstants,DecoratorUIConstants>();
//	private static ArrayList<String> countryValues = new ArrayList<String>();
//	private static ArrayList<String> yearValues = new ArrayList<String>();
//	private static ArrayList<String> ahaValues = new ArrayList<String>();
//	private static ArrayList<String> anaValues = new ArrayList<String>();
//	private static ArrayList<String> hostValues = new ArrayList<String>();
	
	
	
	public static void decoratorTableInit(String[] countryNames, String[] year, String[] aha,
			String[] ana, String[] host) {
		Map<String, DecorateObject> tempMap;
		
		tempMap = new HashMap<String, DecorateObject>();
		for (int i = 0; i < countryNames.length; i++) {
			tempMap.put(countryNames[i], new DecorateObject());
		}
		decoratorTable.put(DecoratorUIConstants.COUNTRY, tempMap);
		
		tempMap = new HashMap<String, DecorateObject>();
		for (int i = 0; i < year.length; i++) {
			tempMap.put(year[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.YEAR, tempMap);
		
		tempMap = new HashMap<String, DecorateObject>();
		for (int i = 0; i < aha.length; i++) {
			tempMap.put(aha[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.A_HA_SUBTYPE, tempMap);
		
		tempMap = new HashMap<String, DecorateObject>();
		for (int i = 0; i < ana.length; i++) {
			tempMap.put(ana[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.A_NA_SYBTYPE, tempMap);
		tempMap = new HashMap<String, DecorateObject>();

		for (int i = 0; i < host.length; i++) {
			tempMap.put(host[i], new DecorateObject());

		}
		decoratorTable.put(DecoratorUIConstants.HOST_SPECIES, tempMap);
	}
}
