package com.pcd.debug;

import com.pcd.DataExchange;

import peersim.core.GeneralNode;
import peersim.core.Node;

public class Debugger {
    private static DataExchange peer;
    
    public static void main(String[] args) {
        Node n = new GeneralNode("");
        peer = new DataExchange("DEBUG");
        peer.makeAltruistic();
        peer.makeFair();
        peer.makeOwnData("d1"); peer.setDataValue("d1",5);
        peer.makeWantData("d2"); peer.setDataValue("d2",5);
        
        // Overlay Network
        
        peer.peerBudget = 1000;
        peer.startingBudget = 1000;
        peer.breakMult = 3.0;
        
        // Policies
        //addPolicy(new DataPolicy(1,"","",false);
        
//        for (int c = 0; c < 25; c += 1) {
//            peer.nextCycle(n, 1);
//        }
    }
}
