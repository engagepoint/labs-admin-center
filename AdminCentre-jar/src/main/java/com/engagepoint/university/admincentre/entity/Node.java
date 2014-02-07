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

    public Node(String parentNodeId, String name) {
        this.parentNodeId = parentNodeId;
        this.id = (parentNodeId.equals("/") ? "/" + name : this.parentNodeId + "/" + name);
        this.name = name;

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

    public void addChildNodeId(String childNodeId) {

        this.childNodeIdList.add(childNodeId);
    }

    public void addKeyId(String keyId) {

        this.keyIdList.add(keyId);
    }
}