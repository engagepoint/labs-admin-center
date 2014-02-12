package com.engagepoint.university.admincentre.datatransfer;

import java.util.prefs.Preferences;

import javax.ejb.Stateless;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

@Stateless
public class DataBean {
    public Preferences getRootPreferences() {
        return new NodePreferences(null, "");
    }
}
