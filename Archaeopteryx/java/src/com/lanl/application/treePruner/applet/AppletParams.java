package com.lanl.application.treePruner.applet;

import java.net.MalformedURLException;
import java.net.URL;

public class AppletParams {

	public static String urlOfTreeToLoad = "";
	public static String configFilename = "";
	public static URL codeBase = null;
	public static String filename = "";
	public static String URLprefix = "";
	public static int applicationType = -1;
	public static String savedAcc = "";
	public static String savedAccFlag = "";
	
	public static void setAppletParams(String urlOfTreeToLoad1,String configFileName1,URL codeBase1, String filename1,
												String URLprefix1,int applicationType1, String savedAcc1, String savedAccFlag1){
		codeBase = codeBase1;
		if(urlOfTreeToLoad1.startsWith("http")){
			urlOfTreeToLoad = urlOfTreeToLoad1;
		}
		else{
			urlOfTreeToLoad = getFullURLString(urlOfTreeToLoad1);
		}
		if(configFileName1.startsWith("http")){
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
}
