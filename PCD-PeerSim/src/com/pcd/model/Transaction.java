package com.pcd.model;

import java.util.ArrayList;
import java.util.HashSet;

import com.pcd.DataExchange;

public class Transaction {
    public int transactionId;
    public int remoteId;
    public long peerID;
    
    public String predicate;
    public int quantity;
    
    public HashSet<PolicySet> policySets;
    public ArrayList<ActionSet> obligedActions;
    
    public int lifetime;
    private int maxLife;
    
    public Transaction(int id, int rId, long peer, String p, int quant, int l) {
        transactionId = id;
        remoteId = rId;
        peerID = peer;
        
        predicate = p;
        quantity = quant;
        
        policySets = new HashSet<PolicySet>();
        obligedActions = new ArrayList<ActionSet>();
        
        lifetime = l;
        maxLife = l;
    }
    
    public void calcActions(PolicySet ps, DataExchange n) {
        for (DataPolicy p : ps.getPolicies()) {
            if (p.mod.equals("O")) {
                double violObl = p.penalty * n.breakMult;
                double fulfilObl = 0;
                for (Action a : p.actions) {
                    fulfilObl -= n.actionCostReq(a);
                }
                if (fulfilObl >= violObl) {
                    ActionSet tmp = new ActionSet(p.reward,p.penalty);
                    for (Action a : p.actions) {
                        if (!a.type.equals("dataAccess")) {
                            tmp.add(a);
                        }
                    }
                    if (tmp.size() > 0) {
                        obligedActions.add(tmp);
                    }
                }
            }
        }
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
