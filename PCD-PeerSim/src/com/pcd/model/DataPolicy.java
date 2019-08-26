package com.pcd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.jpl7.Term;

import com.pcd.DataExchange;
import com.pcd.PrologInterface;

import peersim.core.CommonState;

public class DataPolicy {
	public long ownerID;
	
	public Term orgActCond;
	public Term orgDeactCond;
	public Term orgActions;
	
	public ArrayList<String> actCond;
	public ArrayList<String> deactCond;
	public String mod;
	public LinkedList<String> src;
	public String tgt;
	public ArrayList<Action> actions;    
    public int reward;
    public int penalty;
    
    public int duration;
    public boolean alwaysActive = false;
    public boolean neverDeactive = false;
    
    // [ [ Active ], [ Deactive ], Mod, Src, Iss, Id, [ Action ], Rew, Pen]
    // [ [ requests(d2, peer1)=:=11 ], [ false ], 'F', peer9, peer9, peer2, [ dataAccess(d2, 10)], 5, 0 ]	
	public DataPolicy(long owner, Term policy, int duration, boolean nested) {
		ownerID = owner;
//        Term[] polTerms = policy.toTermArray();
//        
//        orgActCond = polTerms[0];
//        actCond = new ArrayList<String>();
//        for (Term con : Util.listToTermArray(orgActCond)) {         
//            String conString = con.args()[0]+" "+((Compound) con).name()+" "+con.args()[1];
//            actCond.add(conString);
//        }
//        
//        orgDeactCond = polTerms[1];
//        deactCond = new ArrayList<String>();
//        for (Term con : Util.listToTermArray(orgDeactCond)) {         
//            String conString = con.args()[0]+" "+((Compound) con).name()+" "+con.args()[1];
//            deactCond.add(conString);
//        }
//		
//        mod = polTerms[2].toString();
//        src = polTerms[3].toString();
//        tgt = polTerms[5].toString();
//		
//		orgActions = polTerms[5];
//		actions = new ArrayList<Action>();
//		for (Term act : Util.listToTermArray(orgActions)) {		    
//            if (act.toString().startsWith("adopt(")) {
//                DataPolicy adoptPolProc = new DataPolicy(-1,act.args()[0], -1,true);
//                System.out.println(adoptPolProc);
//                actions.add(new Action("adopt("+adoptPolProc.getPolicyString()+act.toString().substring(act.toString().lastIndexOf(","))));
//            } else {
//                actions.add(new Action(act.toString()));
//            }
//		}
//		
//		try {
//		    reward = Integer.parseInt(polTerms[7].toString());
//		    penalty = Integer.parseInt(polTerms[8].toString());
//		} catch (Exception e) {
//		    reward = 0;
//		    penalty = 0;
//		}
//        this.duration = duration;
        
        /*if ((ownerID == 4 || owner == 2) && id.equals("peer2") && dataItem.equals("d2")) {
            System.out.println(policy);
            System.out.println("Conditions: "+conditions);
            System.out.println("Pre-Obligations: "+preObligations);
            System.out.println(toString());
        }*/
		
		//if (!nested) { System.out.println(toString());}
	}
	
