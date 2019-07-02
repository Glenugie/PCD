package com.pcd;

import peersim.core.Node;

public class P2PMessage {
	public Node sender;
	public String type;
	public Object[] payload;
	public long time;
	
	public P2PMessage(Node s, String t, Object[] p, long ti) {
		sender = s;
		type = t;
		payload = p;
		time = ti;
	}
}
