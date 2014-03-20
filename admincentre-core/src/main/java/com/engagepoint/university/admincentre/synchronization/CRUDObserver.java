package com.engagepoint.university.admincentre.synchronization;

import java.util.Observable;
import java.util.Observer;

/**
 * Realization of observer pattern, provided by java SE.
 *
 * @author Roman Garkavenko
 *
 */
public class CRUDObserver implements Observer {

		@Override
		public void update(Observable arg0, Object arg1) {
			if(arg1 instanceof CRUDPayload){
				CRUDPayload payload = (CRUDPayload) arg1;
				if(payload.getCrudOperation() != null
						&& payload.getCrudOperation() != CRUDOperation.READ
						&& payload.getEntity() != null
						&& SynchMaster.connected()){
							SynchMaster.getInstance().send(payload);
				}
			}else{
				throw new IllegalArgumentException("Wrong type of the second argument");
			}
		}
}
