package com.engagepoint.university.admincentre.entity;
import java.io.Serializable;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Indexed
public abstract class AbstractEntity implements Serializable {
    @Field(analyze = Analyze.NO)
    protected String id;
    @Field(analyze = Analyze.NO)
    protected String name;
    @Field(analyze = Analyze.NO)
    protected String parentNodeId = "";

    public String getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setId();
    }

    public String getId() {
        return id;
    }

    protected void setId() {
        this.id = this.parentNodeId + "/" + name;
    }

}
