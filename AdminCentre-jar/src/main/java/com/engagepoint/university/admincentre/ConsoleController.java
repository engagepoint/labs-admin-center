package com.engagepoint.university.admincentre;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.dao.NodeDAO;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.entity.Node;

public class ConsoleController {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final StringBuilder ALIGN_STRING = new StringBuilder("---");
    private final NodeDAO nodeDAO = new NodeDAO();
    private final KeyDAO keyDAO = new KeyDAO();

    private Node currentNode = new NodeDAO().getRoot();


    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node node) {
        this.currentNode = node;
    }


    public void showHelp() {
        System.out.println("Options ...");
        for (Commands commands : Commands.values()) {
            String name = commands.getName();
            StringBuilder stringBuilder = buildAlignmentString(name.length());
            System.out.println("  " + name + stringBuilder + commands.getDescription());
        }
        System.out.println();
    }

    public void showVersion() {
        System.out.println("Current application version is " + 1.0);
    }

    private StringBuilder buildAlignmentString(int length) {
        int fixLength = 30;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fixLength - length; i++) {
            stringBuilder = stringBuilder.append(" ");
        }
        return stringBuilder;
    }

    public void displayNodes(Node node) {
        System.out.println(ALIGN_STRING + "Node id ---->" + node.getId());
        //System.out.println(ALIGN_STRING + "Node name -->" + node.getName());
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
                            + "     Key = " + key.getName() + ";" + " Type = " + key.getType()
                            + "; Value = " + key.getValue());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean selectNode(String nodeId) {
        try {
            Node node = nodeDAO.read(nodeId);
            if (node != null) {
                displayNodes(node);
                currentNode = node;
                return true;
            } else {
                System.out.println("Wrong path to node. Enter correct path.");
            }
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
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
        if (nameValidation(nodeName)) {
            Node newNode = new Node();
            newNode.setName(nodeName);
            try {
                currentNode.addChildNodeId(newNode);
                nodeDAO.create(newNode);
                nodeDAO.update(currentNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayNodes(currentNode);
        }
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        if (nameValidation(keyName) &&
                keyTypeValidation(keyType)) {
            Key newKey = new Key(keyName, KeyType.valueOf(keyType), keyValue);
            try {
                currentNode.addKeyId(newKey);
                keyDAO.create(newKey);
                nodeDAO.update(currentNode);
            } catch (Exception e) {
                LOGGER.warning(e.getMessage());
            }
        }
        displayNodes(currentNode);
    }

    public boolean nameValidation(String name) {
        boolean value = name.matches("^[A-Z][a-z0-9]+([A-Z][a-z0-9]+|[A-Z]$)*$");
        if (!value) {
            System.out.println("You enter not valid name...");
        }
        return value;
    }

    /**
     * Allows to verify entered key type from console
     *
     * @param keyType String param which comes from console
     * @return true if key type exist in enum KeyType
     */
    public boolean keyTypeValidation(String keyType) {
        try {
            KeyType.valueOf(keyType);
        } catch (IllegalArgumentException e) {
            KeyType[] keyTypeList = KeyType.values();
            System.out.println("You enter wrong key type. Use one of the next types :");
            for (KeyType keyTypeTemp : keyTypeList) {
                System.out.println("  " + keyTypeTemp.toString());
            }
            return false;
        }
        return true;
    }
}
