package com.lanl.application.TPTD.applet;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;


import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.forester.archaeopteryx.ControlPanel;
import org.forester.archaeopteryx.MainFrame;

import org.json.JSONArray;

import com.lanl.application.treeDecorator.applet.communication.TreeDecoratorCommunication;
import com.lanl.application.treeDecorator.applet.ui.frames.SemiDecorateFrame;
import com.lanl.application.treeDecorator.dataStructures.DecoratorTable;
import com.lanl.application.treePruner.applet.TreePrunerCommunication;
import com.lanl.application.treePruner.custom.data.WorkingSet;

public class ControlPanelAdditions {
	
	private WorkingSet ws = new WorkingSet();
	
	private JButton save_to_file;
	private JButton delete_from_db;
	private JButton discard;
	private JButton undo;
	private JButton refresh;
	private JButton semi_decorate;
	private JButton legend;
	public JLabel subTreeWindowHierarchy = new JLabel(subTreeWindowHierarchyTEXT,JLabel.CENTER);;
	public static String subTreeWindowHierarchyTEXT ="Tree level: ";
	static CommunicationMessageWarningWindow warningWindow;
	public ControlPanel controlPanel;
	public AutoSave autoSave = new AutoSave();
	//public ControlPanelAdditions controlPanelExtras;
	
	public ControlPanelAdditions(ControlPanel cp) {
		this.controlPanel = cp;
		save_to_file = new JButton("Save");
		save_to_file
				.setToolTipText("Save the net result of actions until now.");
		delete_from_db = new JButton("Commit changes; finish session");
		delete_from_db
				.setToolTipText("Implement all pruning actions in your working set; close applet.");
		discard = new JButton("Discard all");
		discard
				.setToolTipText("Discard everything and restore tree to initial state.");
		refresh = new JButton("<html><i>Refresh</html></i>");
	    refresh.setToolTipText("Refreshes the tree in all open applet windows");
		undo = new JButton("Discard recent");
		undo
				.setToolTipText("Discard all tree actions since most recent save.");
		semi_decorate = new JButton("Decorate tree");
		semi_decorate.setToolTipText("Opens a new window to allow you to perform automatic decoration of the tree");
		legend = new JButton("LEGEND");
		legend.setToolTipText("Opens a new window to view your decoration legend");
	    

	}
	
	public void addTreePrunerButtons() {
		final JLabel spacer = new JLabel("");
		final JLabel spacer2 = new JLabel("");
		
		controlPanel.addLabel(spacer);
		controlPanel.addLabel(spacer2);
		controlPanel.add_additional_JButton(refresh, controlPanel);
		controlPanel.add_additional_JButton(save_to_file, controlPanel);
		final JPanel discard_panel = new JPanel(new GridLayout(1, 2, 0, 0));
		discard_panel.setBackground(controlPanel.getBackground());
		controlPanel.addPanel(discard_panel);
		controlPanel.add_additional_JButton(discard, discard_panel);
		controlPanel.add_additional_JButton(undo, discard_panel);
		controlPanel.add_additional_JButton(delete_from_db, controlPanel);
		
	//	controlPanel.addLabel(spacer3);

	}
	
	public void addTreeDecoratorButtons() {
		final JLabel spacer2 = new JLabel("");
		controlPanel.addLabel(spacer2);
		controlPanel.addLabel(spacer2);
		controlPanel.add_additional_JButton(semi_decorate, controlPanel);
		controlPanel.add_additional_JButton(legend, controlPanel);
		controlPanel.add_additional_JButton(refresh, controlPanel);
		final JPanel discard_panel = new JPanel(new GridLayout(1, 2, 0, 0));
		discard_panel.setBackground(controlPanel.getBackground());
		controlPanel.addPanel(discard_panel);
		controlPanel.add_additional_JButton(discard, discard_panel);
		controlPanel.add_additional_JButton(undo, discard_panel);
		controlPanel.add_additional_JButton(save_to_file, controlPanel);
		
	}
	
	
	public void addSubTreeWindowHierarchyLabel(){
		String hierarchyNumber="1.";
		for(int i = 0;i <= SubTreePanel.subTreeHierarchy.size()-1;i++){
			hierarchyNumber+= SubTreePanel.subTreeHierarchy.get(i).toString();
			if(i==SubTreePanel.subTreeHierarchy.size()-1){
				
			}
			else{
				hierarchyNumber+=".";
			}
		}
		if(hierarchyNumber=="1."){
			hierarchyNumber = "1";
		}
		subTreeWindowHierarchy.setText(subTreeWindowHierarchyTEXT + hierarchyNumber);
		subTreeWindowHierarchy.setVerticalTextPosition(JLabel.TOP);
		subTreeWindowHierarchy.setHorizontalTextPosition(JLabel.CENTER);
		controlPanel.addLabel(subTreeWindowHierarchy);
	}
	public void callTreePrunerAutosaveToAdd(){
		AutoSave.doAutoSaveForTreePruner(this, ws, warningWindow);
		autoSave.addAutoSaveLabel(controlPanel);
	}
	public void callTreePrunerAutoSaveToRefresh(){
		AutoSave.doAutoSaveForTreePruner(this, ws, warningWindow);
		autoSave.refreshAutoSaveLabel(); 
	}
	 
