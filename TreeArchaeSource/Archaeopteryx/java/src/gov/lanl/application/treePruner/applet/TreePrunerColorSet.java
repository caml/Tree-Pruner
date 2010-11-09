package gov.lanl.application.treePruner.applet;

import java.awt.Color;

public class TreePrunerColorSet {
	private static Color notRemoveColor = Color.BLACK; // black // keep color
	private static Color inactiveInKeepColor = Color.BLUE;   // blue //in keep_seq before marking permanent keep ununtouched //kmohan 07/24
	private static Color removeColor = new Color(190,190,190); // gray // for inactive nodes
	private static Color backgroundColor = Color.WHITE; // white // for background
	private static Color defaultBranchColor = Color.BLACK; // black // default color for branch,node,nodeBox, strain color
	private static Color trackSubtreeCircleColor = Color.RED; //red // for the circle that colors the subtree roots on all windows
	
	public static Color getKeepColor() {
        return notRemoveColor;
    }
    
    public static Color getInactiveInKeepColor() {
        return inactiveInKeepColor;
    }
    
    public static Color getRemoveColor(){
    	return removeColor;
    }
    
    public static Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public static Color getDefaultBranchColor() {
        return defaultBranchColor;
    }
    
    public static Color getTrackSubtreeCircleColor(){
        return trackSubtreeCircleColor;
    }
    
    
}
