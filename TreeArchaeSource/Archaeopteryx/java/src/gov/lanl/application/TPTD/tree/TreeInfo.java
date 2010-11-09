package gov.lanl.application.TPTD.tree;

import java.util.ArrayList;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public class TreeInfo {
	public ArrayList<Integer> getAllNodeIdsAsArrayList(Phylogeny _phylogeny) {
		ArrayList<Integer> allNodeIds = new ArrayList<Integer>();
		for (final PhylogenyNodeIterator iter = _phylogeny.iteratorPreorder(); iter
				.hasNext();) {
			final PhylogenyNode node = iter.next();
			allNodeIds.add(node.getNodeId());
		}
		return allNodeIds;
	}
	
	 public ArrayList<String> getAllNodeACCAsArrayList(Phylogeny _phylogeny) {
	    	ArrayList<String> allNodeAcc = new ArrayList<String>();
	        for( final PhylogenyNodeIterator iter = _phylogeny.iteratorPreorder(); iter.hasNext(); ) {
	            final PhylogenyNode node = iter.next();
	            	if(node.extraNodeInfo.getNodeAcc()!=null)
	            		allNodeAcc.add( node.extraNodeInfo.getNodeAcc());
	        }
	        return allNodeAcc;
	    }
	 
	 public ArrayList<PhylogenyNode> getAllNodesAsArrayList(Phylogeny _phylogeny) {
	    	ArrayList<PhylogenyNode> allNodes = new ArrayList<PhylogenyNode>();
	        for( final PhylogenyNodeIterator iter = _phylogeny.iteratorPreorder(); iter.hasNext(); ) {
	            final PhylogenyNode node = iter.next();
	            allNodes.add( node);
	        }
	        return allNodes;
	    }
}
