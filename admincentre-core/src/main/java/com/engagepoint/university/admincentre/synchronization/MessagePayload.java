package com.engagepoint.university.admincentre.synchronization;

import com.engagepoint.university.admincentre.entity.AbstractEntity;

public final class MessagePayload {
	
	private final CRUDOperation crudOperation;
	private final AbstractEntity entity;
	
	public MessagePayload(CRUDOperation crudOperation,
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
	public String toString() {
		return crudOperation + " " + entity.getId();
	}
}
