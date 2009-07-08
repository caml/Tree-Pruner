/**
 * 
 * @author Mohan Krishnamoorthy
 * 
 * @version 1.02 BETA
 * 
 */
// Mira Dimitrijevic, 06-12-08
// Class to handle all data that is directly connected to a working set in the database. 
// E.g. Get node info for sequences to be marked for removal from WS
package com.lanl.application.treePruner.custom.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.forester.archaeopteryx.TreePanel;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import com.lanl.application.treePruner.applet.SubTreePanel;

import java.util.*;

public class WorkingSet {

	//mira 052108 - delete DBAccess stuff again when replaced with server-client interaction mechanism
	
	final static public int 			set_id 										= 6787;
	private boolean 					rmFromWS;
    //per session - these ArrayLists get emptied out after committing to the WS
	//mira 052008 - no type sepcified for ArrayLists because they are int not of type Object
	//mira 071408: "ACTIVE" - to paint node in black; "INACTIVE" - to paint node in gray; - this is for toggling back and forth
    final static ArrayList     			KEEP_ACTIVE 		 			 		 	= new ArrayList(); //mira 052008 - memorize all nodes selected to keep in working set for current session
//    final static ArrayList    		    REMOVE_INACTIVE 			  				= new ArrayList(); //not used anymore
    final static ArrayList    		    REMOVE_ACTIVE 			  					= new ArrayList(); //used to store accessions of nodes that are clicked per session for checks b/w keep and remove
    final static ArrayList    		    REMOVE_ALL 							  		= new ArrayList(); //mira 052008 - no type sepcified because this can be seq name or seq id or accession etc.
//    final static ArrayList    		    REVERT_ACTIVE 			  					= new ArrayList(); //not used anymore
 //   final static ArrayList 				remClicked									= new ArrayList(); //not used anymore
 //   final static ArrayList 				remClicked1									= new ArrayList(); //kmohan 07/29 - to remember the nodes clicked.only remembers the nodes that were cliked and not the children. 
    final static ArrayList 				COLOR_NODES									= new ArrayList(); //kmohan 07/25 - Intermediate list to check which are to be colored 
//    final static ArrayList				PARENT_TO_ROOT								= new ArrayList(); //not used anymore
    final static ArrayList<String>              ACC                                 = new ArrayList<String>(); //kmohan 10/16  used to store the external nodes node accessions.
    final static ArrayList<String>              rem_ACC                             = new ArrayList<String>(); //kmohan 10/20  used to remember the last the external nodes node accessions.used for comparison b/w ACC and rem_ACC
    final static ArrayList<String>              keepACC                             = new ArrayList<String>();
    final        ArrayList<String>               temp                                = new ArrayList<String>(); //storing accession temporily of all external nodes in keep and remove
    
    static int action = 0;  // kmohan 0729 action being performed (keep or un-keep)
    //per ATV application (while ATV window is open) - get emptied out with garbage collection
	
	public WorkingSet(){
		
	}
	
	// //mira 052108 - delete again
	/*public void connectDB(){
		System.out.println("ATVcontrol.connectDB() with url jdbc:postgresql://asiatic.lanl.gov:5432/isdora and schema postgres");
		DBAccess.setURL( "jdbc:postgresql://asiatic.lanl.gov:5432/isdora");
		DBAccess.setSchema( "postgres");
		try{
			dba = DBAccess.getInstance();
		}
		catch( SQLException sqle){
			System.out.println( "\nATVcontrol.connectDB(): " + sqle.getMessage());
			System.exit(0);
		}
	}
	
	public void disconnectDB(){
		try{
			dba.commit();
			dba.closeConnection();
		}
		catch( SQLException sqle){
			System.out.println( "\nPopulateTable() - Error comitting to or disconnecting from DB:\n" + sqle.getMessage());
		}
	}
	
    */
   
	/**
     * 
     * 
     */
	
