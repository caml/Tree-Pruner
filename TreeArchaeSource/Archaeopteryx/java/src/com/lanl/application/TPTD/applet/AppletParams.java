package com.lanl.application.TPTD.applet;

import java.net.MalformedURLException;
import java.net.URL;

public class AppletParams {

	public static String urlOfTreeToLoad = "";
	public static String configFilename = "";
	public static URL codeBase = null;
	public static String filename = "";
	public static String URLprefix = "";
	/**
	 * application type: <br>(Others are those who are using the software from open source distribution)<br><br>
	 * -1,"": Archaeopteryx for Others<br>
	 *  0: Archaeopteryx for LANL/BHB <br>
	 *  1: Tree Pruner for BHB<br>
	 *  2: Tree Pruner for LANL<br>
	 *  3: Tree Pruner for Others<br>
	 *  4: Tree Decorator for BHB<br>
	 *  5: Tree Decorator for LANL<br>
	 *  6: Tree Decorator for Others<br>
	 *  
	 */
	public static int applicationType = -1;     
	public static String savedAccFlag = "";
	public static String tabName = "";
	public static String remoteUser = ""; //LANL only
	public static boolean isFluTypeA = false;
	//****AC**** New version has this parameter when reading phylogenies from URL of tree or subtree
	public static boolean phyloxml_validate_against_xsd = false;
	
//	public static void setAppletParams(String urlOfTreeToLoad1,String configFileName1,URL codeBase1, String filename1,
//												String URLprefix1,int applicationType1, String savedAccFlag1, String tabName1,
//												String user1){
	public static void setAppletParams(String urlOfTreeToLoad1,String configFileName1,URL codeBase1, String filename1,
			String URLprefix1,int applicationType1, String savedAccFlag1, String tabName1,
			String user1, boolean phyloxml_validate_against_xsd1){
		codeBase = codeBase1;
		if(urlOfTreeToLoad1.startsWith("http")||urlOfTreeToLoad1.startsWith("file")){
			urlOfTreeToLoad = urlOfTreeToLoad1;
		}
		else{
			urlOfTreeToLoad = getFullURLString(urlOfTreeToLoad1);
		}
		if(configFileName1.startsWith("http")||configFileName1.startsWith("file")){
			configFilename = configFileName1;
		}
		else{
			configFilename = getFullURLString(configFileName1);
		}
		phyloxml_validate_against_xsd=phyloxml_validate_against_xsd1;
		filename = filename1;
		URLprefix = URLprefix1; 
		applicationType = applicationType1;
		savedAccFlag = savedAccFlag1;
		tabName = tabName1;
		remoteUser = user1;  //LANL only
		
	}
	
	public static String getAllAppletParamsAsString(){
		String params = "";
		params+= "\nApplet Parameters:";
		params+= "\nURL of Tree To Load: "+urlOfTreeToLoad;
		params+= "\nConfig file url: "+configFilename;
		params+= "\nCodebase: "+codeBase;
		params+= "\nFilename: "+filename;
		params+= "\nURL Prefix: "+URLprefix;
		params+= "\nApplication type: "+applicationType;
		params+= "\nSaved Accession Flag: "+savedAccFlag;
		params+= "\nTab Name: "+tabName;
		params+= "\nRemote User: "+remoteUser;
		params+= "\nPhyloxml Validate Against XSD Bool: "+phyloxml_validate_against_xsd;
		return params;
	}
	
	private static String getFullURLString(String partialURL){
		String fullURL="";
		try {
			URL u = new URL(codeBase, partialURL);
			fullURL = u.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return fullURL;
	}
	
	public static boolean isEitherTPorTDForLANLorBHB(){
		if(applicationType == 1 || applicationType == 4   //BHB
				|| applicationType == 2 || applicationType == 5){  //LANL
				 
			return true;
		}
		return false;
	}
	
	public static boolean isEitherTPorTDForAll(){
		if(applicationType == 1 || applicationType == 4   //BHB
				|| applicationType == 2 || applicationType == 5 //LANL
				||applicationType == 3 || applicationType == 6){ //Others
				 
			return true;
		}
		return false;
	}
	
	public static boolean isEitherTPorTDForLANL(){
		if(applicationType == 2 || applicationType == 5){  //Others 
			return true;
		}
		return false;
	}
	
	public static boolean isEitherTPorTDForOthers(){
		if(applicationType == 3 || applicationType == 6){  //Others 
			return true;
		}
		return false;
	}
	
	
	
	public static boolean isTreePrunerForBHB(){
		if(applicationType == 1){
			return true;
		}
		return false;
	}
	
	public static boolean isTreeDecoratorForBHB(){
		if(applicationType == 4){
			return true;
		}
		return false;
	}
	
	public static boolean isTreePrunerForLANL(){
		if(applicationType == 2){
			return true;
		}
		return false;
	}
	
	public static boolean isTreeDecoratorForLANL(){
		if(applicationType == 5){
			return true;
		}
		return false;
	}
	
	public static boolean isTreePrunerForOthers(){
		if(applicationType == 3){
			return true;
		}
		return false;
	}
	public static boolean isTreeDecoratorForOthers(){
		if(applicationType == 6){
			return true;
		}
		return false;
	}
	
	public static boolean isTreePrunerForAll(){
		if(applicationType == 1 || applicationType == 2 || applicationType == 3 ){
			return true;
		}
		return false;
	}
	
	public static boolean isTreeDecoratorForAll(){
		if(applicationType == 4 ||applicationType == 5 || applicationType == 6 ){
			return true;
		}
		return false;
	}
	// Unused: Kept If Requirements arise
	public static boolean isArchaeopteryxForBHBorLANL(){
		if(applicationType == 0 ){
			return true;
		}
		return false;
	}
	
	public static boolean isArchaeopteryxForOthers(){
		if(applicationType == -1 ){
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
}
