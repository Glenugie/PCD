package com.pcd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Term;
import org.jpl7.Util;
import org.jpl7.Variable;

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
    private final boolean DATA_REQUEST_FORWARDING = true;

    //private SimpleDateFormat prologDateFormat;
    private Random rng;
    private Term[] masterDataArray;
    protected long peerID;
    protected long peerPublicKey;
    private long peerPrivateKey;
    private String connectionType;

    private boolean disconnecting;

    protected HashMap<String, Node> overlayNetwork; //Maps PeerIDs to Peers

    protected HashMap<String, Integer> desiredData;
    protected HashMap<String, Integer> pendingData;
    protected HashMap<String, Integer> receivedData;
    protected ArrayList<String> generatedData;
    protected ArrayList<String> ownedData;
    protected HashMap<String, Integer> dataValue;

    protected ArrayList<DataPolicy> policies;
    protected HashMap<DataPolicy,HashMap<Action,Integer>> obligations;
    //protected ArrayList<ArrayList<Action>> obligedActions;
    protected HashSet<DataElement> dataCollection;

    //private int activeRequests;
    protected int peerBudget, startingBudget;

    protected int dataReceived;
    protected long disconnectTime;
    protected int disconnectType;
    protected int penaltyRounds;
    
    private boolean altruistic;
    private boolean fair;
    private boolean faulty;

    protected ArrayList<P2PMessage> messages;

    public DataExchange(String prefix) {
        rng = new Random(CommonState.r.getLastSeed());
        //prologDateFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss,0,z,'false'");

        disconnecting = false;
        dataReceived = 0;
        disconnectTime = -1;
        disconnectType = -1;
        penaltyRounds = 0;
        
        altruistic = false;
        fair = false;
        faulty = false;
    }
    
    private void firstCycleInit(Node node, int protocolID) {
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);
        
        for (int i = 0; i < linkable.degree(); i += 1) {
            overlayNetwork.put("peer" + linkable.getNeighbor(i).getID(), linkable.getNeighbor(i));
            //PrologInterface.assertFact("connected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + linkable.getNeighbor(i).getID()) });
        }
        
//        if (PrologInterface.confTopology == 1) { // Mesh Network
//            int numConnectionsToKill = rng.nextInt((PrologInterface.confTopologyVal/2));
//            for (int i = 0; i < numConnectionsToKill; i += 1) {                
//                overlayNetwork.remove(((String) overlayNetwork.keySet().toArray()[0]));
//            }
//        }
    }

    public void nextCycle(Node node, int protocolID) {
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);

        //On the first cycle, inserts information about neighbours into prolog
        if (peersim.core.CommonState.getTime() == 0) {
            firstCycleInit(node,protocolID);
        }

        //Process Messages
        dataReceived = 0;
        processMessages(node, protocolID);

        // Policy Processing
        for (int i = policies.size() - 1; i >= 0; i -= 1) {
            DataPolicy pol = policies.get(i);
            if (pol.duration != -99) {
                pol.duration -= 1;
                if (pol.duration <= 0) {
                    policies.remove(pol);
                    PrologInterface.retractFact("policy", new Term[] { new Atom("peer" + peerID), pol.getPrologTerm() });

                    //Mark any obligations associated with enforcing Policy as fulfilled
                }
            }
        }

        // Obligation Processing
        processObligations(node, protocolID);

        peerBudget -= PrologInterface.confCycleCost;
        //At the end of each cycle, need to reason on remaining value from desired data, vs the predicted cost of remaining in the network to get it
        if (desiredData.size() > 0 && linkable.degree() > 0 && peerBudget >= PrologInterface.confCycleCost && penaltyRounds == 0) {
            for (String gD : generatedData) {
                if (!ownedData.contains(gD)) { ownedData.add(gD);}
                dataCollection.add(new DataElement(gD,generateDataElement()));
            }
            
            Node bestNeighbour = null;
            String bestData = "";
            double bestDataVal = 0.0;
            for (String d : desiredData.keySet()) {
                double bestChance = 0.0;
                Node localBestNeighbour = null;
                for (String n : overlayNetwork.keySet()) {
                    double prob = 0.0;
                    
                    //TODO: Implement the below probability calculation
                    //If N has previously given at least Q items of D to R, Prob = 1
                    //Else If N has previously given less than Q items of D to R, Prob = 0.95
                    //Else If N has never been asked for D, Prob = 0.5
                    //Else If N has previously had D but not given it to R, Prob = Min(0.75,0.25 + (0.05 * Number Of Cycles Since Last Request))
                    //Else If N has previously not had D, Prob = 0.05
                    //Else, Prob = 0
                    
                    //IMPLICIT: if (Own >= Obl.Data_Quantity of Obl.Data_Item) { completionProb *= 1.0;}    
                    /*if (PrologInterface.runGroundQuery("recordRequest", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom("peer"+peerID), new Atom(obl.payload[0]), new Variable("_"), new Variable("_"), new Atom("true")})) { completionProb *= 0.95;}
                    if (PrologInterface.runGroundQuery("hasData", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom(obl.payload[0])})) { completionProb *= 0.75;}
                    else if (PrologInterface.runGroundQuery("possibleData", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom(obl.payload[0])})) { completionProb *= 0.5;}
                    else if (!PrologInterface.runGroundQuery("possibleData", new Term[]{ new Atom("peer"+peerID), new Variable("_"), new Atom(obl.payload[0])})) { completionProb *= 0.05;}
                    else { completionProb *= 0.0;}*/
                    
                    if (prob > bestChance) { 
                        bestChance = prob;
                        localBestNeighbour = overlayNetwork.get(n);
                    }
                }
                //TODO: This should also calculate penalties of policies this will break/rewards of obligations this will fulfil
                int costOfRequest = PrologInterface.confCycleCost * AVG_TRANS_LENGTH;
                double dataVal = ((getDataValue(d) * desiredData.get(d)) * bestChance) - costOfRequest;
                if (dataVal > bestDataVal) {
                    bestData = d;
                    bestNeighbour = localBestNeighbour;
                }
            }
            
            if (!bestData.equals("") && bestNeighbour != null) {
                //TODO: Penalise self here for any policies broken by this request
                
                String dataItem = bestData;
                Node peer = bestNeighbour;
                DataExchange n = (DataExchange) peer.getProtocol(protocolID);

                //Send Desired_Data[RND],1 to Neighbour[RND] as "Data_Request"
                int quantity = desiredData.get(dataItem); if (quantity == -1) { quantity = 10;}
                n.sendMessage(protocolID, peer, node, "DATA_REQUEST", new Object[] { dataItem, quantity, 0 });

                pendingData.put(dataItem, desiredData.get(dataItem));
                desiredData.remove(dataItem);
            }
            //Need to reason before disconnecting, in some situations there may be a good enough reason to stay a while longer (incoming pay-off)
        }

        //If settings permit (and not currently penalised), forms new connections up to the degree of connectedness in config file
