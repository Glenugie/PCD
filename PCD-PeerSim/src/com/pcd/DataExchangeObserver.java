package com.pcd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.pcd.model.SocialWelfare;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class DataExchangeObserver implements Control {
    private static final String PAR_PROT = "protocol";
    
    private final NumberFormat satisfactionFormat = new DecimalFormat("#0.00"); 
    private final NumberFormat debugPrintFormat = new DecimalFormat("#0.####");     
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
    private final String name;
    private final int pid;
    private final int cycleCost;
    private File logMain;
    
    private LinkedHashMap<String,Integer> masterMessageTotalsCumulative;
    private long cumulativeTotal;
    
    private LinkedHashSet<String> masterRoles;
    private HashMap<String, Integer> roles;
    
    private LinkedList<HashMap<String, Integer>> regularCycles;
    private LinkedList<HashMap<String, Integer>> rewardCycles;
    private LinkedList<HashMap<String, Integer>> penaltyCycles;
    private LinkedList<HashMap<String, Long>> cycleTimes;
    
    private LinkedList<HashMap<String, SocialWelfare>> socialWelfare;
    
    
    public DataExchangeObserver(String name) {
        this.name = name;
        pid = Configuration.getPid(name + "." + PAR_PROT);
        cycleCost = Configuration.getInt("init.keys.cycleCost");
        
        masterMessageTotalsCumulative =  new LinkedHashMap<String, Integer>();
        cumulativeTotal = 0;
        
        masterRoles = new LinkedHashSet<String>();
        masterRoles.add("AFF"); masterRoles.add("AFN"); masterRoles.add("ASF"); masterRoles.add("ASN");
        masterRoles.add("SFF"); masterRoles.add("SFN"); masterRoles.add("SSF"); masterRoles.add("SSN");
        roles = new HashMap<String, Integer>(); for (String r : masterRoles) { roles.put(r, 0);}

        regularCycles = new LinkedList<HashMap<String, Integer>>();
        rewardCycles = new LinkedList<HashMap<String, Integer>>();
        penaltyCycles = new LinkedList<HashMap<String, Integer>>();
        cycleTimes = new LinkedList<HashMap<String, Long>>();
        
        socialWelfare = new LinkedList<HashMap<String, SocialWelfare>>();
        
        String s = "";
        try {
            s = Configuration.getString("simulation.title");
        } catch (Exception e) {
            s = dateFormat.format(new Date().getTime());
        }  
        boolean newFile = false;
        try {                    
            newFile = false;
            int i = 1;
            while (i < 100 && !newFile) {
                logMain = new File("C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/"+s+"/Run_"+s+"_"+i+".csv");
                new File("C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/"+s).mkdirs();
                newFile = logMain.createNewFile();
                i += 1;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }    
    }
    
    public boolean execute() {
        LinkedHashMap<String,Integer> masterMessageTotals = new LinkedHashMap<String, Integer>();
        
        HashMap<String, Integer> regularCycle = new HashMap<String, Integer>();
        HashMap<String, Integer> rewardCycle = new HashMap<String, Integer>();
        HashMap<String, Integer> penaltyCycle = new HashMap<String, Integer>();
        HashMap<String, Long> cycleTime = new HashMap<String, Long>();
        HashMap<String, SocialWelfare> welfare = new HashMap<String, SocialWelfare>();
        
        String csvLine = "";
        
        for (String r : masterRoles) { 
            if (!regularCycle.containsKey(r)) { regularCycle.put(r, 0);}
            if (!rewardCycle.containsKey(r)) { rewardCycle.put(r, 0);}
            if (!penaltyCycle.containsKey(r)) { penaltyCycle.put(r, 0);}
            if (!cycleTime.containsKey(r)) { cycleTime.put(r, (long) 0);}
            if (!welfare.containsKey(r)) { welfare.put(r, new SocialWelfare());}            
        }
        
        if (peersim.core.CommonState.getTime() == 0) {
            int networkSize = Network.size();
            for (int i = 0; i < networkSize; i+= 1) {
                DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);
                String role = protocol.getRole();
                roles.replace(role, roles.get(role)+1);
            }
            
            csvLine = "//[PARAMETERS], Size: "+PrologInterface.confPeers+", Cycles: "+PrologInterface.confCycles+", Topology: "+PrologInterface.confTopology+" ("+PrologInterface.confTopologyVal+"), Altruist: "+
                    PrologInterface.confAltruistic+"%, Fair: "+PrologInterface.confFair+"%, Fault: "+PrologInterface.confFaultyPeers+"% ("+PrologInterface.confFaultRate+"%), Default P: "+
                    PrologInterface.confDefaultPermit+", Max Neighbours: "+PrologInterface.confMaxNeighbours+", Policies: "+PrologInterface.confMinPols+" - "+PrologInterface.confMaxPols+", Budget: "+
                    PrologInterface.confMinBudget+" - "+PrologInterface.confMaxBudget+", Data: "+PrologInterface.confDataFile+", Policies: "+PrologInterface.confPolicyFile+"\n";
            csvLine += "Cycle,";
            DataExchange protocol = (DataExchange) Network.get(0).getProtocol(pid);
            for (String type : protocol.messageTotals.keySet()) {
                csvLine += type+",";
            }
            csvLine += "FREE_TRANS,";
            for (String r : masterRoles) {
                csvLine += r+"-Num,";
                csvLine += r+"-Cyc,";
                csvLine += r+"-Rew,";
                csvLine += r+"-Pen,";
                csvLine += r+"-Time,";
                csvLine += r+"-SW-Num,";
                csvLine += r+"-SW-Avg,";
                csvLine += r+"-SW-Dev,";
                csvLine += r+"-SW-Min,";
                csvLine += r+"-SW-Max,";
                csvLine += r+"-SW-All,";
            }
            csvLine = csvLine.substring(0,csvLine.length()-1);
        } else {        
            int networkSize = Network.size();
            int freeTrans = 0;
            for (int i = 0; i < networkSize; i+= 1) {
                DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);
                for (String type : protocol.messageTotals.keySet()) {
                    if (!masterMessageTotals.containsKey(type)) {
                        masterMessageTotals.put(type, 0);
                    }
                    if (!masterMessageTotalsCumulative.containsKey(type)) {
                        masterMessageTotalsCumulative.put(type, 0);
                    }
                    masterMessageTotals.replace(type, masterMessageTotals.get(type)+protocol.messageTotals.get(type));                
                    masterMessageTotalsCumulative.replace(type, masterMessageTotalsCumulative.get(type)+protocol.messageTotals.get(type));
                }
                protocol.initMessageTotals();
                
                freeTrans += protocol.freeTransactions.size();
                
                String role = protocol.getRole();
                switch (protocol.lastCycle) {
                    case 0: // Regular
                        regularCycle.replace(role, regularCycle.get(role)+1);
                        break;
                    case 1: // Reward
                        rewardCycle.replace(role, rewardCycle.get(role)+1);
                        break;
                    case 2: // Penalty
                        penaltyCycle.replace(role, penaltyCycle.get(role)+1);
                        break;
                }
                cycleTime.replace(role, cycleTime.get(role)+protocol.lastTime);
                //if (protocol.dataReceived != 0) {
                    welfare.get(role).addValue(protocol.dataReceived);
                //}
            }
            //System.out.println("Free Transactions: "+(freeTrans/networkSize)+" ("+freeTrans+")");
            
            for (String r : welfare.keySet()) {
                welfare.get(r).calculate();
            }
            
            regularCycles.push(regularCycle);
            rewardCycles.push(rewardCycle);
            penaltyCycles.push(penaltyCycle);
            cycleTimes.push(cycleTime);
            socialWelfare.push(welfare);

            csvLine = ""+CommonState.getTime()+",";
            System.out.println("\n===================== Cycle "+CommonState.getTime()+" =====================");
            int tot = 0;
            for (String type : masterMessageTotals.keySet()) {
                //if (masterMessageTotals.get(type) > 0 || peersim.core.CommonState.getTime() == PrologInterface.confCycles-1) {
                    System.out.println(type+": "+masterMessageTotals.get(type)+" ("+masterMessageTotalsCumulative.get(type)+")");
                //}
                csvLine += masterMessageTotals.get(type)+",";
                tot += masterMessageTotals.get(type);
                cumulativeTotal += masterMessageTotals.get(type);
            }     
            csvLine += freeTrans+",";
            System.out.println("TOTAL: "+tot+" ("+cumulativeTotal+")");   
            for (String r : masterRoles) {
                if (roles.get(r) > 0) {
                    System.out.println("\t"+r+" ("+roles.get(r)+"): "+regularCycle.get(r)+"C/"+rewardCycle.get(r)+"R/"+penaltyCycle.get(r)+"P, "+(cycleTime.get(r)/roles.get(r))+"ms ("+cycleTime.get(r)+"ms)");
                    if (welfare.get(r).vals.size() > 0) {
                        System.out.println("\t\t"+welfare.get(r).vals.size()+" NUM, "+welfare.get(r).average+" AVG, "+welfare.get(r).deviation+" DEV, "+welfare.get(r).min+" MIN, "+welfare.get(r).max+" MAX");
                    }
                }
                //SSN-Num,SSN-Cyc,SSN-Rew,SSN-Pen,SSN-Time,SSN-SW-Num,SSN-SW-Avg,SSN-SW-Dev,SSN-SW-Min,SSN-SW-Max
                csvLine += roles.get(r)+",";
                csvLine += regularCycle.get(r)+",";
                csvLine += rewardCycle.get(r)+",";
                csvLine += penaltyCycle.get(r)+",";
                csvLine += cycleTime.get(r)+",";
                csvLine += welfare.get(r).vals.size()+",";
                csvLine += welfare.get(r).average+",";
                csvLine += welfare.get(r).deviation+",";
                csvLine += welfare.get(r).min+",";
                csvLine += welfare.get(r).max+",";
                csvLine += welfare.get(r).getValues()+",";
            }
            System.out.println("==========================================\n");
            csvLine = csvLine.substring(0,csvLine.length()-1);
        }
        
        try {              
            BufferedWriter out = new BufferedWriter(new FileWriter(logMain,true));
            out.write(csvLine+"\n");
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
        return false;
    }
}