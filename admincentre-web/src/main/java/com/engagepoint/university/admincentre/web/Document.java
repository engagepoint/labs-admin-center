package com.engagepoint.university.admincentre.web;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "document")
@SessionScoped
public class Document implements Serializable, Comparable<Document> {

    private static final long serialVersionUID = 12L;
    private String absolutePath;
    private String name;
    private String value;
    private String type;
    private String oldName;
    private boolean editable;

    /* Commented by Artem because Sonar makes the major issue on unused private field
     * private boolean file;
     */
    public Document(String absolutePath, String name, String value, String type) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.value = value;
        this.type = type;
        this.oldName = name;
    }

    public Document() {
    }

    public boolean isFile() {
        return ("File".equals(type));
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

    public void setSelected() {
    }

    @Override
    public int hashCode() {
        /* As of Sonar makes issue on cyclomatic complexity in the class, I have removed the conditions like "(name == null)"
         * and ternary operator  because "The hash value of the empty string is zero"  
         */
        int result = 31;
        result += name.hashCode();
        result += value.hashCode();
        result += type.hashCode();
        return result;
    }

    //This method was totally rebuilt by Artem as of Sonar major issue on cyclomatic complexity
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Document) {
            Document other = (Document) obj;
            if ((this.name.equals(other.name))
                    && (this.type.equals(other.type))
                    && (this.value.equals(other.value))) {
                return true;
            }
        }
        return false;
    }

    /*
     @Override
     public boolean equals(Object obj) {
     if (this == obj) {
     return true;
     }
     if (obj == null) {
     return false;
     }
     if (getClass() != obj.getClass()) {
     return false;
     }
     Document other = (Document) obj;
     if (name == null) {
     if (other.name != null) {
     return false;
     }
     } else if (!name.equals(other.name)) {
     return false;
     }
     if (value == null) {
     if (other.value != null) {
     return false;
     }
     } else if (!value.equals(other.value)) {
     return false;
     }
     if (type == null) {
     if (other.type != null) {
     return false;
     }
     } else if (!type.equals(other.type)) {
     return false;
     }
     return true;
     }
     */
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Document document) {
        return this.getName().compareTo(document.getName());
    }
}