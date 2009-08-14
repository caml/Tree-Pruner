package com.lanl.application.treeDecorator.enumeration;

public enum CommunicationEnum {
	///incoming JSON - Sequence details
	DECORATOR("DECORATOR"),
	SEQUENCE_DETAILS("Sequence Details"),
	HA("HA"),
	NA("NA"),
	YEAR("Year"),
	COUNTRY("Country"),
	HOST("Host"),
	
	///incoming JSON - Saved Decoraion
	/**
	 * used from DecoratorUIConstants: NODE,STRAIN,NODE_COLOR,SHAPES,STRAIN_COLOR,CASE,FONT,STYLE,SIZE
	 * used from DecorationStyles: all except NULL
	 * 
	 */
	DECORATION("Decoration"),
	DECORATED_BY("Decorated by"),
	
	//common to both above
	GET_WHAT_KEY("Get What"),
	GET_WHAT_SEQ_DETAILS("Sequence Details"),
	GET_WHAT_SAVED_DECORATIONS("Saved Decorations"),
	SERVER_ERROR_SEQ_DETAILS("There was a problem getting details of your sequences from the server\n" +
		    			"Tree decortor cannot function without the details of your sequences\n Therefore tree Decorator will have to exit\n" +
		    			"Exititng now. If the problem persists please contact flu@lanl.gov"),
	SERVER_ERROR("Server Error");
	private final String name;
	
	CommunicationEnum(String s){
		this.name = s;
	}
	public String getName(){
		return this.name;
	}
}
