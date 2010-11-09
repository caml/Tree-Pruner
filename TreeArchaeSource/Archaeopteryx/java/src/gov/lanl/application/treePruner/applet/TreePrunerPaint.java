package gov.lanl.application.treePruner.applet;

import gov.lanl.application.treePruner.custom.data.WorkingSet;

import java.awt.Color;

import java.awt.Graphics;
import java.util.ArrayList;


import org.forester.archaeopteryx.TreePanel;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.util.ForesterUtil;


public class TreePrunerPaint {
	public ArrayList rmActiveSeqIds, rmInactiveSeqIds, rmAllSeqIds, keepSeqIds,
			revertSeqIds, colorNodesSeqIds, remClickedSeqIds,
			parentToRootSeqIds, rmAllSeqACC, keepACC, rem_ACC;
	public TreePanel treePanel;
	public WorkingSet ws;

	public TreePrunerPaint(TreePanel tp, WorkingSet ws) {
		this.ws = ws;
		this.treePanel = tp;
	}

	public void initArrayLists() {
		rmActiveSeqIds = ws.getRemoveActiveSequenceIds();
		// rmInactiveSeqIds = ws.getRemoveInactiveSequenceIds();
		rmAllSeqIds = ws.getRemoveAllSequenceIds();
		keepSeqIds = ws.getKeepSequenceIds();
		// revertSeqIds = ws.getRevertSequenceIds();
		colorNodesSeqIds = ws.getColorNodes();
		// remClickedSeqIds = ws.getRemClickSequenceIds();
		// parentToRootSeqIds = ws.getParentToRoot();
		rmAllSeqACC = ws.getACC();
		keepACC = ws.getkeepACC();
		rem_ACC = ws.getRemember_ACC();
	}

	public void paintKeepRemove(Graphics g, PhylogenyNode node) {
//		System.out.println("--------->ATVtreePanel.paintNodeData() with"); // commented-
																			// kmohan
																			// 07/24
//		System.out.println("remove active:   " + rmActiveSeqIds.toString());
		
//		System.out.println("remove_all:      " + rmAllSeqIds.toString());
//		System.out.println("keep:            " + keepSeqIds.toString());
//		System.out.println("color nodes      " + colorNodesSeqIds.toString());
//		System.out.println("savedRemoveALL       " + ws.getSavedRemoveAll().toString());
//		System.out.println("savedACC       " + ws.getSavedACC().toString());
//		System.out.println("rememberACC       " + ws.getRemember_ACC().toString());
		
//		System.out.println("rmAllSeqACC     " + rmAllSeqACC.toString());
//		System.out.println("keepACC     " + keepACC.toString());
//		System.out.println("rem_ACC     " + rem_ACC.toString());
		
	

		if (rmAllSeqIds.contains(node.getNodeId())) {
			g.setColor(TreePrunerColorSet.getRemoveColor());

		} else if (colorNodesSeqIds.contains(node.getNodeId())) {
			g.setColor(TreePrunerColorSet.getInactiveInKeepColor());

			// } else if (remClickedSeqIds.contains(node.getNodeId())) {
			// g.setColor(treePanel.getTheTreeColorSet().get_species_name_color());
		}

		else {
			g.setColor(TreePrunerColorSet.getDefaultBranchColor());

		}
	}

	public void drawThickLine(Graphics g, PhylogenyNode node, float x1, float y1, float x2, float y2) {
		if(keepSeqIds.contains(node.getNodeId())){
    		drawThickLine(g,(int)x1,(int)y1,(int)x2,(int)y2,2);
    	}
    	//if(parentToRootSeqIds.contains(node.getNodeId())){
    		//drawThickLine(g,(int)x1,(int)y1,(int)x2,(int)y2,2);
    	//}
    	else {
    		drawLine( x1, y1, x2, y2, g );
    	}
		
	}
	public void drawThickLine(Graphics g, 
			int x1,
			int y1,
			int x2, 
			int y2, 
			int thickness) {

		// The thick line is in fact a filled polygon
		
		int dX = x2 - x1;
		int dY = y2 - y1;
		// line length
		double lineLength = Math.sqrt(dX * dX + dY * dY);
		
		double scale = (double)(thickness) / (2 * lineLength);
		
		// The x,y increments from an endpoint needed to create a rectangle...
		double ddx = -scale * (double)dY;
		double ddy = scale * (double)dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		int dx = (int)ddx;
		int dy = (int)ddy;
		
		// Now we can compute the corner points...
		int xPoints[] = new int[4];
		int yPoints[] = new int[4];
		
		xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
		xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
		xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
		xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;
		
		g.fillPolygon(xPoints, yPoints, 4);
	}
	private static void drawLine( final double x1, final double y1, final double x2, final double y2, final Graphics g ) {
        final int x1_ = ForesterUtil.roundToInt( x1 );
        final int x2_ = ForesterUtil.roundToInt( x2 );
        final int y1_ = ForesterUtil.roundToInt( y1 );
        final int y2_ = ForesterUtil.roundToInt( y2 );
        if ( ( x1_ == x2_ ) && ( y1_ == y2_ ) ) {
            return;
        }
        g.drawLine( x1_, y1_, x2_, y2_ );
    }
}
