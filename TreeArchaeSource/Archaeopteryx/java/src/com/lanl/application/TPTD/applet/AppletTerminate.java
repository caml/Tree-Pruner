package com.lanl.application.TPTD.applet;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.forester.archaeopteryx.MainFrame;
import org.forester.archaeopteryx.MainPanel;
import org.forester.archaeopteryx.TreePanel;


import org.forester.phylogeny.PhylogenyNode;
import org.json.JSONArray;

import com.lanl.application.treePruner.applet.TreePrunerCommunication;
import com.lanl.application.treePruner.applet.TreePrunerCommunicationNames;
import com.lanl.application.treePruner.custom.data.WorkingSet;

public class AppletTerminate {
	WorkingSet ws = new WorkingSet();
	MainFrame mainFrame;
	public static AppletContext appletContext;
	public AppletTerminate(MainFrame mf) {
		this.mainFrame = mf;
	}
	
	@Deprecated
	public void closeAdditionalTasks() {
		JOptionPane
				.showMessageDialog(
						mainFrame,
						"WARNING:You are closing the main applet window without closing "
								+ "the subtree windows.\n"
								+ "This will result in termination of your applet session. \n"
								+ "Your current session will be saved so that you may return to it if required.");
		JSONArray accToRemove = ws.getACCasJSONarray();
		if (ws.toCommunicateWithServer()) {
			TreePrunerCommunication.saveToFileComm(accToRemove);
		} else if (!ws.toCommunicateWithServer()) {

		} else {
			// Don't do anything if all arrays (keep, remove and revert) are
			// empty
		}
		for (MainFrame o : SubTreePanel.mainFrames) {
			if (o != null) {
				o.closeOnDelete();
			}
		}
		SubTreePanel.clearListsOnClose();
		TreePrunerCommunication.lastAction = "";
		ws.clearAllLists();
		AutoSave.resetAutoSave();
	}

