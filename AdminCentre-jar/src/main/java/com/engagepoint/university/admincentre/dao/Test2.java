package com.engagepoint.university.admincentre.dao;

import java.io.IOException;
import java.util.prefs.Preferences;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

public class Test2 {
    public static void main(String[] args) {
        try {
            Preferences preferences = new NodePreferences(null, "");
            Preferences another = preferences.node("/newNode/Node1");
            another.putBoolean("bool", true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
