package com.engagepoint.university.admincentre.dao;

import java.util.prefs.Preferences;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

public class Test2 {
    public static void main(String[] args) {

            Preferences preferences = new NodePreferences(null, "");
        Preferences another = preferences.node("/newNode/Node1/Node1.1");
        Preferences another1 = preferences.node("/newNode/Node1/Node1.2");
        Preferences another2 = preferences.node("/newNode/Node1");
        another.put("11", "11");
        another1.put("12", "12");
        another2.put("13", "13");



    }
}
