package com.engagepoint.university.admincentre.dao;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

public class Test2 {
    public static void main(String[] args) {

            Preferences preferences = new NodePreferences(null, "");
//        Preferences another = preferences.node("/newNode/Node1/Node1.1");
//        Preferences another1 = preferences.node("/newNode/Node1/Node1.2");
//        Preferences another2 = preferences.node("/newNode/Node1");
//        another.put("11", "11");
//        another1.put("12", "12");
//        another2.put("13", "13");
          try {
			String[] names = preferences.node("newNode").node("Node1").childrenNames();
			for(int i = 0; i < names.length; i++){
				System.out.println(names[i]);
			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }
}
