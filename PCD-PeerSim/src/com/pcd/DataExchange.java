package com.pcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import org.jpl7.Atom;
import org.jpl7.Term;
import org.jpl7.Variable;

import com.pcd.model.Action;
import com.pcd.model.DataElement;
import com.pcd.model.DataPackage;
import com.pcd.model.DataPolicy;
import com.pcd.model.Knowledgebase;
import com.pcd.model.P2PMessage;
import com.pcd.model.PolicySet;
import com.pcd.model.Transaction;
import com.pcd.model.TransactionRecord;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

public class DataExchange implements CDProtocol {
    private final boolean POLICY_OVERLAP_WARNING = true;
    private final boolean DEBUG_POL_COST = false;
    private final int MAX_GROUPS = 100;
    private final int AVG_TRANS_LENGTH = 4;
    private final int DATA_ELEMENT_LENGTH = 5;    

    //private SimpleDateFormat prologDateFormat;
    private Random rng;
    //private Term[] masterDataArray;
    protected long peerID;
    //private String connectionType;

    private boolean disconnecting;
    
    protected int lastCycle;
    protected long lastTime;

    protected HashMap<String, Node> overlayNetwork; //Maps PeerIDs to Peers
    protected Knowledgebase kb;

//    protected HashMap<String, Integer> desiredData;
//    protected HashMap<String, Integer> pendingData;
//    protected HashMap<String, Integer> receivedData;
    protected HashSet<String> wantedData;
    protected HashSet<String> ownedData;
    protected HashSet<String> producedData;
    protected HashMap<String, Integer> dataValue;
    protected HashSet<DataElement> dataCollection;

    protected ArrayList<DataPolicy> policies;
    //protected HashMap<DataPolicy,HashMap<Action,Integer>> obligations;
    //protected ArrayList<ArrayList<Action>> obligedActions;

    //private int activeRequests;
    protected int peerBudget, startingBudget;

    protected int dataReceived;
    protected long disconnectTime;
    protected int disconnectType;
    protected int rewardCycles;
    protected int penaltyCycles;
    
    private boolean altruistic; // Altruistic peers will always comply with policies if possible, even at a loss of value. Self-interested peers will use profit estimates to determine the most valuable choice; preferring compliance if possible
    private boolean fair; // Fair peers promote social welfare, forward messages etc.. Selfish peers will not do any of this.
    private boolean faulty; // Faulty peers have a PrologInterface.confFaultRate % chance to drop outgoing messages. Non-faulty peers will always send messages successfully.

    protected ArrayList<P2PMessage> messages;
    protected LinkedHashMap<String,Integer> messageTotals;
    
    protected ArrayList<Integer> freeTransactions;
    private HashMap<Integer, Transaction> inTransactionStack;
    private HashMap<Integer, Transaction> outTransactionStack;
    private HashSet<TransactionRecord> transactions;

    public DataExchange(String prefix) {
        rng = CommonState.r;
        //prologDateFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss,0,z,'false'");
        
        disconnecting = false;
        disconnectTime = -1;
        disconnectType = -1;
        
        dataReceived = 0;
        
        rewardCycles = 0;
        penaltyCycles = 0;
        
        altruistic = false;
        fair = false;
        faulty = false;
        
        lastCycle = -1;
        lastTime = -1;
        
        initPeer(-1);
    }
    
    public void initPeer(long id) {        
        wantedData = new HashSet<String>();
        ownedData = new HashSet<String>();
        producedData = new HashSet<String>();
        dataValue = new HashMap<String, Integer>();
        dataCollection = new HashSet<DataElement>();
        
        overlayNetwork = new HashMap<String, Node>();
        kb = new Knowledgebase();
        
        messages = new ArrayList<P2PMessage>();
        initMessageTotals();
        
        freeTransactions = new ArrayList<Integer>();
        for (int i = 0; i < PrologInterface.MAX_TRANSACTIONS; i += 1) { freeTransactions.add(i);}
        inTransactionStack = new HashMap<Integer, Transaction>();
        outTransactionStack = new HashMap<Integer, Transaction>();
        transactions = new HashSet<TransactionRecord>();
        
        peerID = id;
    }
    
    public void initMessageTotals() {
        messageTotals = new LinkedHashMap<String, Integer>();
        messageTotals.put("DATA_REQUEST", 0);
        messageTotals.put("POLICY_INFORM", 0);
        messageTotals.put("RECORD_INFORM", 0);
        messageTotals.put("DATA_RESULT", 0);
        messageTotals.put("DATA_RESULT_Y", 0);
        messageTotals.put("DATA_RESULT_N", 0);
        messageTotals.put("NO_DATA", 0);
        messageTotals.put("NO_ACCESS", 0);
        messageTotals.put("REJECT_POLICIES", 0);
        messageTotals.put("MALFORMED_RECORDS", 0);
        messageTotals.put("INVALID_TRANSACTION", 0);
        messageTotals.put("WAIT", 0);
        messageTotals.put("CONFIRM_WAIT", 0);
        messageTotals.put("INFORM", 0);
        messageTotals.put("PEER_DOWN", 0);
        messageTotals.put("PEER_OVERLOAD", 0);
        messageTotals.put("MESSAGE_FAULT", 0);
        messageTotals.put("CHAIN_FAILURE", 0);
    }
    
    public void firstCycleInit(Node node, int protocolID) {
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);
        
        /*for (int i = 0; i < node.protocolSize(); i += 1) {
            System.out.println(i+": "+node.getProtocol(i));
        }*/

        // Internal representation of the Overlay network
        //System.out.println("Init "+node.getID());
        for (int i = 0; i < linkable.degree(); i += 1) {
            //System.out.println(linkable.getNeighbor(i).getID()+" ?= "+node.getID());
            if (linkable.getNeighbor(i).getID() != node.getID()) {
                overlayNetwork.put("peer" + linkable.getNeighbor(i).getID(), linkable.getNeighbor(i));
            }
            //PrologInterface.assertFact("connected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + linkable.getNeighbor(i).getID()) });
        }
        
        if (PrologInterface.confMaxBudget == PrologInterface.confMinBudget ) {
            peerBudget = PrologInterface.confMaxBudget;
        } else {
            peerBudget = rng.nextInt(PrologInterface.confMaxBudget - PrologInterface.confMinBudget) + PrologInterface.confMinBudget;
        }
        startingBudget = peerBudget;
        
