package com.pcd;

public class Transaction {
    public int transactionId;
    public String predicate;
    public int quantity;
    
    public Transaction(int id, String p, int quant) {
        transactionId = id;
        predicate = p;
        quantity = quant;
    }
}
