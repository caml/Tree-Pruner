package com.lanl.application.treeDecorator.applet.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.lanl.application.TPTD.applet.AppletParams;
import com.lanl.application.treeDecorator.enumeration.CommunicationEnum;
import com.lanl.application.treePruner.applet.TreePrunerCommunicationNames;


public class TreeDecoratorCommunication {
	static URL seqDetailsURL;
	static String seqDetails;
	public static boolean isCommError = false;
	
	private static String connectToServerForGET(CommunicationEnum getWhat){
		isCommError = false;
		if(getWhat == CommunicationEnum.GET_WHAT_SEQ_DETAILS){
			String seqDetailsJSONString="";
			try {
				seqDetailsURL= new URL(seqDetails);
				HttpURLConnection seqDetailsConn = (HttpURLConnection) seqDetailsURL.openConnection();
				seqDetailsConn.setRequestMethod("GET");
				BufferedReader rd;
				seqDetailsConn.setDoInput (true);
		        rd = new BufferedReader(new InputStreamReader(seqDetailsConn.getInputStream()));
		        String line;
		        while ((line = rd.readLine()) != null) {
		        	seqDetailsJSONString += line; 
		        }
		        rd.close();
		        seqDetailsConn.disconnect();
		        return seqDetailsJSONString;
		    } catch (MalformedURLException e1) {
		    	showServerErrorJOptionPane(CommunicationEnum.SERVER_ERROR_SEQ_DETAILS.getName(), 
		    			CommunicationEnum.SERVER_ERROR.getName(), e1);
		    	isCommError = true;
		    } catch (IOException e) {
				showServerErrorJOptionPane(CommunicationEnum.SERVER_ERROR_SEQ_DETAILS.getName(), 
		    			CommunicationEnum.SERVER_ERROR.getName(), e);
				isCommError = true;
		    } 
		}
		else if(getWhat == CommunicationEnum.GET_WHAT_SAVED_DECORATIONS){
			return "";
		}
		return "";
	}
	
	public static void getSequenceDetailsComm(){
		seqDetails  = AppletParams.URLprefix + "/sd/"+AppletParams.filename;
		String seqDetailsJSONString = connectToServerForGET(CommunicationEnum.GET_WHAT_SEQ_DETAILS);
		if(!seqDetailsJSONString.equals("")){
			DecoratorJSONHandler.storeCharacteristicValues(seqDetailsJSONString);
		}
	}
	
	protected static void showServerErrorJOptionPane(String msg,String title,Exception e){
		JOptionPane.showMessageDialog( null, msg+"\n\n"+e.toString(),
    			title,JOptionPane.ERROR_MESSAGE);
	}
		

}
