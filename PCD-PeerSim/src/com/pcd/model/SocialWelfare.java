package com.pcd.model;

import java.util.ArrayList;

public class SocialWelfare {
    public double total;
    public ArrayList<Double> vals;
    
    public double average;
    public double deviation;
    public double min;
    public double max;
    
    private boolean firstVal = false;
    private boolean calc = false;
    
    public SocialWelfare() {
        total = 0.0;
        vals = new ArrayList<Double>();
        
        average = 0.0;
        deviation = 0.0;
        min = 0.0;
        max = 0.0;
    }
    
    public void addValue(double val) {
        if (!firstVal) {
            min = val;
            max = val;
            firstVal = true;
        }
        
        total += val;
        vals.add(val);
        
        if (val < min) { min = val;}
        if (val > max) { max = val;}
    }
    
    public String getValues() {
        String s = "";
        if (vals.size() > 0) {
            for (Double d : vals) {            
                s += d+",";
            }
            s = s.substring(0,s.length()-1);
        }
        return "\""+s+"\"";
    }
    
    public void calculate() {
        if (!calc && vals.size() > 0) {
            average = (total / vals.size());
            
            double var = 0.0;
            for (Double v : vals) {
                var += Math.pow((v - average),2);
            }
            var /= vals.size();
            deviation = Math.sqrt(var);
        }
    }
}
