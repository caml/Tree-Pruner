package com.lanl.application.treeDecorator.enumeration;

public enum DecoratorUIConstants {
	TREE_DECORATOR_FRAME_HEADER("Decorator Functions"),
	SEMI_AUTOMATIC_DECORATION_TITLE("Semi-Automatic Decoration"),
	MANUAL_DECORATION_TITLE("Manual Decoration"),
	INFORMATION("Information"),
	SELECTED_CHARACTERISTIC_LABEL_TEXT ("You have selected characteristic as "),
	SELECTED_TO_DECORATE_LABEL_TEXT (", to decorate as "),
	SELECTED_DECORATION_STYLE_LABEL_TEXT (" and decoration option as "),
	TO_SELECT_CHARACTERISTIC_LABEL_TEXT ("Please select a characteristic to decorate."),
	TO_SELECT_TO_DECORATE_LABEL_TEXT ("Please select whether you want to decorate the node or the strain."),
	TO_SELECT_DECORATION_STYLE_LABEL_TEXT ("Please select a decoration style for "),
	TO_SELECT_MANUAL_APPLY_STYLE_LABEL_TEXT ("Please check manual if you want to do manual decoration " +
			"or click on apply to perform semi - automatic decoration"),
	MANUAL("Manual?"),
	APPLY("Apply"),
	CLOSE("Close"),
	BACK("Back"),
	DEFAULT("Revert to Default"),
	RESET("Reset"),
	NODE_COLOR("Node Color"),
	SHAPES("Shapes"),
	STRAIN_COLOR("Strain Color"),
	CASE("Case"),
	FONT("Font"),
	STYLE("Style"),
	SIZE("Size"),
	STRAIN_DECORATION("Strain Decoration               "),
	NODE_DECORATION("Node Decoration"),
	DECORATION_OPTIONS("Decoration Options"),
	NODE("Node"),
	STRAIN("Strain"),
	TO_DECORATE("To Decorate"),
	COUNTRY("Country"),
	YEAR("Year"),
	A_HA_SUBTYPE("A/HA subtype"),
	A_NA_SYBTYPE("A/NA subtype"),
	HOST_SPECIES("Host species"),
	CHARACTERISTIC("Characteristic"),
	NULL("None");
	
	private final String name;
	
	DecoratorUIConstants(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
}
