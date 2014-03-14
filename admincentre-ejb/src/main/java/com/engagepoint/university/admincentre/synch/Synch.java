package com.engagepoint.university.admincentre.synch;

import javax.ejb.Local;

import com.engagepoint.university.admincentre.exception.SynchronizationException;

/**
 * Local interface of Synch EJB.
 * @author Roman Garkavenko
 *
 */
@Local
public interface Synch {
	
	/**
	 * 
	 * @return whether channel is connected
	 */
	boolean isConnected();
	
	/**
	 * @return name of the channel.
	 */
	String getChannelName();
	
	/**
	 * 
	 * @param name of the channel
	 * @throws IllegalStateException if channel is disconnected
	 */
	void setChannelName(String name);
	
	/**
	 * 
	 * @return string representation of mode
	 */
	String getMode();
	
	/**
	 * Set mode - AUTO or MANUAL
	 * @throws IllegalStateException if member of cluster is not synchronized.
	 */
	void setMode(String mode) throws IllegalStateException;
	
	/**
	 * @return cluster name, null if disconnected.
	 */
	String getClusterName();
	
	/**
	 * 
	 * @param clusterName - name of the cluster to connect
	 * @throws IllegalStateException if channel is already connected
	 */
	void connect(String clusterName) throws IllegalStateException;
	
	/**
	 * Connects to cluster and auto-synchronize.
	 * @throws IllegalStateException if already connected
	 * @throws SynchronizationException if synchronization fails
	 */
	boolean autoConnect(String clusterName) throws IllegalStateException, SynchronizationException;
	
	/**
	 * Disconnects the channel, does nothing if channel is disconnected or closed.
	 */
	void disconnect();
	
}
