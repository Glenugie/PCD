package com.pcd;

import org.jpl7.Term;
import org.jpl7.Util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Role {
    private static final boolean DEBUG_ROLE_PARSE = false;
    
    public String name;
    public String[] groups;
    public String connection;
    public String[] policies;
    public String[][] desiredData;
    public String[][] ownedData;
    public String[] generatedData;
    public double distribution;
    
    /*
      {
        "name": "Bus Stop",
        "groups": ["g1", "g3", "g4"],
        "connection": "Hybrid",
        "policies": [
          //["P", "id|any", "d2", [], [], [], "false"],
          //["P|F", "any", "d2", [], [], [], "false"]
          ["P", "g1", "d1", [], [], [], "false"],
          ["P", "g2", "d2", [], [], [], "false"],
          ["F", "any", "d1", ["lastAccess(self,d1) > date(=,=,=,=,-10,=,0,local,=)"], [], [], "false"],
          ["P", "any", "d2", ["recordsAccessed(self,d1) < 10"],[],[],"false"],
          ["P", "any", "d2", [], ["provide(d2, n, self)"], [], "false"] 
        ],

        //                 [Data, Quantity, Value]
        "desiredData":      [["d1",-1,5],["d2",-1,5]],
        "ownedData":        [["d1",10,5],["d2",10,5]],

        "generatedData":    [],
        "distribution": 5  (Percentage of Total Peers that have this role) 
      }
     */
    
    public Role (JsonObject role) {
        name = role.get("name").getAsString();
        connection = role.get("connection").getAsString();
        
        JsonArray groupsArray = role.get("groups").getAsJsonArray();
        groups = new String[groupsArray.size()];
        for (int i = 0; i < groupsArray.size(); i += 1) {
            groups[i] = groupsArray.get(i).getAsString();
        }
        
        // ["P", "g1", "d1", [], [], [], "false"],
        JsonArray policyArray = role.get("policies").getAsJsonArray();
        policies = new String[policyArray.size()];
        for (int i = 0; i < policyArray.size(); i += 1) {
            JsonArray pol = policyArray.get(i).getAsJsonArray();
            Term[] polTerm = Util.textToTerm(pol.toString()).toTermArray();
            String newPol = "[";
            newPol += "'"+pol.get(0).toString().substring(1,2)+"',";
            newPol += pol.get(1).toString().substring(1,pol.get(1).toString().length()-1)+",";
            newPol += pol.get(2).toString().substring(1,pol.get(2).toString().length()-1)+",";

            newPol += "[";
                Term[] conTerm = polTerm[3].toTermArray();
                for (Term con : conTerm) {
                    newPol += con.toString().substring(1, con.toString().length()-1)+",";
                }
                if (conTerm.length > 0) { newPol = newPol.substring(0, newPol.length()-1);}
            newPol += "],";

            newPol += "[";
                Term[] preOblTerm = polTerm[4].toTermArray();
                for (Term preObl : preOblTerm) {
                    newPol += preObl.toString().substring(1, preObl.toString().length()-1)+",";
                }
                if (preOblTerm.length > 0) { newPol = newPol.substring(0, newPol.length()-1);}
            newPol += "],";
            
            //[[[inform(peer1)], 2, 8]]
            newPol += "[";
                Term[] oblTerm = polTerm[5].toTermArray();
                for (Term obl : oblTerm) {
                    Term[] oblSet = obl.toTermArray();
                    Term[] obls = oblSet[0].toTermArray();
                    
                    newPol += "[[";
                    for (Term o : obls) {
                        newPol += o.toString().substring(1, o.toString().length()-1)+",";
                    }
                    if (obls.length > 0) { newPol = newPol.substring(0, newPol.length()-1);}
                    newPol += "],"+oblSet[1]+","+oblSet[2]+"],";
                }
                if (oblTerm.length > 0) { newPol = newPol.substring(0, newPol.length()-1);}
            newPol += "],";

            newPol += pol.get(6).toString()+",";
            newPol += pol.get(7).toString()+",";
            newPol += pol.get(8).toString();
            newPol += "]";

            //System.out.println(pol+":\n\t"+newPol);
            policies[i] = newPol;
        }
        
        JsonArray desiredDataArray = role.get("desiredData").getAsJsonArray();
        desiredData = new String[desiredDataArray.size()][3];
        for (int i = 0; i < desiredDataArray.size(); i += 1) {
            JsonArray desiredDataElement = desiredDataArray.get(i).getAsJsonArray();
            desiredData[i][0] = desiredDataElement.get(0).getAsString();
            desiredData[i][1] = desiredDataElement.get(1).getAsString();
            desiredData[i][2] = desiredDataElement.get(2).getAsString();
        }
        
        JsonArray ownedDataArray = role.get("ownedData").getAsJsonArray();
        ownedData = new String[ownedDataArray.size()][3];
        for (int i = 0; i < ownedDataArray.size(); i += 1) {
            JsonArray ownedDataElement = ownedDataArray.get(i).getAsJsonArray();
            ownedData[i][0] = ownedDataElement.get(0).getAsString();
            ownedData[i][1] = ownedDataElement.get(1).getAsString();
            ownedData[i][2] = ownedDataElement.get(2).getAsString();
        }
        
        JsonArray generatedArray = role.get("generatedData").getAsJsonArray();
        generatedData = new String[generatedArray.size()];
        for (int i = 0; i < generatedArray.size(); i += 1) {
            generatedData[i] = generatedArray.get(i).getAsString();
        }
        
        distribution = role.get("distribution").getAsDouble();
        
        if (DEBUG_ROLE_PARSE) {
            System.out.println(role.toString());
            
            System.out.println("Name: "+name);

            String groupsString = ""; if (groups.length > 0) { for (String g : groups) { groupsString += g+", ";} groupsString = groupsString.substring(0, groupsString.length()-2);}
            System.out.println("Groups: "+groupsString);
            
            String policyString = ""; for (String p : policies) { policyString += "\t"+p+"\n";}
            System.out.println("Policies:\n"+policyString);
            
            String desiredDataString = ""; for (String[] dD : desiredData) { desiredDataString += "\t["+dD[0]+", "+dD[1]+", "+dD[2]+"]\n";}
            System.out.println("Desired Data:\n"+desiredDataString);
            
            String ownedDataString = ""; for (String[] oD : ownedData) { ownedDataString += "\t["+oD[0]+", "+oD[1]+", "+oD[2]+"]\n";}
            System.out.println("Owned Data:\n"+ownedDataString);
            
            String generatedString = ""; if (generatedData.length > 0) { for (String g : generatedData) { generatedString += g+", ";} generatedString = generatedString.substring(0, generatedString.length()-2);}
            System.out.println("Generated Data: "+generatedString);
            
            System.out.println("Connection: "+connection);
            
            System.out.println("Distribution: "+distribution+"%");
        }
    }
}
