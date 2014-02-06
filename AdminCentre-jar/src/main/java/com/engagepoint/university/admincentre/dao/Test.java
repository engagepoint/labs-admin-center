package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.Node;

import java.util.logging.Logger;

public class Test {

    private static final Logger LOGGER = Logger.getLogger(Test.class.getName());
    public Test() {
    }

    public static void main(String[] args) {
        NodeDAO nd = new NodeDAO();
        Node node = new Node();
        node.setName("node");
        Node node1 = new Node();
        node1.setName("node1");

        Node root;
        try {
            root = nd.getRoot();
            root.addChildNodeId(node);
            root.addChildNodeId(node1);
            nd.create(node);
            nd.create(node1);
            nd.update(root);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
