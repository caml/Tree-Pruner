package com.lanl.application.treePruner.applet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.forester.archaeopteryx.MainFrame;
import org.forester.archaeopteryx.TreePanel;
import org.forester.archaeopteryx.Util;
//import org.forester.archaeopteryx.Util;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

public class NewWindowSubtree {
	private final static int               MAX_SUBTREES                      = 100;
	public SubTreePanel subTreePanel;
	TreePanel treePanel;
	public NewWindowSubtree(TreePanel tp){
		this.treePanel = tp;
	}
	public void subTree(PhylogenyNode node, Phylogeny _phylogeny,SubTreePanel subTreePanel){
		int temp_count = TreePanel.get_subtree_index();
		if ( !node.isExternal() && !node.isRoot() && ( TreePanel.get_subtree_index() <= ( MAX_SUBTREES - 1 ) ) ) {
	        SubTreePanel._phylogenies.add(_phylogeny);
	        SubTreePanel._phylogenies_subtree.add(_phylogeny.subTree( node.getNodeId() ));
	        
	        SubTreePanel.mainFrames.add(subTreePanel.archaeA.create_new_Frame());
	        SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).repaintPanel();
	        TreePanel.set_subtree_index(TreePanel.get_subtree_index()+1);
	        Phylogeny[] phys=null;
			try {
				phys = Util.readPhylogeniesFromUrlForSubtree( new URL(AppletParams.urlOfTreeToLoad) );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				Util.addPhylogeniesToTabsForSubtree( phys, new File( new URL(AppletParams.urlOfTreeToLoad).getFile() ).getName(), 
						AppletParams.urlOfTreeToLoad, SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).get_configuration(),
						SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).get_main_panel() );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).get_main_panel().get_control_panel().show_whole_all();
	        SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).get_main_panel().adjust_JScroll_pane();
	        SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).get_main_panel().get_current_treePanel().repaint();
	        SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count).repaint();
	        
	        SubTreePanel.sub_frame_count++;
	        SubTreePanel.mainAppletFrame.get_main_panel().get_current_treePanel().update_subSuperTree_button();
	        for(MainFrame o : SubTreePanel.mainFrames){
    			if(o!=null){
    				o.get_main_panel().get_current_treePanel().update_subSuperTree_button();
    			}
    		}
	    }
	    else if ( node.isRoot() && ( TreePanel.get_subtree_index() >= 1 ) ) {
	    	if(SubTreePanel._phylogenies_subtree.get(temp_count-1).getRoot().getNodeId()==node.getNodeId()){
	            int temp = SubTreePanel.sub_frame_count;
	            MainFrame mf=SubTreePanel.mainFrames.get(--temp);
	            mf.closeSubTree();
	            SubTreePanel.mainAppletFrame.get_main_panel().get_current_treePanel().update_subSuperTree_button();
	            for(MainFrame o : SubTreePanel.mainFrames){
	    			if(o!=null){
	    				o.get_main_panel().get_current_treePanel().update_subSuperTree_button();
	    			}
	            }
	    	}
	    	else{
	    		JOptionPane.showMessageDialog( treePanel, "You can only close on the most recent subtree window." );
	    	}
	    }
		treePanel.getMainPanel().get_control_panel().show_whole();
		treePanel.repaint();
	}
	
	public void superTree() {
    	
    	if(TreePanel.get_subtree_index() >= 1){
    		JOptionPane.showMessageDialog( treePanel, "You can only close on the most recent subtree window.\n" +
    				" This action will close the most recent subtree window" );
    		int temp = SubTreePanel.sub_frame_count;
    		MainFrame mf=SubTreePanel.mainFrames.get(--temp);
    		mf.closeSubTree();
    		SubTreePanel.mainAppletFrame.get_main_panel().get_current_treePanel().update_subSuperTree_button();
    		for(MainFrame o : SubTreePanel.mainFrames){
    			if(o!=null){
    				o.get_main_panel().get_current_treePanel().update_subSuperTree_button();
    			}
    		}
    		
    	}
    	else{
    		//this should not happen as if only the root parent window is present then the "back to supertree" option should be disabled
    	}
	}
}
