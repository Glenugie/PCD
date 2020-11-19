package com.pcd;

import java.util.ArrayList;

import com.pcd.model.DataConfig;

public class PrologInterface {	    
    public static boolean TRUE_RANDOM;
    public static boolean REASONING;
    public static boolean DATA_REQUEST_FORWARDING;
    public static int MAX_TRANSACTIONS;
    public static int TRANS_LIFETIME;
    public static int MIN_UTIL;
    
	public static boolean printSimInfo;
	public static boolean debugProlog;	
	public static boolean debugMessages;
	
	public static double confSeed;
	public static int confExperiments;
	public static int confCycles;
	public static int confPeers;
	public static int confMaxForward;
	
	public static int confCycleCost;
    public static int confMinBudget;
    public static int confMaxBudget;
    public static int confMinPols;
    public static int confMaxPols;
	public static int confFaultyPeers;
	public static int confFaultRate;
	public static int confAltruistic;
	public static int confFair;
	public static int confMaxNeighbours;
	public static boolean confDefaultPermit;
	
	public static String confDataFile;
	public static ArrayList<DataConfig> confDataTypes;
	public static String confPolicyFile;
    public static ArrayList<String> confProtoPolicies;
    public static ArrayList<String> confProtoPermitPolicies;
	
	public static int confTopology;
	public static int confTopologyVal;
	public static boolean confNewConnections;
	
	private static String debugFilter = "";
	