        initPeerPolicies();
    }
    
    public void initPeerPolicies() {
        //System.out.println(peerID+", Altruistic: "+altruistic+", Fair: "+fair+", Faulty: "+faulty);

        // Choose Policies
        policies = new ArrayList<DataPolicy>();
        int numPolicies = rng.nextInt((PrologInterface.confMaxPols - PrologInterface.confMinPols)) + PrologInterface.confMinPols; 
        int i = 0;
        while (policies.size() < numPolicies && i < (numPolicies * 5)) {
            String chosenPolRaw = PrologInterface.confProtoPolicies.get(rng.nextInt(PrologInterface.confProtoPolicies.size()));
            String chosenPol = "";
            if (chosenPolRaw.contains("{")) {
                //Generate policy
                String[] subs = new String[99];
                HashSet<String> validData = new HashSet<String>(); validData.addAll(ownedData); validData.addAll(wantedData);
                while (chosenPolRaw.contains("{")) {
                    //System.out.println(chosenPolRaw);
                    int subStart = chosenPolRaw.indexOf("{");
                    int subEnd = chosenPolRaw.indexOf("}")+1;
                    String sub = chosenPolRaw.substring(subStart, subEnd);
                    String subBody = sub.substring(1,sub.lastIndexOf("~"));
                    String subIDRaw = sub.substring(sub.indexOf("~")+1,sub.length()-1);
                    int subID = 0; if (!subIDRaw.equals("*")) { subID = Integer.parseInt(subIDRaw);}
                    
                    String subVal = "";
                    if (subs[subID] == null) {
                        if (sub.contains("\"")) {
                            String[] choices = subBody.split(",");
                            //System.out.print("Choices: "); for (String c : choices) { System.out.print(c+", ");} System.out.println("");
                            subVal = choices[rng.nextInt(choices.length)];
                            subVal = subVal.substring(1,subVal.length()-1);
                        } else if (sub.startsWith("{DATA")) {      
                            //System.out.println("Valid: "+validData);
                            subVal = (String) validData.toArray()[rng.nextInt(validData.size())];
                        } else if (sub.startsWith("{ID")) {
                            //System.out.println("Net: "+overlayNetwork.keySet());
                            subVal = (String) overlayNetwork.keySet().toArray()[rng.nextInt(overlayNetwork.size())];
                        } else if (sub.contains("-")) {
                            int lower = Integer.parseInt(subBody.split("-")[0]);
                            int upper = Integer.parseInt(subBody.split("-")[1]);
                            //System.out.println(lower+" to "+upper);
                            subVal = ""+(rng.nextInt(upper-lower)+lower);
                        }
                    } else {
                        subVal = subs[subID];
                    }
                    chosenPolRaw = chosenPolRaw.substring(0,subStart)+subVal+chosenPolRaw.substring(subEnd);
                    if (subID != 0) { subs[subID] = subVal;}
                    //System.out.println("\t"+sub+" - "+subVal+" = "+chosenPolRaw);
                }
            }
            chosenPol = chosenPolRaw;
            //System.out.println(chosenPol);
            
            DataPolicy pol = new DataPolicy(peerID,chosenPol,"",false);            
            addPolicy(pol);
            
            i += 1;
        }        
    }

    public void nextCycle(Node node, int protocolID) {
        System.out.print("\t{"+CommonState.getTime()+"} peer"+peerID+" ("+messages.size()+") [");
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);
        long start = System.currentTimeMillis();
        //long tmp = System.currentTimeMillis();

        //On the first cycle, initialises the peer
        if (peersim.core.CommonState.getTime() == 0) {
            firstCycleInit(node,protocolID);
            System.out.print("*");
        } else {
            // Policy Processing (Compliance/Violation Check)
            checkPolicyCompliance();
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Policy Compliance]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
    
            // Process Messages
            //System.out.println(peerID);
            processMessages(node, protocolID);
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Messages]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
            
            // Update Policies
            updatePolicies(); // Empty hook for now
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Policy Update]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
    
            // Obligation Processing, determines current possible actions and carries one out
            processActions(node, protocolID);   
            System.out.print("-");    
            //System.out.println("Peer "+peerID+" [Actions]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis(); 
    
            // If settings permit (and not currently penalised), forms new connections up to the degree of connectedness in config file
            if (PrologInterface.confNewConnections && overlayNetwork.size() < PrologInterface.confMaxNeighbours && penaltyCycles == 0) {
                Node randomPeer = Network.get(rng.nextInt(Network.size()));
                //If the random peer is online, and not already connected
                if (!overlayNetwork.containsKey("peer" + randomPeer.getID()) && randomPeer.isUp()) {
                    overlayNetwork.put("peer" + randomPeer.getID(), randomPeer);
                }
            }
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Overlay]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
            
            // Produce Data
            for (String d : producedData) {
                dataCollection.add(new DataElement(d, generateDataElement()));
            }
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Data Generation] "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
    
            //Query q = new Query(new Compound("listing", new Term[]{new Compound("noRequest",new Term[0])})); q.oneSolution(); q.close();
            PrologInterface.retractFact("noRequest", new Term[] { new Atom("peer"+peerID), new Variable("_"), new Variable("_"), new org.jpl7.Integer(peersim.core.CommonState.getTime())});
            if (penaltyCycles > 0) {
                peerBudget -= PrologInterface.confCycleCost;
                penaltyCycles = Math.max(0, (penaltyCycles - 1));
                lastCycle = 2;
            } else if (rewardCycles > 0) {
                rewardCycles = Math.max(0, (rewardCycles - 1));
                lastCycle = 1;
            } else {
                peerBudget -= PrologInterface.confCycleCost;
                lastCycle = 0;
            }
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Cycle Tick]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
            
            processTransactionStack();
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Transaction Stack]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
            
            decideToLeaveNetwork();
            System.out.print("-");
            //System.out.println("Peer "+peerID+" [Network Leave]: "+(System.currentTimeMillis()-tmp)+"ms"); tmp = System.currentTimeMillis();
        }
        System.out.println("]");
        //System.out.println("Peer "+peerID+" done in "+(System.currentTimeMillis()-start)+"ms");
        lastTime = (System.currentTimeMillis()-start);
        //System.out.println("");
    }
    
    private void checkPolicyCompliance() {
//      for (int i = policies.size() - 1; i >= 0; i -= 1) {
//      DataPolicy pol = policies.get(i);
//      if (pol.duration != -99) {
//          pol.duration -= 1;
//          if (pol.duration <= 0) {
//              policies.remove(pol);
//              PrologInterface.retractFact("policy", new Term[] { new Atom("peer" + peerID), pol.getPrologTerm() });
//
//              //Mark any obligations associated with enforcing Policy as fulfilled
//          }
//      }
//  }
    }

    private void processMessages(Node node, int protocolID) {
        if (PrologInterface.printSimInfo) { System.out.println(peerID+" has "+messages.size()+" messages");}
        for (int i = messages.size() - 1; i >= 0; i -= 1) {
            P2PMessage msg = messages.get(i);
            if (msg.time <= peersim.core.CommonState.getTime()
                    && (penaltyCycles == 0 || (penaltyCycles > 0 && (msg.type.equals("DATA_REQUEST") || msg.type.equals("REJECT_POLICIES") 
                    || msg.type.equals("RECORD_INFORM") || msg.type.equals("OBLIGATION_COMPLETE") || msg.type.equals("CONFIRM_WAIT")
                    || msg.type.equals("MALFORMED_RECORDS") || msg.type.equals("INVALID_TRANSACTION"))))) {
                DataExchange n = (DataExchange) msg.sender.getProtocol(protocolID);
                try {
                    switch (msg.type) {
                        case "DATA_REQUEST":
                            System.out.print("A");
                            processMsg_DataRequest(n, msg, node, protocolID);           // Requestor -> Provider
                            break;
                        case "NO_DATA":
                            System.out.print("B");
                            processMsg_NoData(n, msg, node, protocolID);                // Provider -> Requestor
                            break;
                        case "NO_ACCESS":
                            System.out.print("C");
                            processMsg_NoAccess(n, msg, node, protocolID);              // Provider -> Requestor
                            break;
                        case "POLICY_INFORM":
                            System.out.print("D");
                            processMsg_PolicyInform(n, msg, node, protocolID);          // Provider -> Requestor
                            break;
                        case "RECORD_INFORM":
                            System.out.print("E");
                            processMsg_RecordInform(n, msg, node, protocolID);          // Requestor -> Provider
                            break;
                        case "DATA_RESULT":
                            System.out.print("F");
                            processMsg_DataResult(n, msg, node, protocolID);            // Provider -> Requestor
                            break;
                        case "REJECT_POLICIES":
                            System.out.print("G");
                            processMsg_RejectPolicies(n, msg, node, protocolID);        // Requestor -> Provider
                            break;
                        case "WAIT":
                            System.out.print("H");
                            processMsg_Wait(n, msg, node, protocolID);                  // Requestor -> Provider
                            break;
                        case "CONFIRM_WAIT":
                            System.out.print("I");
                            processMsg_ConfirmWait(n, msg, node, protocolID);           // Provider -> Requestor
                            break;
                        case "MALFORMED_RECORDS":
                            System.out.print("J");
                            processMsg_MalformedRecords(n, msg, node, protocolID);      // Provider -> Requestor
                            break;
                        case "INVALID_TRANSACTION":
                            System.out.print("K");
                            processMsg_InvalidTransaction(n, msg, node, protocolID);    // Provider -> Requestor
                            break;
                        case "PEER_OVERLOAD":
                            System.out.print("L");
                            processMsg_PeerOverload(n, msg, node, protocolID);          // Provider -> Requestor
                            break;
                        case "PEER_DOWN":
                            System.out.print("M");
                            processMsg_PeerDown(n, msg, node, protocolID);              // Either -> Either
                            break;
                        case "INFORM":
                            System.out.print("N");
                            processMsg_Inform(n, msg, node, protocolID);                // Requestor -> Provider
                            break;
                    }
                } catch (Exception e) {
                    String payload = "";
                    for (Object o : msg.body) { 
                        payload += o.toString()+", ";
                    }
                    payload = "["+payload.substring(0,payload.length()-2)+"]";
                    System.err.println("Could not process message "+i+" ("+n.peerID+" -> "+peerID+") ["+msg.type+" ("+msg.prvTransId+" | "+msg.reqTransId+"): "+payload+"]");
                    //System.err.println(e.getMessage());
                    e.printStackTrace();
                }

                Object test = messages.remove(i);
                if (test == null) {
                    System.err.println("ERROR removing message "+i+" from inbox of "+peerID);
                }
            }
        }
    }
    
    private void processMsg_DataRequest(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Data_Request -> Data_Item, Data_Quantity   
        HashSet<PolicySet> relPolSets = null;
        if (transactionFree() && !hasOpenInTrans(n.peerID, (String) msg.body[0])) {           
            if (entails((String) msg.body[0])) {
                int newTID = getFreeTransaction("pDR");
                
                relPolSets = generatePolicySets(msg.sender, (String) msg.body[0]);
                if (relPolSets.size() > 0 || PrologInterface.confDefaultPermit) {
                    Transaction t = new Transaction(newTID, msg.reqTransId, n.peerID, (String) msg.body[0], (int) msg.body[1], PrologInterface.TRANS_LIFETIME);
                    t.policySets = relPolSets;
                    inTransactionStack.put(newTID, t);
                    n.sendMessage(protocolID, msg.sender, node, newTID, msg.reqTransId, "POLICY_INFORM", new Object[] { (String) msg.body[0], relPolSets }, null);
                } else {
                    DataPackage datalessPackage = assembleDataPackage(null,generateTransactionRecords(),msg.sender.getID());
                    n.sendMessage(protocolID, msg.sender, node, newTID, msg.reqTransId, "NO_ACCESS", new Object[] { datalessPackage }, null);
                    if (!freeTransactions.contains(newTID)) { freeTransactions.add(newTID);}
                }
            } else {       
                DataPackage datalessPackage = assembleDataPackage(null,generateTransactionRecords(),msg.sender.getID());
                n.sendMessage(protocolID, msg.sender, node, -1, msg.reqTransId, "NO_DATA", new Object[] { datalessPackage }, null);         
            }
            
            if (PrologInterface.DATA_REQUEST_FORWARDING && fair) {
                for (Node nT : getForwardingNeighbours((String) msg.body[0])) {
                    if (!msg.inChain(nT)) {
                        ((DataExchange) nT.getProtocol(protocolID)).sendMessage(protocolID, nT, msg.sender, -1, -1, "DATA_REQUEST", new Object[] { (String) msg.body[0], new Integer((int) msg.body[1]) }, msg.getChain());
                    }
                }
            }
        } else {
            DataPackage datalessPackage = assembleDataPackage(null,generateTransactionRecords(),msg.sender.getID());
            n.sendMessage(protocolID, msg.sender, node, -1, msg.reqTransId, "PEER_OVERLOAD", new Object[] { datalessPackage }, null);                  
        }
        
        rewardCycles += checkCompliance(relPolSets, (String) msg.body[0], n.peerID);
        penaltyCycles += checkViolation(relPolSets, (String) msg.body[0], n.peerID);
    }
    
    private boolean entails(String s) {
        if (PrologInterface.TRUE_RANDOM) {
            if (rng.nextInt(25) != 0) {
                return true;
            }
            return false;
        } else {
            if (countData(s) > 0) {
                return true;
            }
            return false;
        }
    }
    
    private HashSet<PolicySet> generatePolicySets(Node req, String pred) {
        HashSet<PolicySet> relPolicySets = new HashSet<PolicySet>();
        
        if (PrologInterface.TRUE_RANDOM) {
            if (rng.nextInt(5) != 0) {
                relPolicySets.add(new PolicySet());
            }
        } else {
            HashSet<DataPolicy> relPolicies = relevantPolicies(req, pred);
            HashSet<DataPolicy> toRemove = new HashSet<DataPolicy>();
            for (DataPolicy pol : relPolicies) {
                if (!toRemove.contains(pol)) {
                    PolicySet pSet = new PolicySet();
                    for (DataPolicy tPol : relPolicies) {
                        if (!toRemove.contains(tPol)) {
                            if (pol.equals(tPol)) {
                                pSet.addPrimary(tPol, 0.0, 0.0);
                                toRemove.add(tPol);
                            } else if (!pol.mutuallyExclusive(tPol)) {
                                pSet.addSecondary(tPol, 0.0, 0.0);                        
                            }
                        }
                    }
                    double utilP = policyProfitPrv_Permit(pSet);
                    DataPolicy neg = negativeOptional(pSet);
                    while (utilP < PrologInterface.MIN_UTIL && neg != null) {
                        pSet.remove(neg);
                        utilP = policyProfitPrv_Permit(pSet);
                        neg = negativeOptional(pSet);
                    }
                    pSet.providerValue = utilP;
                    relPolicySets.add(pSet);
                }
            }
            PolicySet forbidPols = prohibitPolicies(req, pred);
            double utilF = policyProfitPrv_Prohibit(forbidPols);
            relPolicySets = removeBelowThreshold(relPolicySets, utilF);            
        }
                
        return relPolicySets;
    }
    
    /* Relevant here are those policies which in some way reference the requestor's identity (specifically, or as a group 
     * they are a part of) and the data in question (either the specific predicate or as the wildcard). 
     * We note that these relevant policies need not necessarily be active in the current state, but it must be possible to 
     * activate them. Concretely, a policy that activates at a later time is acceptable, but not one that requires a different 
     * identity, or to undo an event that has already occurred.
     */
    private HashSet<DataPolicy> relevantPolicies(Node req, String pred) {
        HashSet<DataPolicy> relPolicies = new HashSet<DataPolicy>();
        
        for (DataPolicy p : policies) {
            HashMap<String, Integer> data = p.getData("peer"+req.getID());
            if ((data.containsKey(pred) || data.containsKey("any")) && (p.tgt.equals("peer"+req.getID()) || p.tgt.equals("any"))) {
                if (p.isActivatable(this)) {
                    relPolicies.add(p);
                }
            }
        }
        
        return relPolicies;
    }
    
    private double policyProfitPrv_Permit(PolicySet ps) {
        if (PrologInterface.TRUE_RANDOM) {
            return rng.nextInt(50)-25;
        } else {
            double u = 0.0;
            for (DataPolicy p : ps.getPolicies()) {
                switch (p.mod) {
                    case "P":
                        u += p.reward;
                        break;
                    case "F":
                        u -= p.penalty;
                        break;
                    case "O":
                        u += policyProfitPrv_Oblige(p);
                        break;
                }
            }
            return u;
        }
    }
    
    private double policyProfitPrv_Prohibit(PolicySet ps) {
        if (PrologInterface.TRUE_RANDOM) {
            return rng.nextInt(50)-25;
        } else {
            double u = 0.0;
            for (DataPolicy p : ps.getPolicies()) {
                switch (p.mod) {
                    case "P":
                        u -= p.penalty;
                        break;
                    case "F":
                        u += p.reward;
                        break;
                    case "O":
                        break;
                }
            }
            return u;
        }
    }
    
    private double policyProfitPrv_Oblige(DataPolicy p) {
        if (PrologInterface.TRUE_RANDOM) {
            return rng.nextInt(50)-25;
        } else {
            double u = 0.0;
            for (Action a : p.actions) {
                switch (a.type) {
                    case "obtain":
                        break;
                    case "wipe":
                        break;
                    case "provide":
                        if (Long.parseLong(a.payload[2]) == peerID) {
                            u += dataValue.get(a.payload[0]) * Integer.parseInt(a.payload[1]);
                        }
                        break;
                    case "adopt":
                        u += benefitOfAdopt(new DataPolicy(-1,a.payload[0],"",false));
                        break;
                    case "revoke":
                        u += benefitOfRevoke(new DataPolicy(-1,a.payload[0],"",false));
                        break;
                    case "inform":
                        break;
                }
            }
            return u;
        }
}
    
    private double benefitOfAdopt(DataPolicy p) {
        return 0;
    }
    
    private double benefitOfRevoke(DataPolicy p) {
        return 0;
    }
    
    private DataPolicy negativeOptional(PolicySet ps) {
        return null;
    }
    
    private PolicySet prohibitPolicies(Node req, String pred) {
        return new PolicySet(); 
    }
    
    private HashSet<PolicySet> removeBelowThreshold(HashSet<PolicySet> relPols, double utilF) {
        return relPols;
    }
    
    private int checkCompliance(HashSet<PolicySet> polSets, String pred, long peer) {
        int rew =  0;
        if (PrologInterface.TRUE_RANDOM) {
            rew = rng.nextInt(100);
            if (rew < 99) { rew = 0;} else { rew = 1;} 
        } else {
            for (DataPolicy pol : policies) {
                boolean complied = true;
                if (pol.mod.equals("F") && pol.tgt.equals("peer"+peer) && pol.isActive(this)) {
                    Set<String> data = pol.getData("peer"+peer).keySet();
                    if (data.contains(pred)) {
                        // Prohibition is complied with if polSets contains at least one policy set who has pol as a primary policy
                        boolean found = false;
                        for (PolicySet ps : polSets) {
                            for (DataPolicy pTest : ps.getPrimary()) {
                                if (pol.trueEquals(pTest)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) { break;}
                        }
                        
                        if (found) {
                            complied = false;
                        }
                    }
                }
                if (complied) {
                    rew += pol.reward;
                }
            }
        }
        //System.out.println("Reward Given: "+rew);
        return rew;
    }
    
    private int checkViolation(HashSet<PolicySet> polSets, String pred, long peer) {
        int pen =  0;
        if (PrologInterface.TRUE_RANDOM) {
            pen = rng.nextInt(100);
            if (pen < 99) { pen = 0;} else { pen = 1;} 
        } else {
            for (DataPolicy pol : policies) {
                boolean violated = false;
                if (pol.mod.equals("P") && pol.tgt.equals("peer"+peer) && pol.isActive(this)) {
                    Set<String> data = pol.getData("peer"+peer).keySet();
                    if (data.contains(pred)) {
                        // Permission is violated if polSets doesn't contain a policy set who has pol as a primary policy
                        boolean found = false;
                        for (PolicySet ps : polSets) {
                            for (DataPolicy pTest : ps.getPrimary()) {
                                if (pol.trueEquals(pTest)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) { break;}
                        }
                        
                        if (!found) {
                            violated = true;
                        }
                    }
                }
                if (violated) {
                    pen += pol.penalty;
                }
            }
        }
        //System.out.println("Penalty Given: "+pen);
        return pen;
    }
    private HashSet<Node> getForwardingNeighbours(String pred) {
        HashSet<Node> targets = new HashSet<Node>();
        for (String n : overlayNetwork.keySet()) {
            if (PrologInterface.TRUE_RANDOM) {
                if (rng.nextBoolean()) {
                    targets.add(overlayNetwork.get(n));
                }
            } else if (!PrologInterface.REASONING) {
                targets.add(overlayNetwork.get(n));                
            } else {                
                if (kb.mightHaveData(n, pred)) {
                    targets.add(overlayNetwork.get(n));                        
                }
            }
        }
        return targets;
    }
    
    private void processMsg_NoData(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //No_Data -> Sender_ID, Data_Item, Data_Package[]
        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records
        
        processIncomingDataPackage((DataPackage) msg.body[0], msg.sender, protocolID);
        if (PrologInterface.REASONING) {
            Transaction t = getOpenOutTrans(n.peerID, msg.reqTransId);
            kb.add("noData", new String[]{ "peer"+n.peerID, t.predicate});
        }
        
        //Prolog State of Affairs Add: Sender_ID does not have Data_Item
        //PrologInterface.assertFact("noData", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.body[0]) });
        removeOutTrans(n.peerID, msg.reqTransId);
    }
    
    private void processMsg_NoAccess(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //No_Access -> Sender_ID, Data_Item, Data_Package[]
        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records
        
        processIncomingDataPackage((DataPackage) msg.body[0], msg.sender, protocolID);
        if (PrologInterface.REASONING) {
            Transaction t = getOpenOutTrans(n.peerID, msg.reqTransId);
            kb.add("refusedData", new String[]{ "peer"+n.peerID, t.predicate});
        }
        
        //Prolog State of Affairs Add: Sender ID may have Data_item, but won't allow us access
        //PrologInterface.assertFact("noAccess", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]) });
        removeOutTrans(n.peerID, msg.reqTransId);
    }
    
    private void processMsg_PolicyInform(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Policy_Inform -> Data_Item, HashSet<PolicySet> relPolicySets
        
        HashSet<PolicySet> policySets = (HashSet<PolicySet>) msg.body[1];
        for (PolicySet ps : policySets) {
            double val = policyProfitReq(ps);
            ps.requestorValue = val;
            ps.cullOptionalPolicies();
        }
        
        PolicySet chosenPS = choosePolicySet(policySets);
        if (chosenPS == null) {
            n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "REJECT_POLICIES", new Object[] { }, null);
            removeOutTrans(n.peerID, msg.reqTransId);
        } else if (chosenPS.isActive()) {
            //System.out.println(outTransactionStack.keySet());
            boolean hasTrans = hasOpenOutTrans(n.peerID, msg.reqTransId);
            if (hasTrans || (!hasTrans && transactionFree() && !hasOpenOutTrans(n.peerID, (String) msg.body[0]) && wantedData.contains((String) msg.body[0]))) {
                // Should accept if not in outTransactionStack, as long as the data being offered is wanted
                int tID = msg.reqTransId;
                if (!hasTrans) {
                    tID = getFreeTransaction("pPI");
                    outTransactionStack.put(tID, new Transaction(tID, msg.prvTransId, n.peerID, (String) msg.body[0], 1, PrologInterface.TRANS_LIFETIME));
                }/* else {
                    System.out.println(outTransactionStack.keySet());
                    System.err.println(outTransactionStack.containsKey(tID)+ " ?= "+hasOpenOutTrans(n.peerID, tID)+": "+tID+", "+msg.transactionId+" ("+hasTrans+")");
                }*/
                try {
                    outTransactionStack.get(tID).policySets = policySets;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(outTransactionStack.containsKey(tID)+" ("+hasTrans+" - "+hasOpenOutTrans(n.peerID, tID)+") - "+tID);
                }
                HashSet<TransactionRecord> relRecords = getRelRecords();
                n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, tID, "RECORD_INFORM", new Object[] { chosenPS, relRecords }, null);
            }
        } else if (chosenPS.canActivate()) {
            scheduleActions(chosenPS);
            n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "WAIT", new Object[] { }, null);
        }

        if (PrologInterface.REASONING) {
            kb.add("hasData", new String[]{ "peer"+n.peerID, (String) msg.body[0]});
        }
    }
    
    private PolicySet choosePolicySet(HashSet<PolicySet> policySets) {
        PolicySet chosenPS = new PolicySet();
        if (rng.nextInt(50) == 0) {
            return null;
        }
        return chosenPS;
    }
    
    private double policyProfitReq(PolicySet ps) {
        return rng.nextInt(50)-25;
    }
    
    private double actionCostReq() {
        return rng.nextInt(50)-25;
    }
    
    private HashSet<TransactionRecord> getRelRecords() {
        HashSet<TransactionRecord> relRecords = new HashSet<TransactionRecord>();
        if (transactions.size() > 0) {
            int randNum = rng.nextInt(transactions.size());
            for (TransactionRecord r : transactions) {
                if (randNum <= 1) {
                    break;
                }
                relRecords.add(r);
                randNum -= 1;
            }
        }
        return relRecords;
    }
    
    private void scheduleActions(PolicySet ps) {
        
    }
    
    private void processMsg_RecordInform(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Record_Inform -> Chosen_PolicySet, Rel_Records
        if (!hasOpenInTrans(n.peerID, msg.prvTransId)) {
            n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "INVALID_TRANSACTION", new Object[] { }, null);            
        } else {
            Transaction t = getOpenInTrans(n.peerID, msg.prvTransId);
            if (t.remoteId == -1) { t.remoteId = msg.reqTransId;}
            HashSet<TransactionRecord> relRecords = null;
            try {
                relRecords = (HashSet<TransactionRecord>) msg.body[1];
            } catch (ClassCastException e) {
                //Could not cast records, malformed message
            }
            
            PolicySet chosenPolicySet = null;
            try {
                chosenPolicySet = (PolicySet) msg.body[0];
            } catch (ClassCastException e) {
                //Could not cast policy set, malformed message
            }
    
    
            if (relRecords == null) {
                n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "MALFORMED_RECORDS", new Object[] { }, null);
            } else if (chosenPolicySet == null) { // A policy set was not chosen, kill the transaction
                removeInRemoteTrans(n.peerID, msg.reqTransId);      
            } else {
                assimilateRecords(relRecords);
                
                HashSet<DataPolicy> active = chosenPolicySet.activeSet();
                HashSet<DataElement> data = null;
                if (active.size() == chosenPolicySet.size() || (active.size() > 0 && policyProfitPrv_Permit(chosenPolicySet) > PrologInterface.MIN_UTIL && chosenPolicySet.permitsAccess(t.predicate))) {
                    data = chooseData(chosenPolicySet, t);
                } else { 
                    // Nothing
                }
                
                DataPackage dp = assembleDataPackage(data,generateTransactionRecords(),msg.sender.getID()); 
                n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "DATA_RESULT", new Object[] { dp }, null);
                
                removeInRemoteTrans(n.peerID, msg.reqTransId);
            }
        }
    }
    
    private void assimilateRecords(HashSet<TransactionRecord> relRecords) {
        // Permanently store some details, temporarily store others
    }
    
    private HashSet<DataElement> chooseData(PolicySet ps, Transaction t) {
        HashSet<DataElement> data = new HashSet<DataElement>();
        while (rng.nextInt(2) == 0 && data.size() < t.quantity) {
            data.add(new DataElement(t.predicate,generateDataElement()));
        }
        return data;
    }
    
    private void processMsg_DataResult(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Data_Result -> Data_Package[]
        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records

        DataPackage dataPackage = (DataPackage) msg.body[0];
        processIncomingDataPackage(dataPackage,msg.sender,protocolID);
    }
    
    private void processMsg_RejectPolicies(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Reject_Policies -> Null
        //Prolog State of Affairs Add: Sender_ID rejected policies for Data_Item
        if (hasOpenInRemoteTrans(n.peerID, msg.reqTransId)) {
            removeInRemoteTrans(n.peerID, msg.reqTransId);
        }
    }
    
    private void processMsg_Wait(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        if (hasOpenInRemoteTrans(n.peerID, msg.reqTransId)) {
            n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "CONFIRM_WAIT", new Object[] { }, null);
        }
    }
    
    private void processMsg_ConfirmWait(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        hasOpenOutTrans(n.peerID, msg.reqTransId);
    }
    
    private void processMsg_MalformedRecords(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Malformed_Records -> Sender_ID, Data_Item

        if (hasOpenOutTrans(n.peerID, msg.reqTransId)) {
            Transaction t = outTransactionStack.get(msg.reqTransId);
            // Fake a new POLICY_INFORM
            System.out.println("MALF");
            n.sendMessage(protocolID, node, msg.sender, msg.prvTransId, msg.reqTransId, "POLICY_INFORM", new Object[] { t.predicate, t.policySets }, null);
        }
    }
    
    private void processMsg_InvalidTransaction(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        removeOutTrans(n.peerID, msg.reqTransId);                
    }
    
    private void processMsg_PeerOverload(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //No_Access -> Sender_ID, Data_Item, Data_Package[]
        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records
        
        processIncomingDataPackage((DataPackage) msg.body[0], msg.sender, protocolID);
        removeOutTrans(n.peerID, msg.reqTransId);        
    }
    
    
    private void processMsg_PeerDown(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        //Peer_Down -> Sender_ID, Data_Item
        if (overlayNetwork.containsKey("peer" + msg.sender.getID())) {
            overlayNetwork.remove("peer" + msg.sender.getID());
            kb.add("offline", new String[] { "peer"+n.peerID});
            //PrologInterface.retractFact("connected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + msg.sender.getID()) });
            //PrologInterface.assertFact("peerOffline", new Term[] { new Atom("peer"+peerID), new Atom("peer" + msg.sender.getID()) });
        }

//        if (pendingData.containsKey(msg.body[0])) {
//            desiredData.put((String) msg.body[0], pendingData.get(msg.body[0]));
//            pendingData.remove((String) msg.body[0]);
//            //activeRequests -= 1;
//        }
    }
    
    private void processMsg_Inform(DataExchange n, P2PMessage msg, Node node, int protocolID) {
        // Open hook for strategic planning
    }
    
    private void updatePolicies() {
        
    }

    private void processActions(Node node, int protocolID) {     
      //At the end of each cycle, need to reason on remaining value from desired data, vs the predicted cost of remaining in the network to get it
        
        Node neighbour = overlayNetwork.get(overlayNetwork.keySet().toArray()[rng.nextInt(overlayNetwork.size())]);
        String data = (String) wantedData.toArray()[rng.nextInt(wantedData.size())];
        //System.out.println(n.peerID+" ?= "+neighbour.getID()+" for "+node.getID());
        if (!hasOpenOutTrans(((DataExchange) neighbour.getProtocol(protocolID)).peerID, data)) {
            //System.out.println(node.getID()+", "+neighbour.getID()+" ("+overlayNetwork.keySet()+")");
            sendDataRequest(protocolID, node, neighbour, data);
        }
        
//        if (desiredData.size() > 0 && linkable.degree() > 0 && peerBudget >= PrologInterface.confCycleCost && penaltyRounds == 0) {
//            for (String gD : generatedData) {
//                if (!ownedData.contains(gD)) { ownedData.add(gD);}
//                dataCollection.add(new DataElement(gD,generateDataElement()));
//            }
//            
//            Node bestNeighbour = null;
//            String bestData = "";
//            double bestDataVal = 0.0;
//            for (String d : desiredData.keySet()) {
//                double bestChance = 0.0;
//                Node localBestNeighbour = null;
//                for (String n : overlayNetwork.keySet()) {
//                    double prob = 0.0;
//                    
//                    //TODO: Implement the below probability calculation
//                    //If N has previously given at least Q items of D to R, Prob = 1
//                    //Else If N has previously given less than Q items of D to R, Prob = 0.95
//                    //Else If N has never been asked for D, Prob = 0.5
//                    //Else If N has previously had D but not given it to R, Prob = Min(0.75,0.25 + (0.05 * Number Of Cycles Since Last Request))
//                    //Else If N has previously not had D, Prob = 0.05
//                    //Else, Prob = 0
//                    
//                    //IMPLICIT: if (Own >= Obl.Data_Quantity of Obl.Data_Item) { completionProb *= 1.0;}    
//                    /*if (PrologInterface.runGroundQuery("recordRequest", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom("peer"+peerID), new Atom(obl.payload[0]), new Variable("_"), new Variable("_"), new Atom("true")})) { completionProb *= 0.95;}
//                    if (PrologInterface.runGroundQuery("hasData", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom(obl.payload[0])})) { completionProb *= 0.75;}
//                    else if (PrologInterface.runGroundQuery("possibleData", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom(obl.payload[0])})) { completionProb *= 0.5;}
//                    else if (!PrologInterface.runGroundQuery("possibleData", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom(obl.payload[0])})) { completionProb *= 0.05;}
//                    else { completionProb *= 0.0;}*/
//                    
//                    if (prob > bestChance) { 
//                        bestChance = prob;
//                        localBestNeighbour = overlayNetwork.get(n);
//                    }
//                }
//                //TODO: This should also calculate penalties of policies this will break/rewards of obligations this will fulfil
//                int costOfRequest = PrologInterface.confCycleCost * AVG_TRANS_LENGTH;
//                double dataVal = ((getDataValue(d) * desiredData.get(d)) * bestChance) - costOfRequest;
//                if (dataVal > bestDataVal) {
//                    bestData = d;
//                    bestNeighbour = localBestNeighbour;
//                }
//            }
//            
//            if (!bestData.equals("") && bestNeighbour != null) {
//                //TODO: Penalise self here for any policies broken by this request
//                
//                String dataItem = bestData;
//                Node peer = bestNeighbour;
//                DataExchange n = (DataExchange) peer.getProtocol(protocolID);
//
//                //Send Desired_Data[RND],1 to Neighbour[RND] as "Data_Request"
//                int quantity = desiredData.get(dataItem); if (quantity == -1) { quantity = 10;}
//                n.sendMessage(protocolID, peer, node, "DATA_REQUEST", new Object[] { dataItem, quantity, 0 });
//
//                pendingData.put(dataItem, desiredData.get(dataItem));
//                desiredData.remove(dataItem);
//            }
//            //Need to reason before disconnecting, in some situations there may be a good enough reason to stay a while longer (incoming pay-off)
//        }
        
//        if (obligations.size() > 0) {
//            ArrayList<DataPolicy> toRemove = new ArrayList<DataPolicy>();
//            for (DataPolicy obl : obligations.keySet()) {
//                boolean fulfilled = true, violated = false;
//                for (Action a : obligations.get(obl).keySet()) {
//                    if (obligations.get(obl).get(a) != 2) {
//                        fulfilled = false;
//                        if (peersim.core.CommonState.getTime() >= a.expiry) {
//                            violated = true;
//                            penaltyRounds += obl.penalty;
//                        }
//                        break;
//                    }
//                }
//                
//                if (fulfilled) {
//                    //TODO: If this was being done to access some data...
//                    toRemove.add(obl);
//                } else if (violated) {
//                    System.out.println("Penalty of "+obl.penalty+" applied to peer"+peerID);
//                    toRemove.add(obl);
//                }
//            }
//            for (DataPolicy obl : toRemove) { obligations.remove(obl);}
//
//            //If all obligations are marked as processed (or fulfilled), set all processed obligations to unprocessed
//            boolean allProcessed = true;
//            for (DataPolicy obl : obligations.keySet()) {
//                for (Action a : obligations.get(obl).keySet()) {
//                    if (obligations.get(obl).get(a) == 0) {
//                        allProcessed = false;
//                        break;
//                    }
//                }
//                if (!allProcessed) {
//                    break;
//                }
//            }
//            if (allProcessed) {
//                for (DataPolicy obl : obligations.keySet()) {
//                    for (Action a : obligations.get(obl).keySet()) {
//                        if (obligations.get(obl).get(a) == 1) {
//                            obligations.get(obl).replace(a, 0);
//                        }
//                    }
//                }
//            }
//
//            DataPolicy oblP = null;
//            Action actP = null;
//            for (DataPolicy obl : obligations.keySet()) { 
//                ArrayList<Integer> times = new ArrayList<Integer>();
//                for (Action a : obl.getObligedActions()) {
//                    times.add(a.time);                 
//                }
//                Collections.sort(times);
//                Collections.reverse(times);
//                
//                int cur = 0, total = 0;
//                for (Integer t : times) {
//                    if ((total-cur) < t) {
//                        total += (t-(total-cur));
//                    }
//                    cur += 1;
//                }
//                
//                //spareCycles = 
//            }
//            //TODO: Choose the best obligation to fulfil next
//            
//            /*
//            int oblSetIndex = 0, oblIndex = 0;
//            ObligationSet oblSet = null;
//            Obligation obl = null;
//            while (oblSetIndex < obligationSets.size() && obl == null) {
//                oblSet = obligationSets.get(oblSetIndex);
//                float oblCosts[] = obligationCost(oblSet);
//                if (oblCosts[0] < oblCosts[1]) {
//                    oblIndex = 0;
//                    Obligation lastObl = null;
//                    obl = null;
//                    for (Obligation oblTest : oblSet.obligations.keySet()) {
//                        if (oblSet.obligations.get(oblTest) == 0) {
//                            obl = oblTest;
//                            break;
//                        }
//                        oblIndex += 1;
//                        lastObl = oblTest;
//                    }
//                    //If Obl type is INFORM and the Obligation prior to Obl in OblSet is unfulfilled
//                    if (obl != null && obl.type.equals("inform") && lastObl != null && oblSet.obligations.get(lastObl) != 2) {
//                        obl = null;
//                    }
//                }
//                oblSetIndex += 1;
//            }*/
//            
//            if (actP != null) {
//                switch (actP.type) {
//                    case "obtain": {
//                        //[Data_Item, Data_Quantity] <- Obtain
//                        int quantityDataOwned = 0;
//                        if (ownedData.contains(actP.payload[0])) {
//                            Term result = PrologInterface.runQueryFirstResult("numData", new Term[] { new Atom("peer" + peerID), new Atom(actP.payload[0]), new Variable("Z") }, "Z");
//                            if (result != null) {
//                                quantityDataOwned = result.intValue();
//                            }
//                        }
//                        
//                        int quantityNeeded = Integer.parseInt(actP.payload[1]) - quantityDataOwned;
//                        if (quantityNeeded > 0) {
//                            if (desiredData.containsKey(actP.payload[0])) {
//                                desiredData.replace(actP.payload[0], desiredData.get(actP.payload[0]) + quantityNeeded);
//                            } else {
//                                desiredData.put(actP.payload[0], quantityNeeded);
//                            }
//                        } else {
//                            obligations.get(oblP).replace(actP, 2); //Mark Obligation as fulfilled                            
//                        }                        
//                        break;
//                    }    
//                    case "provide":
//                        //[Data_Item, Data_Quantity, Data_Recipient] <- Provide
//                        int quantityDataOwned = 0;
//                        if (ownedData.contains(actP.payload[0])) {
//                            Term result = PrologInterface.runQueryFirstResult("numData", new Term[] { new Atom("peer" + peerID), new Atom(actP.payload[0]), new Variable("Z") }, "Z");
//                            if (result != null) {
//                                quantityDataOwned = result.intValue();
//                            }
//                        }
//
//                        if (quantityDataOwned >= Integer.parseInt(actP.payload[1])) {
//                            HashSet<Term> relPolicies = new HashSet<Term>();
//                            //Prolog Query: Relevant Policies for Data_Recipient and Data_Item -> Rel_Policies
//                            HashSet<Term> result = PrologInterface.runQuery("relPolicies", new Term[] { new Atom("peer" + peerID), new Atom(actP.payload[2]), new Atom(actP.payload[0]), new Variable("L") }, "L");
//                            for (Term t : result) {
//                                //relPolicies.add(PrologInterface.policyToTerm(t));
//                                relPolicies.add(t);
//                            }
//
//                            //Send ["POLICY_INFORM", Data_Recipient, Data_Item, Data_Quantity, Rel_Policies]                        
//                            if (relPolicies.size() > 0) {
//                                Node n = getPeerByID(actP.payload[2]);
//                                if (n != null) {
//                                    ((DataExchange) n.getProtocol(protocolID)).sendMessage(protocolID, n, node, "POLICY_INFORM", new Object[] { actP.payload[0], Integer.parseInt(actP.payload[1]), relPolicies });
//                                }
//                            } else {
//                                //Could change policies to allow this obligation to be fulfilled. Potentially
//                            }
//                        } else {
//                            if (desiredData.containsKey(actP.payload[0])) {
//                                desiredData.replace(actP.payload[0], desiredData.get(actP.payload[0]) + Integer.parseInt(actP.payload[1]));
//                            } else {
//                                desiredData.put(actP.payload[0], Integer.parseInt(actP.payload[1]));
//                            }
//                        }
//                        break;
//
//                    case "adopt":
//                        //[Policy, Duration] <- Adopt
//                        boolean conflict = false;
//                        DataPolicy polToCheck = new DataPolicy(peerID,Util.textToTerm(actP.payload[0]),Integer.parseInt(actP.payload[1]),false);
//                        //Prolog Query: Does adding Policy to my policy collection create a conflict -> Conflicted_Policies
//                        HashSet<Term> result = PrologInterface.runQuery("polsViolatedBy", new Term[] { new Atom("peer" + peerID), polToCheck.getPrologTerm(), new Variable("L") }, "L");
//                        if (result.size() > 0) { conflict = true;}
//                        if (!conflict) {
//                            //Prolog Query: Does Policy, enacted by Issuer_ID already exist -> Current_Policy_Expiry
//                                //% Finds a policy which matches policy(Issuer_ID,Policy,_), extract the current duration of this policy
//                            //If Current_Policy_Expiry is NULL
//                                //Prolog Assert: Add Policy to Policy Collection with expiry in Duration cycles
//                                //% policy(Self,Policy,Duration)
//                            //Else if Current_Policy_Expiry is not infinite
//                                //Prolog Assert: Add Duration cycles to the current expiry of Policy
//                                //% policy(Self,Policy,(Current_Policy_Duration + Duration))
//                            //End
//                            policies.add(polToCheck);
//                            PrologInterface.assertFact("policy", new Term[]{ new Atom("peer"+peerID),polToCheck.getPrologTerm()});
//                        }
//                        break;
//
//                    case "inform":
//                        //[Inform_Recipient] <- Inform
//                        HashSet<String> completedObl = new HashSet<String>();
//                        boolean othersDone = true;
//                        for (Action actTest : obligations.get(oblP).keySet()) {
//                            if (obligations.get(oblP).get(actTest) == 2) {
//                                completedObl.add(actTest.toString());
//                            } else {
//                                othersDone = false;
//                                break;
//                            }
//                        }
//                        
//                        if (othersDone) {
//                            //Send ["OBLIGATION_INFORM", Inform_Recipient, Completed_Obl, null, null]                       
//                            Node n = getPeerByID(actP.payload[0]);
//                            if (n != null) {
//                                ((DataExchange) n.getProtocol(protocolID)).sendMessage(protocolID, n, node, "OBLIGATION_INFORM", new Object[] { completedObl });                            
//                                obligations.get(oblP).replace(actP, 2); //Mark Obligation as fulfilled
//                            }
//                        }
//                        break;
//                }
//                if (obligations.get(oblP).get(actP) == 0) {
//                    obligations.get(oblP).replace(actP, 1);
//                }
//            }
//        }
    }
    
    private void sendDataRequest(int protocolID, Node send, Node rec, String data) {
        if (transactionFree()) {
            int tID = getFreeTransaction("sDR");
            DataExchange n = (DataExchange) rec.getProtocol(protocolID);
            //System.out.print(rec.getID()+", "+send.getID()+" ("+overlayNetwork.keySet()+")");
            n.sendMessage(protocolID, rec, send, -1, tID, "DATA_REQUEST", new Object[] { data, 1 }, null);
            //System.out.println("");
            outTransactionStack.put(tID, new Transaction(tID, -1, n.peerID, data, 1, PrologInterface.TRANS_LIFETIME));
        }
    }
    
    private void processTransactionStack() {
        HashSet<Integer> toRemove = new HashSet<Integer>();
        for (int tKey : inTransactionStack.keySet()) {
            if (inTransactionStack.get(tKey).decrementLife()) {
                toRemove.add(tKey);
            }
        }
        //if (toRemove.size() > 0) { System.out.println("Removing "+toRemove.size()+" incoming transactions ("+inTransactionStack.size()+")");}
        for (int i : toRemove) { 
//            inTransactionStack.remove(i);
//            if (!freeTransactions.contains(i) && !inTransactionStack.containsKey(i)) { freeTransactions.add(i);}
//            if (inTransactionStack.containsKey(i)) { System.out.println("REMOVAL FAILED FOR "+i);}
            removeInTrans(-1,i);
        }
        //if (toRemove.size() > 0) { System.out.println("\tRemoved "+inTransactionStack.size());}
        
        
        toRemove = new HashSet<Integer>();
        for (int tKey : outTransactionStack.keySet()) {
            if (outTransactionStack.get(tKey).decrementLife()) {
                toRemove.add(tKey);
            }
        }
        //if (toRemove.size() > 0) { System.out.println("Removing "+toRemove.size()+" incoming transactions ("+outTransactionStack.size()+")");}
        for (int i : toRemove) { 
//            outTransactionStack.remove(i);
//            if (!freeTransactions.contains(i) && !outTransactionStack.containsKey(i)) { freeTransactions.add(i);}
//            if (outTransactionStack.containsKey(i)) { System.out.println("REMOVAL FAILED FOR "+i);}
            removeOutTrans(-1,i);
        }
        //if (toRemove.size() > 0) { System.out.println("\tRemoved "+outTransactionStack.size());}
    }
    
    private void decideToLeaveNetwork() {
        // Current experiments do not allow peers to leave
//        if (requestor && desiredData.size() == 0 && pendingData.size() == 0) {
//            System.err.println("Peer "+peerID+" got all necessary data");
//            gracefulDisconnect(node, protocolID, 0);
//        } else if (peerBudget < cycleCost) {
//            System.err.println("Peer "+peerID+" ran out of budget");
//            gracefulDisconnect(node, protocolID, 1);
//        } else if (overlayNetwork.size() == 0) {
//            System.err.println("Peer "+peerID+" ran out of neighbours");
//            gracefulDisconnect(node, protocolID, 2);
//        } else {
//            // Stay
//        }
    }
        
    public double policyValueProvider(DataPolicy p) {
        double profit = 0.0; 
        switch (p.mod) {
            case "P":
                profit = p.reward;
                break;
            case "F":
                profit = p.penalty;
                break;
            case "O":
                //TODO: policyValueProvider() for Obligations
                break;
        }
        return profit;
    }
    
    public double policyValueRequestor(DataPolicy p, String d, int n) {
        double profit = 0.0;
        
        switch (p.mod) {
            case "P":
                for (Action a : p.actions) {
                    if (a.type.equals("dataAccess") && a.payload[0].equals(d)) {
                        profit += getDataValue(a.payload[0]) * Math.min(n,Integer.parseInt(a.payload[1]));
                        break;
                    }
                }
                profit -= (PrologInterface.confCycleCost*AVG_TRANS_LENGTH-1);
                //TODO: if Pol is not currently active
                    //$\mathit{Profit} \leftarrow \mathit{Profit} - \mathit{Cost~of~actions~to~achieve~Pol.ACon}$ // Includes cost of time to complete and probability of success
                break;
            case "F":
                double dataProfit = 0.0;
                for (Action a : p.actions) {
                    if (a.type.equals("dataAccess") && a.payload[0].equals(d)) {
                        dataProfit += getDataValue(a.payload[0]) * Math.min(n,Integer.parseInt(a.payload[1]));
                        break;
                    }
                }
                
                double deactivateCost = 0.0;
                //TODO: $\mathit{deactivateCost} \leftarrow \mathit{Cost~of~actions~to~achieve~Pol.DCon}$ // Includes cost of time to complete and probability of success
                
                if (dataProfit >= deactivateCost) {
                    profit += (dataProfit - deactivateCost);
                }
                break;
            case "O":
                profit -= Math.min(costToFulfilObligation(p), p.penalty);
                break;
        }
        
        return profit;
    }
    
    public double costToFulfilObligation(DataPolicy p) {
        double cost = 100.0;
        
        //TODO: $\mathit{fulfilObligation} \leftarrow \mathit{Cost~of~actions~in~Pol.Action}$ // See %*Listing~\ref{lst:req-oblige}*) .
        /*
        %*\textbf{for}*) Action $\in$ Policy.action
        switch Action
            case $\mathit{obtain(Data, Quantity, Duration)}$ or $\mathit{provide(Data, Quantity, Peer, Duration)}$
                if You have less than Action.Quantity of Action.Data
                    $\mathit{Profit} \leftarrow -\textit{Cost~of~a~typical~transaction}$
                    $\mathit{Profit} \leftarrow \mathit{Profit} + (\mathit{Action.Quantity} \times \mathit{Value~of~Action.Data})$ 
                    $\mathit{Profit} \leftarrow \mathit{Profit} - \mathit{Penalty~of~policy~violations~for~obtaining~Action.Data}$ 
            case $\mathit{provide(Data, Quantity, Peer, Duration)}$
                $\mathit{Profit} \leftarrow -\textit{Cost~of~a~typical~transaction}$
                $\mathit{Profit} \leftarrow \mathit{Profit} -  \mathit{Penalty~of~policy~violations~for~providing~Action.Data~to}$ $\mathit{Action.Peer}$
            case $\mathit{adopt(Policy, Duration)}$
                $\mathit{Profit} \leftarrow -(\mathit{CycleCost} \times \mathit{adopt.Duration})$
                $\mathit{Profit} \leftarrow \mathit{Profit} - \mathit{Penalty~of~obligations~that~are~now~unfulfillable~due~to}$ $\mathit{adopt.Policy}$
                $\mathit{Profit} \leftarrow \mathit{Profit} - \mathit{Penalty~of~obligations~violated~by~adopt.Policy}$
            case $\mathit{inform(Peer, Duration)}$
                $\mathit{Profit} \leftarrow -\mathit{CycleCost}$        
        $\mathit{Profit} \leftarrow \mathit{Profit} - (\mathit{Action.Penalty} \times (1 - \mathit{probabilityOfCompletion})) + $$(\mathit{Action.Reward} \times \mathit{probabilityOfCompletion})$
        */
        
        return cost;
    }
    
    public boolean fulfilObligations(PolicySet polSet) {
        double fulfilProfit = 0.0, breakProfit = 0.0;
        for (DataPolicy pol : polSet.getPrimary()) {
            if (pol.mod.equals("O")) {
                fulfilProfit += costToFulfilObligation(pol);
                breakProfit += pol.penalty;
            }
        }
        for (DataPolicy pol : polSet.getSecondary()) {
            //TODO: If this policy is active
            if (pol.mod.equals("O")) {
                fulfilProfit += costToFulfilObligation(pol);
                breakProfit += pol.penalty;
            }
        }
        
        if (fulfilProfit >= breakProfit) {
            return true;
        }
        return false;
    }
       
    public void sendMessage(int protocolID, Node r, Node s, int pTrId, int rTrId, String type, Object[] body, HashSet<Node> chain) {
        if (faulty && (PrologInterface.confFaultRate == 100 || rng.nextInt((100-PrologInterface.confFaultRate)) == 0)) {
            if (messageTotals.containsKey("MESSAGE_FAULT")) { messageTotals.replace("MESSAGE_FAULT", messageTotals.get("MESSAGE_FAULT")+1);}
            if (PrologInterface.debugMessages) {
                System.out.println("MESSAGE FAULT");
            }
        } else {
            if (r.isUp() && !disconnecting) {
                if (r.getID() != s.getID()) {
                    P2PMessage msg = new P2PMessage(s, r, pTrId, rTrId, type, (peersim.core.CommonState.getTime() + 1), body);
                    if (chain != null) { msg.addChain(chain);}
                    int chainSize = msg.getChain().size();
                    if (chainSize >= 25 || (chainSize > 5 && rng.nextInt(100-((chainSize-5)*5)) == 0)) {
                        if (messageTotals.containsKey("CHAIN_FAILURE")) { messageTotals.replace("CHAIN_FAILURE", messageTotals.get("CHAIN_FAILURE")+1);}
                        if (PrologInterface.debugMessages) {
                            System.out.println("CHAIN FAILURE ("+chainSize+")");
                        }
                    } else {
                        messages.add(msg);
                        if (type.equals("DATA_RESULT")) {
                            if (messageTotals.containsKey(type)) { messageTotals.replace(type, messageTotals.get(type)+1);}
                            try {
                                if (((DataPackage) body[0]).dataItems.size() > 0) {
                                    type = "DATA_RESULT_Y";                                    
                                } else {
                                    type = "DATA_RESULT_N";                                    
                                }
                            } catch (Exception e) {
                                type = "DATA_RESULT_N";
                            }
                        }
                        if (messageTotals.containsKey(type)) { messageTotals.replace(type, messageTotals.get(type)+1);}
                        if (PrologInterface.debugMessages) {
                            String payloadString = "";
                            for (Object p : body) {
                                payloadString += p + ",";
                            }
                            if (payloadString.length() > 0) {
                                payloadString = payloadString.substring(0, payloadString.length() - 1);
                            }
                            if (chain != null) { System.out.print("[F] ");}
                            System.out.println(s.getID() + " -> " + r.getID() + ", " + type + " (" + pTrId + " | " + rTrId + "), [" + payloadString + "], " + (peersim.core.CommonState.getTime() + 1)+" - "+msg.chainString());
                        }
                    }
                } else {
                    System.out.println("CIRCULAR MESSAGE  ("+type+")");
                }
            } else if (s.isUp()) {
                DataExchange sDE = ((DataExchange) s.getProtocol(protocolID));
                if (!sDE.disconnecting) {
                    String returnData = "-1";
                    try {
                        returnData = (String) body[0];
                    } catch (ClassCastException e) {
                    }
                    sDE.messages.add(new P2PMessage(r, s, pTrId, rTrId, "PEER_DOWN", (peersim.core.CommonState.getTime() + 1), new String[] { returnData }));
                    if (messageTotals.containsKey("PEER_DOWN")) { messageTotals.replace("PEER_DOWN", messageTotals.get("PEER_DOWN")+1);}
                    if (PrologInterface.debugMessages) {
                        System.out.println(r.getID() + " -> " + s.getID() + ", PEER_DOWN (" + pTrId + " | " + rTrId +"), [" + returnData + "], " + (peersim.core.CommonState.getTime() + 1));
                    }
                }
            }
        }
    }

    protected void gracefulDisconnect(Node node, int protocolID, int dType) {
        disconnecting = true;
        for (P2PMessage msg : messages) {
            DataExchange n = (DataExchange) msg.sender.getProtocol(protocolID);
            n.sendMessage(protocolID, msg.sender, node, msg.prvTransId, msg.reqTransId, "PEER_DOWN", new String[] { (String) msg.body[0] }, null);
        }

        messages.clear();
        disconnectTime = peersim.core.CommonState.getTime();
        disconnectType = dType;
        DataExchange.suspend(node);
    }

    protected static void suspend(Node node) {
        node.setFailState(Fallible.DOWN);
    }
    
    public void makeAltruistic() {
        altruistic = true;
    }
    
    public void makeFair() {
        fair = true;
    }
    
    public void makeFaulty() {
        faulty = true;
    }    
    
    public void makeWantData(String d) {
        wantedData.add(d);
    }
    
    public void makeOwnData(String d) {
        ownedData.add(d);
        producedData.add(d);
    }
    
    private void addPolicy(DataPolicy pol) {
        boolean found = false;
        for (DataPolicy p : policies) {
            if (pol.equals(p)) {
                found = true;
                break;
            }
        }
        if (!found) {
            policies.add(pol);
        }
    }
    
    public int getDataValue(String data) {
        if (dataValue.containsKey(data)) {
            return dataValue.get(data);
        }
        return 1;
    }

    public void setDataValue(String data, int value) {
        if (!dataValue.containsKey(data)) {
            dataValue.put(data, value);
        } else {
            dataValue.replace(data, value);
        }

    }
    
    public int countData(String d) {
        int dCount = 0;
        for (DataElement data : dataCollection) {
            if (data.dataID.equals(d)) { 
                dCount += 1;
            }
        }
        return dCount;
    }
    
    public String getRole() {
        String role = "";
        if (altruistic) { role += "A";} else { role += "S";}
        if (fair) { role += "F";} else { role += "S";}
        if (faulty) { role += "F";} else { role += "N";}
        return role;
    }
    
    private boolean transactionFree() {
        return (freeTransactions.size() > 0);
    }
    
    private int getFreeTransaction(String code) {
        if (transactionFree()) {
            int t = freeTransactions.get(0);
            freeTransactions.remove(0);
            if (inTransactionStack.containsKey(t) || outTransactionStack.containsKey(t)) {
                System.out.println("Key Reissued: "+t+" ["+code+"]\n\tIn:  "+inTransactionStack.get(t)+"\n\tOut: "+outTransactionStack.get(t)+"");
            }
            return t;
        } else {
            return -1;
        }
    }
    
    private boolean hasOpenInTrans(long peer, String pred) {
        for (int tKey : inTransactionStack.keySet()) {
            Transaction t = inTransactionStack.get(tKey);
            if (t.peerID == peer && t.predicate.equals(pred)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasOpenInTrans(long peer, int id) {
        if (inTransactionStack.containsKey(id) && inTransactionStack.get(id).peerID == peer) {
            inTransactionStack.get(id).resetLife();
            return true;
        }
        return false;
    }
    
    private boolean hasOpenInRemoteTrans(long peer, int id) {
        for (int key : inTransactionStack.keySet()) {
            if (inTransactionStack.get(key).remoteId == id && inTransactionStack.get(key).peerID == peer) {
                inTransactionStack.get(key).resetLife();
                return true;
            }
        }
        return false;
    }
    
    private Transaction getOpenInRemoteTrans(long peer, int id) {
        for (int key : inTransactionStack.keySet()) {
            if (inTransactionStack.get(key).remoteId == id && inTransactionStack.get(key).peerID == peer) {
                return inTransactionStack.get(key);
            }
        }
        return null;
    }
    
    private Transaction getOpenInTrans(long peer, int id) {
        for (int key : inTransactionStack.keySet()) {
            if (inTransactionStack.get(key).transactionId == id && inTransactionStack.get(key).peerID == peer) {
                return inTransactionStack.get(key);
            }
        }
        return null;
    }
    
    private boolean removeInTrans(long peer, int id) {
        if (inTransactionStack.containsKey(id) && (peer == -1 || inTransactionStack.get(id).peerID == peer)) {
            inTransactionStack.remove(id);
            if (!freeTransactions.contains(id)) { freeTransactions.add(id);}
            if (inTransactionStack.containsKey(id)) { System.out.println("REMOVAL (IN) FAILED FOR "+id);}
            return true;
        }
        return false;
    }
    
    private boolean removeInRemoteTrans(long peer, int id) {
        for (int key : inTransactionStack.keySet()) {
            if (inTransactionStack.get(key).remoteId == id && inTransactionStack.get(key).peerID == peer) {
                inTransactionStack.remove(key);
                if (!freeTransactions.contains(key)) { freeTransactions.add(key);}
                if (inTransactionStack.containsKey(key)) { System.out.println("REMOVAL (IN) FAILED FOR "+key);}
                return true;
            }
        }
        return false;
    }
    
    private boolean hasOpenOutTrans(long peer, String pred) {
        for (int tKey : outTransactionStack.keySet()) {
            Transaction t = outTransactionStack.get(tKey);
            if (t.peerID == peer && t.predicate.equals(pred)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasOpenOutTrans(long peer, int id) {
        if (id >= 0) {
            for (int tKey : outTransactionStack.keySet()) {
                Transaction t = outTransactionStack.get(tKey);
                if (t.peerID == peer && t.remoteId == id) {
                    t.resetLife();
                    return true;
                }
            }
        }
        return false;
    }
    
    private Transaction getOpenOutTrans(long peer, int id) {
        for (int key : outTransactionStack.keySet()) {
            if (outTransactionStack.get(key).transactionId == id && outTransactionStack.get(key).peerID == peer) {
                return outTransactionStack.get(key);
            }
        }
        return null;
    }
    
    private boolean removeOutTrans(long peer, int id) {
        if (outTransactionStack.containsKey(id) && (peer == -1 || outTransactionStack.get(id).peerID == peer)) {
            outTransactionStack.remove(id);
            if (!freeTransactions.contains(id)) { freeTransactions.add(id);}
            if (outTransactionStack.containsKey(id)) { System.out.println("REMOVAL (OUT) FAILED FOR "+id);}
            return true;
        }
        return false;
    }
    
    private void processIncomingDataPackage(DataPackage dataPackage, Node sender, int protocolID) {
        DataExchange n = (DataExchange) sender.getProtocol(protocolID);
        dataPackage.decrypt();
        
        for (DataElement d : dataPackage.dataItems) {
            dataReceived += dataValue.get(d.dataID);
        }
        
        for (TransactionRecord r : dataPackage.transactionRecords) {
            addTransRecordToCollection(r);
        }
//        
//        HashSet<String> dataTypesInPackage = new HashSet<String>();
//
//        for (DataElement d : dataPackage.dataItems) {
//            //Manages current collection of data that is desired. Can be safely ignored for incoming data that you don't care about
//            if (pendingData.containsKey(d.dataID) || desiredData.containsKey(d.dataID)) {
//                if (!desiredData.containsKey(d.dataID)) {
//                    desiredData.put(d.dataID, pendingData.get (d.dataID));
//                }
//                pendingData.remove(d.dataID);
//                
//                if (desiredData.get(d.dataID) != -1) {
//                    if (desiredData.get(d.dataID) <= 1) {
//                        desiredData.remove(d.dataID);
//                    } else {
//                        desiredData.replace(d.dataID, desiredData.get(d.dataID) - 1);
//                    }
//                }
//            }
//            
//            //Updates information about received data (used to calculate satisfaction)
//            if (!receivedData.containsKey(d.dataID)) { receivedData.put(d.dataID,0);}
//            receivedData.replace(d.dataID, receivedData.get(d.dataID)+1);            
//
//            //If there is an (unfulfilled) obligation for collecting Data_Item, and the quantity of Data_Item you own exceeds Data_Quantity 
//            //Mark any obligations associated with acquiring Data_Quantity of Data_Item as fulfilled
//
//            dataReceived += 1;
//            /*if (!ownedData.contains(d.dataID)) {
//                if (!dataCollection.contains(d)) {
//                    PrologInterface.assertFact("dataElement", new Term[] { new Atom("peer" + peerID), new Atom(d.dataID), new Atom(d.data) });
//                }
//                dataCollection.add(d);
//                if (provider) {
//                    ownedData.add(d.dataID);
//                }
//                peerBudget += getDataValue(d.dataID);
//            }*/
//            dataTypesInPackage.add(d.dataID);
//            if (!ownedData.contains(d.dataID)) { ownedData.add(d.dataID);}
//            if (!dataCollection.contains(d)) {
//                PrologInterface.assertFact("dataElement", new Term[] { new Atom("peer" + peerID), new Atom(d.dataID), new Atom(d.data)});
//                dataCollection.add(d);
//            }
//            peerBudget += getDataValue(d.dataID);
//
//            //Prolog State of Affairs Add: Sender_ID has Data_Item, Receiver_ID has Data_Item
////            if (overlayNetworkEnabled) {
////                overlayNetwork.put("peer" + sender.getID(), sender);
////                PrologInterface.assertFact("connected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID) });
////            }
//        }
//        
//        for (String d : dataTypesInPackage) {
//            PrologInterface.assertFactIfNotExist("hasData", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom(d) });
//            PrologInterface.assertFactIfNotExist("hasData", new Term[] { new Atom("peer" + peerID), new Atom("peer" + peerID), new Atom(d) });
//        }
//
//        for (String r : dataPackage.transactionRecords) {
//            addTransRecordToCollection(r);
//        }
//
//        for (DataPolicy o : dataPackage.obligations) {
//            //Can just add this, conflict checking has been done when deciding to accept the policy(s) 
//            //Actually needs to update existing obligations if a new one comes in that adds to them
//            //Currently obliged to send 5 d1, then obliged to send 5 d1, would mean 10 d1 total
//            HashMap<Action,Integer> oProcessMap = new HashMap<Action,Integer>();
//            for (Action oAct : o.getObligedActions()) {
//                oProcessMap.put(oAct, 0);
//            }
//            obligations.put(o,oProcessMap);
//        }
    }
    
    private void addTransRecordToCollection(TransactionRecord r) {
        //if (r.endsWith("]")) { r = r.substring(1, r.length()-1);} else { r = r.substring(1);}
        //String[] rSplit = r.split(",");

        //Prolog Records Add: Transaction_Records
        //PrologInterface.assertFact("recordRequest", new Term[]{new Atom("peer"+peerID),new Atom(rSplit[0]),new Atom(rSplit[1]),new org.jpl7.Integer(Integer.parseInt(rSplit[2])),new Atom(rSplit[3]),new Atom(rSplit[4]),new Variable()});

        transactions.add(r);
        //PrologInterface.assertFact("recordRequest", PrologInterface.stringToTransRecord(peerID, r));
    }

//    public Term generatePolicy(String polTarget, String dataItem, int reward, int penalty, int nestingLevel, boolean canSelfTarget) {
//        String modality = "P"; if (rng.nextInt(4) == 0) { modality = "F";}
//
//        if (polTarget.equals("")) {
//            int polTargetID = rng.nextInt(Network.size());
//            polTarget = "peer" + polTargetID;
//            if (rng.nextInt(50) == 0) {
//                polTarget = "any";
//            } else if (rng.nextInt(20) == 0) {
//                HashSet<Term> result = PrologInterface.runQuery("group", new Term[] { new Variable("G"), new Variable("_") }, "G");
//                if (result.size() > 0) {
//                    Term[] resultArray = result.toArray(new Term[0]);
//                    polTarget = resultArray[rng.nextInt(resultArray.length)].toString();
//                }
//            }
//        }
//
//        if (dataItem.equals("")) {
//            dataItem = ownedData.get(rng.nextInt(ownedData.size()));
//            if (rng.nextInt(25) == 0) {
//                dataItem = "any";
//            }
//        }
//
//        //Conditions: [Con1,Con2]
//        Term[] conditions = new Term[0];
//        if (rng.nextInt(25) == 0) {
//            int numConditions = rng.nextInt(100);
//            if (numConditions <= 75) {
//                numConditions = 1;
//            } else if (numConditions <= 95) {
//                numConditions = 2;
//            } else {
//                numConditions = 3;
//            }
//
//            conditions = new Term[numConditions];
//            for (int j = 0; j < numConditions; j += 1) {
//                Term con = null;
//
//                int opType = rng.nextInt(6);
//                String op = "";
//                switch (opType) {
//                    case 0:
//                        op = ">";
//                        break;
//                    case 1:
//                        op = ">=";
//                        break;
//                    case 2:
//                        op = "<";
//                        break;
//                    case 3:
//                        op = "=<";
//                        break;
//                    case 4:
//                        op = "=:=";
//                        break;
//                    case 5:
//                        op = "=\\=";
//                        break;
//                }
//
//                int n = 0;
//                int conType = rng.nextInt(5);
//                Term date = null;
//                switch (conType) {
//                    case 0: { // Operator(recordsAccessed(ID,Data),N)
//                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
//                        con = new Compound("recordsAccessed", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
//                        n = rng.nextInt(100) + 1;
//                        break;
//                    }
//                    case 1: { // Operator(recordsRequested(ID,Data),N)
//                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
//                        con = new Compound("recordsRequested", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
//                        n = rng.nextInt(100) + 1;
//                        break;
//                    }
//                    case 2: { // Operator(requests(ID,Data),N)
//                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
//                        con = new Compound("requests", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
//                        n = rng.nextInt(100) + 1;
//                        break;
//                    }
//                    case 3: { // Operator(lastRequest(ID,Data),N)
//                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
//                        con = new Compound("lastRequest", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
//                        date = new Compound("date", new Term[] { new org.jpl7.Integer(2017), new org.jpl7.Integer(rng.nextInt(12)+1), new org.jpl7.Integer(rng.nextInt(31)+1), new org.jpl7.Integer(rng.nextInt(24)), new org.jpl7.Integer(rng.nextInt(60)),new org.jpl7.Float(0.0f),new org.jpl7.Integer(0),new Atom("local"),new Atom("false")});
//                        n = rng.nextInt(100) + 1;
//                        break;
//                    }
//                    case 4: { // Operator(lastAccess(ID,Data),N)
//                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
//                        con = new Compound("lastAccess", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
//                        date = new Compound("date", new Term[] { new org.jpl7.Integer(2017), new org.jpl7.Integer(rng.nextInt(12)+1), new org.jpl7.Integer(rng.nextInt(31)+1), new org.jpl7.Integer(rng.nextInt(24)), new org.jpl7.Integer(rng.nextInt(60)),new org.jpl7.Float(0.0f),new org.jpl7.Integer(0),new Atom("local"),new Atom("false")});
//                        n = rng.nextInt(100) + 1;
//                        break;
//                    }
//                    case 5: { // Operator(year(Year),N)
//                        con = new Compound("year", new Term[0]);
//                        n = rng.nextInt(2) + 2017;
//                        break;
//                    }
//                    case 6: { // Operator(month(Month),N)
//                        con = new Compound("month", new Term[0]);
//                        n = rng.nextInt(12) + 1;
//                        break;
//                    }
//                    case 7: { // Operator(day(Day),N)
//                        con = new Compound("day", new Term[0]);
//                        n = rng.nextInt(31) + 1;
//                        break;
//                    }
//                    case 8: { // Operator(hour(Hour),N)
//                        con = new Compound("hour", new Term[0]);
//                        n = rng.nextInt(24);
//                        break;
//                    }
//                    case 9: { // Operator(minute(Minute),N)
//                        con = new Compound("minute", new Term[0]);
//                        n = rng.nextInt(60);
//                        break;
//                    }
//                }
//
//                if (date == null) {
//                    conditions[j] = new Compound(op, new Term[] { con, new org.jpl7.Integer(n) });
//                } else {
//                    conditions[j] = new Compound(op, new Term[] { con, date });
//                }
//                //conditions[j] = Util.textToTerm(op+"("+n+")");
//            }
//        }
//
//        //Obligations: [ [[Obl1], Penalty1, Duration1], [[Obl2,Obl3], Penalty2, Duration2] ]
//        Term[] preObligations = new Term[0];
//        Term[] obligations = new Term[0];
//        if (rng.nextInt(5) == 0) {
//            int numObligations = rng.nextInt(100);
//            if (numObligations <= 75) {
//                numObligations = 1;
//            } else if (numObligations <= 95) {
//                numObligations = 2;
//            } else {
//                numObligations = 3;
//            }
//            int numPreObligations = rng.nextInt(100);
//            if (numPreObligations <= 80) {
//                numPreObligations = 0;
//            } else {
//                numPreObligations = 1;
//            }
//
//            preObligations = new Term[numPreObligations];
//            obligations = new Term[numObligations];
//            for (int j = 0; j < (numObligations + numPreObligations); j += 1) {
//                Term[] obl = new Term[3];
//
//                boolean preObl = (j >= numObligations);
//                int oblType = rng.nextInt(4);
//                if (oblType == 2 && nestingLevel >= 5) {
//                    oblType = rng.nextInt(3);
//                    if (oblType == 2) {
//                        oblType = 3;
//                    }
//                } //If this policy is nested more than 5 times, prevent further "adopt" obligations
//                if (preObl && oblType == 3) {
//                    oblType = rng.nextInt(3);
//                } //Pre obligations don't use inform(), as this is implicitly built into the mechanism
//                switch (oblType) {
//                    case 0: { // obtain(Data,Quantity)
//                        String dataItemObl = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        Term oblTerm = new Compound("obtain", new Term[] { new Atom(dataItemObl), new org.jpl7.Integer(rng.nextInt(10) + 1) });
//                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
//                        obl[1] = new org.jpl7.Integer((rng.nextInt(10) + 1));
//                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
//                        break;
//                    }
//                    case 1: { // provide(Data,Quantity,Peer)
//                        String dataItemObl = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
//                        String peerTargetObl = "peer" + rng.nextInt(Network.size());
//                        Term oblTerm = new Compound("provide", new Term[] { new Atom(dataItemObl), new org.jpl7.Integer(rng.nextInt(10) + 1), new Atom(peerTargetObl) });
//                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
//                        obl[1] = new org.jpl7.Integer((rng.nextInt(10) + 1));
//                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
//                        break;
//                    }
//                    case 2: { // adopt(Policy,Duration)
//                        int oblPenalty = (rng.nextInt(10) + 1);
//                        Term oblTerm = new Compound("adopt", new Term[] { generatePolicy("","",0,oblPenalty,nestingLevel + 1,true), new org.jpl7.Integer(rng.nextInt(5) + 1) });
//                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
//                        obl[1] = new org.jpl7.Integer(oblPenalty);
//                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
//                        break;
//                    }
//                    case 3: { // inform(Peer)
//                        String peerTargetObl = "peer" + peerID; //This inform will target the policy owner 80% of the time
//                        if (rng.nextInt(5) == 0) {
//                            peerTargetObl = "peer" + rng.nextInt(Network.size());
//                        }
//                        Term oblTerm = new Compound("inform", new Term[] { new Atom(peerTargetObl) });
//                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
//                        obl[1] = new org.jpl7.Integer((rng.nextInt(10) + 1));
//                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
//                        break;
//                    }
//                }
//                if (preObl) {
//                    preObligations[j - numObligations] = Util.listToTermArray(obl[0])[0];
//                } else {
//                    obligations[j] = Util.termArrayToList(obl);
//                }
//            }
//        }
//        
//        boolean selfPol = false;
//        if (canSelfTarget && rng.nextInt(10) == 0) { selfPol = true;}
//        
//        int polReward = reward;
//        if (reward == -1) {
//            reward = rng.nextInt(10);
//        }
//        int polPenalty = penalty;
//        if (penalty == -1) {
//            penalty = rng.nextInt(10);
//        }
//        
//        /*if (conditions.length > 0) {
//            System.out.print(peerID+": "+modality+", "+polTarget+", "+dataItem+"\n\t");
//            for (Term v : conditions) { System.out.print(v+", ");}
//            System.out.print("\n\t");
//            for (Term v : preObligations) { System.out.print(v+", ");}
//            System.out.print("\n\t");
//            for (Term v : obligations) { System.out.print(v+", ");}
//            System.out.println("");
//        }*/
//        return Util.termArrayToList(new Term[] { new Atom(modality), new Atom(polTarget), new Atom(dataItem), Util.termArrayToList(conditions), Util.termArrayToList(preObligations), Util.termArrayToList(obligations), new Atom(""+selfPol), new org.jpl7.Integer(polReward), new org.jpl7.Integer(polPenalty) });
//    }

    public String generateDataElement() {
        String dataElement = "";
        for (int i = 0; i < DATA_ELEMENT_LENGTH; i += 1) {
            switch (rng.nextInt(3)) {
                case 0: //Uppercase (65-90)
                    dataElement += "" + ((char) (rng.nextInt(26) + 65));
                    break;
                case 1: //Lowercase (97-122)
                    dataElement += "" + ((char) (rng.nextInt(26) + 97));
                    break;
                case 2: //Number
                    dataElement += rng.nextInt(10);
                    break;
            }
        }
        //System.out.println(dataElement);
        return dataElement;
    }

    //private DataPackage assembleDataPackage(HashMap<String, HashSet<Term>> transRecords, long msgID) {
    private DataPackage assembleDataPackage(HashSet<DataElement> data, HashSet<TransactionRecord> transRecords, long msgID) {
        DataPackage dataPackage = new DataPackage();
//        for (Term tR : transRecords) {
//            String r = tR.toString();
//            String rBody = r.substring(r.indexOf("(") + 1, r.length() - 1);
//            String[] rSplit = rBody.split(",");
//            for (int j = 0; j < rSplit.length; j += 1) {
//                rSplit[j] = rSplit[j].trim();
//            }
//            PrologInterface.assertFact("recordRequest", PrologInterface.stringToTransRecord(peerID, r));
//            dataPackage.transactionRecords.add(r);
//            
//            if (rSplit[rSplit.length-1].equals("true")) {
//                int dataQuantity = Integer.parseInt(rSplit[4]);
//                if (dataQuantity > 0) {
//                    for (int j = 0; j < dataQuantity; j += 1) {
//                        String dBody = "";
//                        for (DataElement dE : dataCollection) {
//                            if (dE.dataID.equals(rSplit[3])) {
//                                dBody = dE.data;
//                            }
//                        }
//                        dataPackage.dataItems.add(new DataElement(rSplit[3], dBody));
//                    }
//                }
//            }
//        }
//        if (obligations != null) {
//            dataPackage.obligations.addAll(obligations);
//        }
        if (data != null) { dataPackage.dataItems.addAll(data);}
        if (transRecords != null) { dataPackage.transactionRecords.addAll(transRecords);}
        dataPackage.encrypt();
        return dataPackage;
    }
    
    private HashSet<TransactionRecord> generateTransactionRecords() {
        HashSet<TransactionRecord> transRecords = new HashSet<TransactionRecord>();
        
        //Store all transaction records;
        for (TransactionRecord r : transRecords) {
            addTransRecordToCollection(r);
        }
        
        return transRecords;
    }

    private Node getPeerByID(String peerID) {
        Node n = null;

        for (int p = 0; p < Network.size(); p += 1) {
            if (("peer" + Network.get(p).getID()).equals(peerID)) {
                n = Network.get(p);
                break;
            }
        }

        return n;
    }

    public Object clone() {
        DataExchange dataExchange = null;
        try {
            dataExchange = (DataExchange) super.clone();
        } catch (CloneNotSupportedException e) {
            // Never happens
        }
        return dataExchange;
    }
}
