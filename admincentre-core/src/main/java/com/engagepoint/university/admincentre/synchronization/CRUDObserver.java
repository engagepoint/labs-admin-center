package com.engagepoint.university.admincentre.synchronization;

import java.util.Observable;
import java.util.Observer;

public class CRUDObserver implements Observer {

		@Override
		public void update(Observable arg0, Object arg1) {
			if(arg1 instanceof CRUDPayload){
				CRUDPayload payload = (CRUDPayload) arg1;
				if(payload.getCrudOperation() == CRUDOperation.CREATE
						&& payload.getEntity() != null){
				SynchMaster.getInstance().send(payload);
//					System.out.println(					//temporary, delete it
//							"CRUDObserver: created  "
//							+ payload.getEntity().getId());
					
				}
				if(payload.getCrudOperation() == CRUDOperation.READ
						&& payload.getEntity() != null){
//					SynchMaster.getInstance();
//					System.out.println(					//temporary, delete it
//							"CRUDObserver: read  "
//							+ payload.getEntity().getId());
					
				}
				if(payload.getCrudOperation() == CRUDOperation.UPDATE
						&& payload.getEntity() != null){
					SynchMaster.getInstance().send(payload);
//					System.out.println(					//temporary, delete it
//							"CRUDObserver: updated  "
//							+ payload.getEntity().getId());
					
				}
				if(payload.getCrudOperation() == CRUDOperation.DELETE
						&& payload.getEntity() != null){
					SynchMaster.getInstance().send(payload);
//					System.out.println(					//temporary, delete it
//							"CRUDObserver: deleted  "
//							+ payload.getEntity().getId());
					
				}
			}else{
				throw new IllegalArgumentException("Wrong type of the second argument");
			}
			
		}
		
}
