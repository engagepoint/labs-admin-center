package com.engagepoint.university.admincentre;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

public class ConsoleController {


    private final static int FIX_LENGTH = 30;
    private final static StringBuilder ALIGN_STRING = new StringBuilder("---");
    private Preferences currentPreferences = new NodePreferences(null, "");

    public Preferences getCurrentPreferences() {
        return currentPreferences;
    }

    public void setCurrentPreferences(Preferences currentPreferences) {
        this.currentPreferences = currentPreferences;
    }


    public void showHelp() {
        System.out.println("Options ...");
        for (Commands commands : Commands.values()) {
            StringBuilder stringBuilder = buildAlignmentString(commands.getName().length());
            System.out.println("  " + commands.getName() + stringBuilder + commands.getDescription());
        }
        System.out.println();
    }


    private StringBuilder buildAlignmentString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < FIX_LENGTH - length; i++) {
            stringBuilder = stringBuilder.append(" ");
        }
        return stringBuilder;
    }

    public void displayNodes(Preferences preference) {
        System.out.println(ALIGN_STRING + " name = " + preference.name());
        displayKeys(preference);
        try {
            if (preference.childrenNames().length != 0) {
                ALIGN_STRING.insert(0, "   ");
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");

                for (int i = 0; i < preference.childrenNames().length; i++) {
                    displayNodes(preference.node(
                            preference.childrenNames()[i]));
                }
                ALIGN_STRING.delete(0, 3);

            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void displayKeys(Preferences preferance) {
        String[] keys;
        try {
            keys = preferance.keys();
            if (keys.length != 0) {
                for (int i = 0; i < keys.length; i++) {

                    System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3)
                            + " Key = " + keys[i] + ";" + "Value = "
                            + preferance.get(keys[i], "value wasn`t found"));

                }
                System.out.println();
            }
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean chooseChildNode(String childNodeId) {

        this.currentPreferences = currentPreferences.node(childNodeId);
        displayNodes(currentPreferences);
     return true;
    }

    public boolean chooseParentNode() {
        this.currentPreferences = currentPreferences.parent();
        displayNodes(this.currentPreferences);

        return true;
    }

    public void createNode(String nodeName) {
       String newPath =  (currentPreferences.absolutePath().equals("/") ? "/" + nodeName
                : currentPreferences.absolutePath() + "/" + nodeName);
        currentPreferences.node(newPath);
        currentPreferences.node(currentPreferences.absolutePath());
        displayNodes(this.currentPreferences);
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        currentPreferences.put(keyName, keyValue);
        displayNodes(currentPreferences);

    }

    // //TODO
    public boolean nameValidation(String name) {
        if (Character.isDigit(name.charAt(0))) {

        }
        return false;
    }


}
