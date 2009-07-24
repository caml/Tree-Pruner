package com.lanl.application.treePruner.applet;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.forester.archaeopteryx.MainFrame;
import org.forester.archaeopteryx.TreePanel;
import org.forester.archaeopteryx.Util; //import org.forester.archaeopteryx.Util;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.util.ForesterUtil;

public class NewWindowSubtree {
	private final static int MAX_SUBTREES = 100;
	public SubTreePanel subTreePanel;
	TreePanel treePanel;
	private static TreePrunerCommunicationMessageWarningWindow warningWindow;
	static WindowAdapter doNothingWindowAdapter = new WindowAdapter(){
		public void windowClosing( final WindowEvent e ) {
			JOptionPane.showMessageDialog( null, "You can only close the lowest level subtree.\n");
		}
	};
	public NewWindowSubtree(TreePanel tp) {
		this.treePanel = tp;
	}

	public void subTree(PhylogenyNode node, Phylogeny _phylogeny,
			SubTreePanel subTreePanel) {
		int temp_count = TreePanel.get_subtree_index();
		boolean alreadyClickedOnPhylogeny = false;
		for (Phylogeny p : SubTreePanel._phylogenies) {
			if (p == _phylogeny) {
				alreadyClickedOnPhylogeny = true;
				break;
			}
		}

		if (!node.isExternal() && !node.isRoot()
				&& (TreePanel.get_subtree_index() <= (MAX_SUBTREES - 1))) {
			if (!alreadyClickedOnPhylogeny) {
				warningWindow = new TreePrunerCommunicationMessageWarningWindow();
				SubTreePanel._phylogenies.add(_phylogeny);
				SubTreePanel.subTreeHierarchy.add(1);
				SubTreePanel._phylogenies_subtree.add(_phylogeny.subTree(node
						.getNodeId()));
				SubTreePanel.subTreeRootNode.put(node.getNodeId(), null);

				SubTreePanel.mainFrames.add(subTreePanel.archaeA
						.create_new_Frame());
				SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count)
						.repaintPanel();
				TreePanel.set_subtree_index(TreePanel.get_subtree_index() + 1);
				Phylogeny[] phys = null;
				try {
					phys = Util.readPhylogeniesFromUrlForSubtree(new URL(
							AppletParams.urlOfTreeToLoad));
				} catch (FileNotFoundException e) {
					destroyWarningWindow();
					e.printStackTrace();
				} catch (MalformedURLException e) {
					destroyWarningWindow();
					e.printStackTrace();
				} catch (IOException e) {
					destroyWarningWindow();
					e.printStackTrace();
				}
				try {
					Util.addPhylogeniesToTabsForSubtree(phys, new File(new URL(
							AppletParams.urlOfTreeToLoad).getFile()).getName(),
							AppletParams.urlOfTreeToLoad,
							SubTreePanel.mainFrames.get(
									SubTreePanel.sub_frame_count)
									.get_configuration(),
							SubTreePanel.mainFrames.get(
									SubTreePanel.sub_frame_count)
									.get_main_panel());
				} catch (MalformedURLException e) {
					destroyWarningWindow();
					e.printStackTrace();
				}
				SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count)
						.get_main_panel().get_control_panel().show_whole_all();
				SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count)
						.get_main_panel().adjust_JScroll_pane();
				SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count)
						.get_main_panel().get_current_treePanel().repaint();
				SubTreePanel.mainFrames.get(SubTreePanel.sub_frame_count)
						.repaint();

				SubTreePanel.sub_frame_count++;
				handleBackToSubTreeButton();
				handleCloseXButton();
				destroyWarningWindow();
			} else {
				JOptionPane
						.showMessageDialog(
								treePanel,
								"You already have one subtree of this tree open. You cannot open a second subtree.");
			}
		} else if (node.isRoot() && (TreePanel.get_subtree_index() >= 1)) {
			if (SubTreePanel._phylogenies_subtree.get(temp_count - 1).getRoot()
					.getNodeId() == node.getNodeId()) {
				int temp = SubTreePanel.sub_frame_count;
				MainFrame mf = SubTreePanel.mainFrames.get(--temp);
				mf.close_();
				
			} else {
				JOptionPane
						.showMessageDialog(treePanel,
								"You can only close the lowest level subtree.");
			}
		}
		treePanel.getMainPanel().get_control_panel().show_whole();
		treePanel.repaint();
	}

	public void superTree() {

		if (TreePanel.get_subtree_index() >= 1) {
			int temp = SubTreePanel.sub_frame_count;
			MainFrame mf = SubTreePanel.mainFrames.get(--temp);
			mf.close_();
			

		} else {
			// this should not happen as if only the root parent window is
			// present then the "back to supertree" option should be disabled
		}
	}

	public static void handleBackToSubTreeButton() {
		if (SubTreePanel.mainFrames.isEmpty()
				|| SubTreePanel.mainFrames.size() < 1
				|| SubTreePanel.sub_frame_count == 0) {
		} else {
			SubTreePanel.mainAppletFrame.get_main_panel()
					.get_current_treePanel().updateSubSuperTreeButton(true);
			for (MainFrame o : SubTreePanel.mainFrames) {
				if (o != null) {
					o.get_main_panel().get_current_treePanel()
							.updateSubSuperTreeButton(true);
				}
			}
			SubTreePanel.mainFrames.get(SubTreePanel.mainFrames.size() - 1)
					.get_main_panel().get_current_treePanel()
					.updateSubSuperTreeButton(false);
		}
	}
	public static  void handleCloseXButton(){
		if (SubTreePanel.mainFrames.isEmpty()
				|| SubTreePanel.mainFrames.size() < 1
				|| SubTreePanel.sub_frame_count == 0) {
			removeWindowListners(SubTreePanel.mainAppletFrame);
			SubTreePanel.mainAppletFrame.addWindowListener(SubTreePanel.mainAppletFrame.closeWindowAdapter);
			SubTreePanel.mainAppletFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			removeWindowListners(SubTreePanel.mainAppletFrame);
			SubTreePanel.mainAppletFrame.addWindowListener(doNothingWindowAdapter);
			SubTreePanel.mainAppletFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			for (MainFrame o : SubTreePanel.mainFrames) {
				if (o != null) {
					removeWindowListners(o);
					o.addWindowListener(doNothingWindowAdapter);
					o.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
			
			removeWindowListners(SubTreePanel.mainFrames.get(SubTreePanel.mainFrames.size() - 1));
		
			SubTreePanel.mainFrames.get(SubTreePanel.mainFrames.size() - 1)
			.addWindowListener(SubTreePanel.mainFrames.get(SubTreePanel.mainFrames.size() - 1).closeWindowAdapter);
			SubTreePanel.mainFrames.get(SubTreePanel.mainFrames.size() - 1).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			
		}
	}
	private static void removeWindowListners(MainFrame mf){
		WindowListener x[] = mf.getWindowListeners();
		for(int i = 0; i < x.length; i++){
			mf.removeWindowListener(x[i]);
		}
	}
	
	public static void destroyWarningWindow() {
		if (warningWindow != null) {
			warningWindow.close();

		}
	}

	public void paintNodeTracker(Graphics g, double x, double y,
			PhylogenyNode node, boolean toPdf, boolean toGraphicsFile) {
		if (SubTreePanel.subTreeRootNode.containsKey(node.getNodeId())
				&& !toPdf && !toGraphicsFile) {
			ArrayList<Double> temp = new ArrayList<Double>();
			temp.add(x);
			temp.add(y);
			SubTreePanel.subTreeRootNode.put(node.getNodeId(), temp);
			g.setColor(TreePrunerColorSet.getTrackSubtreeCircleColor());
			drawOval(
					SubTreePanel.subTreeRootNode.get(node.getNodeId()).get(0) - 8,
					SubTreePanel.subTreeRootNode.get(node.getNodeId()).get(1) - 8,
					16, 16, g);
			drawOval(
					SubTreePanel.subTreeRootNode.get(node.getNodeId()).get(0) - 9,
					SubTreePanel.subTreeRootNode.get(node.getNodeId()).get(1) - 8,
					17, 17, g);
			drawOval(
					SubTreePanel.subTreeRootNode.get(node.getNodeId()).get(0) - 9,
					SubTreePanel.subTreeRootNode.get(node.getNodeId()).get(1) - 9,
					18, 18, g);

		}
	}

	private static void drawOval(final double x, final double y,
			final double width, final double heigth, final Graphics g) {
		g.drawOval(ForesterUtil.roundToInt(x), ForesterUtil
						.roundToInt(y), ForesterUtil.roundToInt(width),
						ForesterUtil.roundToInt(heigth));
	}
	
}
