package com.engagepoint.university.admincentre.dao;

import java.io.IOException;

import com.engagepoint.university.admincentre.entity.Node;

public class NodeDAO extends AbstractDAO<Node> {
    public NodeDAO() {
        super(Node.class);
    }

    // TODO delete this method
    public Node getRoot() {
        try {
            return read("/");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
