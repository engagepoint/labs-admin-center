package com.engagepoint.university.admincentre.synchronization;

import java.util.Observable;
import java.util.Observer;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.Node;

public class CRUDObserver implements Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof MessagePayload){
//			MessagePayload nof = (MessagePayload) arg1;
			SynchMaster.getInstance();
			//TODO
		}else{
			throw new IllegalArgumentException("Wrong type of the second argument");
		}
		
	}
	
}
