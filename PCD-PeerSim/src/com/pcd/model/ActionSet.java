package com.pcd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.pcd.DataExchange;

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
    
    public HashMap<String,Integer> getData() {
        HashMap<String,Integer> dataQuants = new HashMap<String, Integer>();
        for (Action a : actions) {
            if (a.type.equals("obtain")) {
                if (!dataQuants.containsKey(a.payload[0])) { dataQuants.put(a.payload[0], 0);}
                dataQuants.replace(a.payload[0], dataQuants.get(a.payload[0]) + Integer.parseInt(a.payload[2]));
            }            
        }
        return dataQuants;
    }
    
    public int getDuration(DataExchange n) {
        int dur = 0;
        for (Action a : actions) {
            switch (a.type) {
                case "obtain":
                    dur += 4;
                    break;
                case "provide":
                    dur += 4;
                    if (n.countData(a.payload[0]) < Integer.parseInt(a.payload[3])) { dur += 4;}
                    break;
                case "adopt": case "revoke":
                    dur += Integer.parseInt(a.payload[2]);
                    break;
                case "wipe": case "inform":
                    dur += 1;
                    break;
            }
        }
        return dur;
    }
    
    public void calcDln() {
        long minDln = -1;
        for (Action a : actions) {
            if (minDln == -1 || a.expiry < minDln) { minDln = a.expiry;}
        }
        dln = minDln;
    }
}
