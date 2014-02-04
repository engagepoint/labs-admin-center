package com.engagepoint.university.admincentre;

import java.util.Iterator;
import java.util.List;

public class ConsoleController {

    private static Node currentNode = DB.getParentNode();

    private final static int FIX_LENGTH = 30;
    private final static StringBuilder ALIGN_STRING = new StringBuilder("---");

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
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

    public void displayNodes(Node node) {
        System.out.println(ALIGN_STRING + "com.engagepoint.university.admincentre.Node name = " + node.getName());
        displayKeys(node);
        if( (node.getChildNodes() != null) && (!node.getChildNodes().isEmpty()) ){
            ALIGN_STRING.insert(0, "   ");
            System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
            System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
            Iterator iterator = node.getChildNodes().iterator();
            while (iterator.hasNext()) {
                Node n = (Node) iterator.next();
                displayNodes(n);
            }
            if (!iterator.hasNext()) {
                ALIGN_STRING.delete(0, 3);
            }
        }
    }

    private void displayKeys(Node node) {
        List<Key> keyList = node.getKeys();
        if (keyList != null) {
            for (Key key : keyList) {
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "   com.engagepoint.university.admincentre.Key = " + key.getKey() + ";" +
                        " Type = " + key.getType() + "; Value = " + key.getValue());

            }
            System.out.println();
        }
    }

    public boolean chooseChildNode(String childNodeName) {
        List<Node> nodeList = currentNode.getChildNodes();
        if (nodeList != null) {
            for (Node node : nodeList) {
                if (node.getName().equals(childNodeName)) {
                    currentNode = node;
                    displayNodes(currentNode);
                    System.out.println();
                    System.out.println("Current com.engagepoint.university.admincentre.Node node is ----> " + childNodeName);
                    return true;
                }
            }
        }
        System.out.println();
        System.out.println("Wrong child com.engagepoint.university.admincentre.Node name...");
        return false;
    }


    public boolean chooseParentNode() {
        Node parentNode = currentNode.getParentNode();
        if (parentNode != null) {
            currentNode = parentNode;
            displayNodes(currentNode);
            System.out.println();
            System.out.println("Current com.engagepoint.university.admincentre.Node node is ----> " + currentNode.getName());
            return true;
        }
        System.out.println();
        System.out.println("Wrong parent com.engagepoint.university.admincentre.Node name...");
        return false;
    }

    public void createNode(String nodeName) {
        Node newNode = new Node();
        newNode.setName(nodeName);
        newNode.setParentNode(currentNode);
        currentNode.getChildNodes().add(newNode);
        displayNodes(currentNode);
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        Key newKey = new Key(keyName, keyType, keyValue);
        currentNode.getKeys().add(newKey);
        displayNodes(currentNode);
    }

    //TODO
    public boolean nameValidation(String name) {
        if (Character.isDigit(name.charAt(0))) {

        }
        return false;
    }
}
