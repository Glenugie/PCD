package com.pcd;

import peersim.core.Node;

public class P2PMessage {
	public Node sender;
	public Node receiver;
	public int transactionId;
	public String type;    
    public long time;
	
	public Object[] body;
	
	public P2PMessage(Node s, Node r, int id, String t, long ti, Object[] b) {
		sender = s;
		receiver = r;
		transactionId = id;
		type = t;
        time = ti;
		
        body = b;		
	}
}
