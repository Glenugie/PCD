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
        ArrayList<Integer> activeNodes = new ArrayList<Integer>();
        int[] conns = new int[Network.size()];
        for (int i = 1; i < Network.size(); ++i) {
            conns[i] = CommonState.r.nextInt(k) + 1;
            activeNodes.add(i);
        }
        
        for (int i = 1; i < Network.size(); ++i) {
            Node n = (Node) g.getNode(i);
            while (conns[i] < g.getNeighbours(i).size()) {
                int newNeighbour = CommonState.r.nextInt(activeNodes.size());
                int newNeighbourID = activeNodes.get(newNeighbour);
                g.setEdge(i, newNeighbourID);
                if (conns[newNeighbourID] >= g.getNeighbours(newNeighbourID).size()) {
                    activeNodes.remove(newNeighbour);
                }
            }
            activeNodes.remove((Integer) i);
        }
    }

}