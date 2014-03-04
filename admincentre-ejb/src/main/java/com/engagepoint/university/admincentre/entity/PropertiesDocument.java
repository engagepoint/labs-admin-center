package com.engagepoint.university.admincentre.entity;

import java.io.Serializable;

public class PropertiesDocument implements Serializable, Comparable<PropertiesDocument> {

    private static final long serialVersionUID = 12L;
    private String absolutePath;
    private String name;
    private String value;
    private String type;
    private String oldName;
    private boolean editable;
    private boolean file;
    private boolean directoryForAdding = false;

    public PropertiesDocument(String absolutePath, String name, String value, String type) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.value = value;
        this.type = type;
        this.oldName = name;
        this.file = "File".equals(type);
    }
    public PropertiesDocument(String absolutePath, String name, String value, String type, boolean file) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.value = value;
        this.type = (file) ? "File" : type;
        this.oldName = oldName;
        this.file = file;
    }
    public PropertiesDocument(String absolutePath, String name, String value, String type, String oldName, boolean file) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.value = value;
        this.type = (file) ? "File" : type;
        this.oldName = oldName;
        this.file = file;
    }

    public PropertiesDocument() {
    }

    public void setFile(boolean file) {
        this.file = file;
        this.type = (file) ? "File" : "";
    }

    public boolean isFile() {
        return file;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.oldName = this.name;
        this.name = name;
    }

    public String getOldName() {
        return oldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        file = ("File".equals(type)) ? true : false;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String editAction() {
        this.setEditable(true);
        return null;
    }

    public String saveAction() {
        //get all existing value but set "editable" to false
        this.setEditable(false);
        //return to current page
        return null;
    }

    public boolean isDirectoryForAdding() {
        return directoryForAdding;
    }

    public void setDirectoryForAdding(boolean directoryForAdding) {
        this.directoryForAdding = directoryForAdding;
    }

    @Override
    public int hashCode() {
        int result = 31;
        result += name.hashCode();
        result += value.hashCode();
        result += type.hashCode();
        result = (result < 0) ? -1 * result : result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PropertiesDocument) {
            PropertiesDocument other = (PropertiesDocument) obj;
            if ((this.oldName.equals(other.name))
                    && (this.type.equals(other.type))
                    && (this.value.equals(other.value))
                    && (this.absolutePath.equals(other.absolutePath))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(PropertiesDocument document) {
        return this.getName().compareTo(document.getName());
    }
}
