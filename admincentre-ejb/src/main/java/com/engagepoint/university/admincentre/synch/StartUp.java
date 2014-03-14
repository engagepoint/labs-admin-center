package com.engagepoint.university.admincentre.synch;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.engagepoint.university.admincentre.exception.SynchronizationException;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;
import com.engagepoint.university.admincentre.util.ConfLoader;

/**
 * Synchronize after starting application.
 * Configurations are loaded from config.xml
 * @author Roman Garkavenko
 *
 */
@Singleton
@Startup
public class StartUp {

	ConfLoader confLoader;
	SynchMaster synchMaster;
	
    /**
     * Default constructor. 
     */
    public StartUp() {
    	confLoader = ConfLoader.getInstance();
    	synchMaster = SynchMaster.getInstance();
    	
    	synchMaster.connect(confLoader.getClusterName());
    	try {
			synchMaster.autoSynch();
//			System.out.println("DONE WELL!!!! synchMaster.autoSynch()");
		} catch (SynchronizationException e) {
//			System.out.println("ERROR synchMaster.autoSynch()");
			//TODO	catch ex
		}
    }

}
