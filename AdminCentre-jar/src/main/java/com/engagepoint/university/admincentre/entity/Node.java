package com.engagepoint.university.admincentre.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.search.annotations.Indexed;

@Indexed
public class Node extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 7481984532481844209L;

    private List<String> keyIdList = new LinkedList<String>();
    private List<String> childNodeIdList = new LinkedList<String>();


    public Node() {
    }

    public Node(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public List<String> getKeyIdList() {
        return keyIdList;
    }

    public void setKeyIdList(List<String> keyIdList) {
        this.keyIdList = keyIdList;
    }

    public List<String> getChildNodeIdList() {
        return childNodeIdList;
    }

    //TODO refactor this method
    public void addChildNodeId(Node childNode) throws Exception {
        childNode.setParentNodeId(getId());
        childNode.setId();
        if (!childNodeIdList.contains(childNode.getId())) {
            childNodeIdList.add(childNode.getId());
        } else {
            throw new Exception("This node is already exists");
        }
    }

    public void addKeyId(Key key) throws Exception {
        key.setParentNodeId(getId());
        key.setId();
        if (!keyIdList.contains(key.getId())) {
            keyIdList.add(key.getId());
        } else {
            throw new Exception("This key is already exists");
        }
    }
}