	public void memorizeKeepNodes( final PhylogenyNode n, TreePanel treePanel){
		PhylogenyNode r = SubTreePanel.getTree_always_full().getNode(n.getNodeId());
		boolean cant_revert_remove=false;
		if(REMOVE_ALL.contains(n.getNodeId())){
			cant_revert_remove=true;
		}
		if(cant_revert_remove==false){
			if(n.isExternal()){            //CASE 1 and 2
				if(KEEP_ACTIVE.contains(n.getNodeId())){  //CASE 1 : leaf node and remove
					KEEP_ACTIVE.remove(KEEP_ACTIVE.indexOf(n.getNodeId()));  //remove the node
					if(n.extraNodeInfo.getNodeAcc()!=null && keepACC.contains(n.extraNodeInfo.getNodeAcc()))
						keepACC.remove(keepACC.indexOf(n.extraNodeInfo.getNodeAcc()));
				
					
					PhylogenyNode parent = r;                //remove the parent until the root for that node if Keep Active does not 
					PhylogenyNode nextParent = null;         // contan that the parents child 1 or child 2 
		         	while( ( nextParent = parent.getParent()) != null){
		         		if(KEEP_ACTIVE.contains(parent.getNodeId()) && (KEEP_ACTIVE.contains(parent.getChildNode1().getNodeId())||KEEP_ACTIVE.contains(parent.getChildNode2().getNodeId())))
		        			break;
		        		if(KEEP_ACTIVE.contains(parent.getNodeId())){	
			        		KEEP_ACTIVE.remove(KEEP_ACTIVE.indexOf(parent.getNodeId()));
		        		}
			        	parent = nextParent;
		        	}
				}
				else {                                                             //CASE 2 leaf node and  add
					KEEP_ACTIVE.add(n.getNodeId());                               //add the node
					if(n.extraNodeInfo.getNodeAcc()!=null && !keepACC.contains(n.extraNodeInfo.getNodeAcc()))
						keepACC.add(n.extraNodeInfo.getNodeAcc());
					PhylogenyNode parent = r;                //add the parent till the root if the parent not already in KEEP ACTIVE 
					PhylogenyNode nextParent = null;          
		         	while( ( nextParent = parent.getParent()) != null){
		         		if(!KEEP_ACTIVE.contains(parent.getNodeId())){
		         			KEEP_ACTIVE.add(parent.getNodeId());
		         		}
		         		parent=nextParent;
		         	}
				}
				
			}
			else if(!n.isExternal()){           //CASE 3 and 4
				//CHECK IF IT IS CASE 3 or 4 by seeing if any external child is not in KEEP, if yes then case 4 (add) else case 3(remove)
				boolean add=false;
				int id=0;
				
				temp.clear();
				Iterator its  = n.getAllExternalDescendants().iterator();
				while(its.hasNext()){
					PhylogenyNode currNode = (PhylogenyNode) its.next();
					id = currNode.getNodeId();
					if(currNode.extraNodeInfo.getNodeAcc()!=null)
						temp.add(currNode.extraNodeInfo.getNodeAcc());  //stored the accessions of all ext children 
					if(!KEEP_ACTIVE.contains(id))
						add=true;
				}
				int k =id;   //storing the last of the external node id's for later on adding them or removing them
				if(add==false){   //Case 3 node is internal and remove because all external children are in keep active
					if(KEEP_ACTIVE.contains(n.getNodeId()))
						KEEP_ACTIVE.remove(KEEP_ACTIVE.indexOf(n.getNodeId()));
					for(int j=n.getNodeId(); j<=k;j++){
						if(KEEP_ACTIVE.contains(j)){
							KEEP_ACTIVE.remove(KEEP_ACTIVE.indexOf(j));
						}
					}
					PhylogenyNode parent = r;                //remove the parent until the root for that node if Keep Active does not 
					PhylogenyNode nextParent = null;		 // contan that the parents child 1 or child 2
					while( ( nextParent = parent.getParent()) != null){
		         		if(KEEP_ACTIVE.contains(parent.getNodeId()) && (KEEP_ACTIVE.contains(parent.getChildNode1().getNodeId())||KEEP_ACTIVE.contains(parent.getChildNode2().getNodeId())))
		        			break;
		        		if(KEEP_ACTIVE.contains(parent.getNodeId())){	
		        			KEEP_ACTIVE.remove(KEEP_ACTIVE.indexOf(parent.getNodeId()));
		        		}
			        	parent = nextParent;
		        	}
					Iterator ext_acc =temp.iterator();
					while(ext_acc.hasNext()){
						Object acc1 = ext_acc.next();
						if(keepACC.contains(acc1)){
							keepACC.remove(keepACC.indexOf(acc1));
						}
					}
				} //end of add==false case 3 
				else if(add==true){ //Case 4 node is internal and add because any one/all of the external children are not in keep active
					if(!KEEP_ACTIVE.contains(n.getNodeId())){
						KEEP_ACTIVE.add(n.getNodeId());
					}
					for(int j=n.getNodeId(); j<=k;j++){
						if(!KEEP_ACTIVE.contains(j)){
							KEEP_ACTIVE.add(j);
						}
					}
					PhylogenyNode parent = r;                //add the parent till the root if the parent not already in KEEP ACTIVE 
					PhylogenyNode nextParent = null;          
		         	while( ( nextParent = parent.getParent()) != null){
		         		if(!KEEP_ACTIVE.contains(parent.getNodeId())){
		         			KEEP_ACTIVE.add(parent.getNodeId());
		         		}
		         		parent=nextParent;
		         	}
		         	Iterator ext_acc =temp.iterator();
		         	while(ext_acc.hasNext()){
						Object acc1 = ext_acc.next();
						if(!keepACC.contains(acc1)){
							keepACC.add(acc1.toString());
						}
					}
				} //end of add==true case 4
			}// end of !n.isExternal
		
			ArrayList allNodeIds = treePanel.subTreePanel.getSubTreeNodeIds();  //SIGMA
			for( Object o : allNodeIds){
				if(COLOR_NODES.contains(o) && KEEP_ACTIVE.contains(o)){
					COLOR_NODES.remove(COLOR_NODES.indexOf(o));
				}
				else if( !COLOR_NODES.contains( o) && !KEEP_ACTIVE.contains(o)){
	    			COLOR_NODES.add( o);
	    		}
			}
			ArrayList allNodeACC = treePanel.subTreePanel.getSubTreeNodeAcc();
	    	//System.out.println("123"+allNodeACC);
	    	for( Object o : allNodeACC){
	    		if(!keepACC.contains(o)){
	    			if(!keepACC.contains(o) && !ACC.contains(o)){
		    			ACC.add(o.toString());
		    		}
		    	}
	    		else if(keepACC.contains(o)){ 
	    			if(ACC.contains(o) && keepACC.contains(o)){
	    					ACC.remove(ACC.indexOf(o));
	    			}
	    		}	
	    	 }
		}// end of cant revert remove ==false
	} //end of method keep
	
