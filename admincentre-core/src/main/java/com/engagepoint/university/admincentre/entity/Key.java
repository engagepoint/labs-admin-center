package com.engagepoint.university.admincentre.entity;

import org.hibernate.search.annotations.Indexed;

@Indexed
public class Key extends AbstractEntity {

    private static final long serialVersionUID = -5430748795395302687L;
    private KeyType type;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Key)) {
            return false;
        }
        Key key = (Key) obj;
        return super.getId().equals(key.getId())
                && type == key.type
                && value.equals(key.value);
    }

    @Override
    public int hashCode() {
        return (super.getId() + type.toString() + value).hashCode();
    }
    
    /* do not delete!*/
    @Override
    public String toString() {
        return super.getId() + " " + type + " " + value;
    }
}