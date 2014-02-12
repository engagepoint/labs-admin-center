package com.engagepoint.university.admincentre.entity;



public class Key extends AbstractEntity {

    private static final long serialVersionUID = -5430748795395302687L;

    private KeyType type;
    //TODO change value type to byte
    private String value;

    public Key() {

    }

    public Key(String parentNodeId, String name, KeyType type, String value) {
        super(name, parentNodeId);
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