	public void memorizeRemoveNodes( final PhylogenyNode n){
		//REMOVE_ACTIVE //used to store the currently removed node ACC per session if any to check between keep and remove
		PhylogenyNode r = SubTreePanel.getTree_always_full().getNode(n.getNodeId());
		if(n.isExternal()){            //CASE 1 and 2
			if(REMOVE_ALL.contains(n.getNodeId())){  //CASE 1 : leaf node and remove
				REMOVE_ALL.remove(REMOVE_ALL.indexOf(n.getNodeId()));  //remove the node
				
				
				if(n.extraNodeInfo.getNodeAcc()!=null && ACC.contains(n.extraNodeInfo.getNodeAcc()))
					ACC.remove(ACC.indexOf(n.extraNodeInfo.getNodeAcc()));
				if(n.extraNodeInfo.getNodeAcc()!=null && REMOVE_ACTIVE.contains(n.extraNodeInfo.getNodeAcc()))
					REMOVE_ACTIVE.remove(REMOVE_ACTIVE.indexOf(n.extraNodeInfo.getNodeAcc()));
				
				
				PhylogenyNode parent = r.getParent();                //remove the parent until the root for that node if remove all does not 
				PhylogenyNode nextParent = null;         // contain that the parents child 1 or child 2 
	         	while( ( nextParent = parent.getParent()) != null){
	         		if(REMOVE_ALL.contains(parent.getNodeId())){	
		        		REMOVE_ALL.remove(REMOVE_ALL.indexOf(parent.getNodeId()));
	        		}
		        	parent = nextParent;
	        	}
			}
			else {                                                             //CASE 2 leaf node and  add
				REMOVE_ALL.add(n.getNodeId());                               //add the node
				if(n.extraNodeInfo.getNodeAcc()!=null && !ACC.contains(n.extraNodeInfo.getNodeAcc()))
					ACC.add(n.extraNodeInfo.getNodeAcc());
				if(n.extraNodeInfo.getNodeAcc()!=null && !REMOVE_ACTIVE.contains(n.extraNodeInfo.getNodeAcc()))
					REMOVE_ACTIVE.add(n.extraNodeInfo.getNodeAcc());
				PhylogenyNode parent = r.getParent();                //add the parent till the root if the parent not already in remove all 
				PhylogenyNode nextParent = null;          
	         	while( ( nextParent = parent.getParent()) != null){
	         		if(!REMOVE_ALL.contains(parent.getNodeId()) && (REMOVE_ALL.contains(parent.getChildNode1().getNodeId())&&REMOVE_ALL.contains(parent.getChildNode2().getNodeId()))){
	        			//break;
	         		//if(!REMOVE_ALL.contains(parent.getNodeId())){
	         			REMOVE_ALL.add(parent.getNodeId());
	         		}
	         		else break;
	         		parent=nextParent;
	         	}
			}
			
		}
		else if(!n.isExternal()){           //CASE 3 and 4
			//CHECK IF IT IS CASE 3 or 4 by seeing if any external child is not in remove all, if yes then case 4 (add) else case 3(remove)
			boolean add=false;
			int id=0;
			temp.clear();
			Iterator its  = n.getAllExternalDescendants().iterator();
			while(its.hasNext()){
				PhylogenyNode currNode = (PhylogenyNode) its.next();
				if(currNode.extraNodeInfo.getNodeAcc()!=null)
					temp.add(currNode.extraNodeInfo.getNodeAcc());  //stored the accessions of all ext children
				id = currNode.getNodeId();
				if(!REMOVE_ALL.contains(id))
					add=true;
			}
			int k =id;   //storing the last of the external node id's for later on adding them or removing them
			if(add==false){   //Case 3 node is internal and remove because all external children are in remove all
				if(REMOVE_ALL.contains(n.getNodeId()))
					REMOVE_ALL.remove(REMOVE_ALL.indexOf(n.getNodeId()));
				for(int j=n.getNodeId(); j<=k;j++){
					if(REMOVE_ALL.contains(j)){
						REMOVE_ALL.remove(REMOVE_ALL.indexOf(j));
					}
				}
				
				
				PhylogenyNode parent = r.getParent();                //remove the parent until the root for that node if remove all does not 
				PhylogenyNode nextParent = null;		 // contan that the parents child 1 or child 2
				while( ( nextParent = parent.getParent()) != null){
	         		
	        		if(REMOVE_ALL.contains(parent.getNodeId())){	
	        			REMOVE_ALL.remove(REMOVE_ALL.indexOf(parent.getNodeId()));
	        		}
		        	parent = nextParent;
	        	}
				Iterator ext_acc =temp.iterator();
				while(ext_acc.hasNext()){
					Object acc1 = ext_acc.next();
					if(ACC.contains(acc1)){
						ACC.remove(ACC.indexOf(acc1));
					}
					if(REMOVE_ACTIVE.contains(acc1)){
						REMOVE_ACTIVE.remove(REMOVE_ACTIVE.indexOf(acc1));
					}
				}
			} //end of add==false case 3 
			else if(add==true){ //Case 4 node is internal and add because any one/all of the external children are not in remove all
				if(!REMOVE_ALL.contains(n.getNodeId())){
					REMOVE_ALL.add(n.getNodeId());
				}
				for(int j=n.getNodeId(); j<=k;j++){
					if(!REMOVE_ALL.contains(j)){
						REMOVE_ALL.add(j);
					}
				}
				PhylogenyNode parent = r.getParent();                //add the parent till the root if the parent not already in remove all 
				PhylogenyNode nextParent = null;          
	         	while( ( nextParent = parent.getParent()) != null){
	         		if(!REMOVE_ALL.contains(parent.getNodeId()) && (REMOVE_ALL.contains(parent.getChildNode1().getNodeId())&&REMOVE_ALL.contains(parent.getChildNode2().getNodeId()))){
	        		//	break;
	         		//if(!REMOVE_ALL.contains(parent.getNodeId())){
	         			REMOVE_ALL.add(parent.getNodeId());
	         		}
	         		else break;
	         		parent=nextParent;
	         	}
	         	Iterator ext_acc =temp.iterator();
	         	while(ext_acc.hasNext()){
					Object acc1 = ext_acc.next();
					if(!ACC.contains(acc1)){
						ACC.add(acc1.toString());
					}
					if(!REMOVE_ACTIVE.contains(acc1)){
						REMOVE_ACTIVE.add(acc1.toString());
					}
				}
			} //end of add==true case 4
		}// end of !n.isExternal
	} //end of method remove
    public void crashRecovery(ArrayList<String> ACC_in){
    	
   // 	PARENT_TO_ROOT.clear();
    	REMOVE_ALL.clear();
  //  	REMOVE_INACTIVE.clear();
//		REVERT_ACTIVE.clear();
		keepACC.clear();
		KEEP_ACTIVE.clear();
		COLOR_NODES.clear();
//		remClicked.clear();
//		REVERT_ACTIVE.clear();
//		rem_ACC.clear();
 //   	ACC.clear();
    	//last_saved_ACC.clear();
    	ACC.addAll(ACC_in);
    	ArrayList<PhylogenyNode> removed_nodes = new ArrayList<PhylogenyNode>();
    	ArrayList<PhylogenyNode> allNodes = SubTreePanel.getAllNodes();
    	
    	for ( PhylogenyNode pn : allNodes){
    		for( String o: ACC){
    			
    			if(pn.extraNodeInfo.getNodeAcc()!=null){
	    			if(pn.extraNodeInfo.getNodeAcc().equals(o)){
	    				memorizeRemoveNodes(pn);
	    				//if(!REMOVE_ALL.contains(pn.getNodeId())){
	    					//REMOVE_ALL.add(pn.getNodeId());
	    					//removed_nodes.add(pn);
	    					
	    				//}
	    			}
    			}
    		}
    	}
  //  	REMOVE_INACTIVE.clear();
//		REVERT_ACTIVE.clear();
		keepACC.clear();
		KEEP_ACTIVE.clear();
		COLOR_NODES.clear();
//		remClicked.clear();
//		REVERT_ACTIVE.clear();
		rem_ACC.clear();
//		PARENT_TO_ROOT.clear();
    	//for ( PhylogenyNode n : removed_nodes){
    	//	memorizeRemoveNodes(n);
    	//}
    	
    	
    	
    }
    public String get_rem_acc(){
    	String rmSeqs = "";
    	
    	if(ACC.isEmpty() ||(rem_ACC.containsAll(ACC) && ACC.containsAll(rem_ACC)))
    		setRmFromWS( false );
    	else setRmFromWS( true );
    	String s2="";
    	 //String a1[] = new Array()
    	Object a1[] = ACC.toArray();
    	for(int i =0; i<a1.length;i++){
    	   s2 += a1[i].toString() +" ";
    	}
    	System.out.println("S2"+" "+s2);
    	//rmSeqs=ACC.toString();
    	//String s1 = rmSeqs.replaceAll("\\[", "");
	    //String s2 = s1.replaceAll("\\]", "");
	    //System.out.println("returning from WorkingSet.getRemoveSequences() with: " + s2 + "\n\n");
	    return s2;
    }
    public void copy_acc(){
    	rem_ACC.clear();
        rem_ACC.addAll(ACC);	
    		
    	
    }
    
