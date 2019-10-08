package com.pcd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

public class WireMesh extends WireGraph {
    
    private static final String PAR_DEGREE = "k";
    private final int k;
    
    public WireMesh(String prefix) {
        super(prefix);
        k = Configuration.getInt(prefix + "." + PAR_DEGREE);
    }
    
    public void wire(Graph g) {
        ArrayList<Integer> masterNodes = new ArrayList<Integer>();
        ArrayList<Integer> supportNodes = new ArrayList<Integer>();
        int[] conns = new int[Network.size()];
        for (int i = 0; i < Network.size(); ++i) {
            conns[i] = CommonState.r.nextInt(k) + 1;
            masterNodes.add(i);
            supportNodes.add(i);
            //System.out.println(k+" ?= "+conns[i]);
        }

        for (int i = 0; i < Network.size(); ++i) {
            ArrayList<Integer> activeNodes = (ArrayList<Integer>) masterNodes.clone();
            Node n = (Node) g.getNode(i);
            while (conns[i] > g.getNeighbours(i).size() && activeNodes.size() > 0) {
                int newNeighbour = CommonState.r.nextInt(activeNodes.size());
                int newNeighbourID = activeNodes.get(newNeighbour);
                if (g.getNeighbours(newNeighbourID).size() < k) {
                    //System.out.println(conns[i]+" ?= "+g.getNeighbours(i).size()+" ("+g.getNeighbours(newNeighbourID).size()+")");
                    //System.out.println(i+" <-> "+newNeighbourID);
                    g.setEdge(i, newNeighbourID);
                    g.setEdge(newNeighbourID, i);
                    //System.out.println(conns[i]+" ?= "+g.getNeighbours(i).size()+" ("+g.getNeighbours(newNeighbourID).size()+")");
                    //if (conns[newNeighbourID] >= g.getNeighbours(newNeighbourID).size()) {
                        activeNodes.remove(newNeighbour);
                    //}
                } else {
                    //System.out.println(newNeighbourID+" is full");
                    activeNodes.remove(newNeighbour);
                }
            }
            if (g.getNeighbours(i).size() == 0) {
                int newNeighbour = CommonState.r.nextInt(supportNodes.size());
                int newNeighbourID = activeNodes.get(newNeighbour);
                g.setEdge(i, newNeighbourID);
                g.setEdge(newNeighbourID, i);
                supportNodes.remove(i);
            }
            masterNodes.remove((Integer) i);
        }        

        for (int i = 0; i < Network.size(); ++i) {
            //System.out.println("Goal: "+conns[i]+", Actual: "+g.getNeighbours(i).size());
        }
    }

}