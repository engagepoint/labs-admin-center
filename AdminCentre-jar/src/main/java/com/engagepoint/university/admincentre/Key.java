package com.engagepoint.university.admincentre;

public class Key {
    private static final long serialVersionUID = 1L;

    private String key;
    private String type;
    private String value;

    public String getKey() {
        return key;
    }

    Key(String key, String type, String value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}