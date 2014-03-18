package com.engagepoint.university.admincentre.synch;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.exception.SynchronizationException;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;
import com.engagepoint.university.admincentre.util.ConfLoader;

/**
 * Synchronize after starting application. Configurations are loaded from
 * config.xml
 *
 * @author Roman Garkavenko
 *
 */
@Singleton
@Startup
public class StartUp {

    ConfLoader confLoader;
    SynchMaster synchMaster;
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUp.class.getName());

    /**
     * Default constructor.
     */
    public StartUp() {
    	confLoader = ConfLoader.getInstance();
        synchMaster = SynchMaster.getInstance();
        synchMaster.connect(confLoader.getClusterName());
        try {
            synchMaster.autoSynch();
        } catch (SynchronizationException e) {
            LOGGER.error("ERROR synchMaster.autoSynch() /n", e);
        }
    }

}
