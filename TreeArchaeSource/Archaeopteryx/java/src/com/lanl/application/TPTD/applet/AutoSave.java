package com.lanl.application.TPTD.applet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;

import org.forester.archaeopteryx.ControlPanel;
import org.forester.archaeopteryx.MainFrame;
import org.json.JSONArray;

import com.lanl.application.treePruner.applet.TreePrunerCommunication;
import com.lanl.application.treePruner.custom.data.WorkingSet;

public class AutoSave {
	private static long base;
	private static String autoSaveTimeTEXT = "Last Autosaved at: ";
	private  JLabel autoSaveTimeLabel= new JLabel(autoSaveTimeTEXT,JLabel.CENTER);
	private static boolean autoSaved=false;
	private static String autoSaveDateTime = "Not Saved Yet";
	
	public void addAutoSaveLabel(ControlPanel controlPanel){
		autoSaveTimeLabel.setText(autoSaveTimeTEXT + autoSaveDateTime);
		autoSaveTimeLabel.setVerticalTextPosition(JLabel.TOP);
		autoSaveTimeLabel.setHorizontalTextPosition(JLabel.CENTER);
		controlPanel.addLabel(autoSaveTimeLabel);
		refreshAutoSaveLabel();
	}
	
	public void refreshAutoSaveLabel(){
		if(autoSaved){
			autoSaveDateTime = currentDateTime();
			autoSaveTimeLabel.setText(autoSaveTimeTEXT + autoSaveDateTime );
		    refreshAutoSaveTimeInAllFrames();
		}
		else{}
		
	}
	
	public void refreshAutoSaveTimeInAllFrames(){
		for(MainFrame mf : SubTreePanel.mainFrames){
			mf.get_main_panel().get_control_panel().controlPanelAdditions.autoSave.autoSaveTimeLabel.setText(autoSaveTimeTEXT + autoSaveDateTime);
		}
		SubTreePanel.mainAppletFrame.get_main_panel().get_control_panel().
		controlPanelAdditions.autoSave.autoSaveTimeLabel.setText(autoSaveTimeTEXT + autoSaveDateTime);
	}
	
	private static String currentDateTime() {
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

    }
	
	private static boolean timeToAutoSave(){
		if(base == 0){
			base = System.currentTimeMillis();
		}
		long delay = System.currentTimeMillis() - base;
		if(delay >= 600000){    //10 minutes =600000 ms
		//if(delay >= 20000){
			base=System.currentTimeMillis();
			delay=0;
			return true;
		}
		else{
			return false;
		}
	}
	
	public static void doAutoSave(ControlPanelAdditions controlPanelAdditions, WorkingSet ws,CommunicationMessageWarningWindow warningWindow){
		if(timeToAutoSave()){
			JSONArray accToRemove = ws.getACCasJSONarray();   //acc ==accToRemove //get_rem_acc == toCommunicateWithServer && getACCasString
		    if(ws.toCommunicateWithServer()){
            	warningWindow = new CommunicationMessageWarningWindow();
            	TreePrunerCommunication.saveToFileComm(accToRemove);
            	ws.copyAccToRememberAcc();
            	destroyWarningWindow(warningWindow);
            	autoSaved = true;
            }
            else if(!ws.toCommunicateWithServer()){
            }
            else{
            	//Don't do anything if all arrays (keep, remove and revert) are empty
            }
            
         //   ws.clearListsForNextSession();   //This will clear out all the lists (node lists) which we dont want // Also we dont want the color nodes 
            //to be copied to the remove all nodes which will gray them.
       }
		else{
			autoSaved = false;
		}
	}
	private static void destroyWarningWindow(CommunicationMessageWarningWindow warningWindow){
        if(warningWindow!=null){
        	warningWindow.close();
                
        }
    }
	
	public static void resetAutoSave(){   //CALL from EXIT 
		base = 0;
		autoSaveDateTime = "Not Saved Yet";
	}
}