	private static boolean initialised = false;	
	
//	private static void init() {
//		/*Query q = new Query("consult", new Term[] { new Atom("PL_PCD.pl")});
//		HashMap<String,Term> res = (HashMap<String, Term>) q.oneSolution();
//		if (res == null) {
//			//System.out.println(q.toString()+": FALSE");
//		} else {
//			//System.out.println(q.toString()+": TRUE ("+res.keySet()+")");
//			initialised = true;
//		}		
//		q.close();*/		
//
//        //PrologInterface.runGroundQuery("trace",new Term[] { new Atom("requestData")});
//        //PrologInterface.runGroundQuery("trace",new Term[] { new Atom("noRequest")});
//	}
//	
//	public static HashSet<Term> runQuery(String pred, Term[] terms, String var) {
//		if (!initialised) { init();}
//		
//		Query q = new Query(new Compound(pred, terms));
//		Map<String,Term> res = q.oneSolution();
//		if (res == null) {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": FALSE");}
//		} else {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": TRUE ("+res.keySet()+")");}	
//			HashSet<Term> result = new HashSet<Term>();	
//			while (q.hasMoreSolutions()) {
//				Map<String,Term> binding = q.nextSolution();				 
//				Term t = (Term) binding.get(var);
//				result.add(t);
//				if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println("\t"+var+": "+t+" ("+binding.keySet()+")");}
//			}
//			q.close();
//			return result;
//		}
//		q.close();
//		return new HashSet<Term>();
//	}
//	
//	public static HashMap<String,HashSet<Term>> runMultiVarQuery(String pred, Term[] terms, String[] vars) {
//		if (!initialised) { init();}
//		
//		HashMap<String,HashSet<Term>> resultMap = new HashMap<String,HashSet<Term>>();	
//		for (String var : vars) {
//			resultMap.put(var, new HashSet<Term>());
//		}
//		
//		Query q = new Query(new Compound(pred, terms));
//		Map<String,Term> res = q.oneSolution();
//		if (res == null) {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": FALSE");}
//		} else {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": TRUE ("+res.keySet()+")");}	
//			HashSet<Term> result = new HashSet<Term>();	
//			while (q.hasMoreSolutions()) {
//				Map<String,Term> binding = q.nextSolution();			
//				for (String var : vars) {
//					Term t = (Term) binding.get(var);
//					resultMap.get(var).add(t);
//					if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println("\t"+var+": "+t+" ("+binding.keySet()+")");}
//				}
//			}
//		}
//
//		q.close();
//		return resultMap;
//	}
//	
//	public static Term runQueryFirstResult(String pred, Term[] terms, String var) {
//		if (!initialised) { init();}
//		
//		Query q = new Query(new Compound(pred, terms));
//		Map<String,Term> res = q.oneSolution();
//		if (res == null) {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": FALSE");}
//		} else {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": TRUE ("+res.keySet()+")");}	
//			while (q.hasMoreSolutions()) {
//				Map<String,Term> binding = q.nextSolution();				 
//				Term t = (Term) binding.get(var);
//				q.close();
//				if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println("\t"+var+": "+t+" ("+binding.keySet()+")");}
//				
//				return t;
//			}
//		}
//		q.close();
//		return null;
//	}
//	
//	public static boolean runGroundQuery(String pred, Term[] terms) {
//		if (!initialised) { init();}
//		
//		Query q = new Query(new Compound(pred, terms));
//		HashMap<String,Term> res = (HashMap<String, Term>) q.oneSolution();
//		if (res == null) {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": FALSE");}
//	        q.close();
//			return false;
//		} else {
//			if (debugProlog && (debugFilter.equals("") || pred.equals(debugFilter))) { System.out.println(q.toString()+": TRUE ("+res.keySet()+")");}
//	        q.close();
//			return true;
//			/*while (q.hasMoreSolutions()) {
//				Map<String,Term> binding = q.nextSolution();	
//				//System.out.println("\t"+binding.keySet());
//			}*/
//		}	
//	}
//    
//    public static void assertFact(String pred, Term[] terms) {
//        PrologInterface.runGroundQuery("assert",new Term[]{new Compound(pred,terms)});
//    }
//    
//    public static void assertFactIfNotExist(String pred, Term[] terms) {
//        if (!PrologInterface.runGroundQuery(pred,terms)) {
//            PrologInterface.runGroundQuery("assert",new Term[]{new Compound(pred,terms)});
//        }
//    }
//	
//	public static void retractFact(String pred, Term[] terms) {
//		PrologInterface.runGroundQuery("retract",new Term[]{new Compound(pred,terms)});
//	}
//	
//	public static void getListing() {
//		if (!initialised) { init();}
//		
//		Query q = new Query(new Compound("listing", new Term[0]));
//		q.oneSolution();
//		q.close();
//		/*HashMap<String,Term> res = (HashMap<String, Term>) q.oneSolution();
//		if (res == null) {
//			System.out.println(q.toString()+": FALSE");
//		} else {
//			System.out.println(q.toString());
//		}*/
//	}
//	
//	public static void dumpListing() {
//		if (!initialised) { init();}
//
//		Query q = null;
//		//q = new Query(new Compound("listing", new Term[]{new Compound("data",new Term[0])})); q.oneSolution(); q.close();
//		//q = new Query(new Compound("listing", new Term[]{new Compound("peer",new Term[0])})); q.oneSolution(); q.close();
//		//q = new Query(new Compound("listing", new Term[]{new Compound("hasData",new Term[0])})); q.oneSolution(); q.close();
//		//q = new Query(new Compound("listing", new Term[]{new Compound("group",new Term[0])})); q.oneSolution(); q.close();
//		//q = new Query(new Compound("listing", new Term[]{new Compound("policy",new Term[0])})); q.oneSolution(); q.close();
//		//q = new Query(new Compound("listing", new Term[]{new Compound("defaultPermit",new Term[0])})); q.oneSolution(); q.close();
//		//q = new Query(new Compound("listing", new Term[]{new Compound("connected",new Term[0])})); q.oneSolution(); q.close();
//		
//		//runGroundQuery("peer", new Term[]{new Variable("_")});
//		//runGroundQuery("hasData", new Term[]{new Variable("_"),new Variable("_"),new Variable("_"),new Variable("_")});
//		//runGroundQuery("group", new Term[]{new Variable("_"),new Variable("_")});
//		//runGroundQuery("policy", new Term[]{new Variable("_"),new Variable("_"),new Variable("_")});
//	}
//	
//	/*public static Term policyToTerm(Term t) {
//		Term[] pol = t.toTermArray();
//		pol[3] = Util.termArrayToList(pol[3].toTermArray());
//		pol[4] = Util.termArrayToList(pol[3].toTermArray());
//		return Util.termArrayToList(pol);
//	}*/
//	
//	// recordRequest(peer1, peer0, d7, 1, date('date(2017', '1', '25', '14', '33', '50.400300979', '0', ''UTC'', ''-')'), true)
//	public static Term[] stringToTransRecord(long peerID, String r) {
//		String rBody = r.substring(r.indexOf("(")+1,r.length()-1);
//		String[] rSplit = rBody.split(","); for (int i = 0; i < rSplit.length; i += 1) { rSplit[i] = rSplit[i].trim();}
//		rSplit[5] = rSplit[5].substring(5);
//		rSplit[12] = rSplit[12].substring(1, rSplit[12].length()-1);
//		rSplit[13] = rSplit[13].substring(1, rSplit[13].length()-2);
//		
//		Compound rDate = new Compound("date",new Term[]{
//				new org.jpl7.Integer(Integer.parseInt(rSplit[5])),
//				new org.jpl7.Integer(Integer.parseInt(rSplit[6])),
//				new org.jpl7.Integer(Integer.parseInt(rSplit[7])),
//				new org.jpl7.Integer(Integer.parseInt(rSplit[8])),
//				new org.jpl7.Integer(Integer.parseInt(rSplit[9])),
//				new org.jpl7.Float(Float.parseFloat(rSplit[10])),
//				new org.jpl7.Integer(Integer.parseInt(rSplit[11])),
//				new Atom(rSplit[12]),
//				new Atom(rSplit[13])
//		});
//		Term[] rRecon = new Term[]{new Atom("peer"+peerID),new Atom(rSplit[1]),new Atom(rSplit[2]),new Atom(rSplit[3]),new org.jpl7.Integer(Integer.parseInt(rSplit[4])),rDate,new Atom(rSplit[rSplit.length-1])};
//		
//		return rRecon;
//	}
}
