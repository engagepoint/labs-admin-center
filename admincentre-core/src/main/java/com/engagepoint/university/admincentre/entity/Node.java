package com.engagepoint.university.admincentre.entity;

import java.util.LinkedList;
import java.util.List;
import org.hibernate.search.annotations.Indexed;

@Indexed
public class Node extends AbstractEntity {

    private static final long serialVersionUID = 7481984532481844209L;
    private List<String> keyIdList = new LinkedList<String>();
    private List<String> childNodeIdList = new LinkedList<String>();

    public Node(String parentNodeId, String name) {
        super(name, parentNodeId);
    }

    public Node() {
    }

    public void setChildNodeIdList(List<String> childNodeIdList) {
        this.childNodeIdList = childNodeIdList;
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
        if (!childNodeIdList.contains(childNodeId)) {
            this.childNodeIdList.add(childNodeId);
        }
    }

    public void addKeyId(String keyId) {
        if (!keyIdList.contains(keyId)) {
            this.keyIdList.add(keyId);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node entity = (Node) obj;
        return super.getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return super.getId().hashCode();
    }

    @Override
    public String toString() {
        return super.getId();
    }
}
