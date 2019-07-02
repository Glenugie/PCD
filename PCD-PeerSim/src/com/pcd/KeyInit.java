package com.pcd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jpl7.Atom;
import org.jpl7.Term;
import org.jpl7.Variable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class KeyInit implements Control {
    private static final String PAR_PROT = "protocol";
    private final String name;
    private final int pid;
    private final int numDataInNetwork;
	private final double percentProviders, percentRequestors;
	
	private File rolesFile;
	private ArrayList<Role> roles;
    
    public KeyInit(String name) {
        this.name = name;
        pid = Configuration.getPid(name + "." + PAR_PROT);
        
		percentProviders = Configuration.getDouble(name+".percentProviders");
		percentRequestors = Configuration.getDouble(name+".percentRequestors");

        PrologInterface.printSimInfo = Configuration.getBoolean(name+".printSimInfo");
        PrologInterface.debugProlog = Configuration.getBoolean(name+".debugProlog");
        PrologInterface.debugMessages = Configuration.getBoolean(name+".debugMessages");        
        numDataInNetwork = Configuration.getInt(name+".numDataInNetwork");
        
        roles = null;
        rolesFile = null;
        try { 
            String rolesFileName = Configuration.getString("protocol.pcd.roles");
            if (!rolesFileName.equals("")) {
                rolesFile = new File(rolesFileName);
            }
            //System.out.println("\""+rolesFileName+"\": "+rolesFile);
        } catch (Exception e) {
            //Could not load roles file
            System.err.println("Could not find roles file: "+Configuration.getString("protocol.pcd.roles"));
        }

        if (PrologInterface.printSimInfo) {
	        System.err.println("\nSimulation Parameters:");
			System.err.println("\t Network Size: "+Configuration.getInt("network.size"));
			System.err.println("\t Neighbours per Peer: "+Configuration.getInt("init.rnd.k"));
			System.err.println("\t Cycles: "+Configuration.getInt("simulation.cycles"));
			System.err.println("\t Cycle Cost: "+Configuration.getInt("protocol.pcd.cycleCost"));
			System.err.println("\t Data in Network: "+Configuration.getInt(name+".numDataInNetwork"));
			System.err.println("\t Data Desired: "+Configuration.getInt("protocol.pcd.minDataDesired")+" - "+Configuration.getInt("protocol.pcd.maxDataDesired"));
			System.err.println("\t Data Owned: "+Configuration.getInt("protocol.pcd.minDataOwned")+" - "+Configuration.getInt("protocol.pcd.maxDataOwned"));
			System.err.println("\t Policies: "+Configuration.getInt("protocol.pcd.minPolicies")+" - "+Configuration.getInt("protocol.pcd.maxPolicies"));
			System.err.println("\t Peer Budget: "+Configuration.getInt("protocol.pcd.minPeerBudget")+" - "+Configuration.getInt("protocol.pcd.maxPeerBudget"));
			System.err.println("\t Max Data Hops: "+Configuration.getInt("protocol.pcd.maxDataHops"));
			//System.err.println("\t Desired Overrides Owned: "+Configuration.getBoolean("protocol.pcd.desiredOverridesOwned"));
			System.err.println("\t Allow New Connections: "+Configuration.getBoolean("protocol.pcd.allowNewConnections"));
			System.err.println("\t Default Permit: "+Configuration.getBoolean("protocol.pcd.defaultPermit"));
			System.err.println("");
        }
    }

	public boolean execute() {		
		for (int i = 0; i < numDataInNetwork; i += 1) {
			PrologInterface.assertFact("data", new Term[]{new Atom("d"+(i+1)),new Atom("d0"),new Atom("d0"),new Atom("000000")});
		}
		
		HashSet<Term> masterData = PrologInterface.runQuery("data", new Term[]{new Variable("X"), new Variable("_"), new Variable("_"), new Variable("_")},"X");
		Term[] masterDataArray = masterData.toArray(new Term[0]);

		int numProviders = (int) Math.ceil((Network.size()/100.0) * percentProviders);
		int numRequestors = (int) Math.ceil((Network.size()/100.0) * percentRequestors);
		
		if (rolesFile != null) {
		    parseRoles();
		    
		    int curPeers = 0;
		    for (Role r : roles) {
		        double roleDist = r.distribution;
		        int numRole = (int) Math.ceil((Network.size()/100.0) * roleDist);
		        for (int i = 0; i < numRole; i += 1) {
		            //System.out.println(r.name+" ["+i+"] ("+curPeers+" >= "+Network.size()+")");
		            if (curPeers >= Network.size()) { break;}
	                DataExchange protocol = (DataExchange) Network.get(curPeers).getProtocol(pid);
	                protocol.initPeer(Network.get(curPeers).getID(),masterDataArray,r);    
		            curPeers += 1;
		        }
		    }
		    for (int i = curPeers; i < Network.size(); i += 1) {
                //System.out.println("NO ROLE ["+i+"] ("+curPeers+" >= "+Network.size()+")");
                DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);
                protocol.initPeer(Network.get(i).getID(),masterDataArray,2);       
                curPeers += 1;
		    }
		} else {		
            for (int i = 0; i < Network.size(); i += 1) {
                DataExchange protocol = (DataExchange) Network.get(i).getProtocol(pid);
                int type = -1;
                if (i < numRequestors) {
                	type = 1;
                } else if (i >= numRequestors && i < (numRequestors+numProviders)) {
                	type = 0;
                } else {
                	type = 2;
                }
                protocol.initPeer(Network.get(i).getID(),masterDataArray,type);        	
            }
		}
        
        PrologInterface.dumpListing();
        
		return false;
	}
	
	private void parseRoles() {
	    Gson g = new Gson();
	    
	    roles = new ArrayList<Role>();
	    try {
            BufferedReader bufRdr = new BufferedReader(new FileReader(rolesFile));  
            String json = "", line;
            while ((line = bufRdr.readLine()) != null) {
                if (!line.trim().startsWith("//") && !line.trim().equals("")) {
                    json += line+"\n";
                }
            }
            //System.out.println(json);
    	    JsonReader reader = new JsonReader(bufRdr);    	    
    	    
    	    /*Role r = g.fromJson(json, Role.class);
    	        	    
    	    System.out.println("Role 1 Name: "+r.name);*/
    	    
    	    
            JsonArray rolesArray = new JsonParser().parse(json).getAsJsonObject().get("roles").getAsJsonArray();
            for (int i = 0; i < rolesArray.size(); i += 1) {
                JsonObject roleObj = rolesArray.get(i).getAsJsonObject();
                roles.add(new Role (roleObj));                
            }
            
            bufRdr.close();
            reader.close();
	    } catch (IOException e) {
	        //Cannot read roles file
	        System.err.println("Cannot read roles file: "+e.getMessage());
	    }
	}
}
