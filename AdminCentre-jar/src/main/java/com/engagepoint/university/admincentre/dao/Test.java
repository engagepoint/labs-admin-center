package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.Node;

public class Test {
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
            System.out.println(nd.getRoot().getChildNodeIdList());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
