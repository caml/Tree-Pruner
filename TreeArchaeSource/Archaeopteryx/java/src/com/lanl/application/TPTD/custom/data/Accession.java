package com.lanl.application.TPTD.custom.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.forester.phylogeny.PhylogenyNode;

public class Accession {
	public static String removeAccessionFromStrain(PhylogenyNode node){
		String start=null;
    	Pattern p = Pattern.compile("'(.*)-(.*)'");
    	Matcher m = p.matcher(node.getNodeName());
    	Pattern p1 = Pattern.compile("(.*)-(.*)");
    	Matcher m1 = p1.matcher(node.getNodeName());
        int i,j=-1;
        if(m.matches()){
        	start=m.group(1);
        	j=m.end(1);
	        while(start.contains("-")){
//	        	System.out.println("START in ATV treePANEL 1 " + start);
	        	p=Pattern.compile("(.*)-(.*)");
	        	m=p.matcher(start);
	        	if(m.matches()){
	        		start=m.group(1);
	        		j=m.end(1);
	        		j++;
	        	}
	        	else break;
	         }
	      }
        else if(m1.matches()){
        	start=m1.group(1);
        	j=m1.end(1);
	        while(start.contains("-")){
//	        	System.out.println("START in ATV treePANEL 2 " + start);
	        	p1=Pattern.compile("(.*)-(.*)");
	        	m1=p1.matcher(start);
	        	if(m1.matches()){
	        		start=m1.group(1);
	        		j=m1.end(1);
	        		
	        	}
	        	else break;
	         }
        }
         node.extraNodeInfo.setNodeAcc(start);
	     String str; 
	      str = node.getNodeName().substring(j+1);
	      if(m.matches())
	    	  str = str.substring(0,str.length()-1);
          return str;
	}
	
	public static void extractAccessionFromStrain(PhylogenyNode node){
		String start=null;
    	Pattern p = Pattern.compile("'(.*)-(.*)'");
    	Matcher m = p.matcher(node.getNodeName());
    	Pattern p1 = Pattern.compile("(.*)-(.*)");
    	Matcher m1 = p1.matcher(node.getNodeName());
        int i,j=-1;
        if(m.matches()){
        	start=m.group(1);
        	j=m.end(1);
	        while(start.contains("-")){
//	        	System.out.println("START in ATV treePANEL 1 " + start);
	        	p=Pattern.compile("(.*)-(.*)");
	        	m=p.matcher(start);
	        	if(m.matches()){
	        		start=m.group(1);
	        		j=m.end(1);
	        		j++;
	        	}
	        	else break;
	         }
	      }
        else if(m1.matches()){
        	start=m1.group(1);
        	j=m1.end(1);
	        while(start.contains("-")){
//	        	System.out.println("START in ATV treePANEL 2 " + start);
	        	p1=Pattern.compile("(.*)-(.*)");
	        	m1=p1.matcher(start);
	        	if(m1.matches()){
	        		start=m1.group(1);
	        		j=m1.end(1);
	        		
	        	}
	        	else break;
	         }
        }
         node.extraNodeInfo.setNodeAcc(start);
	}
}

