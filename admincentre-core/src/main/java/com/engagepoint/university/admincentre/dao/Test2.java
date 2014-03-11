package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

import java.util.prefs.Preferences;

public class Test2 {
    public static void main(String[] args) {

        Preferences preferences = new NodePreferences(null, "");
        Preferences another = preferences.node("/newNode/Node1/Node1.1");
        Preferences another1 = preferences.node("/newNode/Node1/Node1.2");
        Preferences another2 = preferences.node("/newNode/Node1");
        // try {
        // another2.removeNode();
        // } catch (BackingStoreException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        another.put("11", "11");

        another1.put("12", "12");
        another2.put("13", "13");
//        another1.put("14", "14");
//        another2.put("15", "15");
//        another1.put("16", "16");
//        another2.put("17", "17");

        // NodeDAO nodeDAO = new NodeDAO();
        //
        // try {
        // System.out.println(nodeDAO.search("ewNo").get(0).getName());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }

}
