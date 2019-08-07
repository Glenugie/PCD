package com.pcd.model;

import java.util.HashSet;

import peersim.core.Node;

public class P2PMessage {
	public Node sender;
	public Node receiver;
	public int transactionId;
	public String type;    
    public long time;
	
	public Object[] body;
	
	private HashSet<Node> chain;
	
	public P2PMessage(Node s, Node r, int id, String t, long ti, Object[] b) {
	    //System.out.println(s.getID()+", "+r.getID());
		sender = s;
		receiver = r;
		transactionId = id;
		type = t;
        time = ti;
		
        body = b;		
        
        chain = new HashSet<Node>();
        chain.add(s);
        chain.add(r);
	}
	
	public HashSet<Node> getChain() {
	    return chain;
	}
	
	public void addChain(HashSet<Node> nodes) {
	    chain.addAll(nodes);
	}
    
    public boolean inChain(Node test) {
        for (Node n : chain) {
            if (n.getID() == test.getID()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean inChain(long test) {
        for (Node n : chain) {
            if (n.getID() == test) {
                return true;
            }
        }
        return false;
    }
	
	public String chainString() {
        String chainString = "";
        for (Node n : chain) {
            chainString = "peer"+n.getID()+",";
        }
        if (chainString.length() > 0) {
            chainString = "<"+chainString.substring(0, chainString.length() - 1)+">";
        }
        return chainString;
	}
}
