package com.pcd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.pcd.DataExchange;

import peersim.core.CommonState;

public class ActionSet {
    public HashSet<Action> actions;
    public long dln;
    public int prof;
    public int duration;
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
    
    public boolean completes(ActionSet aSet) {
        for (Action a : actions) {
            for (Action aCmp : aSet.actions) {
                if (a.equals(aCmp)) {
                    return true;
                } else if (a.type.equals(aCmp.type)) {
                    if (a.type.equals("obtain") && a.payload[0].equals(aCmp.payload[0])) {
                        return true;                        
                    } else if (a.type.equals("provide") && a.payload[0].equals(aCmp.payload[0]) && a.payload[1].equals(aCmp.payload[1])) {
                        return true;
                    } else if (a.type.equals("wipe") && a.payload[0].equals(aCmp.payload[0])) {
                        return true;
                    } else if ((a.type.equals("adopt") || a.type.equals("revoke")) && a.payload[0].equals(aCmp.payload[0])) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        int minDur = 0;
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
                    dur += 1;
                    minDur = Math.max(minDur, Integer.parseInt(a.payload[2]));
                    break;
                case "wipe": case "inform":
                    dur += 1;
                    break;
            }
        }
        duration = Math.max(dur,minDur);
        return duration;
    }
    
    public void calcDln() {
        long minDln = -1;
        for (Action a : actions) {
            if (minDln == -1 || a.expiry < minDln) { minDln = a.expiry;}
        }
        dln = minDln;
    }
    
    public Action getRand() {
        if (actions.size() == 0) { 
            return null;
        } else {
            Action[] tmp = (Action[]) actions.toArray();
            return tmp[CommonState.r.nextInt(actions.size())];
        }
    }
}
