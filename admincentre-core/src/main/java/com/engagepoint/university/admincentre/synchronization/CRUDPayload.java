package com.engagepoint.university.admincentre.synchronization;

import java.io.Serializable;

import com.engagepoint.university.admincentre.entity.AbstractEntity;

public class CRUDPayload implements Serializable {

	private static final long serialVersionUID = -703924255956246679L;
	private final CRUDOperation crudOperation;
	private final AbstractEntity entity;
	
	public CRUDPayload(CRUDOperation crudOperation,
								AbstractEntity entity){
		this.crudOperation = crudOperation;
		this.entity = entity;
	}
	
	public CRUDOperation getCrudOperation() {
		return crudOperation;
	}
	
	public AbstractEntity getEntity() {
		return entity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ( !(obj instanceof CRUDPayload) )
			return false;
		CRUDPayload crudPayload = (CRUDPayload) obj;
		return (crudOperation == crudPayload.crudOperation)
				&& entity.equals(crudPayload.entity);
	}
	
	@Override
	public int hashCode() {
		return 31 * crudOperation.hashCode() + 17 * entity.hashCode();
	}
	
	@Override
	public String toString() {
		return crudOperation + "\t" + entity.toString();
	}
}
