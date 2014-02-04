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
    public void addChildNodeId(Node childNode) {
        childNode.setParentNodeId(this.getId());
        childNode.setId();
        this.childNodeIdList.add(childNode.getId());
    }

    public void addKeyId(Key key) {
        key.setParentNodeId(this.getId());
        key.setId();
        this.keyIdList.add(key.getId());
    }


}