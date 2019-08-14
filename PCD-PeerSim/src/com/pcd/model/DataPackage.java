package com.pcd.model;

import java.util.ArrayList;

public class DataPackage {
	public ArrayList<DataElement> dataItems;
	public ArrayList<TransactionRecord> transactionRecords;
	
	public DataPackage() {
		dataItems = new ArrayList<DataElement>();
		transactionRecords = new ArrayList<TransactionRecord>();
	}
	
	public void encrypt() {
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			//Unable to simulate sleep
//			System.err.println("Unable to simulate encryption");
//		}
	}
	
	public void decrypt() {
//		try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			//Unable to simulate sleep
//			System.err.println("Unable to simulate decryption");
//		}
	}
	
	public String toString() {
	    String dataPackage = "DATA PACKAGE: [";
	    
	    if (dataItems.size() > 0) {
    	    dataPackage += "Data Elements: ";
    	    for (DataElement d : dataItems) {
    	        dataPackage += d.dataID+" ("+d.data+"), ";
    	    }
            dataPackage = dataPackage.substring(0, dataPackage.length()-2)+" | ";
	    }
    	 
	    if (transactionRecords.size() > 0) {   
    	    dataPackage += "Transaction Records: ";
    	    for (TransactionRecord tR : transactionRecords) {
    	        dataPackage += tR.toString()+", ";
    	    }
    	    dataPackage = dataPackage.substring(0, dataPackage.length()-2)+"";
    	}
	    
	    dataPackage += "]";
	    
	    return dataPackage;
	}
}
