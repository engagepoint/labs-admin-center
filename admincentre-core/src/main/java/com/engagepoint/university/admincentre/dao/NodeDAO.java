package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.Node;


public class NodeDAO extends AbstractDAO<Node> {
    private static volatile NodeDAO instance;

    private NodeDAO() {
        super(Node.class);
    }

    public static NodeDAO getInstance() {
        if (instance == null) {
            instance = new NodeDAO();
        }
        return instance;
    }
}
