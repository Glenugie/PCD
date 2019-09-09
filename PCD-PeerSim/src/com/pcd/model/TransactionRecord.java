package com.pcd.model;

import java.util.ArrayList;
import java.util.HashSet;

public class TransactionRecord {
    public String reqID;
    public String prvID;
    
    public String pred;
    public HashSet<String> result;
    
    public int qtyRequested;
    public int qtyGiven;
    
    public int cycle;
    
    public TransactionRecord(String req, String prv, String p, ArrayList<String> r, int n, int cyc) {
        reqID = req;
        prvID = prv;
        pred = p;
        result = new HashSet<String>(); result.addAll(r);
        qtyRequested = n;
        qtyGiven = result.size();
    }
    
    public boolean refersTo(String id) {
        if (reqID.equals(id) || prvID.equals(id)) {
            return true;
        }
        return false;
    }
    
    public boolean refersToPred(String p) {
        if (pred.substring(0,pred.indexOf("(")).equals(p)) {
            return true;
        } else {
            for (String pRes : result) {
                if (pRes.substring(0,pRes.indexOf("(")).equals(p)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public String toString() {
        return "";
    }
}
