package com.engagepoint.university.admincentre.entity;

import java.io.Serializable;

public class Key extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = -5430748795395302687L;

    private KeyType type;
    //TODO change value type to byte
    private String value;

    public Key() {

    }

    public Key(String parentNodeId, String name, KeyType type, String value) {
        this.parentNodeId = parentNodeId;
        this.id = (parentNodeId.equals("/") ? "/" + name : this.parentNodeId + "/" + name);
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public KeyType getType() {
        return type;
    }

    public void setType(KeyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}