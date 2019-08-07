package com.pcd.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jpl7.Term;
import org.jpl7.Util;

import peersim.core.CommonState;

public class PolicySet {
    private HashSet<DataPolicy> primary;
    private HashSet<DataPolicy> secondary;
    private HashMap<DataPolicy,Double> providerValues;
    private HashMap<DataPolicy,Double> requestorValues;
    
    public double providerValue;
    public double worstProviderValue;
    public double requestorValue;
    public double worstRequestorValue;
    
    public PolicySet() {
        primary = new HashSet<DataPolicy>();
        secondary = new HashSet<DataPolicy>();
        
        providerValues = new HashMap<DataPolicy,Double>();
        requestorValues = new HashMap<DataPolicy,Double>();

        providerValue = 0.0;
        worstProviderValue = 0.0;
        requestorValue = 0.0;
        worstRequestorValue = 0.0;
    }
    
    public void addPrimary(DataPolicy p, Double pVal, Double rVal) {
        primary.add(p);
        if (pVal != null) {
            providerValues.put(p, pVal);
        }
        if (rVal != null) {
            requestorValues.put(p, rVal);
        }
    }
    
    public void addSecondary(DataPolicy p, Double pVal, Double rVal) {
        secondary.add(p);
        if (pVal != null) {
            providerValues.put(p, pVal);
        }
        if (rVal != null) {
            requestorValues.put(p, rVal);
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
    
    public HashSet<DataPolicy> getPrimary() {
        return primary;
    }
    
    
    public HashSet<DataPolicy> getSecondary() {
        return secondary;
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
    
    public boolean isActive() {
        if (CommonState.r.nextInt(25) == 0) {
            return false;
        }
        return true;
    }
    
    public HashSet<DataPolicy> activeSet() {
        return primary;
    }
    
    public boolean canActivate() {
        if (CommonState.r.nextInt(25) == 0) {
            return false;
        }
        return true;
    }
    
    public void cullOptionalPolicies() {
        
    }
    
    public int size() {
        return (primary.size() + secondary.size());
    }
}