//        if (allowNewConnections && overlayNetwork.size() < idealPeers && penaltyRounds == 0) {
//            Node randomPeer = Network.get(rng.nextInt(Network.size()));
//            //If the random peer is online, and not already connected
//            if (!overlayNetwork.containsKey("peer" + randomPeer.getID()) && randomPeer.isUp()) {
//                overlayNetwork.put("peer" + randomPeer.getID(), randomPeer);
//            }
//        }

        //Query q = new Query(new Compound("listing", new Term[]{new Compound("noRequest",new Term[0])})); q.oneSolution(); q.close();
        PrologInterface.retractFact("noRequest", new Term[] { new Atom("peer"+peerID), new Variable("_"), new Variable("_"), new org.jpl7.Integer(peersim.core.CommonState.getTime())});
        penaltyRounds = Math.max(0, (penaltyRounds - 1));
    }

    private void processMessages(Node node, int protocolID) {
        for (int i = messages.size() - 1; i >= 0; i -= 1) {
            P2PMessage msg = messages.get(i);
            if (msg.time <= peersim.core.CommonState.getTime()
                    && (penaltyRounds == 0 || (penaltyRounds > 0 && (msg.type.equals("DATA_REQUEST") || msg.type.equals("REJECT_POLICIES") || msg.type.equals("RECORD_INFORM") || msg.type.equals("OBLIGATION_COMPLETE"))))) {
                DataExchange n = (DataExchange) msg.sender.getProtocol(protocolID);

                switch (msg.type) {
                    case "PEER_DOWN":
                        //Peer_Down -> Sender_ID, Data_Item
                        if (overlayNetwork.containsKey("peer" + msg.sender.getID())) {
                            overlayNetwork.remove("peer" + msg.sender.getID());
                            PrologInterface.retractFact("connected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + msg.sender.getID()) });
                            PrologInterface.assertFact("peerOffline", new Term[] { new Atom("peer"+peerID), new Atom("peer" + msg.sender.getID()) });
                        }

                        if (pendingData.containsKey(msg.payload[0])) {
                            desiredData.put((String) msg.payload[0], pendingData.get(msg.payload[0]));
                            pendingData.remove((String) msg.payload[0]);
                            //activeRequests -= 1;
                        }
                        break;
                    case "DATA_REQUEST":
                        //Data_Request -> Sender_ID, Data_Item, Data_Quantity, Hops		        		
                        if (ownedData.contains(msg.payload[0])) { //If Data_Item in Owned_Data
                            HashSet<Term> relPolicies = new HashSet<Term>();
                            //Query Prolog: Relevant Policies for Sender_ID and Data_Item -> Rel_Policies
                            HashSet<Term> result = PrologInterface.runQuery("relPolicies", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]), new Variable("L") }, "L");
                            
                            HashMap<DataPolicy,Integer> rPols = new HashMap<DataPolicy,Integer>();                            
                            for (Term pol : result) {
                                DataPolicy relPol = new DataPolicy(peerID,pol,-1,false);
                                if (relPol.mod.equals("P") || relPol.mod.equals("F")) {
                                    rPols.put(relPol, 0);
                                }
                            }
                            
                            HashSet<PolicySet> relPolSets = new HashSet<PolicySet>();
                            for (DataPolicy pol : rPols.keySet()) {
                                if (rPols.get(pol) == 0) {
                                    PolicySet polSet = new PolicySet();
                                    polSet.addPrimary(pol, policyValueProvider(pol), 0.0);
                                    rPols.replace(pol, 1);
                                    
                                    // Identify policies with identical conditions (i.e., completely overlapping with) to Pol, and add as secondary policies to PolSet
                                    for (DataPolicy sPol : rPols.keySet()) {
                                        if (rPols.get(sPol) == 0 && pol.equals(sPol)) {
                                            polSet.addPrimary(sPol, policyValueProvider(sPol), 0.0);
                                            rPols.replace(sPol, 1);
                                        }
                                    }
                                    
                                    // Identify non-mutually exclusive policies, and add as tertiary policies to PolSet
                                    HashSet<DataPolicy> psPols = new HashSet<DataPolicy>(); psPols.add(pol); psPols.addAll(polSet.getSecondary());
                                    for (DataPolicy polPS : psPols) {
                                        for (DataPolicy tPol : rPols.keySet()) {
                                            //TODO: DataPolicy.mutuallyExclusive()
                                            if (rPols.get(tPol) == 0 && !polSet.getSecondary().contains(tPol) && !tPol.mutuallyExclusive(polPS)) {
                                                // Identify policies with conditions that are NOT mutually exclusive to Pol-PS, and add as tertiary policy to PolSet (if not already present)  
                                                polSet.addSecondary(tPol, policyValueProvider(tPol), 0.0);
                                            }
                                        }
                                    }
                                    // TODO: Compute estimated value of every policy in set (for R)                                    
                                    
                                    // Value of set computed and stored as the combined profit of primary/secondary policies
                                    polSet.computeValue();
                                    if (polSet.providerValue > 0 && polSet.worstProviderValue > 0 && polSet.containsPermit()) {       
                                        //TODO: Add polSet to some kind of register to prove it is a valid offer
                                        relPolSets.add(polSet);
                                    }
                                }
                            }

                            //Send Data_Item, Data_Quantity, Rel_Policies to Sender_ID as "Policy_Inform"   
                            if (relPolSets.size() > 0) {                                
                                n.sendMessage(protocolID, msg.sender, node, "POLICY_INFORM", new Object[] { (String) msg.payload[0], new Integer((int) msg.payload[1]), relPolSets });
                            } else {
                                //Generates a Dataless DataPackage
                                // requestData( Provider, Requestor, PolicySet, Data, Quantity, Records, Reward, Penalty)
                                HashSet<Term> transRecords = PrologInterface.runQuery("generateDatalessPackage", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]), new org.jpl7.Integer((Integer) msg.payload[1]), new Variable("R") }, "R");
                                DataPackage dataPackage = assembleDataPackage(transRecords, null, msg.sender.getID());
                                
                                n.sendMessage(protocolID, msg.sender, node, "NO_ACCESS", new Object[] { (String) msg.payload[0], dataPackage });
                            }
                        } else { //Peer doesn't have the data, attempting to find neighbour who does
                            Node[] nodeTargets = new Node[0];
                            /*if (((int) msg.payload[2]) < maxDataHops) {
                                HashSet<Term> potentialTargetsSet = PrologInterface.runQuery("findData", new Term[] { new Atom("peer" + peerID), new Atom((String) msg.payload[0]), new Variable("L") }, "L");
                                Term potentialTargetsTerm = (Term) potentialTargetsSet.toArray()[0];
                                String[] potentialTargets = Util.atomListToStringArray(potentialTargetsTerm);

                                ArrayList<Node> potentialNodeTargets = new ArrayList<Node>();
                                for (int j = 0; j < potentialTargets.length; j += 1) {
                                    if (overlayNetwork.containsKey(potentialTargets[j])) {
                                        potentialNodeTargets.add(overlayNetwork.get(potentialTargets[j]));
                                    }
                                }
                                nodeTargets = potentialNodeTargets.toArray(new Node[0]);
                            }*/
                            
                            //Generates a Dataless DataPackage
                            HashSet<Term> transRecords = PrologInterface.runQuery("generateDatalessPackage", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]), new org.jpl7.Integer((Integer) msg.payload[1]), new Variable("R") }, "R");
                            DataPackage dataPackage = assembleDataPackage(transRecords, null, msg.sender.getID());
                            n.sendMessage(protocolID, msg.sender, node, "NO_DATA", new Object[] { (String) msg.payload[0], nodeTargets, msg.payload[2], dataPackage }); //Send Data_Item to Sender_ID as "No_Data"
                        }
                        
