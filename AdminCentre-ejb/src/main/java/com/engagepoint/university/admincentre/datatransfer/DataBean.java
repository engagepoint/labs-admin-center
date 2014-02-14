package com.engagepoint.university.admincentre.datatransfer;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.ejb.Stateless;

import com.engagepoint.university.admincentre.entity.Document;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.TreeProperties;
import com.engagepoint.university.admincentre.preferences.NodePreferences;


@Stateless
public class DataBean {


    public TreeProperties getPreferencesTree(Preferences preferences) {
        TreeProperties root = new TreeProperties("root", null);
        // Preferences preferences = new NodePreferences(null, "");
        buildTree((NodePreferences) preferences, root);
        return root;

    }

    private void buildTree(NodePreferences preferences, TreeProperties parentTreeNode) {

        TreeProperties treeNode = new TreeProperties(new Document(preferences.getCurrentNode()
                .getId(), preferences.name(),
                "-", "File"),
                parentTreeNode);

        addLeaves(preferences, parentTreeNode);
        try {
            if (preferences.childrenNames().length != 0) {
                for (int i = 0; i < preferences.childrenNames().length; i++) {
                    buildTree((NodePreferences) preferences.node(preferences.childrenNames()[i]),
                            treeNode);
                }

            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    };

    private void addLeaves(NodePreferences nodePreferences, TreeProperties parentTreeNode) {
        try {

            for (int i = 0; i < nodePreferences.keys().length; i++) {
                Key key = nodePreferences.getKey(nodePreferences.keys()[i]);
                new TreeProperties(new Document(key.getId(), key.getName(), key.getValue(), key
                        .getType()
                        .toString()), parentTreeNode);
            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
