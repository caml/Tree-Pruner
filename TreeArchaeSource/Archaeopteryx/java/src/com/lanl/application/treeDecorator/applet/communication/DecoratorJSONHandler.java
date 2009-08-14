package com.lanl.application.treeDecorator.applet.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.forester.phylogeny.PhylogenyNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.lanl.application.treeDecorator.dataStructures.DecorateObject;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treeDecorator.enumeration.CommunicationEnum;
import com.lanl.application.treeDecorator.enumeration.DecorationEnumHelper;
import com.lanl.application.treeDecorator.enumeration.DecorationStyles;
import com.lanl.application.treeDecorator.enumeration.DecoratorUIConstants;

public class DecoratorJSONHandler {
	
	static String sequenceDetailsJSON = null;
	public static void storeSavedStyles(String incomingSavedStyles){  //crash recovery
		try {
			JSONTokener incomingSavedStylesJT = new JSONTokener(incomingSavedStyles);
			JSONObject incomingSavedStylesJO = new JSONObject(incomingSavedStylesJT);
			
			JSONObject  savedStylesJO = incomingSavedStylesJO.
				getJSONObject(CommunicationEnum.DECORATION.getName());
			
			if(savedStylesJO.has(DecoratorUIConstants.NODE.getName())){
				JSONObject nodeSavedStylesJO =  savedStylesJO.getJSONObject(DecoratorUIConstants.NODE.getName());
				Iterator nodeIterator = nodeSavedStylesJO.keys();
				while(nodeIterator.hasNext()){
					String styleName = nodeIterator.next().toString();
					String charName = nodeSavedStylesJO.getJSONObject(styleName).
						getString(DecoratorUIConstants.CHARACTERISTIC.getName());
					DecoratorUIConstants charNameObj = 
						DecorationEnumHelper.getDecoratorUIConstantsObject(charName);
					DecoratorUIConstants styleNameObj = 
						DecorationEnumHelper.getDecoratorUIConstantsObject(styleName);
					DecoratorTable.styleCharacteristicMapping.
						put(styleNameObj,charNameObj);
					JSONObject decorationJO = nodeSavedStylesJO.getJSONObject(styleName).
						getJSONObject(CommunicationEnum.DECORATED_BY.getName());
					Iterator decorateIterator = decorationJO.keys();
					while(decorateIterator.hasNext()){
						DecorationStyles styleValue = DecorationEnumHelper.
							getDecorationStylesObject(decorateIterator.next().toString());
						JSONArray charValueArray = decorationJO.getJSONArray(styleValue.getName());
						for(int i=0; i<charValueArray.length();i++){
							DecoratorTable.decoratorTable.get(charNameObj).
								get(charValueArray.get(i)).setAnyDecorationStyle(styleNameObj, styleValue);
						}
					}
				}
			}
			else if(savedStylesJO.has(DecoratorUIConstants.STRAIN.getName())){
				JSONObject strainSavedStylesJO =  savedStylesJO.getJSONObject(DecoratorUIConstants.STRAIN.getName());
				Iterator strainIterator = strainSavedStylesJO.keys();
				while(strainIterator.hasNext()){
					String styleName = strainIterator.next().toString();
					String charName = strainSavedStylesJO.getJSONObject(styleName).
						getString(DecoratorUIConstants.CHARACTERISTIC.getName());
					DecoratorUIConstants charNameObj = 
						DecorationEnumHelper.getDecoratorUIConstantsObject(charName);
					DecoratorUIConstants styleNameObj = 
						DecorationEnumHelper.getDecoratorUIConstantsObject(styleName);
					DecoratorTable.styleCharacteristicMapping.
						put(styleNameObj,charNameObj);
					JSONObject decorationJO = strainSavedStylesJO.getJSONObject(styleName).
						getJSONObject(CommunicationEnum.DECORATED_BY.getName());
					Iterator decorateIterator = decorationJO.keys();
					while(decorateIterator.hasNext()){
						DecorationStyles styleValue = DecorationEnumHelper.
							getDecorationStylesObject(decorateIterator.next().toString());
						JSONArray charValueArray = decorationJO.getJSONArray(styleValue.getName());
						for(int i=0; i<charValueArray.length();i++){
							DecoratorTable.decoratorTable.get(charNameObj).
								get(charValueArray.get(i)).setAnyDecorationStyle(styleNameObj, styleValue);
						}
					}
				}
			}
		//	print(DecoratorTable.decoratorTable);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void print(Map<DecoratorUIConstants, Map<String, DecorateObject>> decoratorTable){  //temp method
		for (DecoratorUIConstants dui: decoratorTable.keySet()){
			System.out.println();
			System.out.println(dui.getName());
			for (String m: decoratorTable.get(dui).keySet()){
				System.out.println(m);
				System.out.println(decoratorTable.get(dui).get(m).toString());
			}
		}
	}
	
	public static void storeCharacteristicValues(String incomingSeqDetailsString){   //get seq details
		ArrayList<String> countryValuesList = new ArrayList<String>();
    	ArrayList<String> yearValuesList = new ArrayList<String>();
		ArrayList<String> ahaValuesList = new ArrayList<String>();
		ArrayList<String> anaValuesList = new ArrayList<String>();
		ArrayList<String> hostValuesList = new ArrayList<String>();
		try{
			JSONTokener incomingSeqDetailsJT = new JSONTokener(incomingSeqDetailsString);
			JSONObject incomingSeqDetailsJO = new JSONObject(incomingSeqDetailsJT);
			
			sequenceDetailsJSON = new String(incomingSeqDetailsString);
			JSONObject  charValuesJO = incomingSeqDetailsJO.getJSONObject(CommunicationEnum.SEQUENCE_DETAILS.getName());
			Iterator iterator = charValuesJO.keys();
			while(iterator.hasNext()){
				String key = iterator.next().toString();
				String c = charValuesJO.getJSONObject(key).
					getString(CommunicationEnum.COUNTRY.getName());
				String y = charValuesJO.getJSONObject(key).
					getString(CommunicationEnum.YEAR.getName());
				String ha = charValuesJO.getJSONObject(key).
					getString(CommunicationEnum.HA.getName());
				String na = charValuesJO.getJSONObject(key).
					getString(CommunicationEnum.NA.getName());
				String host = charValuesJO.getJSONObject(key).
					getString(CommunicationEnum.HOST.getName());
				
				if(!countryValuesList.contains(c)&&!c.equals("null")){
					countryValuesList.add(c);
				}
				if(!yearValuesList.contains(y)&&!y.equals("null")){
					yearValuesList.add(y);
				}
				if(!ahaValuesList.contains(ha)&&!ha.equals("null")){
					ahaValuesList.add(ha);
				}
				if(!anaValuesList.contains(na)&&!na.equals("null")){
					anaValuesList.add(na);
				}
				if(!hostValuesList.contains(host)&&!host.equals("null")){
					hostValuesList.add(host);
				}
			}
		} catch (JSONException e) {
			TreeDecoratorCommunication.showServerErrorJOptionPane(CommunicationEnum.SERVER_ERROR_SEQ_DETAILS.getName(), 
	    			CommunicationEnum.SERVER_ERROR.getName(), e);
			TreeDecoratorCommunication.isCommError = true;
	    }
		String[] countryValueArray = (String []) countryValuesList.toArray (new String [countryValuesList.size ()]);
		String [] yearValueArray = (String []) yearValuesList.toArray (new String [yearValuesList.size ()]);
		String [] ahaValueArray = (String []) ahaValuesList.toArray (new String [ahaValuesList.size ()]);
		String [] anaValueArray = (String []) anaValuesList.toArray (new String [countryValuesList.size ()]);
		String [] hostrValueArray = (String []) hostValuesList.toArray (new String [hostValuesList.size ()]);
		
		DecoratorTable.decoratorTableInit(countryValueArray,yearValueArray,ahaValueArray,anaValueArray,hostrValueArray);
	//	print(DecoratorTable.decoratorTable);
	}
	
	public static String getSavedStyles(){ //save/autosave
		Map<String,ArrayList<String>> decorationMap;
		ArrayList<String> charValueList;
		JSONObject mainJO = new JSONObject();
		JSONObject innerNodeJO = new JSONObject();
		JSONObject innerStrainJO = new JSONObject();
		try{
			for(DecoratorUIConstants styleName: DecoratorTable.styleCharacteristicMapping.keySet()){
				DecoratorUIConstants charName = DecoratorTable.styleCharacteristicMapping.get(styleName);
				Map<String,DecorateObject> decoratorTableForCharName = DecoratorTable.decoratorTable.get(charName);
				decorationMap = new HashMap<String,ArrayList<String>>();
				DecorationStyles styleValue = DecorationStyles.NULL;
				for(String charValue : decoratorTableForCharName.keySet() ){
					styleValue = decoratorTableForCharName.
						get(charValue).getAnyDecorationStyle(styleName);
					if(!DecorationEnumHelper.isStyleValueDefault(styleName, styleValue)){
						if(decorationMap.containsKey(styleValue.getName())){
							decorationMap.get(styleValue.getName()).add(charValue);
						}
						else{
							charValueList = new ArrayList<String>();
							charValueList.add(charValue);
							decorationMap.put(styleValue.getName(),charValueList);
						}
					}
				}
				JSONObject decorationJO = new JSONObject(decorationMap);
				JSONObject styleNameJO = new JSONObject();
				styleNameJO.put(DecoratorUIConstants.CHARACTERISTIC.getName(), charName.getName());
				styleNameJO.put(CommunicationEnum.DECORATED_BY.getName(), decorationJO);
				DecoratorUIConstants toDecorate = DecorationEnumHelper.getNodeOrStrain(styleName);
				if(toDecorate == DecoratorUIConstants.NODE){
					if(innerNodeJO.has(styleName.getName())){
						innerNodeJO.accumulate(styleName.getName(), styleNameJO);
					}
					else{
						innerNodeJO.put(styleName.getName(), styleNameJO);
					}
				}
				else if(toDecorate == DecoratorUIConstants.STRAIN){
					if(innerStrainJO.has(styleName.getName())){
						innerStrainJO.accumulate(styleName.getName(), styleNameJO);
					}
					else{
						innerStrainJO.put(styleName.getName(), styleNameJO);
					}
				}
			}
			if(innerNodeJO.length()>0){
				mainJO.put(CommunicationEnum.DECORATION.getName(), 
						new JSONObject().put(DecoratorUIConstants.NODE.getName(), innerNodeJO));
			}
			if(innerStrainJO.length()>0){
				if(mainJO.has(CommunicationEnum.DECORATION.getName())){
					mainJO.accumulate(CommunicationEnum.DECORATION.getName(), 
							new JSONObject().put(DecoratorUIConstants.STRAIN.getName(), innerStrainJO));
				}
				else{
					mainJO.put(CommunicationEnum.DECORATION.getName(), 
							new JSONObject().put(DecoratorUIConstants.STRAIN.getName(), innerStrainJO));	
				}
			}
			//System.out.println(mainJO.toString(2));
			if(mainJO.length()>0){
				return mainJO.toString();
			}
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void setExtraNodeInfoForTD(PhylogenyNode node){
		try{
			if(node.extraNodeInfo.getNodeAcc()!=null){
				JSONTokener incomingSeqDetailsJT = new JSONTokener(sequenceDetailsJSON);
				JSONObject incomingSeqDetailsJO = new JSONObject(incomingSeqDetailsJT);
				JSONObject  charValuesJO = incomingSeqDetailsJO.getJSONObject(CommunicationEnum.SEQUENCE_DETAILS.getName());
				
				String c  = charValuesJO.getJSONObject(node.extraNodeInfo.getNodeAcc()).
					getString(CommunicationEnum.COUNTRY.getName());
				String y = charValuesJO.getJSONObject(node.extraNodeInfo.getNodeAcc()).
					getString(CommunicationEnum.YEAR.getName());
				String ha = charValuesJO.getJSONObject(node.extraNodeInfo.getNodeAcc()).
					getString(CommunicationEnum.HA.getName());
				String na = charValuesJO.getJSONObject(node.extraNodeInfo.getNodeAcc()).
					getString(CommunicationEnum.NA.getName());
				String host = charValuesJO.getJSONObject(node.extraNodeInfo.getNodeAcc()).
					getString(CommunicationEnum.HOST.getName());
				
				node.extraNodeInfo.setNodeCountry(c);
				node.extraNodeInfo.setNodeYear(y);
				node.extraNodeInfo.setNodeHA(ha);
				node.extraNodeInfo.setNodeNA(na);
				node.extraNodeInfo.setNodeHost(host);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
