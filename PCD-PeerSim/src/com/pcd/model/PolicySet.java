package com.pcd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jpl7.Term;
import org.jpl7.Util;

import com.pcd.DataExchange;
import com.pcd.PrologInterface;

import peersim.core.CommonState;

public class PolicySet {
    private ArrayList<DataPolicy> primary;
    private ArrayList<DataPolicy> secondary;
    private HashMap<String,Double> providerValues;
    private HashMap<String,Double> requestorValues;
    
    public double providerValue;
    public double worstProviderValue;
    public double requestorValue;
    public double worstRequestorValue;
    
    public PolicySet() {
        primary = new ArrayList<DataPolicy>();
        secondary = new ArrayList<DataPolicy>();
        
        providerValues = new HashMap<String,Double>();
        requestorValues = new HashMap<String,Double>();

        providerValue = 0.0;
        worstProviderValue = 0.0;
        requestorValue = 0.0;
        worstRequestorValue = 0.0;
    }
    
    public void addPrimary(DataPolicy p, Double pVal, Double rVal) {
        primary.add(p);
        int newID = primary.size()-1;
        if (pVal != null) {
            providerValues.put("P"+newID, pVal);
        }
        if (rVal != null) {
            requestorValues.put("P"+newID, rVal);
        }
    }
    
    public void addSecondary(DataPolicy p, Double pVal, Double rVal) {
        secondary.add(p);
        int newID = primary.size()-1;
        if (pVal != null) {
            providerValues.put("S"+newID, pVal);
        }
        if (rVal != null) {
            requestorValues.put("S"+newID, rVal);
        }
    }
    
