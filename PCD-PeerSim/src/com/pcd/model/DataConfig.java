package com.pcd.model;

// Parse an arbitrary length list of CSV strings, format: d[n],[Own%],[Want%],[MinU],[MaxU]
// d1,10,100,1,10
public class DataConfig {
    public String dataId;
    public int percOwn;
    public int percWant;
    public int minU;
    public int maxU;
    
    public DataConfig(String i, int o, int w, int min, int max) {
        dataId = i;
        percOwn = o;
        percWant = w;
        minU = min;
        maxU = max;
    }
}
