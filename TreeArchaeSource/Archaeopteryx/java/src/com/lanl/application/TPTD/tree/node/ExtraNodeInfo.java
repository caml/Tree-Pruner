package com.lanl.application.TPTD.tree.node;

public class ExtraNodeInfo {
	private String						   _acc;
	
	public ExtraNodeInfo(){
		_acc=null;
	}
	
	public String getNodeAcc(){
		return _acc;
	}
	public void setNodeAcc(String acc){
		this._acc = acc;
	}
}