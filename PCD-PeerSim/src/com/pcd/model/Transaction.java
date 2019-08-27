package com.pcd.model;

import java.util.HashSet;

public class Transaction {
    public int transactionId;
    public int remoteId;
    public long peerID;
    
    public String predicate;
    public int quantity;
    
    public HashSet<PolicySet> policySets;
    
    public int lifetime;
    private int maxLife;
    
    public Transaction(int id, int rId, long peer, String p, int quant, int l) {
        transactionId = id;
        remoteId = rId;
        peerID = peer;
        
        predicate = p;
        quantity = quant;
        
        policySets = new HashSet<PolicySet>();
        
        lifetime = l;
        maxLife = l;
    }
    
    public void resetLife() {
        lifetime = maxLife;
    }
    
    public boolean decrementLife() {
        lifetime -= 1;
        return (lifetime <= 0);
    }
    
    public String toString() {
        return "t( tid: "+transactionId+" , rid: "+remoteId+" , pid: "+peerID+" , pred: "+predicate+" x "+quantity+" [ "+lifetime+" ] )";
    }
}
