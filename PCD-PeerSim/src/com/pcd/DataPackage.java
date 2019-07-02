package com.pcd;

import java.util.ArrayList;

public class DataPackage {
	public ArrayList<DataElement> dataItems;
	public ArrayList<String> transactionRecords;
	public ArrayList<DataPolicy> obligations;
	
	public DataPackage() {
		dataItems = new ArrayList<DataElement>();
		transactionRecords = new ArrayList<String>();
		obligations = new ArrayList<DataPolicy>();
	}
	
	public void encrypt() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			//Unable to simulate sleep
			System.err.println("Unable to simulate encryption");
		}
	}
	
	public void decrypt() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			//Unable to simulate sleep
			System.err.println("Unable to simulate decryption");
		}
	}
	
	public String toString() {
	    String dataPackage = "DATA PACKAGE: [\n";
	    
	    if (dataItems.size() > 0) {
    	    dataPackage += "Data Elements: ";
    	    for (DataElement d : dataItems) {
    	        dataPackage += d.dataID+" ("+d.data+"), ";
    	    }
            dataPackage = dataPackage.substring(0, dataPackage.length()-2)+"\n";
	    }
    	 
	    if (transactionRecords.size() > 0) {   
    	    dataPackage += "Transaction Records: ";
    	    for (String tR : transactionRecords) {
    	        dataPackage += tR+", ";
    	    }
    	    dataPackage = dataPackage.substring(0, dataPackage.length()-2)+"\n";
    	}
	    
	    if (obligations.size() > 0) {
    	    for (DataPolicy pol : obligations) {
    	        dataPackage += pol.toString()+"\n";
    	    }
            dataPackage = dataPackage.substring(0, dataPackage.length()-2)+"\n";
	    }
	    
	    dataPackage += "]";
	    
	    return dataPackage;
	}
}
