package com.pcd;

import java.util.ArrayList;
import java.util.HashSet;

import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Term;
import org.jpl7.Util;

public class DataPolicy {
	public long ownerID;
	
	public Term orgActCond;
	public Term orgDeactCond;
	public Term orgActions;
	
	public ArrayList<String> actCond;
	public ArrayList<String> deactCond;
	public String mod;
	public String src;
	public String iss;
	public String id;
	public ArrayList<Action> actions;    
    public int reward;
    public int penalty;
    
    public int duration;
    
    // [ [ Active ], [ Deactive ], Mod, Src, Iss, Id, [ Action ], Rew, Pen]
    // [ [ requests(d2, peer1)=:=11 ], [ false ], 'F', peer9, peer9, peer2, [ dataAccess(d2, 10)], 5, 0 ]	
	public DataPolicy(long owner, Term policy, int duration, boolean nested) {
		ownerID = owner;
        Term[] polTerms = policy.toTermArray();
        
        orgActCond = polTerms[0];
        actCond = new ArrayList<String>();
        for (Term con : Util.listToTermArray(orgActCond)) {         
            String conString = con.args()[0]+" "+((Compound) con).name()+" "+con.args()[1];
            actCond.add(conString);
        }
        
        orgDeactCond = polTerms[1];
        deactCond = new ArrayList<String>();
        for (Term con : Util.listToTermArray(orgDeactCond)) {         
            String conString = con.args()[0]+" "+((Compound) con).name()+" "+con.args()[1];
            deactCond.add(conString);
        }
		
        mod = polTerms[2].toString();
        src = polTerms[3].toString();
        iss = polTerms[4].toString();
        id = polTerms[5].toString();
		
		orgActions = polTerms[5];
		actions = new ArrayList<Action>();
		for (Term act : Util.listToTermArray(orgActions)) {		    
            if (act.toString().startsWith("adopt(")) {
                DataPolicy adoptPolProc = new DataPolicy(-1,act.args()[0], -1,true);
                System.out.println(adoptPolProc);
                actions.add(new Action("adopt("+adoptPolProc.getPolicyString()+act.toString().substring(act.toString().lastIndexOf(","))));
            } else {
                actions.add(new Action(act.toString()));
            }
		}
		
		try {
		    reward = Integer.parseInt(polTerms[7].toString());
		    penalty = Integer.parseInt(polTerms[8].toString());
		} catch (Exception e) {
		    reward = 0;
		    penalty = 0;
		}
        this.duration = duration;
        
        /*if ((ownerID == 4 || owner == 2) && id.equals("peer2") && dataItem.equals("d2")) {
            System.out.println(policy);
            System.out.println("Conditions: "+conditions);
            System.out.println("Pre-Obligations: "+preObligations);
            System.out.println(toString());
        }*/
		
		//if (!nested) { System.out.println(toString());}
	}
	
	
	//Produces a policy string formatted for readability
	public String toString() {
		return "[\n\t"+getActCondString()+",\n\t"+getDeactCondString()+",\n"+mod+", Src:"+src+", Iss:"+iss+", Id:"+id+",\n\t"+getActionString()+",\n"+reward+","+penalty+"] (Cycles: "+duration+")";
	}
    
	//Produces a policy string as would appear in our formalism
    public String getPolicyString() {
        return "["+getActCondString()+","+getDeactCondString()+","+mod+","+src+","+iss+","+id+","+getActionString()+","+reward+","+penalty+"]";
    }
    
    public String getActCondString() {
        String conditionString = "[";
        if (actCond.size() > 0) {
            for (String con : actCond) {
                conditionString += con+", ";
            }
            conditionString = conditionString.substring(0, conditionString.length()-2);
        }
        conditionString += "]";
        return conditionString;
    }
    
    public String getDeactCondString() {
        String conditionString = "[";
        if (deactCond.size() > 0) {
            for (String con : deactCond) {
                conditionString += con+", ";
            }
            conditionString = conditionString.substring(0, conditionString.length()-2);
        }
        conditionString += "]";
        return conditionString;
    }
    
    public String getActionString() {
        String actString = "[";
        if (actions.size() > 0) {
            for (Action act : actions) {
                actString += act+", ";
            }
            actString = actString.substring(0, actString.length()-2);
        }
        actString += "]";
        return actString;
    }
    
    public boolean equals(DataPolicy polC) {
        ArrayList<String> activation = new ArrayList<String>(); activation.addAll(actCond);
        for (String aC : polC.actCond) {
            if (activation.contains(aC)) {
                activation.remove(aC);
            } else {
                return false;
            }
        }
        if (activation.size() > 0) { return false;}
        
        ArrayList<String> deactivation = new ArrayList<String>(); deactivation.addAll(deactCond);
        for (String dC : polC.deactCond) {
            if (deactivation.contains(dC)) {
                deactivation.remove(dC);
            } else {
                return false;
            }
        }
        if (deactivation.size() > 0) { return false;}
        
        return true;
    }
    
    public boolean mutuallyExclusive(DataPolicy polC) {
        return true;
    }
    
    /*public String getObligationString() {
        String obligationString = "[";
        if (obligationsProc.size() > 0) {
            for (ObligationSet oblSet : obligationsProc) {
                obligationString += "[[";
                for (Obligation obl : oblSet.obligations.keySet()) {
                    obligationString += obl+", ";
                }
                obligationString = obligationString.substring(0, obligationString.length()-2);
                obligationString += "], "+oblSet.penalty+", "+oblSet.duration+"], ";
            }
            obligationString = obligationString.substring(0, obligationString.length()-2);
        }
        obligationString += "]";
        return obligationString;
    }*/
	
	public Term getPrologTerm() {
		return Util.termArrayToList(new Term[]{orgActCond,orgDeactCond,new Atom(mod),new Atom(src),new Atom(iss),new Atom(id),orgActions,new org.jpl7.Integer(reward),new org.jpl7.Integer(penalty)});
	}
    
    public HashSet<Action> getObligedActions() {
        HashSet<Action> oActions = new HashSet<Action>();
        if (mod.equals("O")) {
            for (Action act : actions) {
                if (!act.type.equals("dataAccess")) {
                    oActions.add(act);
                }
            }
        }
        
        return oActions;
    }
}
