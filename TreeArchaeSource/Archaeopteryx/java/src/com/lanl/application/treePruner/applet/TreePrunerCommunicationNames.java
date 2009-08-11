package com.lanl.application.treePruner.applet;

public enum TreePrunerCommunicationNames {
	
	
	
	SEQ_ACC_TO_REMOVE("Sequence Accession to Remove"),
	ACTION("Action"),
	FILENAME("Filename"),
	PRUNER("Pruner"),
	COMMIT("Commit"),
	SAVE("Save"),
	DISCARD("Discard"),
	SAVE_SUCCESS("Accessions saved successfully"),
	DISCARD_SUCCESS("Accession successfully discarded"),
	COMMIT_SUCCESS("Accessions successfully removed from DB");
	
	
	private final String name;
	
	TreePrunerCommunicationNames( String name ){
		this.name = name;
    }
	
	 public String getName(){
		 return name;
     }
	 
	 
}
