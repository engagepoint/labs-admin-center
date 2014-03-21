package com.engagepoint.university.admincentre.web;

import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.config.ConfLoadBean;
import com.engagepoint.university.admincentre.synch.Synch;

@WebListener
public class SynchSesionListener implements HttpSessionListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SynchSesionListener.class.getName());
	
	private static int MAX_INACTIVE_INTERVAL = 900;
	
    @EJB
    private Synch synch;
    
    @EJB
    ConfLoadBean confLoadBean;
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		event.getSession().setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
			try {
				if(!synch.isConnected()){
					synch.autoConnect(confLoadBean.getCluster());
				}else{
					LOGGER.warn("Already connected.");
				}
			} catch (Exception e) {
				LOGGER.error("Could not connect.", e);
			}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		if(synch.isConnected()){
			synch.disconnect();
		}else{
			LOGGER.warn("Channel is already disconnected, could not disconnect.");
		}
	}

}
