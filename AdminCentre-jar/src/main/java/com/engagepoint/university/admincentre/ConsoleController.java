package com.engagepoint.university.admincentre;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.dao.NodeDAO;


public class ConsoleController {


    private final static int FIX_LENGTH = 30;
    private final static StringBuilder ALIGN_STRING = new StringBuilder("---");
    private final NodeDAO nodeDAO = new NodeDAO();
    private final KeyDAO keyDAO = new KeyDAO();


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

    // public boolean chooseChildNode(String childNodeId) {
    // try {
    // Node node = nodeDAO.read(childNodeId);
    // displayNodes(node);
    // currentNode = node;
    // return true;
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return false;
    // }
    //
    //
    // public boolean chooseParentNode() {
    // Node node = nodeDAO.getRoot();
    // displayNodes(node);
    // currentNode = node;
    // return true;
    // }
    //
    // public void createNode(String nodeName) {
    //
    // Node newNode = new Node();
    // newNode.setName(nodeName);
    // currentNode.addChildNodeId("");
    // try {
    // nodeDAO.create(newNode);
    // nodeDAO.update(currentNode);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // displayNodes(currentNode);
    // }
    //
    // public void createKey(String keyName, String keyType, String keyValue) {
    // Key newKey = new Key("", keyName, KeyType.valueOf(keyType), keyValue);
    // currentNode.addKeyId(null);
    // try {
    // keyDAO.create(newKey);
    // nodeDAO.update(currentNode);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // displayNodes(currentNode);
    // }
    //
    // //TODO
    // public boolean nameValidation(String name) {
    // if (Character.isDigit(name.charAt(0))) {
    //
    // }
    // return false;
    // }


}