	public void callTreeDecoratorAutosaveToAdd(){
		AutoSave.doAutoSaveForTreeDecorator();
		autoSave.addAutoSaveLabel(controlPanel);
	}
	public void callTreeDecoratorAutoSaveToRefresh(){
		AutoSave.doAutoSaveForTreeDecorator();
		autoSave.refreshAutoSaveLabel(); 
	}

	public void addTreePrunerButtonFunctions(ActionEvent e){
		if ( e.getSource() == refresh ) {
			SubTreePanel.refreshAllWindows();
        }
		else if ( e.getSource() == save_to_file ) {
			JSONArray accToRemove = ws.getACCasJSONarray();   
			if(ws.toCommunicateWithServer()){
            	warningWindow = new CommunicationMessageWarningWindow();
            	TreePrunerCommunication.saveToFileComm(accToRemove);
            	ws.copyAccToRememberAcc();
            	ws.clearSavedStuff();
            	destroyWarningWindow();
            }
            else if(!ws.toCommunicateWithServer()){
            	
            }
            else{
            	//Don't do anything if all arrays (keep, remove and revert) are empty
            }
            
            //now clear out the arrays for sequences selected for next session
               
            ws.clearListsForNextSession();
            ws.copyStuffToSavedStuff();
        }
        else if(e.getSource() == delete_from_db){
        	if(!SubTreePanel.mainFrames.isEmpty()|| SubTreePanel.mainFrames.size()>=1 || 
        			   SubTreePanel.sub_frame_count!=0){   //There are slave windows open
            	Object[] options = {"Yes",
    								"No"};
            	int n = JOptionPane.showOptionDialog(null,"Commiting changes will close your applet session \n  "
            												+ "Are you sure you would like to continue?",
            												"Terminate Interrupted",
            												JOptionPane.YES_NO_OPTION,
            												JOptionPane.QUESTION_MESSAGE,
            												null,
            												options,
            												options[1]);
            	
        	
            	if(n==JOptionPane.YES_OPTION){
	        		JSONArray accToRemove = ws.getACCasJSONarray();
	                if (ws.toCommunicateWithServerForDelete()) {
	                	warningWindow = new CommunicationMessageWarningWindow();
	                	TreePrunerCommunication.deleteFromDbComm(accToRemove);
						ws.copyAccToRememberAcc();
						destroyWarningWindow();
				    }
	            	//SIGM START
	                
            
                    for(MainFrame o : SubTreePanel.mainFrames){
	                	if(o!=null){
	                		o.closeOnDelete();
	                	}
	                }
                    SubTreePanel.clearListsOnClose();
	                if(SubTreePanel.mainAppletFrame!=null){
	                	SubTreePanel.mainAppletFrame.closeOnDelete();
	                }
            	}
            	else if(n==JOptionPane.NO_OPTION){ 
            		//dont do anything as user has clicked dont commit 
            	}
	        }
            else {   //Only the parent Window is open
            	JSONArray accToRemove = ws.getACCasJSONarray();
                if (ws.toCommunicateWithServerForDelete()) {
                	warningWindow = new CommunicationMessageWarningWindow();
                	TreePrunerCommunication.deleteFromDbComm(accToRemove);
					ws.copyAccToRememberAcc();
					destroyWarningWindow();
			    }
            	if(SubTreePanel.mainAppletFrame!=null){
            		SubTreePanel.mainAppletFrame.closeOnDelete();
                }
            }
	    }//end of delete from ws  //SIGMA END
        else if(e.getSource() == discard){
            warningWindow = new CommunicationMessageWarningWindow();
            TreePrunerCommunication.discardComm();
            destroyWarningWindow();
            ws.clearAllLists();
            ws.clearSavedStuff();
            AutoSave.resetAutoSave();
            autoSave.refreshAutoSaveTimeInAllFrames();
            controlPanel.displayed_phylogeny_mightHaveChanged(true);
            ws.clear("rm_all");
        }
        else if(e.getSource() == undo){
        	 ws.clearAllLists();
             ws.resetRemoveALL();
             ws.resetACC();
        }
	}
	
	public void addTreeDecoratorButtonFunctions(ActionEvent e){
		if ( e.getSource() == refresh ) {
			SubTreePanel.refreshAllWindows();
        }
		else if ( e.getSource() == semi_decorate ) {
			new SemiDecorateFrame();
		}
		else if ( e.getSource() == save_to_file ) {
			if(DecoratorTable.toSave()){
				TreeDecoratorCommunication.postSaveDecorationsComm(false);
			}
		}
		else if ( e.getSource() == discard ) {
			TreeDecoratorCommunication.postDiscardDecorationsComm();
			AutoSave.resetAutoSave();
			DecoratorTable.resetDecorations();
			SubTreePanel.refreshAllWindows();
		}
		else if(e.getSource() == undo ){
			DecoratorTable.copySavedStuffToStuff();
			SubTreePanel.refreshAllWindows();
		}
		else if(e.getSource() == legend){
			DecoratorTable.legendFrame._frame.setVisible(true);
		}
	}
	
	public static void destroyWarningWindow(){
        if(warningWindow!=null){
        	warningWindow.close();
                
        }
    }

}
