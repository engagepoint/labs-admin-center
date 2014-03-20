package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.preferences.NodePreferences;
import java.util.prefs.Preferences;

public class Test2 {

    public static void main(String[] args) {

        Preferences preferences = new NodePreferences(null, "");
        Preferences another = preferences.node("/newNode/Node1/Node1.1");
        Preferences another1 = preferences.node("/newNode/Node1/Node1.2");
        Preferences another2 = preferences.node("/newNode/Node1");

        another.put("11", "11");
        another1.put("12", "12");
        another2.put("13", "13");
        another2.put("qwe", "bbb");
        another2.put("asd", "aaa");
        another2.put("ert", "ddd");
    }
}
