package com.pcd.model;

import java.util.HashSet;

import peersim.core.Node;

public class P2PMessage {
	//public Node sender;
	//public Node receiver;
    public long sender;
    public long receiver;
    public int prvTransId;
    public int reqTransId;
	public String type;    
    public long time;
	
	public Object[] body;
	
	private HashSet<Long> chain;
	
	public P2PMessage(long s, long r, int prvid, int reqid, String t, long ti, Object[] b) {
	    //System.out.println(s.getID()+", "+r.getID());
		sender = s;
		receiver = r;
        prvTransId = prvid;
        reqTransId = reqid;
		type = t;
        time = ti;
		
        body = b;		
        
        chain = new HashSet<Long>();
        chain.add(s);
        chain.add(r);
	}
	
	public HashSet<Long> getChain() {
	    return chain;
	}
	
	public void addChain(HashSet<Long> nodes) {
	    chain.addAll(nodes);
	}
    
    public boolean inChain(Node test) {
        for (Long n : chain) {
            if (n == test.getID()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean inChain(long test) {
        for (Long n : chain) {
            if (n == test) {
                return true;
            }
        }
        return false;
    }
	
	public String chainString() {
        String chainString = "";
        for (Long n : chain) {
            chainString = "peer"+n+",";
        }
        if (chainString.length() > 0) {
            chainString = "<"+chainString.substring(0, chainString.length() - 1)+">";
        }
        return chainString;
	}
	
	public String toString() {
	    String msg = sender+"->"+receiver+" ["+type+"]: ";
	    for (Object o : body) {
	        msg += o+", ";
	    }
	    msg = msg.substring(0,msg.length()-2);
	    return msg;
	}
}