    public void resetACC(ArrayList a1){
    	ACC.clear();
    	ACC.addAll(a1);
    	rem_ACC.clear();
    	rem_ACC.addAll(a1);
    }
    public void resetRemoveALL(ArrayList a1){
    	REMOVE_ALL.clear();
    	REMOVE_ALL.addAll(a1);
    }
	public void clear( String type){
    	for(Object o: COLOR_NODES){
    		if(!REMOVE_ALL.contains(o)){
    			REMOVE_ALL.add(o);
    			//System.out.println("NODE object" + o.);
    		}
    	}
		
		if( type.equals( "removeActive")){
    		REMOVE_ACTIVE.clear();
    	}
/*     	if( type.equals( "removeInactive")){
    		REMOVE_INACTIVE.clear();
    	}*/
/*    	else if( type.equals( "revert")){
    		REVERT_ACTIVE.clear();
    	}*/
    	else if( type.equals( "keepACC")){
    		keepACC.clear();
    	}
    	else if( type.equals( "keep")){
    		KEEP_ACTIVE.clear();
    	}
    	else if( type.equals( "colorNodes")){
    		COLOR_NODES.clear();
    	}
/*    	else if( type.equals( "remClicked")){
    		remClicked.clear();
    	}*/
/*    	else if( type.equals( "parentToRoot")){
    		PARENT_TO_ROOT.clear();
    	}*/
    	else if( type.equals( "ACC")){
    		ACC.clear();
    	}
    	else if( type.equals( "rem_ACC")){
    		rem_ACC.clear();
    	}
    	else if( type.equals( "rm_all")){
    		REMOVE_ALL.clear();
    	}
    	
    	
       	
    	else{
    		//handle this
    	}
    }
    
    private void setRmFromWS( boolean b){
		rmFromWS = b;
	}
	
	public boolean getRmFromWS(){
		return rmFromWS;
	}
	
	public ArrayList getKeepSequenceIds(){
		return KEEP_ACTIVE;
	}
	
	public ArrayList getRemoveActiveSequenceIds(){
		return REMOVE_ACTIVE;
	}
	
/*	public ArrayList getRemoveInactiveSequenceIds(){
		return REMOVE_INACTIVE;
	}
*/	
	public ArrayList getRemoveAllSequenceIds(){
		return REMOVE_ALL;
	}
	
/*	public ArrayList getRevertSequenceIds(){
		return REVERT_ACTIVE;
	}*/
	public ArrayList getColorNodes(){
		return COLOR_NODES;
	}
/*	public ArrayList getRemClickSequenceIds(){
		return remClicked;
	}*/
/*	public ArrayList getParentToRoot(){
		return PARENT_TO_ROOT;
	}*/
	public ArrayList getACC(){
		return ACC;
	}
	public ArrayList getRem_ACC(){
		return rem_ACC;
	}
	public ArrayList getkeepACC(){
		return keepACC;
	}
	
}
