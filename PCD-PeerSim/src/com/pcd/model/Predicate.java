package com.pcd.model;

public class Predicate {
    public String type;
    public String[] terms;
    public long time;
    
    public Predicate(String t, String[] tr, long ti) {
        type = t;
        terms = tr;
        time = ti;
    }
}
