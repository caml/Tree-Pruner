package com.lanl.application.treePruner.applet;

import java.util.ArrayList;

import org.forester.archaeopteryx.MainFrame;
import org.forester.archaeopteryx.MainFrameApplet;
import org.forester.archaeopteryx.TreePanel;
import org.forester.archaeopteryx.ArchaeopteryxA;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import com.lanl.application.treePruner.tree.TreeInfo;

public class SubTreePanel {
	public static ArrayList<Integer> allNodeIds = new ArrayList<Integer>();
	public static ArrayList<String> allNodeAcc  = new ArrayList<String>();
	public static Phylogeny _full_phylogeny = null;
	public static ArrayList<PhylogenyNode> allNodes = new ArrayList<PhylogenyNode>();
	private ArrayList<Integer> subTreeNodeIds = new ArrayList<Integer>();
	private ArrayList<String> subTreeNodeAcc = new ArrayList<String>();
	public static ArrayList<MainFrame>							mainFrames =new ArrayList<MainFrame>();
	private TreePanel treePanel;
	public static ArrayList<Phylogeny>                         _phylogenies =new ArrayList<Phylogeny>();
    //to store subtree phylogenies
    public static ArrayList<Phylogeny>                         _phylogenies_subtree =new ArrayList<Phylogeny>();
    //to store the atvframes
    final static ArrayList<MainFrame>							appletFrames =new ArrayList<MainFrame>();
	public static int sub_frame_count =0;
	public ArchaeopteryxA archaeA= new ArchaeopteryxA();
	public SubTreePanel(TreePanel tp){
		this.treePanel = tp;
	}
	public static MainFrameApplet mainAppletFrame;
	
	public void setPhylogeny(Phylogeny t){
		if(_phylogenies_subtree.size()>0){
        	treePanel.setPhylogeny(_phylogenies_subtree.get(_phylogenies_subtree.size()-1));
        }
        else{
        	treePanel.setPhylogeny(t);
        }
        
		if(_phylogenies.size()>0){
        	_full_phylogeny = _phylogenies.get(0);
        }
      else{
        	_full_phylogeny = t;
       }
	}
	
	public void setSubTreeNodeInfo(){
		TreeInfo ti = new TreeInfo();
		if ( ( getTree() != null ) && getTree().isEmpty() ) {
			subTreeNodeIds = ti.getAllNodeIdsAsArrayList(getTree());
			subTreeNodeAcc = ti.getAllNodeACCAsArrayList(getTree());
		}
	}
	
	public void setFullTreeNodeInfo(){
		TreeInfo ti = new TreeInfo();
		if ( ( _full_phylogeny != null ) && !_full_phylogeny.isEmpty() ) {
        	allNodeIds = ti.getAllNodeIdsAsArrayList(getTree_always_full());
        	allNodeAcc = ti.getAllNodeACCAsArrayList(getTree_always_full());
        	allNodes = ti.getAllNodesAsArrayList(getTree_always_full());
        }
	}
	
	public ArrayList<Integer> getSubTreeNodeIds(){
		return subTreeNodeIds;
	}
	
	public ArrayList<String> getSubTreeNodeAcc(){
		return subTreeNodeAcc;
	}
	
	public Phylogeny getTree() {
        return (treePanel.getCurrentPhylogeny());
    }
    public static Phylogeny getTree_always_full() {
        return _full_phylogeny;
    }
}
