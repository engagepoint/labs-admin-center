package com.engagepoint.university.admincentre;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.dao.NodeDAO;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.entity.Node;


public class ConsoleController {

    private static Node currentNode = new NodeDAO().getRoot();

    private final static int FIX_LENGTH = 30;
    private final static StringBuilder ALIGN_STRING = new StringBuilder("---");
    private final NodeDAO nodeDAO = new NodeDAO();
    private final KeyDAO keyDAO = new KeyDAO();


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

    public boolean chooseChildNode(String childNodeId) {
        try {
            Node node = nodeDAO.read(childNodeId);
            displayNodes(node);
            currentNode = node;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean chooseParentNode() {
        Node node = nodeDAO.getRoot();
        displayNodes(node);
        currentNode = node;
        return true;
    }

    public void createNode(String nodeName) {

        Node newNode = new Node();
        newNode.setName(nodeName);
        currentNode.addChildNodeId(newNode);
        try {
            nodeDAO.create(newNode);
            nodeDAO.update(currentNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayNodes(currentNode);
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        Key newKey = new Key(keyName, KeyType.valueOf(keyType), keyValue);
        currentNode.addKeyId(newKey);
        try {
            keyDAO.create(newKey);
            nodeDAO.update(currentNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayNodes(currentNode);
    }

    //TODO
    public boolean nameValidation(String name) {
        if (Character.isDigit(name.charAt(0))) {

        }
        return false;
    }


}
