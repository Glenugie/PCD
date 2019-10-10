package com.pcd.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BatchGenerator {
    public static void main(String[] args) {
        String[] topologies = {
            "# Mesh\t\ninit.keys.topology 1\t\ninit.rnd com.pcd.WireMesh\t\ninit.rnd.protocol lnk\t\ninit.rnd.k 10\t\ninit.keys.allowNewConnections false",
            "# Overlay\n\tinit.keys.topology 2\n\tinit.rnd WireKOut\n\tinit.rnd.protocol lnk\n\tinit.rnd.k 5\n\tinit.keys.allowNewConnections true",
            "# Fully Connected\n\tinit.keys.topology 3\n\tinit.rnd WireKOut\n\tinit.rnd.protocol lnk\n\tinit.rnd.k 999999999\n\tinit.keys.allowNewConnections false",
            "# Pseudo-Grid\n\tinit.keys.topology 4\n\tinit.rnd WireRingLattice\n\tinit.rnd.protocol lnk\n\tinit.rnd.k 4\n\tinit.keys.allowNewConnections false",
            "# Ring\n\tinit.keys.topology 5\n\tinit.rnd WireRingLattice\n\tinit.rnd.protocol lnk\n\tinit.rnd.k 2\n\tinit.keys.allowNewConnections false",
            "# Tree\n\tinit.keys.topology 6\n\tinit.rnd WireRegRootedTree\n\tinit.rnd.protocol lnk\n\tinit.rnd.k 5\n\tinit.keys.allowNewConnections false"
        };
        String[] compliances = {"50", "10", "90"};
        String[] fairnesses = {"50", "10", "90"};
        String[] datasets = {"1-10O-100N.pcddata","5-25O-100N.pcddata","10-50O-50N.pcddata"};
        
        ArrayList<String> fileNames = new ArrayList<String>();
        
        for (String t : topologies) {
            for (String c : compliances) {
                for (String f : fairnesses) {
                    for (String d : datasets) {
                        String name = t.substring(2,6)+","+c+","+f+","+d.substring(0,d.length()-8);
                        //System.out.println(name);
                        
                        File newFileF = new File("conf/PCD-Conf_"+name+".txt");
                        try {
                            newFileF.createNewFile();   
                            BufferedWriter outF = new BufferedWriter(new FileWriter(newFileF,false));
                            
                            outF.write("# PCD CONFIG: "+name+"\n");
                            outF.write("simulation.experiments 1\n");
                            outF.write("simulation.cycles 250\n");
                            outF.write("simulation.title "+name+"\n");
                            outF.write("network.size 100\n");
                            outF.write("protocol.lnk IdleProtocol\n");
                            outF.write("protocol.pcd com.pcd.DataExchange\n");
                            outF.write("protocol.pcd.linkable lnk\n");
                            outF.write("init.keys.maxForward 2\n");
                            outF.write("init.keys.peersTrueRandom false\n");
                            outF.write("init.keys.peersReasoning true\n");
                            outF.write("init.keys.dataRequestForwarding true\n");
                            outF.write("init.keys.defaultPermit false\n");    
                            outF.write("init.keys.cycleCost 1 \n");
                            outF.write("init.keys.maxTransactions 2500\n");
                            outF.write("init.keys.transactionLifetime 5\n");
                            outF.write("init.keys.minUtility 10\n");
                            outF.write("init.keys.minBudget 100\n");
                            outF.write("init.keys.maxBudget 100\n");
                            outF.write("init.keys.minPolicies 50\n");
                            outF.write("init.keys.maxPolicies 100\n");
                            outF.write("init.keys.percFaultyPeers 10\n");
                            outF.write("init.keys.percFaultRate 5\n");
                            outF.write("init.keys.maxNeighbours 10\n");
                            outF.write("init.keys.policies Basic.pcdpol\n");

                            outF.write("init.keys.percAltruisticPeers "+c+"\n");
                            outF.write("init.keys.percFairPeers "+f+"\n");
                            
                            outF.write("init.keys.data "+d+"\n");
                            
                            outF.write("init.keys com.pcd.KeyInit\n");
                            outF.write("init.keys.protocol pcd\n");                            
                            outF.write("init.keys.printSimInfo false\n");                 
                            outF.write("init.keys.debugProlog false\n");                 
                            outF.write("init.keys.debugMessages false\n");
                            
                            outF.write("\n\n"+t+"\n\n");
                            
                            outF.write("control.deo com.pcd.DataExchangeObserver\n");
                            outF.write("control.deo.protocol pcd");
            
                            outF.close();
                        } catch (IOException e) {
                            System.err.println("Something went wrong writing file: "+name);
                        } finally {
                            fileNames.add(name);
                        }
                    }
                }
            }
        }
            
        File newFileF = new File("CombinedRuns.bat");
        try {
            newFileF.createNewFile();   
            BufferedWriter outF = new BufferedWriter(new FileWriter(newFileF,false));
            
            for (String f : fileNames) {
                System.out.println("file: "+f);
                outF.write("@cd \"C:/Users/Sam/git/PCD/PCD-PeerSim\"\n");
                //outF.write("@rm \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/"+f+".txt\"\n");
                outF.write("@touch \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/"+f+".txt\"\n");
                outF.write("@echo '' > \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/"+f+".txt\" 2>&1\n");
                for (int i = 0; i < 10; i += 1) {
                    outF.write("@echo "+f+" - Run "+(i+1)+" Start\n");
                    outF.write("@java -Xmx12g -Xms4g -XX:+UseConcMarkSweepGC -classpath .;lib/peersim-1.0.5.jar;lib/jpl.jar;lib/jep-2.3.0.jar;lib/djep-1.0.0.jar;lin/peersim-doclet.jar;bin peersim.Simulator \"conf/PCD-Conf_"+f+".txt\""
                        + " >> \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/"+f+".txt\" 2>&1\n");
                }
                outF.write("@echo Generating graphs for "+f+"...\n");
                outF.write("@Rscript \"C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r\" \""+f+"\""
                        + " >> \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/"+f+".txt\" 2>&1\n");
                
                String fClean = f.replaceAll(",", "-");
                outF.write("@cd \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/"+f+"/EPS\"\n");
//                outF.write("@mv \""+f+"-messages-sankey.eps\" \""+fClean+"-messages-sankey.eps\"\n");
//                outF.write("@mv \""+f+"-messages.eps\" \""+fClean+"-messages.eps\"\n");
//                outF.write("@mv \""+f+"-cycleBreakdown.eps\" \""+fClean+"-cycleBreakdown.eps\"\n");
//                outF.write("@mv \""+f+"-utilScatterAll-Fault.eps\" \""+fClean+"-utilScatterAll-Fault.eps\"\n");
//                outF.write("@mv \""+f+"-utilScatterAll-NoFault.eps\" \""+fClean+"-utilScatterAll-NoFault.eps\"\n");
//                outF.write("@mv \""+f+"-utility-final.eps\" \""+fClean+"-utility-final.eps\"\n");
                
                outF.write("@touch Graphs.tex\n");
                outF.write("@echo \\documentclass{article} >> Graphs.tex\n");
                outF.write("@echo \\usepackage[utf8]{inputenc} >> Graphs.tex\n");
                outF.write("@echo \\usepackage[landscape]{geometry} >> Graphs.tex\n");
                outF.write("@echo \\usepackage{graphicx} >> Graphs.tex\n");
                outF.write("@echo \\usepackage{epstopdf} >> Graphs.tex\n");                    
                outF.write("@echo \\title{"+f+"} >> Graphs.tex\n");
                outF.write("@echo \\begin{document} >> Graphs.tex\n");
                outF.write("@echo \\maketitle >> Graphs.tex\n");
                outF.write("@echo \\includegraphics[width=\\textwidth]{"+fClean+"-messages-sankey.eps} >> Graphs.tex\n");
                outF.write("@echo \\newpage >> Graphs.tex\n");
                outF.write("@echo \\includegraphics[width=\\textwidth]{"+fClean+"-messages.eps} >> Graphs.tex\n");
                outF.write("@echo \\newpage >> Graphs.tex\n");
                outF.write("@echo \\includegraphics[width=\\textwidth]{"+fClean+"-cycleBreakdown.eps} >> Graphs.tex\n");
                outF.write("@echo \\newpage >> Graphs.tex\n");
                outF.write("@echo \\includegraphics[width=\\textwidth]{"+fClean+"-utilScatterAll-Fault.eps} >> Graphs.tex\n");
                outF.write("@echo \\newpage >> Graphs.tex\n");
                outF.write("@echo \\includegraphics[width=\\textwidth]{"+fClean+"-utilScatterAll-NoFault.eps} >> Graphs.tex\n");
                outF.write("@echo \\newpage >> Graphs.tex\n");
                outF.write("@echo \\includegraphics[width=\\textwidth]{"+fClean+"-utility-final.eps} >> Graphs.tex\n");
                outF.write("@echo \\newpage >> Graphs.tex\n");
                outF.write("@echo \\end{document} >> Graphs.tex\n");
                
                outF.write("pdflatex \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/"+f+"/EPS/Graphs.tex\""
                        + " >> \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/"+f+".txt\" 2>&1\n");
                outF.write("@sleep 1\n");
                //outF.write("@rm \"C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/"+f+"/Graphs.tex\"\n");
                outF.write("@rm \"Graphs.tex\"\n");
                outF.write("@rm \"Graphs.aux\"\n");
                outF.write("@rm \"Graphs.log\"\n");
                outF.write("@mv \"Graphs.pdf\" \"C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/"+fClean+".pdf\"\n");
                outF.write("@echo \"\"\n");
                //outF.write("rm $2.aux $2.bbl $2.blg $2.log $2.toc $2.out $2.lof $2.lot $2.out $2.toc texput.log");
                
//                File newTexFile = new File("C:/Users/Sam/Dropbox/PhD/ExperimentRes/csv/"+f+"/EPS/Graphs.tex");
//                try {
//                    new File("C:/Users/Sam/Dropbox/PhD/ExperimentRes/csv/"+f+"/EPS").mkdirs();
//                    newTexFile.createNewFile();
//                    BufferedWriter outTexF = new BufferedWriter(new FileWriter(newTexFile,false));
//                    outTexF.write("\\documentclass{article}\n");
//                    outTexF.write("\\usepackage[utf8]{inputenc}\n");
//                    outTexF.write("\\usepackage[landscape]{geometry}\n");
//                    outTexF.write("\\usepackage{graphicx}\n");
//                    outTexF.write("\\usepackage{epstopdf}\n");                    
//                    outTexF.write("\\title{"+f+"}\n");
//                    outTexF.write("\\begin{document}\n");
//                    outTexF.write("\\maketitle\n");
//                    outTexF.write("\\includegraphics[width=\\textwidth]{"+fClean+"-messages-sankey.eps}\n");
//                    outTexF.write("\\newpage\n");
//                    outTexF.write("\\includegraphics[width=\\textwidth]{"+fClean+"-messages.eps}\n");
//                    outTexF.write("\\newpage\n");
//                    outTexF.write("\\includegraphics[width=\\textwidth]{"+fClean+"-cycleBreakdown.eps}\n");
//                    outTexF.write("\\newpage\n");
//                    outTexF.write("\\includegraphics[width=\\textwidth]{"+fClean+"-utilScatterAll-Fault.eps}\n");
//                    outTexF.write("\\newpage\n");
//                    outTexF.write("\\includegraphics[width=\\textwidth]{"+fClean+"-utilScatterAll-NoFault.eps}\n");
//                    outTexF.write("\\newpage\n");
//                    outTexF.write("\\includegraphics[width=\\textwidth]{"+fClean+"-utility-final.eps}\n");
//                    outTexF.write("\\newpage\n");
//                    outTexF.write("\\end{document}\n");
//                    outTexF.close();
//                } catch (IOException e) {
//                    System.err.println("Something went wrong creating the LaTeX file");
//                    e.printStackTrace();
//                }
                outF.write("\n");
            }
            
            outF.close();
        } catch (IOException e) {
            System.err.println("Something went wrong creating the batch file");
            e.printStackTrace();
        } 
    }
}
