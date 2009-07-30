package com.lanl.application.treePruner.applet;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.forester.archaeopteryx.MainFrame;
import org.forester.archaeopteryx.MainPanel;
import org.forester.archaeopteryx.TreePanel;


import org.forester.phylogeny.PhylogenyNode;

import com.lanl.application.TPTD.applet.AppletParams;
import com.lanl.application.TPTD.applet.AutoSave;
import com.lanl.application.TPTD.applet.SubTreePanel;
import com.lanl.application.treePruner.custom.data.WorkingSet;

public class AppletTerminate {
	ControlPanelAdditions controlPanelAdditions = new ControlPanelAdditions();
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
		String accToRemove = ws.getACCasString();

		String returnedString = "";
		if (ws.toCommunicateWithServer()) {
			returnedString = controlPanelAdditions.saveToFileComm(accToRemove);
		} else if (!ws.toCommunicateWithServer()) {

		} else {
			// Don't do anything if all arrays (keep, remove and revert) are
			// empty
		}
		if (returnedString.equals("Can't open file")) {
			System.out.println("SAVE \n");
			System.out.println(this.toString()
					+ "\n Server reurned: Can't open file");
		} else if (returnedString
				.equals("File successfully opened and written")) {
			System.out.println("SAVE \n");
			System.out
					.println(this.toString()
							+ "\n Server reurned: File successfully opened and written");
		}
		for (MainFrame o : SubTreePanel.mainFrames) {
			if (o != null) {
				o.closeOnDelete();
			}
		}
		SubTreePanel.clearListsOnClose();
		ControlPanelAdditions.lastAction = "";
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
	        String accToRemove = ws.getACCasString();
	        if(ws.toCommunicateWithServer() ){  //CASE: If person clicked on the delete last or did not make any save or delete and has some accessions marked for removal and is quitting.
	        	ControlPanelAdditions.lastAction = "";
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
	    		String returnedString;
	    		if(n==JOptionPane.YES_OPTION){
	    			returnedString = controlPanelAdditions.deleteFromDbComm(accToRemove);
	    			if(returnedString.equals("Accessions successfully deleted")){
	                	JOptionPane.showMessageDialog( mainPanel, "Your Seqences were successfully deleted","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE);
	                }
	                else if (returnedString.equals("Nothing deleted")){
	                	JOptionPane.showMessageDialog( mainPanel, "Your Seqences were not deleted.\n Please make sure " +
	                			"that the sequences are not already deleted. \n " +
	                			"Please contact flu@lanl.gov if your problem persists.","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE );
	                }
	    		}
	    		else if(n==JOptionPane.NO_OPTION){
	    			returnedString = controlPanelAdditions.saveToFileComm(accToRemove);
	    			ControlPanelAdditions.lastAction = "";
	               	if(returnedString.equals("Can't open file")){
	                   	System.out.println("SAVE on TERMINATE PRESSED \n");
	                   	System.out.println(this.toString() + "\n ATVserver.cgi reurned: Can't open file");
	               	}	
	               	else if (returnedString.equals("File successfully opened and written")){
	                   	System.out.println("SAVE on TERMINATE PRESSED \n");
	                   	System.out.println(this.toString() + "\n ATVserver.cgi reurned: File successfully opened and written");
	               	}
	    		}
	    		else if (n==JOptionPane.CANCEL_OPTION){
	    			returnedString = controlPanelAdditions.discardComm();
	    			if(returnedString.equals("success")){
	                	System.out.println("Discard on TERMINATE PRESSED \n");
	                	System.out.println(this.toString() + "\n ATVserver.cgi reurned: file successfully deleted");
	                }
	                else if (returnedString.equals("fail")){
	                	System.out.println("Discard on TERMINATE PRESSED \n");
	                	System.out.println(this.toString() + "\n ATVserver.cgi reurned: Failed to delete the file because no file was present or permissions");
	                }
	    		}
	    	}
	    	else if(ControlPanelAdditions.lastAction.equals("save")){ //CASE: If person clicked on save and then did not make the final delete from DB and is quitting.
	    		ControlPanelAdditions.lastAction = "";
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
	    		String returnedString;
	    		if(n==JOptionPane.YES_OPTION){
	    			returnedString = controlPanelAdditions.deleteFromDbComm(accToRemove);
	    			if(returnedString.equals("Accessions successfully deleted")){
	                	JOptionPane.showMessageDialog( mainPanel, "Your Seqences were successfully deleted","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE);
	                }
	                else if (returnedString.equals("Nothing deleted")){
	                	JOptionPane.showMessageDialog( mainPanel, "Your Seqences were not deleted.\n Please make sure " +
	                			"that the sequences are not already deleted. \n " +
	                			"Please contact flu@lanl.gov if your problem persists.","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE );
	                }
	    		}
	    		else if(n==JOptionPane.CANCEL_OPTION){
	    			returnedString = controlPanelAdditions.discardComm();
	    			if(returnedString.equals("success")){
	                	System.out.println("Discard on TERMINATE PRESSED \n");
	                	System.out.println(this.toString() + "\n ATVserver.cgi reurned: file successfully deleted");
	                }
	                else if (returnedString.equals("fail")){
	                	System.out.println("Discard on TERMINATE PRESSED \n");
	                	System.out.println(this.toString() + "\n ATVserver.cgi reurned: Failed to delete the file because no file was present or permissions");
	                }
	    		}
	    		else if(n==JOptionPane.NO_OPTION){
	    			//keep them as saved //do nothing
	    		}
	    	}
	    	else {}
	    	
	    	ws.clearAllLists();
	    	AutoSave.resetAutoSave();
//	        atvp.set_base(0);
	    	ControlPanelAdditions.lastAction = "";
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
			if(AppletParams.URLprefix.equals("")){  //LANL
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
