package com.lanl.application.treeDecorator.enumeration;

public enum DecoratorUIConstants {
	TREE_DECORATOR_FRAME_HEADER("Decorator Functions"),
	SEMI_AUTOMATIC_DECORATION_TITLE("Semi-Automatic Decoration"),
	MANUAL_DECORATION_TITLE("Manual Decoration"),
	PREVIOUS_SELECTION("Your Previous Selection"),
	MANUAL("Manual?"),
	APPLY("Apply"),
	CLOSE("Close"),
	BACK("Back"),
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
	STRAIN("Strain               "),
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
