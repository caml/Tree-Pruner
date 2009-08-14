package com.lanl.application.TPTD.tree.node;

public class ExtraNodeInfo {
	private String						   _acc =null;
	private String						   _country =null;
	private String						   _year=null;
	private String						   _aha=null;
	private String						   _ana=null;
	private String						   _host=null;
	
	public ExtraNodeInfo(){
		_acc=null;
	}
	
	public String getNodeAcc(){
		return _acc;
	}
	public void setNodeAcc(String acc){
		this._acc = acc;
	}
	public String getNodeCountry(){
		return _country;
	}
	public void setNodeCountry(String country){
		this._country = country;
	}
	public String getNodeYear(){
		return _year;
	}
	public void setNodeYear(String year){
		this._year = year;
	}
	public String getNodeHA(){
		return _aha;
	}
	public void setNodeHA(String aha){
		this._aha = aha;
	}
	public String getNodeNA(){
		return _ana;
	}
	public void setNodeNA(String ana){
		this._ana = ana;
	}
	public String getNodeHost(){
		return _host;
	}
	public void setNodeHost(String host){
		this._host = host;
	}
	
	@Override
	public String toString(){
		String s ="";
		if(_acc!=null)
			 s+= "\nAccession: "+_acc;
		else s+= "\nAccession: null";
		if(_country!=null)
			 s+= "\nCountry: "+_country;
		else s+= "\nCountry: null";
		if(_year!=null)
			 s+= "\nYear: "+_year;
		else s+= "\nYear: null";
		if(_aha!=null)
			 s+= "\nHA: "+_aha;
		else s+= "\nHA: null";
		if(_aha!=null)
			 s+= "\nNA: "+_ana;
		else s+= "\nNA: null";
		if(_host!=null)
			 s+= "\nHost: "+_host;
		else s+= "\nHost: null";
		return s;
		
	}
}