	public void closeOnDeleteAdditionalTasks() {
		// empty
	}

	
	public void terminateAdditionalTasks(MainPanel mainPanel) {
		if(SubTreePanel.mainFrames.isEmpty()||SubTreePanel.mainFrames.size()<1 
				|| SubTreePanel.sub_frame_count==0){
//	    	ATVtreePanel atvp = new ATVtreePanel();
//	    	ATVcontrol atvc = new ATVcontrol();
//	        atvc.auto_save_time= "Not Saved Yet";

	        PhylogenyNode.setNodeCount(0);
	        JSONArray accToRemove = ws.getACCasJSONarray();
	        if(ws.toCommunicateWithServerForDelete() ){  //CASE: If person clicked on the delete last or did not make any save or delete and has some accessions marked for removal and is quitting.
	        	TreePrunerCommunication.lastAction = "";
	    		Object[] options = {"Commit Changes",
	    				             "Resave Changes",
	    				             "Discard Changes"};
	    		int n = JOptionPane.showOptionDialog(mainPanel,
	    				"You have some unsaved changes? What would  "
	    				+ "you like to do?",
	    				"Terminate Interrupted",
	    				JOptionPane.YES_NO_CANCEL_OPTION,
	    				JOptionPane.QUESTION_MESSAGE,
	    				null,
	    				options,
	    				options[1]);	
	    		if(n==JOptionPane.YES_OPTION){
	    			TreePrunerCommunication.deleteFromDbComm(accToRemove);
	    		}
	    		else if(n==JOptionPane.NO_OPTION){
	    			TreePrunerCommunication.saveToFileComm(accToRemove);
	    			TreePrunerCommunication.lastAction = "";
	            }
	    		else if (n==JOptionPane.CANCEL_OPTION){
	    			TreePrunerCommunication.discardComm();
	    		}
	    	}
	      //CASE: If person clicked on save and then did not make the final delete from DB and is quitting.
	    	else if(TreePrunerCommunication.lastAction.equals(TreePrunerCommunicationNames.SAVE.getName())){ 
	    		TreePrunerCommunication.lastAction = "";
	    		Object[] options = {"Commit Changes",
	    							"Resave Changes",
	    							"Discard Changes"};
	    		int n = JOptionPane.showOptionDialog(mainPanel,
	    				"You have some saved changed that have not yet been commited to the Working Set. \n "
	    				+ "What would you like to do?",
	    				"Terminate Interrupted",
	    				JOptionPane.YES_NO_CANCEL_OPTION,
	    				JOptionPane.QUESTION_MESSAGE,
	    				null,
	    				options,
	    				options[1]);
	    		if(n==JOptionPane.YES_OPTION){
	    			TreePrunerCommunication.deleteFromDbComm(accToRemove);
	    		}
	    		else if(n==JOptionPane.CANCEL_OPTION){
	    			TreePrunerCommunication.discardComm();
	    		}
	    		else if(n==JOptionPane.NO_OPTION){
	    			//keep them as saved //do nothing
	    		}
	    	}
	    	else {}
	    	
	    	ws.clearAllLists();
	    	AutoSave.resetAutoSave();
//	        atvp.set_base(0);
	    	TreePrunerCommunication.lastAction = "";
	        SubTreePanel.clearListsOnClose();
    	}
    	else{
    		if(SubTreePanel.mainFrames.contains(mainPanel.returnMainFrame())){
    			int n = SubTreePanel.mainFrames.indexOf(mainPanel.returnMainFrame());
    			SubTreePanel.mainFrames.remove(SubTreePanel.mainFrames.indexOf(mainPanel.returnMainFrame()));
    			SubTreePanel.subTreeRootNode.remove(SubTreePanel._phylogenies_subtree.get(n).getRoot().getNodeId());
    			SubTreePanel._phylogenies_subtree.remove(n);
    			SubTreePanel._phylogenies.remove(n);
    			SubTreePanel.subTreeHierarchy.remove(n);
    			SubTreePanel.sub_frame_count = SubTreePanel.sub_frame_count-1;
	    		TreePanel.set_subtree_index(TreePanel.get_subtree_index()-1);
	    		//ATVappletFrame atvf=ATVtreePanel.atvFrames.get(--ATVtreePanel.sub_frame_count);
	    		//ATVtreePanel.atvFrames.remove(ATVtreePanel.atvFrames.indexOf(atvf));
	    		for(MainFrame o: SubTreePanel.mainFrames){
	        		if(o!=null)
	        			o.repaintPanel();
	        	}
	    		SubTreePanel.mainAppletFrame.repaintPanel();
	            //atvf.close_subtree_window();
    		}
    	}

	}

	public void terminateOnDeleteAdditionalTasks() {

//		ATVtreePanel atvp = new ATVtreePanel();
//		ATVcontrol atvc = new ATVcontrol();
//		atvc.auto_save_time = "Not Saved Yet";
//		String last_action = atvc.get_last_action();
		PhylogenyNode.setNodeCount(0);

		ws.clearAllLists();
		AutoSave.resetAutoSave();
//		atvp.set_base(0);
//		atvc.set_last_action("");
		

	}

	public boolean check_terminate(MainFrame mf) {
		if (SubTreePanel.mainFrames.contains(mf)) {
			return true;
		} else if (SubTreePanel.mainFrames.isEmpty()
				|| SubTreePanel.mainFrames.size() < 1
				|| SubTreePanel.sub_frame_count == 0) {
			return true;
		} else{
			return false;
		}
	}
	
	public static void closePageOnTerminate(MainFrame mf){
		if (SubTreePanel.mainAppletFrame == mf){
			if(AppletParams.codeBase.toString().contains("lanl")){  //LANL
				try {
					URL searchPage = new URL(AppletParams.codeBase,"close.html");
					appletContext.showDocument(searchPage,"_parent");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				
			}
			else{
				//BHB dont do anything
			}
		}
	}
}
