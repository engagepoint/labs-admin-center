package com.engagepoint.university.admincentre.entity;
import java.io.Serializable;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Indexed
public abstract class AbstractEntity implements Serializable {
    @Field(analyze = Analyze.NO)
    private String id;
    @Field(analyze = Analyze.NO)
    private String name;
    @Field(analyze = Analyze.NO)
    private String parentNodeId = "";

    public AbstractEntity() {

    }

    public AbstractEntity(String name, String parentNodeId) {
        super();
        this.parentNodeId = parentNodeId;
        this.id = (parentNodeId.equals("/") ? "/" + name : this.parentNodeId + "/" + name);
        this.name = name;
       
    }

    public String getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
        this.id = (parentNodeId.equals("/") ? "/" + name : this.parentNodeId + "/" + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.id = (parentNodeId.equals("/") ? "/" + name : this.parentNodeId + "/" + name);
    }

    public String getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if ( !(obj instanceof AbstractEntity) )
			return false;
		AbstractEntity entity = (AbstractEntity) obj;
		return this.id.equals(entity.id);
    }
    
    @Override
    public int hashCode() {
    	return this.id.hashCode();
    }
}
