package com.pcd.model;

import java.util.ArrayList;
import java.util.HashSet;

public class ActionSet {
    public HashSet<Action> actions;
    public long dln;
    public int rew;
    public int pen;
    
    public ActionSet(int r, int p) {
        actions = new HashSet<Action>();
    }
    
    public void add(Action a) {
        actions.add(a);
    }
    
    public void remove(Action a) {
        actions.remove(a);
    }
    
    public int size() {
        return actions.size();
    }
    
    public void obtained(String d, long peerID) {
        ArrayList<Action> toRemove = new ArrayList<Action>();
        for (Action a : actions) {
            if (a.type.equals("obtain")) {
                if (a.payload[0].equals(d) && a.payload[1].equals("peer"+peerID)) {
                    a.payload[2] = ""+(Integer.parseInt(a.payload[2])-1);
                    if (Integer.parseInt(a.payload[2]) <= 0) {
                        toRemove.add(a);
                    }
                }
            }
        }
        for (Action a : toRemove) {
            actions.remove(a);
        }
    }
    
    public void calcDln() {
        long minDln = -1;
        for (Action a : actions) {
            if (minDln == -1 || a.expiry < minDln) { minDln = a.expiry;}
        }
        dln = minDln;
    }
}
