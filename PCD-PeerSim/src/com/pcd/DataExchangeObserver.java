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
                logMain = new File("C:/Users/Sam/Dropbox/PhD/ExperimentRes/csv/Run_"+s+"_"+i+".csv");   
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
                if (protocol.dataReceived != 0) {
                    welfare.get(role).addValue(protocol.dataReceived);
                }
            }
            
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
            System.out.println("TOTAL: "+tot+" ("+cumulativeTotal+")");   
            for (String r : masterRoles) {
                if (roles.get(r) > 0) {
                    System.out.println("\t"+r+" ("+roles.get(r)+"): "+regularCycle.get(r)+"/"+rewardCycle.get(r)+"/"+penaltyCycle.get(r)+", "+(cycleTime.get(r)/roles.get(r))+"ms ("+cycleTime.get(r)+"ms)");
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

//    public boolean executeNew() {
//        /*resetStat("Total Peers"); resetStat("Total Unsatisfied"); resetStat("Total Budget Exhausted"); resetStat("Total Orphaned"); resetStat("Total Messages");
//        resetStat("Total Requesting"); resetStat("Total Waiting"); resetStat("Total Requestors"); resetStat("Total Providers"); resetStat("Total Dual Role");
//        resetStat("Total PEER_DOWN"); resetStat("Total MALFORMED_RECORDS"); resetStat("Total DATA_REQUEST"); resetStat("Total NO_DATA"); resetStat("Total NO_ACCESS");
//        resetStat("Total POLICY_INFORM"); resetStat("Total REJECT_POLICIES"); resetStat("Total RECORD_INFORM"); resetStat("Total DATA_RESULT"); resetStat("Total DATA_RESULT (with Data)");
//        resetStat("Total Data Received"); resetStat("Total Profit"); resetStat("Total Neighbours"); resetStat("Total Satisfaction");*/
//        for (String[] s : statsD) { if (s[5].equals("1")) { resetStat(s[0]);}}
//        
//        long time = peersim.core.CommonState.getTime();
//        int networkSize = Network.size();
//        int cycleSpendThisCycle = 0;
//
//        roleNumbersTotal = new HashMap<String,Integer>();
//        roleNumbersActive = new HashMap<String,Integer>();
//        for (int i = 0; i < networkSize; i++) {
//            DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);
//            String role = "No Role"; if (protocol.role != null) { role = protocol.role.name;}
//            if (!roleNumbersTotal.containsKey(role)) { roleNumbersTotal.put(role, 0);} roleNumbersTotal.replace(role, roleNumbersTotal.get(role)+1);
//            
//            if (!protocol.requestor || protocol.desiredData.size() > 0 || protocol.pendingData.size() > 0) { addToStat("Unsatisfied",role,1);}
//            //System.out.println(totalProfit+" += "+protocol.peerBudget+" - "+protocol.startingBudget);
//            addToStat("Total Profit",role,(protocol.peerBudget-protocol.startingBudget));
//            //totalCumulativeProfit += totalProfit;
//            
//            double peerDataReceived = 0.0, peerDataWanted = 0.0;
//            for (String d : protocol.receivedData.keySet()) {
//                peerDataReceived += protocol.receivedData.get(d);
//            }
//            peerDataWanted = peerDataReceived;
//            for (String d : protocol.desiredData.keySet()) {
//                peerDataWanted += protocol.desiredData.get(d);
//            }
//            for (String d : protocol.pendingData.keySet()) {
//                peerDataWanted += protocol.pendingData.get(d);
//            }
//            
//            double satisfaction = 100; if (peerDataWanted != 0.0) { satisfaction = (peerDataReceived / peerDataWanted) * 100;}
//            addToStat("Total Satisfaction",role,satisfaction);
//            
//            if (PEER_ROLE_CALL) { 
//                if (Network.get(i).isUp()) {
//                    System.out.println("Peer "+protocol.peerID+" ["+role+"] is UP");
//                    for (String d : protocol.ownedData) {
//                        //System.out.println("\tData: "+d);
//                    }
//                } else {
//                    System.out.println("Peer "+protocol.peerID+" ["+role+"] is DOWN");
//                }
//            }
//            
//            if (Network.get(i).isUp()) {
//                if (!roleNumbersActive.containsKey(role)) { roleNumbersActive.put(role, 0);} roleNumbersActive.replace(role, roleNumbersActive.get(role)+1);
//                //cycleSpendThisCycle += cycleCost;
//                addToStat("Cycle Spend",role,cycleCost);
//                addToStat("Total Neighbours",role,protocol.overlayNetwork.size());
//                addToStat("Peers Remaining",role,1);
//                
//                if (protocol.requestor && !protocol.provider) { addToStat("Requestors",role,1);}
//                else if (protocol.provider && !protocol.requestor) { addToStat("Providers",role,1);}
//                else if (protocol.requestor && protocol.provider) { addToStat("Dual Role",role,1);}
//                
//                /*int linkableID = FastConfig.getLinkable(pid);
//                Linkable linkable = (Linkable) Network.get(i).getProtocol(linkableID);
//                if (linkable.degree() == 0) { totalNeighbourless += 1;}*/
//                if (protocol.desiredData.size() > 0) { addToStat("Requesting",role,1);}
//                if (protocol.desiredData.size() == 0 && protocol.pendingData.size() > 0) { addToStat("Waiting",role,1);}
//                for (P2PMessage msg : protocol.messages) {
//                    addToStat(msg.type,role,1);
//                    addToStat("Total "+msg.type,role,1);
//                    if (msg.type.equals("DATA_RESULT") && ((DataPackage) msg.payload[1]).dataItems.size() > 0 ) {
//                        addToStat("DATA_RESULT (+Data)",role,1);
//                        addToStat("Total DATA_RESULT (+Data)",role,1);
//                    }
//                }
//                addToStat("Data Received",role,protocol.dataReceived);
//                addToStat("Total Data Received",role,protocol.dataReceived);
//                addToStat("Messages",role,protocol.messages.size());
//            } else if (protocol.disconnectType == 1) {
//                addToStat("Budget Exhausted",role,1);
//            } else if (protocol.disconnectType == 2) {
//                addToStat("Orphaned",role,1);
//            }
//        }
//        
//        //addToStat("Total Cycle Spend","",cycleSpendThisCycle);
//        for (String r : stats.get("Cycle Spend").keySet()) {
//            addToStat("Total Cycle Spend",r,stats.get("Cycle Spend").get(r));
//        }
//        //addToStat("Total Messages","",totalStatRoles("Messages"));
//        for (String r : stats.get("Messages").keySet()) {
//            addToStat("Total Messages",r,stats.get("Messages").get(r));
//        }
//        
//        //double averageSatisfaction = addToStat("Average Satisfaction",totalStatRoles("Total Satisfaction")/networkSize;
//        for (String r : roleNumbersTotal.keySet()) {
//            addToStat("Average Satisfaction",r,(stats.get("Total Satisfaction").get(r)/getRoleNumbersTotal(r)));
//        }        
//        //double averageProfit = (totalStatRoles("Total Profit")/networkSize);
//        for (String r : roleNumbersTotal.keySet()) {
//            addToStat("Average Profit",r,(stats.get("Total Profit").get(r)/getRoleNumbersTotal(r)));
//        }        
//        //double averageDataReceived = 0.0; if (totalPeers > 0) { averageDataReceived = ((double) totalDataReceived/totalPeers);}
//        for (String r : roleNumbersTotal.keySet()) {
//            if (getRoleNumbersActive(r) > 0) {
//                addToStat("Average Data Received",r,(stats.get("Data Received").get(r)/getRoleNumbersActive(r)));
//            } else {
//                addToStat("Average Data Received",r,0);                
//            }
//        }
//        //double averageNormalisedProfit = (double) (totalStatRoles("Total Profit") + totalCumulativeCycleSpend)/networkSize;
//        for (String r : roleNumbersTotal.keySet()) {
//            addToStat("Average Normalised Profit",r,((stats.get("Total Profit").get(r) + stats.get("Total Cycle Spend").get(r))/getRoleNumbersTotal(r)));
//        }
//        //double averageNeighbours = ((double) totalNeighbours/totalPeers);
//        for (String r : roleNumbersTotal.keySet()) {
//            if (getRoleNumbersActive(r) > 0) {
//                addToStat("Average Neighbours",r,(stats.get("Total Neighbours").get(r)/getRoleNumbersActive(r)));
//            } else {
//                addToStat("Average Neighbours",r,0);                
//            }
//        }
//        
//        //Prints detailed information about satisfaction metrics
//        if (PrologInterface.printSimInfo || time == (Configuration.getInt("simulation.cycles")-1)) {
//            String statsOutput = "";
//            if (stats.size() > 0) {
//                int i = 0;
//                for (String statName : stats.keySet()) {
//                    int statIndex = getIndexOfStat(statName);
//                    if (statIndex != -1 && statsD[statIndex][4].equals("1")) {
//                        //if (i != 0) { statsOutput += "\t\t";}
//                        statsOutput += statsD[statIndex][1];
//                        if (statsD[statIndex][1].length() < 17) {
//                            for (int j = statsD[statIndex][1].length(); j < 17; j += 1) { statsOutput += " ";}
//                        }
//                        Double tot = totalStatRoles(statName);
//                        String totString = debugPrintFormat.format(tot);
//                        statsOutput += ": "+totString;
//                        for (int j = totString.length(); j < 12; j += 1) { statsOutput += " ";}
//                        i += 1;
//                        if (i == 2) { 
//                            i = 0; 
//                            //statsOutput = statsOutput.substring(0, statsOutput.length()-2)+"\n";
//                            statsOutput += "\n";
//                        }
//                    }
//                }
//                //if (statsOutput.endsWith(", ")) { statsOutput = statsOutput.substring(0, statsOutput.length()-2);}
//            }
//            if (!statsOutput.equals("")) { System.out.println("\n\n============[Cycle "+time+"]============\n"+statsOutput+"\n");}
//        }
//        
//        if (time == 0) { 
//            if (stats.size() > 0) {
//                String outputLine1 = "Cycle,", outputLine2 = ",";
//                for (String statName : stats.keySet()) {
//                    int statIndex = getIndexOfStat(statName);
//                    if (statIndex != -1 && statsD[statIndex][3].equals("1")) {
//                        outputLine1 += statsD[statIndex][0]+",";
//                        for (String roleName : stats.get(statName).keySet()) {
//                            outputLine1 += ",";
//                            outputLine2 += roleName+",";
//                        }
//                        outputLine2 += "Total,";
//                        /*if (stats.get(statName).size() > 0) {
//                            outputLine1 = outputLine1.substring(0, outputLine1.length()-1);
//                        }*/
//                    }
//                }
//                if (!outputLine1.equals("")) { 
//                    outputLine1 = outputLine1.substring(0, outputLine1.length()-1);
//                    outputLine2 = outputLine2.substring(0, outputLine2.length()-1);
//                    outputMatrix = outputLine1+"\n"+outputLine2+"\n";
//                }
//                //System.out.println(outputLine1+"\n"+outputLine2+"\n");
//            }
//            //outputMatrix = "Cycle, Peers, Peers Satisfied, Average Satisfaction, Ran out of Budget, Orphaned, Average Profit (per peer), Normalised Profit (per peer), Average Data Received (per peer), Total Data Received, Messages, Total Messages\n";
//        }
//
//        if (stats.size() > 0) {
//            outputMatrix += (time+1)+",";
//            for (String statName : stats.keySet()) {
//                int statIndex = getIndexOfStat(statName);
//                if (statIndex != -1 && statsD[statIndex][3].equals("1")) {
//                    for (String roleName : stats.get(statName).keySet()) {
//                        outputMatrix += stats.get(statName).get(roleName)+","; 
//                    }
//                    outputMatrix += totalStatRoles(statName)+",";
//                }
//            }    
//            outputMatrix = outputMatrix.substring(0, outputMatrix.length()-1)+"\n";
//        }
//        
//        if (time == (Configuration.getInt("simulation.cycles")-1)) {      
//            if (PrologInterface.printSimInfo && CSV_TO_CMD) {
//                for (String statName : stats.keySet()) {
//                    int statIndex = getIndexOfStat(statName);
//                    if (statIndex != -1 && statsD[statIndex][4].equals("1")) {
//                        System.out.println(statsD[statIndex][0]);
//                        for (String roleName : stats.get(statName).keySet()) {
//                            System.out.println("\t"+roleName+" - "+stats.get(statName).get(roleName));
//                        }
//                    }
//                }
//            }
//            
//            System.out.println("Run in "+(System.currentTimeMillis()-startTime)+"ms");  
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
//            if (Configuration.getInt("simulation.experiments") == 1) {
//                File log = new File("res/csv/SingleRun_"+dateFormat.format(new Date().getTime())+".csv");
//                try {
//                    log.createNewFile();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//                
//                try {
//                    BufferedWriter out = new BufferedWriter(new FileWriter(log,true));
//                    out.write(outputMatrix);
//                    out.close();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//            
//
//            if (stats.size() > 0) {
//                try {    
//                    String outputHeader = "Date,", outputBody = dateFormat.format(new Date().getTime())+",";
//                    for (String statName : stats.keySet()) {
//                        int statIndex = getIndexOfStat(statName);
//                        if (statIndex != -1 && statsD[statIndex][2].equals("1")) {
//                            outputHeader += statsD[statIndex][0]+",";
//                            outputBody += totalStatRoles(statName)+",";
//                        }
//                    }
//                    outputHeader = outputHeader.substring(0, outputHeader.length()-1);
//                    outputBody = outputBody.substring(0, outputBody.length()-1);
//
//                    File logMain = new File("res/csv/MultiRun.csv");       
//                    boolean newFile = false;
//                    try {                    
//                        newFile = logMain.createNewFile();
//                    } catch (IOException e) {
//                        System.err.println(e.getMessage());
//                    }    
//                    
//                    BufferedWriter out = new BufferedWriter(new FileWriter(logMain,true));
//                    if (newFile) {
//                        //out.write("Date,Max Policies,Peers Satisfied,Average Satisfaction\n");
//                        out.write(outputHeader+"\n");
//                    }
//                    //out.write(dateFormat.format(new Date().getTime())+","+Configuration.getInt("protocol.pcd.maxPolicies")+","+(networkSize-totalUnsatisfied)+","+satisfactionFormat.format(averageSatisfaction)+"\n");
//                    out.write(outputBody+"\n");
//                    out.close();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//            }
//        }
//        
//        return false;
////    }
//        
//    public boolean executeOld() {
//        int totalPeers = 0, totalUnsatisfied = 0, totalBudgetExhausted = 0, totalOrphaned = 0, totalMessages = 0, totalRequesting = 0, totalWaiting = 0, totalRequestors = 0, totalProviders = 0, totalDualRole = 0;
//        int totalPeerDown = 0, totalMalformedRecords = 0, totalDataRequest = 0, totalNoData = 0, totalNoAccess = 0, totalPolicyInform = 0, totalRejectPolicies = 0, totalRecordInform = 0, totalDataResult = 0, totalDataResultWithData = 0;
//        int totalDataReceived = 0, totalProfit = 0, totalNeighbours = 0;
//        double totalSatisfaction = 0;
//
//        long time = peersim.core.CommonState.getTime();
//    	int networkSize = Network.size();
//    	int cycleSpendThisCycle = 0;
//        for (int i = 0; i < networkSize; i++) {
//            DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);   
//                        
//            if (!protocol.requestor || protocol.desiredData.size() > 0 || protocol.pendingData.size() > 0) { totalUnsatisfied += 1;}
//        	//System.out.println(totalProfit+" += "+protocol.peerBudget+" - "+protocol.startingBudget);
//            totalProfit += (protocol.peerBudget-protocol.startingBudget);
//        	//totalCumulativeProfit += totalProfit;
//            
//            double peerDataReceived = 0.0, peerDataWanted = 0.0;
//            for (String d : protocol.receivedData.keySet()) {
//                peerDataReceived += protocol.receivedData.get(d);
//            }
//            peerDataWanted = peerDataReceived;
//            for (String d : protocol.desiredData.keySet()) {
//                peerDataWanted += protocol.desiredData.get(d);
//            }
//            for (String d : protocol.pendingData.keySet()) {
//                peerDataWanted += protocol.pendingData.get(d);
//            }
//            
//            double satisfaction = (peerDataReceived / peerDataWanted) * 100;
//            totalSatisfaction += satisfaction;
//            
//        	if (Network.get(i).isUp()) {
//        		cycleSpendThisCycle += cycleCost;
//        		totalNeighbours += protocol.overlayNetwork.size();
//	            totalPeers += 1;
//	            
//	            if (protocol.requestor && !protocol.provider) { totalRequestors += 1;}
//	            else if (protocol.provider && !protocol.requestor) { totalProviders += 1;}
//	            else if (protocol.requestor && protocol.provider) { totalDualRole += 1;}
//	            
//	            /*int linkableID = FastConfig.getLinkable(pid);
//	            Linkable linkable = (Linkable) Network.get(i).getProtocol(linkableID);
//	            if (linkable.degree() == 0) { totalNeighbourless += 1;}*/
//	            if (protocol.desiredData.size() > 0) { totalRequesting += 1;}
//	            if (protocol.desiredData.size() == 0 && protocol.pendingData.size() > 0) { totalWaiting += 1;}
//	            for (P2PMessage msg : protocol.messages) {
//	            	switch (msg.type) {
//	            		case "PEER_DOWN": totalPeerDown += 1; break;	
//	            		case "MALFORMED_RECORDS": totalMalformedRecords += 1; break;
//		            	case "DATA_REQUEST": totalDataRequest += 1; break;
//		            	case "NO_DATA": totalNoData += 1; break;	    
//		            	case "NO_ACCESS": totalNoAccess += 1; break;
//		            	case "POLICY_INFORM": totalPolicyInform += 1; break;
//			        	case "REJECT_POLICIES": totalRejectPolicies += 1; break;
//			        	case "RECORD_INFORM": totalRecordInform += 1; break;
//			        	case "DATA_RESULT": 
//			        		totalDataResult += 1; 
//			        		if (((DataPackage) msg.payload[1]).dataItems.size() > 0 ) { totalDataResultWithData += 1;}
//			        		//totalDataReceived += ((DataPackage) msg.payload[1]).dataItems.size();
//			        		break;
//	            	}	    
//	            }
//            	totalDataReceived += protocol.dataReceived;
//            	totalCumulativeDataReceived += protocol.dataReceived;
//	            totalMessages += protocol.messages.size();
//        	} else if (protocol.disconnectType == 1) {
//        		totalBudgetExhausted += 1;
//        	} else if (protocol.disconnectType == 2) {
//        		totalOrphaned += 1;
//        	}
//        }     
//        
//        totalPeerDownSim += totalPeerDown;
//        totalMalformedRecordsSim += totalMalformedRecords;
//        totalDataRequestSim += totalDataRequest;
//        totalNoDataSim += totalNoData;
//        totalNoAccessSim += totalNoAccess;
//        totalPolicyInformSim += totalPolicyInform;
//        totalRejectPoliciesSim += totalRejectPolicies;
//        totalRecordInformSim += totalRecordInform;
//        totalDataResultSim += totalDataResult;
//        totalDataResultWithDataSim += totalDataResultWithData;
//        
//        /*PrintStream newout =
//        		(PrintStream)Configuration.getInstance("simulation.stdout",System.out);
//        	if(newout!=System.out) System.setOut(newout);*/
//        
//        double averageSatisfaction = totalSatisfaction/networkSize;
//        double averageProfit = ((double) totalProfit/networkSize);
//        double averageDataReceived = 0.0; if (totalPeers > 0) { averageDataReceived = ((double) totalDataReceived/totalPeers);}
//        double averageNormalisedProfit = (double) (totalProfit + totalCumulativeCycleSpend)/networkSize;
//        double averageNeighbours = ((double) totalNeighbours/totalPeers);
//        totalCumulativeCycleSpend += cycleSpendThisCycle;
//           
//        //Prints detailed information about satisfaction metrics
//        if (PrologInterface.printSimInfo || time == (Configuration.getInt("simulation.cycles")-1)) {
//	        System.out.println(
//	        	"\n\n============[Cycle "+time+"]============\n"+
//	        	"Peers: "+totalPeers+" / "+networkSize+" (Satisfied: "+(networkSize-totalUnsatisfied)+", Avg Satisfaction: "+satisfactionFormat.format(averageSatisfaction)+"%, Ran out of Budget: "+totalBudgetExhausted+", Orphaned: "+totalOrphaned+"), Requesting: "+totalRequesting+", Waiting: "+totalWaiting+", Messages: "+totalMessages+", Providers: "+totalProviders+", Requestors: "+totalRequestors+", Dual Role: "+totalDualRole+"\n"+
//	        	"\t[Current Cycle] "+"PEER_DOWN: "+totalPeerDown+", DATA_REQUEST: "+totalDataRequest+", NO_DATA: "+totalNoData+", NO_ACCESS: "+totalNoAccess+", POLICY_INFORM: "+totalPolicyInform+", REJECT_POLICIES: "+totalRejectPolicies+", RECORD_INFORM: "+totalRecordInform+", MALFORMED_RECORDS: "+totalMalformedRecords+", DATA_RESULT: "+totalDataResult+" (w/ Data: "+totalDataResultWithData+")\n"+
//                "\t[Cumulative]    "+"PEER_DOWN: "+totalPeerDownSim+", DATA_REQUEST: "+totalDataRequestSim+", NO_DATA: "+totalNoDataSim+", NO_ACCESS: "+totalNoAccessSim+", POLICY_INFORM: "+totalPolicyInformSim+", REJECT_POLICIES: "+totalRejectPoliciesSim+", RECORD_INFORM: "+totalRecordInformSim+", MALFORMED_RECORDS: "+totalMalformedRecordsSim+", DATA_RESULT: "+totalDataResultSim+" (w/ Data: "+totalDataResultWithDataSim+")\n"+
//	        	"\t"+"Average Profit (per peer): "+averageProfit+", Normalised Profit (per peer): "+averageNormalisedProfit+", Average Data Received (per peer): "+averageDataReceived+", Total Data Received: "+totalCumulativeDataReceived+", Average Neighbours: "+averageNeighbours
//	        );
//
//	        /*for (int i = 0; i < networkSize; i++) {
//	            DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);  
//    	            System.out.println("\tPEER "+protocol.peerID);
//
//                    double peerDataReceived = 0.0, peerDataWanted = 0.0;                    
//                    for (String d : protocol.receivedData.keySet()) {
//                        peerDataReceived += protocol.receivedData.get(d);
//                        System.out.println("\t\t(RECEIVED) "+d+": "+protocol.receivedData.get(d));
//                    }
//                    peerDataWanted = peerDataReceived;
//                    
//                    for (String d : protocol.desiredData.keySet()) {
//                        peerDataWanted += protocol.desiredData.get(d);
//                        System.out.println("\t\t( DESIRED) "+d+": "+protocol.desiredData.get(d));
//                    }
//                    for (String d : protocol.pendingData.keySet()) {
//                        peerDataWanted += protocol.pendingData.get(d);
//                        System.out.println("\t\t( PENDING) "+d+": "+protocol.desiredData.get(d));
//                    }
//                    
//                    double satisfaction = (peerDataReceived / peerDataWanted) * 100;
//                    System.out.println("\t\tSatisfaction: "+satisfactionFormat.format(satisfaction)+"% satisfied");
//	        }*/
//        }
//        
//        totalCumulativeMessages += totalMessages;
//        
//        if (time == 0) { outputMatrix = "Cycle, Peers, Peers Satisfied, Average Satisfaction, Ran out of Budget, Orphaned, Average Profit (per peer), Normalised Profit (per peer), Average Data Received (per peer), Total Data Received, Messages, Total Messages\n";}
//        
//        outputMatrix += time+",";
//        outputMatrix += totalPeers+",";
//        outputMatrix += (networkSize-totalUnsatisfied)+",";
//        outputMatrix += satisfactionFormat.format(averageSatisfaction)+",";
//        outputMatrix += totalBudgetExhausted+",";
//        outputMatrix += totalOrphaned+",";
//        outputMatrix += averageProfit+",";
//        outputMatrix += averageNormalisedProfit+",";
//        outputMatrix += averageDataReceived+",";
//        outputMatrix += totalCumulativeDataReceived+",";
//		outputMatrix += totalMessages+",";
//		outputMatrix += totalCumulativeMessages+"";
//        
//        outputMatrix += "\n";
//        
//        /*Node node = Network.get(0);
//        DataExchange pNode = (DataExchange) node.getProtocol(pid);
//        System.out.println("\t[First Node: "+node.getID()+"]");
//        System.out.println("\t\tDesired: "+pNode.desiredData);
//        System.out.println("\t\tPending: "+pNode.pendingData);
//        System.out.println("\t\tOwned: "+pNode.ownedData);*/
//        
//        if (time == (Configuration.getInt("simulation.cycles")-1)) {        
//            System.out.println("Run in "+(System.currentTimeMillis()-startTime)+"ms");	
//    		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
//    		if (Configuration.getInt("simulation.experiments") == 1) {
//				File log = new File("res/csv/Results_"+dateFormat.format(new Date().getTime())+".csv");
//				try {
//					log.createNewFile();
//				} catch (IOException e) {
//					System.err.println(e.getMessage());
//				}
//	        	
//	        	try {
//	                BufferedWriter out = new BufferedWriter(new FileWriter(log,true));
//	    	    	out.write(outputMatrix);
//	    		    out.close();
//	        	} catch (IOException e) {
//	        		System.err.println(e.getMessage());
//	        	}
//    		}
//        	
//     	
//        	try {
//                File logMain = new File("res/Results.csv");       
//                boolean newFile = false;
//                try {
//                    
//                    newFile = logMain.createNewFile();
//                } catch (IOException e) {
//                    System.err.println(e.getMessage());
//                }
//    	    	BufferedWriter out = new BufferedWriter(new FileWriter(logMain,true));
//                if (newFile) {
//                    out.write("Date,Max Policies,Peers Satisfied,Average Satisfaction\n");
//                }
//    	    	out.write(dateFormat.format(new Date().getTime())+","+Configuration.getInt("protocol.pcd.maxPolicies")+","+(networkSize-totalUnsatisfied)+","+satisfactionFormat.format(averageSatisfaction)+"\n");
//    		    out.close();
//        	} catch (IOException e) {
//        		System.err.println(e.getMessage());
//        	}
//        }
//        
//        
//        
//        return false;
//    }
}