	// [true],[false],P,peer1,["access({DATA~2},{ID~1},-1)"],{0-5~3},{5-10~4}
	public DataPolicy(long owner, String policy, String srcChain, boolean nested) {
	    if (srcChain.equals("")) { srcChain = "peer"+owner;}
        ownerID = owner;
        
        String actCondS = "", deactCondS = "", actionS = "";
        int actStart = -1, actEnd = -1, deactStart = -1, deactEnd = -1, actionStart = -1, actionEnd = -1, sqBracks = 0;
        for (int i = 0; i < policy.length(); i += 1) {
            if ((""+policy.charAt(i)).equals("[")) {
                if (sqBracks == 0 && actStart == -1 && actEnd == -1) {
                    actStart = i;
                    sqBracks += 1;
                } else if (sqBracks == 0 && actEnd != -1 && deactStart == -1 && deactEnd == -1) {
                    deactStart = i;
                    sqBracks += 1;
                } else if (sqBracks == 0 && deactEnd != -1 && actionStart == -1 && actionEnd == -1) {    
                    actionStart = i;
                    sqBracks += 1;
                } else {
                    sqBracks += 1;
                }
            } else if ((""+policy.charAt(i)).equals("]")) {
                if (sqBracks == 1 && actStart != -1 && actEnd == -1) {
                    actEnd = i;
                    sqBracks -= 1;
                } else if (sqBracks == 1 && actEnd != -1 && deactStart != -1 && deactEnd == -1) {
                    deactEnd = i;
                    sqBracks -= 1;
                }  else if (sqBracks == 1 && deactEnd != -1 && actionStart != -1 && actionEnd == -1) {
                    actionEnd = i;
                    sqBracks -= 1;
                } else {
                    sqBracks -= 1;
                }
            }
        }
        actCondS = policy.substring(actStart+1,actEnd);
        deactCondS = policy.substring(deactStart+1,deactEnd);
        actionS = policy.substring(actionStart+1,actionEnd);
        policy = policy.substring(deactEnd+2,actionStart)+policy.substring(actionEnd+2);
        //System.out.println(actCondS+"\n"+deactCondS+"\n"+actionS+"\n"+policy);
        
        String[] polSplit = policy.split(",");
        mod = polSplit[0];
        tgt = polSplit[1];
        try {
            reward = Integer.parseInt(polSplit[2]);
            penalty = Integer.parseInt(polSplit[3]);
        } catch (Exception e) {
            reward = 0;
            penalty = 0;
        }
        duration = 0;
        
        src = new LinkedList<String>();
        for (String s : srcChain.split(",")) {
            src.add(s);
        }        
        
        actCond = new ArrayList<String>();
        if (actCondS.toLowerCase().equals("true")) {
            alwaysActive = true;
        } else {
            for (String aC : actCondS.split(",")) {
                if (aC.contains("(")) {
                    actCond.add(aC);
                }
            }
        }
        
        deactCond = new ArrayList<String>();
        if (deactCondS.toLowerCase().equals("false")) {
            neverDeactive = true;
        } else {
            for (String dC : deactCondS.split(",")) {
                if (dC.contains("(")) {
                    deactCond.add(dC);
                }
            }
        }
        
        //"access({DATA~2},{ID~1},-1)","access({DATA~2},{ID~1},-1)","access({DATA~2},{ID~1},-1)"
        actions = new ArrayList<Action>();
        int bracks = 0, open = -1, close = -1;
        String actionSTemp = actionS;
        //System.out.println(actionSTemp);
        while (actionSTemp.contains("(")) {
            for (int i = 0; i < actionSTemp.length(); i += 1) {
                if ((""+actionSTemp.charAt(i)).equals("(")) {
                    if (bracks == 0 && open == -1 && close == -1) {
                        open = i;
                        bracks += 1;
                    } else {
                        bracks += 1;
                    }
                } else if ((""+actionSTemp.charAt(i)).equals(")")) {
                    if (bracks == 1 && open != -1 && close == -1) {
                        close = i;
                        bracks -= 1;                    
                        break;
                    } else {
                        bracks -= 1;
                    }
                }
            }
            
            int startQuote = actionSTemp.indexOf("\"");
            //System.out.println("BLOCK: "+startQuote+", "+open+", "+close);
            String act = actionSTemp.substring(startQuote+1,close+1);
            //System.out.println(act);
            actionSTemp = actionSTemp.substring(close+1);
            
            if (act.startsWith("\"")) { act = act.substring(1);}
            if (act.endsWith("\"")) { act = act.substring(0,act.length()-1);}
            if (act.startsWith("adopt(")) {
                String polBody = act.substring(6,act.length()-1);
                DataPolicy adoptPolProc = new DataPolicy(-1, polBody, srcChain, true);
                //System.out.println(adoptPolProc);
                actions.add(new Action("adopt("+adoptPolProc.getPolicyString()+act.substring(act.lastIndexOf(","))));
            } else {
                actions.add(new Action(act));
            }
        }
        //if (!nested) { System.out.println(toString());}
	}
	
	
	//Produces a policy string formatted for readability
	public String toString() {
		return "[\n\t"+getActCondString()+",\n\t"+getDeactCondString()+",\n"+mod+", Src:"+getSourceString()+", Tgt:"+tgt+",\n\t"+getActionString()+",\n"+reward+","+penalty+"] (Cycles: "+duration+")";
	}
    
