package org.forester.archaeopteryx;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import com.lanl.application.treePruner.applet.SubTreePanel;
import com.lanl.application.treePruner.applet.TreePrunerCommunicationMessageWarningWindow;
import com.lanl.application.treePruner.custom.data.WorkingSet;

public class ControlPanelAdditions {
	
	private WorkingSet ws = new WorkingSet();;
	
	private JButton save_to_file;
	private JButton delete_from_db;
	private JButton discard;
	private JButton undo;
	private JButton refresh;
	public static String lastAction="";
	public static String autoSaveTime; //TODO

	public ControlPanel controlPanel;
	//public ControlPanelAdditions controlPanelExtras;
	
	public ControlPanelAdditions() {
		
	}
	public ControlPanelAdditions(ControlPanel cp) {
		this.controlPanel = cp;
		save_to_file = new JButton("Save");
		save_to_file
				.setToolTipText("Save the net result of all pruning actions until now.");
		delete_from_db = new JButton("Commit changes; finish session");
		delete_from_db
				.setToolTipText("Implement all pruning actions in your working set; close applet.");
		discard = new JButton("Discard all");
		discard
				.setToolTipText("Discard all pruning actions and restore tree to initial state.");
		refresh = new JButton("Refresh");
		refresh.setToolTipText("Refreshes the tree in all open applet windows");
		undo = new JButton("Discard recent");
		undo
				.setToolTipText("Discard all pruning actions since most recent save.");

	}

	public void addTreePrunerButtons() {
		final JLabel spacer = new JLabel("");
		final JLabel spacer2 = new JLabel("");
		final JLabel spacer3 = new JLabel("");
		controlPanel.addLabel(spacer2);
		controlPanel.addJButton(refresh, controlPanel);
		
		controlPanel.addLabel(spacer);
		controlPanel.addLabel(spacer2);
		controlPanel.addJButton(save_to_file, controlPanel);
		final JPanel discard_panel = new JPanel(new GridLayout(1, 2, 0, 0));
		discard_panel.setBackground(controlPanel.getBackground());
		controlPanel.addPanel(discard_panel);
		controlPanel.addJButton(discard, discard_panel);
		controlPanel.addJButton(undo, discard_panel);
		controlPanel.addJButton(delete_from_db, controlPanel);
		
		controlPanel.addLabel(spacer3);

	}
	