    public boolean allowsAccess(String pred, String id) {
        for (DataPolicy p : getPolicies()) {
            if (p.mod.equals("P")) {
                HashMap<String,Integer> d = p.getData(id);
                if (d.containsKey("any") || d.containsKey(pred)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void remove(DataPolicy p) {
        int pIndex = primary.indexOf(p);
        if (pIndex != -1) {
            providerValues.remove("P"+pIndex);
            requestorValues.remove("P"+pIndex);
        }
        int sIndex = secondary.indexOf(p);
        if (sIndex != -1) {
            providerValues.remove("S"+sIndex);
            requestorValues.remove("S"+sIndex);
        }
        primary.remove(p);
        secondary.remove(p);
    }
    
    public void addReqValue(DataPolicy p, Double val, boolean prim) {
        int i = 0;
        if (prim) {
            for (DataPolicy pol : primary) {
                if (pol.trueEquals(p)) {
                    requestorValues.put("P"+i,val);
                    break;
                }
                i += 1;
            }
        } else {
            for (DataPolicy pol : secondary) {
                if (pol.trueEquals(p)) {
                    requestorValues.put("S"+i,val);
                    break;
                }
                i += 1;
            }
        }
    }
    
    
    public void computeValue() {        
        providerValue = 0.0;
        worstProviderValue = 0.0;
        requestorValue = 0.0;
        worstRequestorValue = 0.0;
        
        boolean permitSet = false;
        for (DataPolicy pPol : primary) { if (pPol.mod.equals("P")) { permitSet = true; break;}}
        if (!permitSet) { for (DataPolicy sPol : secondary) { if (sPol.mod.equals("P")) { permitSet = true; break;}}}
        
        
        for (DataPolicy pPol : primary) {
            switch (pPol.mod) {
                case "P": 
                    if (permitSet) {
                        providerValue += pPol.reward;
                        //TODO: Scan for existing provide() that this policy fulfils
                    } else {
                        providerValue += pPol.penalty;                        
                    }
                    break;
                case "F": 
                    if (permitSet) {
                        providerValue += pPol.penalty;
                    } else {
                        providerValue += pPol.reward;                        
                    }
                    break;
                case "O": 
                    if (permitSet) {
                        providerValue += providerValues.get(pPol);
                        //TODO: Scan for existing obtain() that this policy has a corresponding provide() for
                    }
                    break;
            }
            requestorValue += requestorValues.get(pPol);            
        }

        worstProviderValue = providerValue;
        worstRequestorValue = providerValue;
        for (DataPolicy sPol : secondary) {
            if (permitSet && sPol.mod.equals("F")) { worstProviderValue += sPol.penalty;}
            else if (!permitSet && sPol.mod.equals("P")) { worstProviderValue += sPol.penalty;}
            /*if (providerValues.get(sPol) < 0) {
                worstProviderValue += providerValues.get(sPol);
            }*/
            if (requestorValues.get(sPol) < 0) {
                worstRequestorValue += requestorValues.get(sPol);
            }
        }
    }
    
    public boolean permitsAccess(String d) {
        return true;
    }
    
    public Term getPrologTerm() {
        Term[] pols = new Term[primary.size()+secondary.size()]; 
        
        int i = 0;
        for (DataPolicy pPol : primary) {
            //pols[i] = pPol.getPrologTerm(); i += 1;
        }
        for (DataPolicy sPol : secondary) {
            //pols[i] = sPol.getPrologTerm(); i += 1;
        }
        return Util.termArrayToList(pols);
    }
    
    public boolean containsPermit() {
        for (DataPolicy pPol : primary) { if (pPol.mod.equals("P")) { return true;}}
        return false;
    }
    
    public ArrayList<DataPolicy> getPrimary() {
        return primary;
    }
    
    public DataPolicy getPrimary(int i) {
        return primary.get(i);
    }        
    
    public ArrayList<DataPolicy> getSecondary() {
        return secondary;
    }
    
    public DataPolicy getSecondary(int i) {
        return secondary.get(i);
    }
    
    public HashSet<DataPolicy> getPolicies() {
        HashSet<DataPolicy> pols = new HashSet<DataPolicy>();
        pols.addAll(primary);
        pols.addAll(secondary);
        return pols;
    }
    
    public HashSet<DataPolicy> getObligations() {
        HashSet<DataPolicy> obligations = new HashSet<DataPolicy>();

        ArrayList<DataPolicy> allPols = new ArrayList<DataPolicy>();
        allPols.addAll(primary); allPols.addAll(secondary);
        for (DataPolicy pol : allPols) {
            if (pol.mod.equals("O")) {
                obligations.add(pol);
            }
        }
        
        return obligations;
    }
    
    public HashSet<Action> getObligedActions() {
        HashSet<Action> oActions = new HashSet<Action>();

        ArrayList<DataPolicy> allPols = new ArrayList<DataPolicy>();
        allPols.addAll(primary); allPols.addAll(secondary);
        for (DataPolicy pol : allPols) {
            if (pol.mod.equals("O")) {
                for (Action act : pol.actions) {
                    if (!act.type.equals("dataAccess")) {
                        oActions.add(act);
                    }
                }
            }
        }
        
        return oActions;
    }
    
    public double getProviderValue(String key) {
        return providerValues.get(key);
    }
    
    public boolean isActive(DataExchange peer) {
        if (PrologInterface.TRUE_RANDOM) {
            if (CommonState.r.nextInt(25) == 0) {
                return false;
            }
            return true;
        } else {
            for (DataPolicy p : primary) {
                if (!p.isActive(peer)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public ArrayList<DataPolicy> activeSet() {
        return primary;
    }
    
    public HashSet<String> getIdentities() {
        HashSet<String> idents = new HashSet<String>();
        for (DataPolicy p : getPolicies()) {
            idents.addAll(p.getIdentities());
        }
        return idents;
    }
    
    public HashSet<String> getPredicates() {
        HashSet<String> preds = new HashSet<String>();
        for (DataPolicy p : getPolicies()) {
            preds.addAll(p.getPredicates());
        }
        return preds;
        
    }
    
    public boolean canActivate(DataExchange peer) {
        if (PrologInterface.TRUE_RANDOM) {
            if (CommonState.r.nextInt(25) == 0) {
                return false;
            }
            return true;
        } else {
            for (DataPolicy p : primary) {
                if (!p.isActivatable(peer)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public double activationCost() {
        return 0.0;
    }
    
    public void cullOptionalPolicies() {
        
    }
    
    public int size() {
        return (primary.size() + secondary.size());
    }
}
