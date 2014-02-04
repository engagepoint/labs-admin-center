package com.engagepoint.university.admincentre;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.dao.NodeDAO;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.Node;


public class ConsoleController {

    private static Node currentNode = new NodeDAO().getRoot();

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
        System.out.println(ALIGN_STRING + " name = " + node.getName());
        NodeDAO nodeDAO = new NodeDAO();
        displayKeys(node);
        if (!node.getChildNodeIdList().isEmpty()) {
            ALIGN_STRING.insert(0, "   ");
            System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
            System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
            Iterator iterator = node.getChildNodeIdList().iterator();
            while (iterator.hasNext()) {
                Node n = null;
                try {
                    n = nodeDAO.read((String) iterator.next());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                displayNodes(n);
            }
            if (!iterator.hasNext()) {
                ALIGN_STRING.delete(0, 3);
            }
        }
    }

    private void displayKeys(Node node) {
        KeyDAO keyDAO = new KeyDAO();
        List<String> keyIdList = node.getKeyIdList();
        if (!keyIdList.isEmpty()) {
            for (String keyId : keyIdList) {
                Key key;
                try {
                    key = keyDAO.read(keyId);
                    System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3)
                            + " Key = " + key.getName() + ";" + " Type = " + key.getType()
                            + "; Value = " + key.getValue());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            System.out.println();
        }
    }

//    public boolean chooseChildNode(String childNodeName) {
//        List<Node> nodeList = currentNode.getChildNodes();
//        if (nodeList != null) {
//            for (Node node : nodeList) {
//                if (node.getName().equals(childNodeName)) {
//                    currentNode = node;
//                    displayNodes(currentNode);
//                    System.out.println();
//                    System.out.println("Current Node node is ----> " + childNodeName);
//                    return true;
//                }
//            }
//        }
//        System.out.println();
//        System.out.println("Wrong child Node name...");
//        return false;
//    }
//
//
//    public boolean chooseParentNode() {
//        Node parentNode = currentNode.getParentNode();
//        if (parentNode != null) {
//            currentNode = parentNode;
//            displayNodes(currentNode);
//            System.out.println();
//            System.out.println("Current Node node is ----> " + currentNode.getName());
//            return true;
//        }
//        System.out.println();
//        System.out.println("Wrong parent Node name...");
//        return false;
//    }
//
//    public void createNode(String nodeName) {
//        Node newNode = new Node();
//        newNode.setName(nodeName);
//        newNode.setParentNode(currentNode);
//        currentNode.getChildNodes().add(newNode);
//        displayNodes(currentNode);
//    }
//
//    public void createKey(String keyName, String keyType, String keyValue) {
//        Key newKey = new Key(keyName, keyType, keyValue);
//        currentNode.getKeys().add(newKey);
//        displayNodes(currentNode);
//    }
//
//    //TODO
//    public boolean nameValidation(String name) {
//        if (Character.isDigit(name.charAt(0))) {
//
//        }
//        return false;
//    }
}
