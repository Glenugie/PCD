package com.pcd;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class DataExchangeObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final boolean PEER_ROLE_CALL = false;
    private final boolean CSV_TO_CMD = false;
    
    private final NumberFormat satisfactionFormat = new DecimalFormat("#0.00"); 
    private final NumberFormat debugPrintFormat = new DecimalFormat("#0.####");     
    private final String name;
    private final int pid;
    private final int cycleCost;
    private final long startTime;
    
    private String outputMatrix;
    private long totalCumulativeMessages;
    private long totalCumulativeDataReceived;
    
    
    //private long totalCumulativeProfit;
    private long totalCumulativeCycleSpend;
    
    private int totalPeerDownSim;
    private int totalMalformedRecordsSim;
    private int totalDataRequestSim;
    private int totalNoDataSim;
    private int totalNoAccessSim;
    private int totalPolicyInformSim;
    private int totalRejectPoliciesSim;
    private int totalRecordInformSim;
    private int totalDataResultSim;
    private int totalDataResultWithDataSim;    

    HashMap<String,Integer> roleNumbersTotal;
    HashMap<String,Integer> roleNumbersActive;
    LinkedHashMap<String,Integer> masterMessageTotalsCumulative;
    
    //Stat Name, Shortened Name, In Multi-Run CSV, In Single-Run CSV, In Cycle Debug Print, Resets Each Cycle
    private String[][] statsD;
    private LinkedHashMap<String,TreeMap<String, Double>> stats;
    
    public DataExchangeObserver(String name) {
        this.name = name;
        pid = Configuration.getPid(name + "." + PAR_PROT);
        cycleCost = Configuration.getInt("init.keys.cycleCost");
        masterMessageTotalsCumulative =  new LinkedHashMap<String, Integer>();
        
        totalCumulativeMessages = 0;
        //totalCumulativeProfit = 0;
        totalCumulativeCycleSpend = 0;
        totalPeerDownSim = 0; totalMalformedRecordsSim = 0; totalDataRequestSim = 0; totalNoDataSim = 0; totalNoAccessSim = 0;
        totalPolicyInformSim = 0; totalRejectPoliciesSim = 0; totalRecordInformSim = 0; totalDataResultSim = 0; totalDataResultWithDataSim = 0;
        
        startTime = System.currentTimeMillis();
        
        statsD = new String[0][0];
        String[][] statsDT = new String[999][6];
        int r = 0;
        //              Name                             Shortened Name                       Multi-Run CSV        Per-Run CSV          Debug Print          Reset Stat Each Cycle
        
        statsDT[r][0] = "Total Messages";                statsDT[r][1] = "Tot Messages";      statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total Cycle Spend";             statsDT[r][1] = "Tot Spend";         statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total Data Received";           statsDT[r][1] = "Tot Data";          statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total PEER_DOWN";               statsDT[r][1] = "Tot PEER_DOWN";     statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "0"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total MALFORMED_RECORDS";       statsDT[r][1] = "Tot MALF_REC";      statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "0"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total DATA_REQUEST";            statsDT[r][1] = "Tot DATA_REQ";      statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total NO_DATA";                 statsDT[r][1] = "Tot NO_DATA";       statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total NO_ACCESS";               statsDT[r][1] = "Tot NO_ACC";        statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total POLICY_INFORM";           statsDT[r][1] = "Tot POL_INF";       statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total REJECT_POLICIES";         statsDT[r][1] = "Tot REJ_POL";       statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total RECORD_INFORM";           statsDT[r][1] = "Tot REC_INF";       statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total DATA_RESULT";             statsDT[r][1] = "Tot DATA_RES";      statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;
        statsDT[r][0] = "Total DATA_RESULT (+Data)";     statsDT[r][1] = "Tot DATA_RES (+D)"; statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "0"; r += 1;

        statsDT[r][0] = "Peers Remaining";              statsDT[r][1] = "Num Peers";          statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Unsatisfied";                  statsDT[r][1] = "Unsatisfied";        statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Budget Exhausted";             statsDT[r][1] = "No Budget";          statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Orphaned";                     statsDT[r][1] = "Orphaned";           statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Messages";                     statsDT[r][1] = "Messages";           statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Requesting";                   statsDT[r][1] = "Requesting";         statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Waiting";                      statsDT[r][1] = "Waiting";            statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Requestors";                   statsDT[r][1] = "Requestors";         statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "0"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Providers";                    statsDT[r][1] = "Providers";          statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "0"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Dual Role";                    statsDT[r][1] = "Dual Role";          statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "0"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "PEER_DOWN";                    statsDT[r][1] = "PEER_DOWN";          statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "MALFORMED_RECORDS";            statsDT[r][1] = "MALF_REC";           statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "DATA_REQUEST";                 statsDT[r][1] = "DATA_REQ";           statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "NO_DATA";                      statsDT[r][1] = "NO_DATA";            statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "NO_ACCESS";                    statsDT[r][1] = "NO_ACC";             statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "POLICY_INFORM";                statsDT[r][1] = "POL_INF";            statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "REJECT_POLICIES";              statsDT[r][1] = "REJ_POL";            statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "RECORD_INFORM";                statsDT[r][1] = "REC_INF";            statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "DATA_RESULT";                  statsDT[r][1] = "DATA_RES";           statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "DATA_RESULT (+Data)";          statsDT[r][1] = "DATA_RES (+D)";      statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Data Received";                statsDT[r][1] = "Data Received";      statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "0"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Cycle Spend";                  statsDT[r][1] = "Cycle Spend";        statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "0"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Total Profit";                 statsDT[r][1] = "Profit";             statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Total Neighbours";             statsDT[r][1] = "Neighbours";         statsDT[r][2] = "0"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Total Satisfaction";           statsDT[r][1] = "Satisfaction";       statsDT[r][2] = "0"; statsDT[r][3] = "0"; statsDT[r][4] = "0"; statsDT[r][5] = "1"; r += 1;

        statsDT[r][0] = "Average Satisfaction";         statsDT[r][1] = "Avg Satisfaction";   statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Average Profit";               statsDT[r][1] = "Avg Profit";         statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Average Data Received";        statsDT[r][1] = "Avg Data";           statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Average Normalised Profit";    statsDT[r][1] = "Avg Norm Profit";    statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        statsDT[r][0] = "Average Neighbours";           statsDT[r][1] = "Avg Neighbours";     statsDT[r][2] = "1"; statsDT[r][3] = "1"; statsDT[r][4] = "1"; statsDT[r][5] = "1"; r += 1;
        
        if (r > 0) {
            statsD = new String[r][statsDT[0].length];
            for (int i = 0; i < r; i += 1) {
                for (int j = 0; j < statsDT[0].length; j += 1) {
                    //System.out.println("Copying statsDT["+i+"]["+j+"] to statsD["+i+"]["+j+"]");
                    //System.out.println("\t"+statsDT[i][j]);
                    statsD[i][j] = statsDT[i][j];
                }
            }
        }
        
        roleNumbersTotal = new HashMap<String,Integer>();
        for (int i = 0; i < Network.size(); i++) {
            DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);
            String role = "No Role";// if (protocol.role != null) { role = protocol.role.name;}
            if (!roleNumbersTotal.containsKey(role)) { roleNumbersTotal.put(role, 0);}
        }
            
        stats = new LinkedHashMap<String,TreeMap<String, Double>>();
        for (String[] s : statsD) { addStat(s[0]);}        
    }
    
    //Could have stats that don't index by role, but by another stat or init value. WOuld just need to block the role insertion in this method
    public void addStat(String statName) {
        TreeMap<String,Double> statTrack = new TreeMap<String,Double>();
        for (String r : roleNumbersTotal.keySet()) { statTrack.put(r,0.0);}
        stats.put(statName, statTrack);
    }
    
    public void resetStat(String statName) {
        if (stats.containsKey(statName)) {
            TreeMap<String,Double> statTrack = new TreeMap<String,Double>();
            for (String r : roleNumbersTotal.keySet()) { statTrack.put(r,0.0);}
            stats.replace(statName, statTrack);
        } else {
            addStat(statName);
        }
    }
    
    public void addToStat(String statName, String role, double val) {
        if (stats.containsKey(statName)) { 
            if (!stats.get(statName).containsKey(role)) { stats.get(statName).put(role, 0.0);}
            stats.get(statName).put(role, stats.get(statName).get(role)+val);
        }
    }
    
    public int getIndexOfStat(String statName) {
        for (int i = 0; i < statsD.length; i += 1) {
            if (statsD[i][0].equals(statName)) {
                return i; 
            }
        }
        return -1;
    }
    
    public int getRoleNumbersActive(String role) {
        if (roleNumbersActive.containsKey(role)) {
            return roleNumbersActive.get(role);
        }
        return 0;
    }
    
    public int getRoleNumbersTotal(String role) {
        if (roleNumbersTotal.containsKey(role)) {
            return roleNumbersTotal.get(role);
        }
        return 0;
    }
    
    public double totalStatRoles(String statName) {
        double total = 0.0;
        for (String roleName : stats.get(statName).keySet()) {
            total += stats.get(statName).get(roleName);
        }
        return total;
    }
    
    public boolean execute() {
        //return executeNew();
        LinkedHashMap<String,Integer> masterMessageTotals = new LinkedHashMap<String, Integer>();
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
        }
        
        for (String type : masterMessageTotals.keySet()) {
            System.out.println(type+": "+masterMessageTotals.get(type)+" ("+masterMessageTotalsCumulative.get(type)+")");
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