	public void addTreePrunerButtonFunctions(ActionEvent e){
		if ( e.getSource() == refresh ) {
        	for(MainFrame o: SubTreePanel.mainFrames){
        		if(o!=null)
        			//paint all slave windows
        			o.getMainPanel().repaint();
        	}
        	//paint the master window
        	SubTreePanel.mainAppletFrame.getMainPanel().repaint();
        }
		
		//#######################
		
		else if ( e.getSource() == save_to_file ) {
			String accToRemove = ws.getACCasString();   //acc ==accToRemove //get_rem_acc == toCommunicateWithServer && getACCasString
			String returnedString="";
            
            if(ws.toCommunicateWithServer()){
            	TreePrunerCommunicationMessageWarningWindow warningWindow 
            	= new TreePrunerCommunicationMessageWarningWindow("Your Action is being performed" +
                        " Please wait...","                                                  " +
                        		"                                                              ");
            	returnedString = saveToFileComm(accToRemove);
            	ws.copyAccToRememberAcc();
            //	ws.clear("");
            	
            	ws.clearSavedStuff();
            	ws.copyStuffToSavedStuff();
            	
            	//System.out.println("++++++++++++_________________"+savedACC.toString() + savedREMOVE_ALL.toString());
            	warningWindow.close();
            }
            else if(!ws.toCommunicateWithServer()){
            	
            }
            else{
            	//Don't do anything if all arrays (keep, remove and revert) are empty
            }
            
            //now clear out the arrays for sequences selected for next session
               
            ws.clearListsForNextSession();
            if(returnedString.equals("Can't open file")){
            	System.out.println("SAVE PRESSED \n");
            	System.out.println(this.toString() + "\n Server reurned: Can't open file");
            }
            else if (returnedString.equals("File successfully opened and written")){
            	System.out.println("SAVE PRESSED \n");
            	System.out.println(this.toString() + "\n Server reurned: File successfully opened and written");
            }
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
	           // 	ATVapplet atva = new ATVapplet();
	           //     _atvappletframe=atva.get_applet_frame();  
            		String accToRemove = ws.getACCasString();
	                String returnedString="";
	                
	                if (ws.toCommunicateWithServer()) {
	                	TreePrunerCommunicationMessageWarningWindow warningWindow = new TreePrunerCommunicationMessageWarningWindow("Your Action is being performed" +
		                        " Please wait...","                                                  " +
		                        		"                                                              ");
						returnedString=deleteFromDbComm(accToRemove);
						ws.copyAccToRememberAcc();
						warningWindow.close();
						if(returnedString.equals("Accessions successfully deleted")){
		                	JOptionPane.showMessageDialog( null, "Your Seqences were successfully deleted","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE);
		                	
		                }
		                else if (returnedString.equals("Nothing deleted")){
		                	JOptionPane.showMessageDialog( null, "Your Seqences were not deleted.\n Please make sure " +
		                			"that the sequences are not already deleted. \n " +
		                			"Please contact flu@lanl.gov if your problem persists.","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE );
		                }
						
	                }
	            	//SIGM START
	                
            
                    for(MainFrame o : SubTreePanel.mainFrames){
	                	if(o!=null){
	                		o.closeOnDelete();
	                	}
	                }
	                if(SubTreePanel.mainAppletFrame!=null){
	                	SubTreePanel.mainAppletFrame.closeOnDelete();
	                }
		            
		            SubTreePanel.clearListsOnClose();
		 //           stored_levels.clear();
	            
            /*
		            System.out.println("9999999999999999999998888888888888888 "+ATVtreePanel.atvFrames);
		            System.out.println("9999999999999999999998888888888888888 "+ATVtreePanel._phylogenies);
		            System.out.println("9999999999999999999998888888888888888 "+ATVtreePanel._phylogenies_subtree);
		            System.out.println("9999999999999999999998888888888888888 "+ATVtreePanel.sub_frame_count);
		            System.out.println("9999999999999999999998888888888888888 "+ATVtreePanel._subtree_index);*/
		            //if(_atvappletframe!=null){
		            //	_atvappletframe.close();
		            //}
            	}
            	else if(n==JOptionPane.NO_OPTION){ 
            		//dont do anything as user has clicked dont commit 
            		
            	}
	        }
            else {   //Only the parent Window is open
            	String accToRemove = ws.getACCasString();
                String returnedString="";
                if (ws.toCommunicateWithServer()) {
                	
                	TreePrunerCommunicationMessageWarningWindow warningWindow = new TreePrunerCommunicationMessageWarningWindow("Your Action is being performed" +
	                        " Please wait...","                                                  " +
	                        		"                                                              ");
					returnedString = deleteFromDbComm(accToRemove);
					ws.copyAccToRememberAcc();
					warningWindow.close();
					if(returnedString.equals("Accessions successfully deleted")){
	                	JOptionPane.showMessageDialog( null, "Your Seqences were successfully deleted","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE);
	                	
	                }
	                else if (returnedString.equals("Nothing deleted")){
	                	JOptionPane.showMessageDialog( null, "Your Seqences were not deleted.\n Please make sure " +
	                			"that the sequences are not already deleted. \n " +
	                			"Please contact flu@lanl.gov if your problem persists.","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE );
	                }
					
                }
            	if(SubTreePanel.mainAppletFrame!=null){
            		SubTreePanel.mainAppletFrame.closeOnDelete();
                }
            }
	    }//end of delete from ws  //SIGMA END
        else if(e.getSource() == discard){
            String returnedString = "";
            TreePrunerCommunicationMessageWarningWindow atvw = new TreePrunerCommunicationMessageWarningWindow("Your Action is being performed" +
                    " Please wait...","                                                  " +
                    		"                                                              ");
            returnedString = discardComm();
            atvw.close();
            if(returnedString.equals("success")){
            	System.out.println("Discard PRESSED \n");
            	System.out.println(this.toString() + "\n Server reurned: file successfully deleted");
            }
            else if (returnedString.equals("fail")){
            	System.out.println("Discard PRESSED \n");
            	System.out.println(this.toString() + "\n Server reurned: Failed to delete the file because no file was present or permissions");
            }	
			
            ws.clearAllLists();
            
           controlPanel.displayedPhylogenyMightHaveChanged(true);
            ws.clear("rm_all");
            /*if(s.equals("Accessions successfully deleted")){
            	JOptionPane.showMessageDialog( null, "Your Seqences were successfully deleted","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE);
            }
            else if (s.equals("Nothing deleted")){
            	JOptionPane.showMessageDialog( null, "Your Seqences were not deleted.\n Please make sure " +
            			"that the sequences are not already deleted. \n " +
            			"Please contact flu@lanl.gov if your problem persists.","Delete Confirmation",JOptionPane.INFORMATION_MESSAGE );
            }*/
        }
        else if(e.getSource() == undo){
        	 ws.clearAllLists();
//             System.out.println("SAVED REMOVE ALL "+savedREMOVE_ALL.toString());
//            System.out.println("SAVED ACC "+savedACC.toString());
             ws.resetRemoveALL();
             ws.resetACC();
        }
		
		
		//########################
		
		
		
		
	}
	//dont use controlPanel variable as call from AppletTerminate uses the empty constructore
	//Beware of nullPointerException
	public String saveToFileComm(String accToRemove){
		lastAction="save";
		String returnedString = "";
		return returnedString;
	}
	//dont use controlPanel variable as call from AppletTerminate uses the empty constructore
	//Beware of nullPointerException
	public String deleteFromDbComm (String accToRemove){
		lastAction="delete";
		String returnedString = "";
		return returnedString;
	}
	//dont use controlPanel variable as call from AppletTerminate uses the empty constructore
	//Beware of nullPointerException
	public String discardComm(){
		lastAction="discard";
		String returnedString ="";
		return returnedString;
	}
	
	

}
