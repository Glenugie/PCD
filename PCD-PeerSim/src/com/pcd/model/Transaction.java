package com.pcd.model;

public class Transaction {
    public int transactionId;
    public long peerID;
    public String predicate;
    public int quantity;
    public int lifetime;
    private int maxLife;
    
    public Transaction(int id, long peer, String p, int quant, int l) {
        transactionId = id;
        peerID = peer;
        predicate = p;
        quantity = quant;
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
}
