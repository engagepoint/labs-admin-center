package com.engagepoint.university.admincentre.entity;

import java.util.ArrayList;
import java.util.List;


public class TreeProperties {
    public static final String DEFAULT_TYPE = "default";

    private String type;

    private Object data;

    private List<TreeProperties> children;

    private TreeProperties parent;

    private boolean expanded;

    public TreeProperties() {
    }

    public TreeProperties(Object data, TreeProperties parent) {
        this.type = DEFAULT_TYPE;
        this.data = data;
        children = new ArrayList<TreeProperties>();
        this.parent = parent;
        if (this.parent != null)
            this.parent.getChildren().add(this);
    }

    public TreeProperties(String type, Object data, TreeProperties parent) {
        this.type = type;
        this.data = data;
        children = new ArrayList<TreeProperties>();
        this.parent = parent;
        if (this.parent != null)
            this.parent.getChildren().add(this);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<TreeProperties> getChildren() {
        return children;
    }

    public void setChildren(List<TreeProperties> children) {
        this.children = children;
    }

    public TreeProperties getParent() {
        return parent;
    }

    public void setParent(TreeProperties parent) {
        this.parent = parent;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;

        if (parent != null) {
            parent.setExpanded(expanded);
        }
    }

    public void addChild(TreeProperties treeNode) {
        treeNode.setParent(this);
        children.add(treeNode);
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isLeaf() {
        if (children == null)
            return true;

        return children.size() == 0;
    }

    @Override
    public String toString() {
        if (data != null)
            return data.toString();
        else
            return super.toString();
    }
}
