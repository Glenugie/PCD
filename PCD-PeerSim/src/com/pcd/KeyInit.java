package com.pcd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class KeyInit implements Control {
    private static final String PAR_PROT = "protocol";
    private final String name;
    private final int pid;
    
    public KeyInit(String name) {
        this.name = name;
        pid = Configuration.getPid(name + "." + PAR_PROT);

        PrologInterface.printSimInfo = Configuration.getBoolean(name+".printSimInfo");
        PrologInterface.debugProlog = Configuration.getBoolean(name+".debugProlog");
        PrologInterface.debugMessages = Configuration.getBoolean(name+".debugMessages");        
        
        //PrologInterface.confSeed = Configuration.getDouble("random.seed");
        PrologInterface.confSeed = CommonState.r.getLastSeed();
        PrologInterface.confExperiments = Configuration.getInt("simulation.experiments");
        PrologInterface.confCycles = Configuration.getInt("simulation.cycles");
        PrologInterface.confPeers = Configuration.getInt("network.size");
        
        PrologInterface.confCycleCost = Configuration.getInt(name+".cycleCost");
        PrologInterface.confMinPols = Configuration.getInt(name+".minPolicies");
        PrologInterface.confMaxPols = Configuration.getInt(name+".maxPolicies");
        PrologInterface.confFaultyPeers = Configuration.getInt(name+".percFaultyPeers");
        PrologInterface.confFaultRate = Configuration.getInt(name+".percFaultRate");
        PrologInterface.confAltruistic = Configuration.getInt(name+".percAltruisticPeers");
        PrologInterface.confFair = Configuration.getInt(name+".percFairPeers");
        PrologInterface.confMaxNeighbours = Math.max(Configuration.getInt("init.rnd.k"),Configuration.getInt(name+".maxNeighbours"));
        PrologInterface.confDefaultPermit = Configuration.getBoolean(name+".defaultPermit");
        
        PrologInterface.confDataTypes = new ArrayList<DataConfig>();
        PrologInterface.confProtoPolicies = new ArrayList<String>();
        
        PrologInterface.confTopology = Configuration.getInt(name+".topology");
        PrologInterface.confTopologyVal = Configuration.getInt("init.rnd.k");
        PrologInterface.confNewConnections = Configuration.getBoolean(name+".allowNewConnections");

        if (PrologInterface.printSimInfo) {
	        System.err.println("\nSimulation Parameters:");
			System.err.println("\t "+PrologInterface.confExperiments+" run(s) of "+PrologInterface.confCycles+" cycle(s) with "+PrologInterface.confPeers+" peer(s)");
			System.err.println("\t "+topologyType(PrologInterface.confTopology)+" ("+PrologInterface.confTopology+") with value "+PrologInterface.confTopologyVal);
			System.err.println("");
        }
    }

	public boolean execute() {		
//		for (int i = 0; i < numDataInNetwork; i += 1) {
//			PrologInterface.assertFact("data", new Term[]{new Atom("d"+(i+1)),new Atom("d0"),new Atom("d0"),new Atom("000000")});
//		}
//		
//		HashSet<Term> masterData = PrologInterface.runQuery("data", new Term[]{new Variable("X"), new Variable("_"), new Variable("_"), new Variable("_")},"X");
//		Term[] masterDataArray = masterData.toArray(new Term[0]);
//
		int numAltruistic = (int) Math.ceil((Network.size()/100.0) * PrologInterface.confAltruistic);
        int numFair = (int) Math.ceil((Network.size()/100.0) * PrologInterface.confFair);
        int numFaulty = (int) Math.ceil((Network.size()/100.0) * PrologInterface.confFaultyPeers);
		
	    ArrayList<Integer> peerIDs1 = new ArrayList<Integer>(); for (int i = 0; i < Network.size(); i += 1) { peerIDs1.add(i);}
        ArrayList<Integer> peerIDs2 = new ArrayList<Integer>(peerIDs1);
        ArrayList<Integer> peerIDs3 = new ArrayList<Integer>(peerIDs1);
        Collections.shuffle(peerIDs1); Collections.shuffle(peerIDs2); Collections.shuffle(peerIDs3);
        
        for (int i = 0; i < Network.size(); i += 1) {
            if (i < numAltruistic) {
                ((DataExchange) Network.get(peerIDs1.get(i)).getProtocol(pid)).makeAltruistic();
            }
            if (i < numFair) {
                ((DataExchange) Network.get(peerIDs2.get(i)).getProtocol(pid)).makeFair();
            }
            if (i < numFaulty) {
                ((DataExchange) Network.get(peerIDs3.get(i)).getProtocol(pid)).makeFaulty();
            }
        }
        
        parseData();
        for (DataConfig dConf : PrologInterface.confDataTypes) {
            int numWant = (int) Math.ceil((Network.size()/100.0) * dConf.percWant);
            int numOwn = (int) Math.ceil((Network.size()/100.0) * dConf.percOwn);
            ArrayList<Integer> peerIDsData = new ArrayList<Integer>(); for (int i = 0; i < Network.size(); i += 1) { peerIDsData.add(i);}
            Collections.shuffle(peerIDsData);
            for (int i = 0; i < Network.size(); i += 1) {
                int uMult = 1;
                if (i < numWant) {
                    ((DataExchange) Network.get(peerIDs1.get(i)).getProtocol(pid)).makeWantData(dConf.dataId);
                    uMult = 2;
                }
                if (i < numOwn) {
                    ((DataExchange) Network.get(peerIDs1.get(i)).getProtocol(pid)).makeOwnData(dConf.dataId);                    
                }
                ((DataExchange) Network.get(peerIDs1.get(i)).getProtocol(pid)).setDataValue(dConf.dataId,(CommonState.r.nextInt((dConf.maxU-dConf.minU))+dConf.minU)*uMult);
            }
        }
        
        parsePolicies();        
        for (int i = 0; i < Network.size(); i += 1) {
            DataExchange protocol = (DataExchange) Network.get(peerIDs1.get(i)).getProtocol(pid);
            protocol.initPeer();        	
        }
        
        PrologInterface.dumpListing();
        
		return false;
	}
	
	// Parse an arbitrary length list of CSV strings, format: d[n],[Own%],[Want%],[MinU],[MaxU]
	// d1,10,100,1,10
	private void parseData() {
	    File dataFile = new File(Configuration.getString(name+".data"));
        try {
            BufferedReader bufRdr = new BufferedReader(new FileReader(dataFile));  
            String line;
            while ((line = bufRdr.readLine()) != null) {
                if (!line.trim().startsWith("//") && !line.trim().equals("")) {
                    String[] dConf = line.trim().split(",");
                    DataConfig dataConf = new DataConfig(dConf[0],Integer.parseInt(dConf[1]),Integer.parseInt(dConf[2]),Integer.parseInt(dConf[3]),Integer.parseInt(dConf[4]));
                    PrologInterface.confDataTypes.add(dataConf);
                }
            }
            bufRdr.close();
        } catch (IOException e) {
            System.err.println("Cannot read data file: "+e.getMessage());
        }
	}
	
	// Arbitrary length list of strings, with placeholders for Data, ID, Number, String ({DATA}, {ID}, {X-Y}, {"S1",...,"SN"})
    // Placeholders are tagged with a numeric identifier or *, allowing multiple versions of the same parameter to be used. 
	//     I.e. {DATA~1}, {DATA~*}, [DATA]{1} would generate {DATA~1}, then use that same value for the second
	// [true],[false],P,{ID~1},["access({DATA~2},{ID~1},-1)"],{0-5~3},{5-10~4}
	private void parsePolicies() {
        File policyFile = new File(Configuration.getString(name+".policies"));
        try {
            BufferedReader bufRdr = new BufferedReader(new FileReader(policyFile));  
            String line;
            while ((line = bufRdr.readLine()) != null) {
                if (!line.trim().startsWith("//") && !line.trim().equals("")) {
                    PrologInterface.confProtoPolicies.add(line.trim());
                }
            }
            bufRdr.close();
        } catch (IOException e) {
            System.err.println("Cannot read policy file: "+e.getMessage());
        }
	}
	
	private String topologyType(int val) {
	    switch(val) {
	        case 1: return "Mesh"; 
	        case 2: return "Overlay"; 
	        case 3: return "Fully Connected"; 
	        case 4: return "Grid"; 
	        case 5: return "Ring"; 
	        case 6: return "Tree";
	    }
	    return "Unknown";
	}
}
