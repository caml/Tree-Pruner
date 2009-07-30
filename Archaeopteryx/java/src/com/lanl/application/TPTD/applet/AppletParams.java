package com.lanl.application.TPTD.applet;

import java.net.MalformedURLException;
import java.net.URL;

public class AppletParams {

	public static String urlOfTreeToLoad = "";
	public static String configFilename = "";
	public static URL codeBase = null;
	public static String filename = "";
	public static String URLprefix = "";
	public static int applicationType = -1;    // 0= TP, 1 = TD, 2 BHB/LANL Archae, else Archae (-1)
	public static String savedAcc = "";
	public static String savedAccFlag = "";
	public static String tabName = "";
	
	public static void setAppletParams(String urlOfTreeToLoad1,String configFileName1,URL codeBase1, String filename1,
												String URLprefix1,int applicationType1, String savedAcc1, String savedAccFlag1, String tabName1){
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
		filename = filename1;
		URLprefix = URLprefix1; 
		applicationType = applicationType1;
		savedAcc = savedAcc1;
		savedAccFlag = savedAccFlag1;
		tabName = tabName1;
		
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
	
	public static boolean isEitherTPorTD(){
		if(applicationType == 0 || applicationType == 1){
			return true;
		}
		return false;
	}
	
	public static boolean isTreePruner(){
		if(applicationType == 0){
			return true;
		}
		return false;
	}
	
	public static boolean isTreeDecorator(){
		if(applicationType == 1){
			return true;
		}
		return false;
	}
	
}
