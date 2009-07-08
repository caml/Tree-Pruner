package com.lanl.application.treePruner.applet;

import java.net.URL;

public class AppletParams {

	public static String urlOfTreeToLoad;
	public static String configFilename;
	public static URL codeBase;
	public static String filename;
	public static String URLprefix;
	public static int applicationType;
	public static String savedAcc;
	public static String savedAccFlag;
	
	public static void setAppletParams(String urlOfTreeToLoad1,String configFileName1,URL codeBase1, String filename1,
												String URLprefix1,int applicationType1, String savedAcc1, String savedAccFlag1){
		urlOfTreeToLoad = urlOfTreeToLoad1;
		configFilename = configFileName1; 
		codeBase = codeBase1;
		filename = filename1;
		URLprefix = URLprefix1; 
		applicationType = applicationType1;
		savedAcc = savedAcc1;
		savedAccFlag = savedAccFlag1;
		
	}
}
