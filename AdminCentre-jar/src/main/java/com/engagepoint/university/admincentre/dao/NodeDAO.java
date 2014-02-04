package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.Node;

import java.io.IOException;

public class NodeDAO extends AbstractDAO<Node> {
    public NodeDAO() {
        super(Node.class);
    }

    public Node getRoot()  {
        try {
            return read("/root");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
