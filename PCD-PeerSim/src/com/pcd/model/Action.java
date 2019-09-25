package com.pcd.model;

public class Action {
    public String actString;
    public String type;
    public String[] payload;
    public int time;
    public long expiry;
    
    public Action(String act) {        
        actString = act;
        type = act.substring(0, act.indexOf("("));
        //System.out.println(act+": "+type);
        if (actString.startsWith("adopt")) { System.out.println("\t\t"+actString+"\n");}
        
        payload = new String[0];
        if (type.equals("adopt")) {
            payload = new String[2];
            payload[0] = act.substring(6,act.lastIndexOf(","));
            payload[1] = act.substring(act.lastIndexOf(", ")+2,act.length()-1);
        } else {
            payload = act.substring(act.indexOf("(")+1, act.indexOf(")")).split(",");
        }
        
        //System.out.println(act+" => "+type+", "+act.substring(act.indexOf("(")+1, act.indexOf(")"))+" ("+payload.length+")");
        
        if (type.equals("access")) { 
            expiry = -1;
        } else {
            expiry = (peersim.core.CommonState.getTime() + Integer.parseInt((String) payload[payload.length-1]));
        }
        
        //TODO: Calculate time to complete action
        time = 0;
    }
    
    public int getDuration() {
        if (type.equals("dataAccess")) { 
            return -1;
        } else {
            return Integer.parseInt((String) payload[payload.length-1]);
        }
    }
    
    public boolean equals(Action a) {
        return toString().equals(a.toString());
    }
    
    public String toString() {
        return actString;
    }
}
