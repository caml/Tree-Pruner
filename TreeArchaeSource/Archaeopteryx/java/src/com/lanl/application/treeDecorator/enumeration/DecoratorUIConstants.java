package com.lanl.application.treeDecorator.enumeration;

public enum DecoratorUIConstants {
	TREE_DECORATOR_FRAME_HEADER("Decorator Functions"),
	SEMI_AUTOMATIC_DECORATION_TITLE("Decoration Selections"),
	MANUAL_DECORATION_TITLE("Manual Decoration"),
	LEGEND_TITLE("Legend"),
	INFORMATION("Information"),
	SELECTED_CHARACTERISTIC_LABEL_TEXT ("You have chosen to decorate based on "),
	SELECTED_TO_DECORATE_LABEL_TEXT (", and will decorate the "),
	SELECTED_DECORATION_STYLE_LABEL_TEXT (" using "),
	TO_SELECT_CHARACTERISTIC_LABEL_TEXT ("Please select a viral characteristic for tree decoration."),
	TO_SELECT_TO_DECORATE_LABEL_TEXT ("Please select whether you want to decorate the node or the strain."),
	TO_SELECT_DECORATION_STYLE_LABEL_TEXT ("Please select a decoration style for "),
	TO_SELECT_MANUAL_APPLY_STYLE_LABEL_TEXT ("Please check manual if you want to do manual decoration " +
			"or click on apply to perform automatic decoration"),
	MANUAL("Manual?"),
	APPLY("Apply"),
	CLOSE("Close"),
	BACK("Back"),
	DEFAULT("Revert to Default"),
	RESET("Reset"),
	NODE_COLOR("Color"),
	SHAPES("Shape"),
	STRAIN_COLOR("Color  "),
	CASE("Case"),
	FONT("Font"),
	STYLE("Style"),
	SIZE("Size"),
	STRAIN_DECORATION("Label appearance               "),
	NODE_DECORATION("Node appearance"),
	DECORATION_OPTIONS("Decoration Options"),
	NODE("Node"),
	STRAIN("Label"),
	TO_DECORATE("To Decorate"),
	COUNTRY("Country"),
	YEAR("Year"),
	A_HA_SUBTYPE("A/HA subtype"),
	A_NA_SYBTYPE("A/NA subtype"),
	HOST_SPECIES("Host species"),
	CHARACTERISTIC("Characteristic"),
	SHOW_COUNTRY("Show Country"),
	SHOW_YEAR("Show Year"),
	SHOW_A_HA_SUBTYPE("Show A/HA subtype"),
	SHOW_A_NA_SUBTYPE("Show A/NA subtype"),
	SHOW_HOST_SPECIES("Show Host species"),
	NULL("None");
	
	private final String name;
	
	DecoratorUIConstants(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
}