//                        if (DATA_REQUEST_FORWARDING && !selfishPeer && ((int) msg.payload[2]) < maxDataHops) {
//                            Node[] nodeTargets = new Node[0];
//                            if (((int) msg.payload[2]) < maxDataHops) {
//                                HashSet<Term> potentialTargetsSet = PrologInterface.runQuery("findData", new Term[] { new Atom("peer" + peerID), new Atom((String) msg.payload[0]), new Variable("L") }, "L");
//                                Term potentialTargetsTerm = (Term) potentialTargetsSet.toArray()[0];
//                                String[] potentialTargets = Util.atomListToStringArray(potentialTargetsTerm);
//
//                                ArrayList<Node> potentialNodeTargets = new ArrayList<Node>();
//                                for (int j = 0; j < potentialTargets.length; j += 1) {
//                                    if (overlayNetwork.containsKey(potentialTargets[j])) {
//                                        potentialNodeTargets.add(overlayNetwork.get(potentialTargets[j]));
//                                    }
//                                }
//                                nodeTargets = potentialNodeTargets.toArray(new Node[0]);
//                            }
//                            
//                            for (Node nT : nodeTargets) {
//                                n.sendMessage(protocolID, nT, msg.sender, "DATA_REQUEST", new Object[] { (String) msg.payload[0], new Integer((int) msg.payload[1]), (((int) msg.payload[2]) + 1) });                                
//                            }
//                        }
                        break;
                    case "NO_DATA":
                        //No_Data -> Sender_ID, Data_Item, Potential_Targets, Hops, Data_Package[]
                        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records
                        
                        processIncomingDataPackage((DataPackage) msg.payload[3],msg.sender,protocolID);
                        
                        //Prolog State of Affairs Add: Sender_ID does not have Data_Item
                        PrologInterface.assertFact("noData", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]) });

                        Node[] potentialTargets = new Node[0];
                        try {
                            potentialTargets = (Node[]) msg.payload[1];
                        } catch (ClassCastException e) {
                            //Could not cast targets, malformed message
                        }                
                        
                        int targetNum = 0;
                        boolean dataRequestSent = false;

                        if (!dataRequestSent) {
                            if (pendingData.containsKey(msg.payload[0])) {
                                desiredData.put((String) msg.payload[0], pendingData.get((String) msg.payload[0]));
                                pendingData.remove((String) msg.payload[0]);
                                //activeRequests -= 1;	
                            }
                        }
                        break;
                    case "NO_ACCESS":
                        //No_Access -> Sender_ID, Data_Item, Data_Package[]
                        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records
                        
                        processIncomingDataPackage((DataPackage) msg.payload[1],msg.sender,protocolID);
                        
                        if (pendingData.containsKey(msg.payload[0])) {
                            desiredData.put((String) msg.payload[0], pendingData.get((String) msg.payload[0]));
                            pendingData.remove((String) msg.payload[0]);
                            //activeRequests -= 1;

                            //Prolog State of Affairs Add: Sender_ID does not have Data_Item
                            PrologInterface.assertFact("noAccess", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]) });
                        }
                        break;
                    case "POLICY_INFORM":
                        //Policy_Inform -> Sender_ID, Data_Item, Data_Quantity, HashSet<PolicySet> relPolicySets
                        HashSet<PolicySet> relPolicySets = null;
                        try {
                            relPolicySets = (HashSet<PolicySet>) msg.payload[2];
                        } catch (ClassCastException e) {
                            //Could not cast policies, malformed message
                        }

                        if (relPolicySets == null) {
                            if (pendingData.containsKey(msg.payload[0])) {
                                desiredData.put((String) msg.payload[0], pendingData.get((String) msg.payload[0]));
                                pendingData.remove((String) msg.payload[0]);
                                //activeRequests -= 1;
                            }
                            break;
                        } else {                            
                            //For all relPolicies, determine which hold for your current records. Evaluate all of these only (the others are irrelevant)
                            //If none are left, then halt this transaction as it will produce no meaningful result
                            HashSet<PolicySet> acceptablePolicySets = new HashSet<PolicySet>();
                            for (PolicySet polSet : relPolicySets) {
                                for (DataPolicy pPol : polSet.getPrimary()) {
                                    polSet.addPrimary(pPol, null, policyValueRequestor(pPol,(String) msg.payload[0],(int) msg.payload[1]));
                                }
                                for (DataPolicy sPol : polSet.getSecondary()) {
                                    polSet.addSecondary(sPol, null, policyValueRequestor(sPol,(String) msg.payload[0],(int) msg.payload[1]));
                                }
                                polSet.computeValue();                                

                                boolean optimal = true;       
                                for (PolicySet pSetComp : relPolicySets) {
                                    if (pSetComp.providerValue > polSet.providerValue && pSetComp.requestorValue > polSet.requestorValue) {
                                        optimal = false;
                                        break;
                                    }
                                }                        
                                
                                //FUTURE: The requestor could here try to mitigate unprofitable tertiary policies
                                if (polSet.requestorValue > 0 && polSet.worstRequestorValue > 0 && optimal) {
                                    acceptablePolicySets.add(polSet);
                                }
                            }
                            //System.out.println(relPolicySets.size()+" => "+acceptablePolicySets.size());
                            
                            //TODO: Likelihood of getting a better offer from elsewhere
                            
                            if (acceptablePolicySets.size() > 0) {
                                PolicySet bestSet = null; double bestValue = 0.0;
                                for (PolicySet pSet : acceptablePolicySets) {
//                                    if (!selfishPeer) {
//                                        double profitRatioFairness = 1.0;
//                                        if (pSet.providerValue > pSet.requestorValue) { profitRatioFairness = Math.abs(1-(pSet.providerValue / pSet.requestorValue));}
//                                        else { profitRatioFairness = Math.abs(1-(pSet.requestorValue / pSet.providerValue));}
//                                        if (bestSet == null || profitRatioFairness < bestValue || (profitRatioFairness == bestValue && pSet.requestorValue > bestSet.requestorValue)) {
//                                            bestSet = pSet;
//                                            bestValue = profitRatioFairness;
//                                        }             
//                                    } else {
//                                        if (bestSet == null || pSet.requestorValue > bestValue) {
//                                            bestSet = pSet;
//                                            bestValue = pSet.requestorValue;
//                                        }
//                                    }
                                }                                

                                HashSet<Term> inactiveConditions = PrologInterface.runQuery("inactiveConditions", new Term[] { bestSet.getPrologTerm(), new Variable("C") }, "C");
                                if (inactiveConditions.size() == 0) {
                                    // Get relevant records, send RECORD_INFORM
                                    HashSet<String> relRecords = new HashSet<String>();
                                    HashSet<Term> result = PrologInterface.runQuery("relRecords", new Term[] { new Atom("peer" + peerID), bestSet.getPrologTerm(), new Variable("R") }, "R");
                                    for (Term t : result) {
                                        relRecords.add(t.toString());
                                    }
    
                                    //Send Data_Item, Data_Quantity, Rel_Records to Sender_ID as "Record_Inform"
                                    n.sendMessage(protocolID, msg.sender, node, "RECORD_INFORM", new Object[] { (String) msg.payload[0], (Integer) msg.payload[1], bestSet, relRecords });
                                } else {
                                    // TODO: Send WAIT and add appropriate actions to goal list
                                }
                            } else if (relPolicySets.size() > 0) {
                                //Send Data_Item to Sender_ID as "Reject_Policies"
                                n.sendMessage(protocolID, msg.sender, node, "REJECT_POLICIES", new String[] { (String) msg.payload[0] });
                                if (pendingData.containsKey(msg.payload[0])) {
                                    desiredData.put((String) msg.payload[0], pendingData.get((String) msg.payload[0]));
                                    pendingData.remove((String) msg.payload[0]);
                                    //activeRequests -= 1;

                                    //Prolog State of Affairs Add: Rejected Sender_ID policies for Data_Item
                                    PrologInterface.assertFact("polRejected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom("peer" + peerID), new Atom((String) msg.payload[0]) });
                                    
                                    //Don't request this data from this provider for X (10?) turns. This mark gets cleared if the your personal value for this data is changed
                                    PrologInterface.retractFact("noRequest", new Term[] { new Atom("peer"+peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]), new Variable("_")});
                                    PrologInterface.assertFact("noRequest", new Term[] { new Atom("peer"+peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]), new org.jpl7.Integer(peersim.core.CommonState.getTime() + 10)});
                                }
                            }
                        }
                        break;
                    case "REJECT_POLICIES":
                        //Reject_Policies -> Sender_ID, Data_Item
                        //Prolog State of Affairs Add: Sender_ID rejected policies for Data_Item
                        PrologInterface.assertFact("polRejected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom((String) msg.payload[0]) });
                        break;
                    case "WAIT":
                        //TODO: WAIT message receipt
                        break;
                    case "CONFIRM_WAIT":
                        //TODO: CONFIRM_WAIT message receipt
                        break;
                    case "RECORD_INFORM":
                        //Record_Inform -> Sender_ID, Data_Item, Data_Quantity, Chosen_PolicySet, Rel_Records
                        HashSet<String> relRecords = null;
                        try {
                            relRecords = (HashSet<String>) msg.payload[3];
                        } catch (ClassCastException e) {
                            //Could not cast records, malformed message
                        }
                        
                        PolicySet chosenPolicySet = null;
                        try {
                            chosenPolicySet = (PolicySet) msg.payload[2];
                        } catch (ClassCastException e) {
                            //Could not cast policy set, malformed message
                        }

                        if (relRecords == null) {
                            n.sendMessage(protocolID, msg.sender, node, "MALFORMED_RECORDS", new String[] { (String) msg.payload[0] });
                            break;
                        } else if (chosenPolicySet == null) {
                            n.sendMessage(protocolID, msg.sender, node, "INVALID_TRANSACTION", new String[] { (String) msg.payload[0] });
                            break;
                        } else {
                            for (String r : relRecords) {
                                PrologInterface.assertFact("recordRequest", PrologInterface.stringToTransRecord(peerID, r));
                            }       

                            //Query Prolog: Permit Sender_ID access to Data_Quantity of Data_item with Rel_Records -> Data_Package
                            HashMap<String, HashSet<Term>> prologDataPack = PrologInterface.runMultiVarQuery("requestData",
                                    new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), chosenPolicySet.getPrologTerm(), new Atom((String) msg.payload[0]), new org.jpl7.Integer((Integer) msg.payload[1]), new Variable("R"), new Variable("Rew"), new Variable("Pen") },
                                    new String[] { "R", "Rew", "Pen" });
                            HashSet<Term> transRecords = prologDataPack.get("R");
                            
                            //TODO: Apply reward/penalty to self
                            double reward = ((Term) prologDataPack.get("Rew").toArray()[0]).doubleValue();
                            double penalty = ((Term) prologDataPack.get("Pen").toArray()[0]).doubleValue();
                            
                            DataPackage dataPackage = assembleDataPackage(transRecords, chosenPolicySet.getObligations(), msg.sender.getID());

                            //Send Data_Item, Data_Package to Sender_ID as "Data_Result" 			
                            //n.sendMessage(msg.sender, node, "DATA_RESULT", new String[]{(String) msg.payload[0],msg.payload[0]+":[peer"+n.peerID+","+msg.payload[0]+","+dataPermitted+","+prologDateFormat.format(new Date().getTime())+","+(dataPermitted > 0)+"])"});
                            n.sendMessage(protocolID, msg.sender, node, "DATA_RESULT", new Object[] { (String) msg.payload[0], dataPackage });
                            //PrologInterface.assertFact("recordRequest", new Term[]{new Atom("peer"+peerID),new Atom("peer"+n.peerID),new Atom((String) msg.payload[0]),new org.jpl7.Integer(dataPermitted),new Atom(prologDateFormat.format(new Date().getTime())),new Atom(dataAllowed.toString())});

                            //TODO: Mark any obligations associated with providing Data_Quantity of Data_Item to Sender_ID as fulfilled
                        }
                        break;
                    case "MALFORMED_RECORDS":
                        //Malformed_Records -> Sender_ID, Data_Item
                        if (pendingData.containsKey(msg.payload[0])) {
                            desiredData.put((String) msg.payload[0], pendingData.get(msg.payload[0]));
                            pendingData.remove((String) msg.payload[0]);
                            //activeRequests -= 1;-
                        }
                        break;
                    case "INVALID_TRANSACTION":
                        //TODO: INVALID_TRANSACTION receipt
                        break;
                    case "DATA_RESULT":
                        //Data_Result -> Sender_ID, Data_Item, Data_Package[]
                        //Data_Package[] -> Data_Item, Data_Quantity, Transaction_Records

                        DataPackage dataPackage = (DataPackage) msg.payload[1];
                        processIncomingDataPackage(dataPackage,msg.sender,protocolID);

                        //activeRequests -= 1;
                        break;
                }

                Object test = messages.remove(i);
                if (test == null) {
                    System.err.println("ERROR");
                }
            }
        }
    }

    private void processObligations(Node node, int protocolID) {       
        if (obligations.size() > 0) {
            ArrayList<DataPolicy> toRemove = new ArrayList<DataPolicy>();
            for (DataPolicy obl : obligations.keySet()) {
                boolean fulfilled = true, violated = false;
                for (Action a : obligations.get(obl).keySet()) {
                    if (obligations.get(obl).get(a) != 2) {
                        fulfilled = false;
                        if (peersim.core.CommonState.getTime() >= a.expiry) {
                            violated = true;
                            penaltyRounds += obl.penalty;
                        }
                        break;
                    }
                }
                
                if (fulfilled) {
                    //TODO: If this was being done to access some data...
                    toRemove.add(obl);
                } else if (violated) {
                    System.out.println("Penalty of "+obl.penalty+" applied to peer"+peerID);
                    toRemove.add(obl);
                }
            }
            for (DataPolicy obl : toRemove) { obligations.remove(obl);}

            //If all obligations are marked as processed (or fulfilled), set all processed obligations to unprocessed
            boolean allProcessed = true;
            for (DataPolicy obl : obligations.keySet()) {
                for (Action a : obligations.get(obl).keySet()) {
                    if (obligations.get(obl).get(a) == 0) {
                        allProcessed = false;
                        break;
                    }
                }
                if (!allProcessed) {
                    break;
                }
            }
            if (allProcessed) {
                for (DataPolicy obl : obligations.keySet()) {
                    for (Action a : obligations.get(obl).keySet()) {
                        if (obligations.get(obl).get(a) == 1) {
                            obligations.get(obl).replace(a, 0);
                        }
                    }
                }
            }

            DataPolicy oblP = null;
            Action actP = null;
            for (DataPolicy obl : obligations.keySet()) { 
                ArrayList<Integer> times = new ArrayList<Integer>();
                for (Action a : obl.getObligedActions()) {
                    times.add(a.time);                 
                }
                Collections.sort(times);
                Collections.reverse(times);
                
                int cur = 0, total = 0;
                for (Integer t : times) {
                    if ((total-cur) < t) {
                        total += (t-(total-cur));
                    }
                    cur += 1;
                }
                
                //spareCycles = 
            }
            //TODO: Choose the best obligation to fulfil next
            
            /*
            int oblSetIndex = 0, oblIndex = 0;
            ObligationSet oblSet = null;
            Obligation obl = null;
            while (oblSetIndex < obligationSets.size() && obl == null) {
                oblSet = obligationSets.get(oblSetIndex);
                float oblCosts[] = obligationCost(oblSet);
                if (oblCosts[0] < oblCosts[1]) {
                    oblIndex = 0;
                    Obligation lastObl = null;
                    obl = null;
                    for (Obligation oblTest : oblSet.obligations.keySet()) {
                        if (oblSet.obligations.get(oblTest) == 0) {
                            obl = oblTest;
                            break;
                        }
                        oblIndex += 1;
                        lastObl = oblTest;
                    }
                    //If Obl type is INFORM and the Obligation prior to Obl in OblSet is unfulfilled
                    if (obl != null && obl.type.equals("inform") && lastObl != null && oblSet.obligations.get(lastObl) != 2) {
                        obl = null;
                    }
                }
                oblSetIndex += 1;
            }*/
            
            if (actP != null) {
                switch (actP.type) {
                    case "obtain": {
                        //[Data_Item, Data_Quantity] <- Obtain
                        int quantityDataOwned = 0;
                        if (ownedData.contains(actP.payload[0])) {
                            Term result = PrologInterface.runQueryFirstResult("numData", new Term[] { new Atom("peer" + peerID), new Atom(actP.payload[0]), new Variable("Z") }, "Z");
                            if (result != null) {
                                quantityDataOwned = result.intValue();
                            }
                        }
                        
                        int quantityNeeded = Integer.parseInt(actP.payload[1]) - quantityDataOwned;
                        if (quantityNeeded > 0) {
                            if (desiredData.containsKey(actP.payload[0])) {
                                desiredData.replace(actP.payload[0], desiredData.get(actP.payload[0]) + quantityNeeded);
                            } else {
                                desiredData.put(actP.payload[0], quantityNeeded);
                            }
                        } else {
                            obligations.get(oblP).replace(actP, 2); //Mark Obligation as fulfilled                            
                        }                        
                        break;
                    }    
                    case "provide":
                        //[Data_Item, Data_Quantity, Data_Recipient] <- Provide
                        int quantityDataOwned = 0;
                        if (ownedData.contains(actP.payload[0])) {
                            Term result = PrologInterface.runQueryFirstResult("numData", new Term[] { new Atom("peer" + peerID), new Atom(actP.payload[0]), new Variable("Z") }, "Z");
                            if (result != null) {
                                quantityDataOwned = result.intValue();
                            }
                        }

                        if (quantityDataOwned >= Integer.parseInt(actP.payload[1])) {
                            HashSet<Term> relPolicies = new HashSet<Term>();
                            //Prolog Query: Relevant Policies for Data_Recipient and Data_Item -> Rel_Policies
                            HashSet<Term> result = PrologInterface.runQuery("relPolicies", new Term[] { new Atom("peer" + peerID), new Atom(actP.payload[2]), new Atom(actP.payload[0]), new Variable("L") }, "L");
                            for (Term t : result) {
                                //relPolicies.add(PrologInterface.policyToTerm(t));
                                relPolicies.add(t);
                            }

                            //Send ["POLICY_INFORM", Data_Recipient, Data_Item, Data_Quantity, Rel_Policies]                        
                            if (relPolicies.size() > 0) {
                                Node n = getPeerByID(actP.payload[2]);
                                if (n != null) {
                                    ((DataExchange) n.getProtocol(protocolID)).sendMessage(protocolID, n, node, "POLICY_INFORM", new Object[] { actP.payload[0], Integer.parseInt(actP.payload[1]), relPolicies });
                                }
                            } else {
                                //Could change policies to allow this obligation to be fulfilled. Potentially
                            }
                        } else {
                            if (desiredData.containsKey(actP.payload[0])) {
                                desiredData.replace(actP.payload[0], desiredData.get(actP.payload[0]) + Integer.parseInt(actP.payload[1]));
                            } else {
                                desiredData.put(actP.payload[0], Integer.parseInt(actP.payload[1]));
                            }
                        }
                        break;

                    case "adopt":
                        //[Policy, Duration] <- Adopt
                        boolean conflict = false;
                        DataPolicy polToCheck = new DataPolicy(peerID,Util.textToTerm(actP.payload[0]),Integer.parseInt(actP.payload[1]),false);
                        //Prolog Query: Does adding Policy to my policy collection create a conflict -> Conflicted_Policies
                        HashSet<Term> result = PrologInterface.runQuery("polsViolatedBy", new Term[] { new Atom("peer" + peerID), polToCheck.getPrologTerm(), new Variable("L") }, "L");
                        if (result.size() > 0) { conflict = true;}
                        if (!conflict) {
                            //Prolog Query: Does Policy, enacted by Issuer_ID already exist -> Current_Policy_Expiry
                                //% Finds a policy which matches policy(Issuer_ID,Policy,_), extract the current duration of this policy
                            //If Current_Policy_Expiry is NULL
                                //Prolog Assert: Add Policy to Policy Collection with expiry in Duration cycles
                                //% policy(Self,Policy,Duration)
                            //Else if Current_Policy_Expiry is not infinite
                                //Prolog Assert: Add Duration cycles to the current expiry of Policy
                                //% policy(Self,Policy,(Current_Policy_Duration + Duration))
                            //End
                            policies.add(polToCheck);
                            PrologInterface.assertFact("policy", new Term[]{ new Atom("peer"+peerID),polToCheck.getPrologTerm()});
                        }
                        break;

                    case "inform":
                        //[Inform_Recipient] <- Inform
                        HashSet<String> completedObl = new HashSet<String>();
                        boolean othersDone = true;
                        for (Action actTest : obligations.get(oblP).keySet()) {
                            if (obligations.get(oblP).get(actTest) == 2) {
                                completedObl.add(actTest.toString());
                            } else {
                                othersDone = false;
                                break;
                            }
                        }
                        
                        if (othersDone) {
                            //Send ["OBLIGATION_INFORM", Inform_Recipient, Completed_Obl, null, null]                       
                            Node n = getPeerByID(actP.payload[0]);
                            if (n != null) {
                                ((DataExchange) n.getProtocol(protocolID)).sendMessage(protocolID, n, node, "OBLIGATION_INFORM", new Object[] { completedObl });                            
                                obligations.get(oblP).replace(actP, 2); //Mark Obligation as fulfilled
                            }
                        }
                        break;
                }
                if (obligations.get(oblP).get(actP) == 0) {
                    obligations.get(oblP).replace(actP, 1);
                }
            }
        }
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
       
    public void sendMessage(int protocolID, Node r, Node s, String type, Object[] payload) {
        if (r.isUp() && !disconnecting) {
            messages.add(new P2PMessage(s, type, payload, (peersim.core.CommonState.getTime() + 1)));
            if (PrologInterface.debugMessages) {
                String payloadString = "";
                /*if (type.equals("DATA_RESULT")) {
                    DataPackage p = (DataPackage) payload[1];
                    payloadString = p.dataItems.toString() + " - " + p.transactionRecords.toString();
                } else {*/
                    for (Object p : payload) {
                        payloadString += p + ",";
                    }
                    if (payloadString.length() > 0) {
                        payloadString = payloadString.substring(0, payloadString.length() - 1);
                    }
                //}
                System.out.println(s.getID() + " -> " + r.getID() + ", " + type + ", [" + payloadString + "], " + (peersim.core.CommonState.getTime() + 1));
            }
        } else if (s.isUp()) {
            DataExchange sDE = ((DataExchange) s.getProtocol(protocolID));
            if (!sDE.disconnecting) {
                String returnData = "-1";
                try {
                    returnData = (String) payload[0];
                } catch (ClassCastException e) {
                }
                sDE.messages.add(new P2PMessage(r, "PEER_DOWN", new String[] { returnData }, (peersim.core.CommonState.getTime() + 1)));
                if (PrologInterface.debugMessages) {
                    System.out.println(r.getID() + " -> " + s.getID() + ", PEER_DOWN, [" + returnData + "], " + (peersim.core.CommonState.getTime() + 1));
                }
            }
        }
    }

    protected void gracefulDisconnect(Node node, int protocolID, int dType) {
        disconnecting = true;
        for (P2PMessage msg : messages) {
            DataExchange n = (DataExchange) msg.sender.getProtocol(protocolID);
            switch (msg.type) {
                case "DATA_REQUEST":
                case "RECORD_INFORM":
                    n.sendMessage(protocolID, msg.sender, node, "PEER_DOWN", new String[] { (String) msg.payload[0] });
                    break;
            }
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
    
    public void initPeer() {
        System.out.println(peerID+", Altruistic: "+altruistic+", Fair: "+fair+", Faulty: "+faulty);
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

    public boolean verifyKey(long key) {
        if ((key + 1) == peerPrivateKey) {
            return true;
        }
        return false;
    }
    
    private void processIncomingDataPackage(DataPackage dataPackage, Node sender, int protocolID) {
        DataExchange n = (DataExchange) sender.getProtocol(protocolID);
        dataPackage.decrypt();
        
        HashSet<String> dataTypesInPackage = new HashSet<String>();

        for (DataElement d : dataPackage.dataItems) {
            //Manages current collection of data that is desired. Can be safely ignored for incoming data that you don't care about
            if (pendingData.containsKey(d.dataID) || desiredData.containsKey(d.dataID)) {
                if (!desiredData.containsKey(d.dataID)) {
                    desiredData.put(d.dataID, pendingData.get (d.dataID));
                }
                pendingData.remove(d.dataID);
                
                if (desiredData.get(d.dataID) != -1) {
                    if (desiredData.get(d.dataID) <= 1) {
                        desiredData.remove(d.dataID);
                    } else {
                        desiredData.replace(d.dataID, desiredData.get(d.dataID) - 1);
                    }
                }
            }
            
            //Updates information about received data (used to calculate satisfaction)
            if (!receivedData.containsKey(d.dataID)) { receivedData.put(d.dataID,0);}
            receivedData.replace(d.dataID, receivedData.get(d.dataID)+1);            

            //If there is an (unfulfilled) obligation for collecting Data_Item, and the quantity of Data_Item you own exceeds Data_Quantity 
            //Mark any obligations associated with acquiring Data_Quantity of Data_Item as fulfilled

            dataReceived += 1;
            /*if (!ownedData.contains(d.dataID)) {
                if (!dataCollection.contains(d)) {
                    PrologInterface.assertFact("dataElement", new Term[] { new Atom("peer" + peerID), new Atom(d.dataID), new Atom(d.data) });
                }
                dataCollection.add(d);
                if (provider) {
                    ownedData.add(d.dataID);
                }
                peerBudget += getDataValue(d.dataID);
            }*/
            dataTypesInPackage.add(d.dataID);
            if (!ownedData.contains(d.dataID)) { ownedData.add(d.dataID);}
            if (!dataCollection.contains(d)) {
                PrologInterface.assertFact("dataElement", new Term[] { new Atom("peer" + peerID), new Atom(d.dataID), new Atom(d.data)});
                dataCollection.add(d);
            }
            peerBudget += getDataValue(d.dataID);

            //Prolog State of Affairs Add: Sender_ID has Data_Item, Receiver_ID has Data_Item
//            if (overlayNetworkEnabled) {
//                overlayNetwork.put("peer" + sender.getID(), sender);
//                PrologInterface.assertFact("connected", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID) });
//            }
        }
        
        for (String d : dataTypesInPackage) {
            PrologInterface.assertFactIfNotExist("hasData", new Term[] { new Atom("peer" + peerID), new Atom("peer" + n.peerID), new Atom(d) });
            PrologInterface.assertFactIfNotExist("hasData", new Term[] { new Atom("peer" + peerID), new Atom("peer" + peerID), new Atom(d) });
        }

        for (String r : dataPackage.transactionRecords) {
            addTransRecordToCollection(r);
        }

        for (DataPolicy o : dataPackage.obligations) {
            //Can just add this, conflict checking has been done when deciding to accept the policy(s) 
            //Actually needs to update existing obligations if a new one comes in that adds to them
            //Currently obliged to send 5 d1, then obliged to send 5 d1, would mean 10 d1 total
            HashMap<Action,Integer> oProcessMap = new HashMap<Action,Integer>();
            for (Action oAct : o.getObligedActions()) {
                oProcessMap.put(oAct, 0);
            }
            obligations.put(o,oProcessMap);
        }
    }
    
    private void addTransRecordToCollection(String r) {
        //if (r.endsWith("]")) { r = r.substring(1, r.length()-1);} else { r = r.substring(1);}
        //String[] rSplit = r.split(",");

        //Prolog Records Add: Transaction_Records
        //PrologInterface.assertFact("recordRequest", new Term[]{new Atom("peer"+peerID),new Atom(rSplit[0]),new Atom(rSplit[1]),new org.jpl7.Integer(Integer.parseInt(rSplit[2])),new Atom(rSplit[3]),new Atom(rSplit[4]),new Variable()});
        PrologInterface.assertFact("recordRequest", PrologInterface.stringToTransRecord(peerID, r));
    }

    public Term generatePolicy(String polTarget, String dataItem, int reward, int penalty, int nestingLevel, boolean canSelfTarget) {
        String modality = "P"; if (rng.nextInt(4) == 0) { modality = "F";}

        if (polTarget.equals("")) {
            int polTargetID = rng.nextInt(Network.size());
            polTarget = "peer" + polTargetID;
            if (rng.nextInt(50) == 0) {
                polTarget = "any";
            } else if (rng.nextInt(20) == 0) {
                HashSet<Term> result = PrologInterface.runQuery("group", new Term[] { new Variable("G"), new Variable("_") }, "G");
                if (result.size() > 0) {
                    Term[] resultArray = result.toArray(new Term[0]);
                    polTarget = resultArray[rng.nextInt(resultArray.length)].toString();
                }
            }
        }

        if (dataItem.equals("")) {
            dataItem = ownedData.get(rng.nextInt(ownedData.size()));
            if (rng.nextInt(25) == 0) {
                dataItem = "any";
            }
        }

        //Conditions: [Con1,Con2]
        Term[] conditions = new Term[0];
        if (rng.nextInt(25) == 0) {
            int numConditions = rng.nextInt(100);
            if (numConditions <= 75) {
                numConditions = 1;
            } else if (numConditions <= 95) {
                numConditions = 2;
            } else {
                numConditions = 3;
            }

            conditions = new Term[numConditions];
            for (int j = 0; j < numConditions; j += 1) {
                Term con = null;

                int opType = rng.nextInt(6);
                String op = "";
                switch (opType) {
                    case 0:
                        op = ">";
                        break;
                    case 1:
                        op = ">=";
                        break;
                    case 2:
                        op = "<";
                        break;
                    case 3:
                        op = "=<";
                        break;
                    case 4:
                        op = "=:=";
                        break;
                    case 5:
                        op = "=\\=";
                        break;
                }

                int n = 0;
                int conType = rng.nextInt(5);
                Term date = null;
                switch (conType) {
                    case 0: { // Operator(recordsAccessed(ID,Data),N)
                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
                        con = new Compound("recordsAccessed", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
                        n = rng.nextInt(100) + 1;
                        break;
                    }
                    case 1: { // Operator(recordsRequested(ID,Data),N)
                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
                        con = new Compound("recordsRequested", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
                        n = rng.nextInt(100) + 1;
                        break;
                    }
                    case 2: { // Operator(requests(ID,Data),N)
                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
                        con = new Compound("requests", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
                        n = rng.nextInt(100) + 1;
                        break;
                    }
                    case 3: { // Operator(lastRequest(ID,Data),N)
                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
                        con = new Compound("lastRequest", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
                        date = new Compound("date", new Term[] { new org.jpl7.Integer(2017), new org.jpl7.Integer(rng.nextInt(12)+1), new org.jpl7.Integer(rng.nextInt(31)+1), new org.jpl7.Integer(rng.nextInt(24)), new org.jpl7.Integer(rng.nextInt(60)),new org.jpl7.Float(0.0f),new org.jpl7.Integer(0),new Atom("local"),new Atom("false")});
                        n = rng.nextInt(100) + 1;
                        break;
                    }
                    case 4: { // Operator(lastAccess(ID,Data),N)
                        String dataItemCon = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        String peerTargetCon = "peer" + rng.nextInt(Network.size());
                        con = new Compound("lastAccess", new Term[] { new Atom(dataItemCon), new Atom(peerTargetCon) });
                        date = new Compound("date", new Term[] { new org.jpl7.Integer(2017), new org.jpl7.Integer(rng.nextInt(12)+1), new org.jpl7.Integer(rng.nextInt(31)+1), new org.jpl7.Integer(rng.nextInt(24)), new org.jpl7.Integer(rng.nextInt(60)),new org.jpl7.Float(0.0f),new org.jpl7.Integer(0),new Atom("local"),new Atom("false")});
                        n = rng.nextInt(100) + 1;
                        break;
                    }
                    case 5: { // Operator(year(Year),N)
                        con = new Compound("year", new Term[0]);
                        n = rng.nextInt(2) + 2017;
                        break;
                    }
                    case 6: { // Operator(month(Month),N)
                        con = new Compound("month", new Term[0]);
                        n = rng.nextInt(12) + 1;
                        break;
                    }
                    case 7: { // Operator(day(Day),N)
                        con = new Compound("day", new Term[0]);
                        n = rng.nextInt(31) + 1;
                        break;
                    }
                    case 8: { // Operator(hour(Hour),N)
                        con = new Compound("hour", new Term[0]);
                        n = rng.nextInt(24);
                        break;
                    }
                    case 9: { // Operator(minute(Minute),N)
                        con = new Compound("minute", new Term[0]);
                        n = rng.nextInt(60);
                        break;
                    }
                }

                if (date == null) {
                    conditions[j] = new Compound(op, new Term[] { con, new org.jpl7.Integer(n) });
                } else {
                    conditions[j] = new Compound(op, new Term[] { con, date });
                }
                //conditions[j] = Util.textToTerm(op+"("+n+")");
            }
        }

        //Obligations: [ [[Obl1], Penalty1, Duration1], [[Obl2,Obl3], Penalty2, Duration2] ]
        Term[] preObligations = new Term[0];
        Term[] obligations = new Term[0];
        if (rng.nextInt(5) == 0) {
            int numObligations = rng.nextInt(100);
            if (numObligations <= 75) {
                numObligations = 1;
            } else if (numObligations <= 95) {
                numObligations = 2;
            } else {
                numObligations = 3;
            }
            int numPreObligations = rng.nextInt(100);
            if (numPreObligations <= 80) {
                numPreObligations = 0;
            } else {
                numPreObligations = 1;
            }

            preObligations = new Term[numPreObligations];
            obligations = new Term[numObligations];
            for (int j = 0; j < (numObligations + numPreObligations); j += 1) {
                Term[] obl = new Term[3];

                boolean preObl = (j >= numObligations);
                int oblType = rng.nextInt(4);
                if (oblType == 2 && nestingLevel >= 5) {
                    oblType = rng.nextInt(3);
                    if (oblType == 2) {
                        oblType = 3;
                    }
                } //If this policy is nested more than 5 times, prevent further "adopt" obligations
                if (preObl && oblType == 3) {
                    oblType = rng.nextInt(3);
                } //Pre obligations don't use inform(), as this is implicitly built into the mechanism
                switch (oblType) {
                    case 0: { // obtain(Data,Quantity)
                        String dataItemObl = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        Term oblTerm = new Compound("obtain", new Term[] { new Atom(dataItemObl), new org.jpl7.Integer(rng.nextInt(10) + 1) });
                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
                        obl[1] = new org.jpl7.Integer((rng.nextInt(10) + 1));
                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
                        break;
                    }
                    case 1: { // provide(Data,Quantity,Peer)
                        String dataItemObl = masterDataArray[rng.nextInt(masterDataArray.length - 1)].toString();
                        String peerTargetObl = "peer" + rng.nextInt(Network.size());
                        Term oblTerm = new Compound("provide", new Term[] { new Atom(dataItemObl), new org.jpl7.Integer(rng.nextInt(10) + 1), new Atom(peerTargetObl) });
                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
                        obl[1] = new org.jpl7.Integer((rng.nextInt(10) + 1));
                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
                        break;
                    }
                    case 2: { // adopt(Policy,Duration)
                        int oblPenalty = (rng.nextInt(10) + 1);
                        Term oblTerm = new Compound("adopt", new Term[] { generatePolicy("","",0,oblPenalty,nestingLevel + 1,true), new org.jpl7.Integer(rng.nextInt(5) + 1) });
                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
                        obl[1] = new org.jpl7.Integer(oblPenalty);
                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
                        break;
                    }
                    case 3: { // inform(Peer)
                        String peerTargetObl = "peer" + peerID; //This inform will target the policy owner 80% of the time
                        if (rng.nextInt(5) == 0) {
                            peerTargetObl = "peer" + rng.nextInt(Network.size());
                        }
                        Term oblTerm = new Compound("inform", new Term[] { new Atom(peerTargetObl) });
                        obl[0] = Util.termArrayToList(new Term[] { oblTerm });
                        obl[1] = new org.jpl7.Integer((rng.nextInt(10) + 1));
                        obl[2] = new org.jpl7.Integer(rng.nextInt(5) + 5);
                        break;
                    }
                }
                if (preObl) {
                    preObligations[j - numObligations] = Util.listToTermArray(obl[0])[0];
                } else {
                    obligations[j] = Util.termArrayToList(obl);
                }
            }
        }
        
        boolean selfPol = false;
        if (canSelfTarget && rng.nextInt(10) == 0) { selfPol = true;}
        
        int polReward = reward;
        if (reward == -1) {
            reward = rng.nextInt(10);
        }
        int polPenalty = penalty;
        if (penalty == -1) {
            penalty = rng.nextInt(10);
        }
        
        /*if (conditions.length > 0) {
            System.out.print(peerID+": "+modality+", "+polTarget+", "+dataItem+"\n\t");
            for (Term v : conditions) { System.out.print(v+", ");}
            System.out.print("\n\t");
            for (Term v : preObligations) { System.out.print(v+", ");}
            System.out.print("\n\t");
            for (Term v : obligations) { System.out.print(v+", ");}
            System.out.println("");
        }*/
        return Util.termArrayToList(new Term[] { new Atom(modality), new Atom(polTarget), new Atom(dataItem), Util.termArrayToList(conditions), Util.termArrayToList(preObligations), Util.termArrayToList(obligations), new Atom(""+selfPol), new org.jpl7.Integer(polReward), new org.jpl7.Integer(polPenalty) });
    }

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
    private DataPackage assembleDataPackage(HashSet<Term> transRecords, HashSet<DataPolicy> obligations, long msgID) {
        DataPackage dataPackage = new DataPackage();
        for (Term tR : transRecords) {
            String r = tR.toString();
            String rBody = r.substring(r.indexOf("(") + 1, r.length() - 1);
            String[] rSplit = rBody.split(",");
            for (int j = 0; j < rSplit.length; j += 1) {
                rSplit[j] = rSplit[j].trim();
            }
            PrologInterface.assertFact("recordRequest", PrologInterface.stringToTransRecord(peerID, r));
            dataPackage.transactionRecords.add(r);
            
            if (rSplit[rSplit.length-1].equals("true")) {
                int dataQuantity = Integer.parseInt(rSplit[4]);
                if (dataQuantity > 0) {
                    for (int j = 0; j < dataQuantity; j += 1) {
                        String dBody = "";
                        for (DataElement dE : dataCollection) {
                            if (dE.dataID.equals(rSplit[3])) {
                                dBody = dE.data;
                            }
                        }
                        dataPackage.dataItems.add(new DataElement(rSplit[3], dBody));
                    }
                }
            }
        }
        if (obligations != null) {
            dataPackage.obligations.addAll(obligations);
        }
        dataPackage.encrypt();
        return dataPackage;
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