	//Produces a policy string as would appear in our formalism
    public String getPolicyString() {
        return "["+getActCondString()+","+getDeactCondString()+","+mod+","+getSourceString()+","+tgt+","+getActionString()+","+reward+","+penalty+"]";
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
    
    public String getSourceString() {
        String sourceString = "[";
        if (src.size() > 0) {
            for (String s : src) {
                sourceString += s+", ";
            }
            sourceString = sourceString.substring(0, sourceString.length()-2);
        }
        sourceString += "]";
        return sourceString;
    }
    
    public boolean equals(DataPolicy polC) {
        if (!condEquals(polC)) { 
            return false;
        }
        if (!mod.equals(polC.mod) || !tgt.equals(polC.tgt)) {
            return false;
        }
        if (!actionEquals(polC)) { 
            return false;
        }
        return true;
    }
    
    public boolean trueEquals(DataPolicy polC) {
        return getPolicyString().equals(polC.getPolicyString());
    }
    
    public boolean condEquals(DataPolicy polC) {
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
    
    public boolean actionEquals(DataPolicy polC) {
        ArrayList<Action> act = new ArrayList<Action>(); act.addAll(actions);
        for (Action aC : polC.actions) {
            boolean found = false;
            for (Action aTest : act) {
                if (aTest.toString().equals(aC.toString())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                act.remove(aC);
            } else {
                return false;
            }
        }
        if (act.size() > 0) { return false;}
        return true;
    }
    
    public boolean mutuallyExclusive(DataPolicy polC) {
        return true;
    }
    
    public HashMap<String, Integer> getData(String id) {
        HashMap<String, Integer> availableData = new HashMap<String, Integer>();
        
        for (Action a : actions) {
            if (a.type.equals("dataAccess")) {
                if (((String) a.payload[1]).equals("_") || ((String) a.payload[1]).equals(id)) {
                    String dType = (String) a.payload[0];
                    int dQuant = Integer.parseInt(a.payload[2]);
                    if (!availableData.containsKey(dType)) { availableData.put(dType, 0);}
                    if (dQuant == -1) {
                        availableData.replace(dType, -1);
                    } else if (availableData.get(dType) != -1) {
                        availableData.replace(dType, availableData.get(dType)+dQuant);
                    }
                }
            }
        }
        
        return availableData;
    }
    
    public boolean isActive(DataExchange peer) {
        if (PrologInterface.TRUE_RANDOM) {
            return true;
        } else {
            for (long actState = CommonState.getTime(); actState >= 0; actState -= 1) {
                boolean actHeld = true;
                for (String cA : actCond) {
                    if (!holds(cA)) {
                        actHeld = false;
                        break;
                    }
                }
                if (actHeld) {
                    boolean deactivated = false;
                    for (long deactState = actState; deactState <= CommonState.getTime(); deactState += 1) {
                        boolean deactHeld = true;
                        for (String cD : deactCond) {
                            if (!holds(cD)) {
                                deactHeld = false;
                                break;
                            }
                        }
                        if (deactHeld) {
                            deactivated = true;
                            break;
                        }
                    }
                    if (!deactivated) {
                        return true;
                    }
                }
            }
        }   
        return false;
    }

    public boolean holds(String cond) {
        return true;
    }
    
    public boolean isActivatable(DataExchange peer) {
        if (PrologInterface.TRUE_RANDOM) {
            return true;
        } else {
            if (isActive(peer)) { 
                return true;
            }
        }     
        return false;        
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
	
//	public Term getPrologTerm() {
//		return Util.termArrayToList(new Term[]{orgActCond,orgDeactCond,new Atom(mod),new Atom(src),new Atom(iss),new Atom(id),orgActions,new org.jpl7.Integer(reward),new org.jpl7.Integer(penalty)});
//	}
    
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
