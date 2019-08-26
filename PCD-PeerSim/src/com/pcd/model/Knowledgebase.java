package com.pcd.model;

import java.util.HashMap;
import java.util.HashSet;

import peersim.core.CommonState;

public class Knowledgebase {
    private HashMap<String, HashSet<Predicate>> kb;
    
    public Knowledgebase() {
        kb = new HashMap<String, HashSet<Predicate>>();
    }
    
    public void add(String t, String[] tr) {
        if (!kb.containsKey(t)) {
            kb.put(t, new HashSet<Predicate>());
        }
        
        Predicate toReplace = null;
        for (Predicate p : kb.get(t)) {
            if (tr.equals(p.terms)) {
                toReplace = p;
                break;
            }
        }
        if (toReplace != null) {
            kb.get(t).remove(toReplace);
        }
        kb.get(t).add(new Predicate(t,tr,CommonState.getTime()));     
    }
    
    public boolean mightHaveData(String peer, String pred) {
        if (kb.containsKey("hasData")) {
            for (Predicate p : kb.get("hasData")) {
                if (p.terms[0].equals(peer) && p.terms[1].equals(pred)) {
                    return true;
                }
            }
        }
        
        if (kb.containsKey("refusedData")) {
            for (Predicate p : kb.get("refusedData")) {
                if (p.terms[0].equals(peer) && p.terms[1].equals(pred)) {
                    return true;
                }
            }
        }
        
        if (kb.containsKey("noData")) {
            for (Predicate p : kb.get("noData")) {
                if (p.terms[0].equals(peer) && p.terms[1].equals(pred)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
