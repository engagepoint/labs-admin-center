package com.engagepoint.university.admincentre.synchronization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.dao.AbstractDAO;
import com.engagepoint.university.admincentre.entity.AbstractEntity;

/**
 * Used for synchronization. It's possible to create new cluster, connect to
 * existing cluster, start and stop synchronization.
 * 
 * Singleton pattern was used. Call {@code SynchMaster.getInstance()} to get the
 * instance of this class
 * 
 * @author Roman Garkavenko
 */
public final class SynchMaster {

	private static final Logger LOGGER = LoggerFactory.getLogger(SynchMaster.class);
	
	
	private static volatile SynchMaster instance;

	private boolean receiveUpdates = true;
	private final JChannel channel;
	private final AbstractDAO<AbstractEntity> abstractDAO;
	private HashMap<String, AbstractEntity> cacheData;
	private HashMap<String, AbstractEntity> receivedcacheData;
	private final List<CRUDPayload> lastReceivedUpdates = new ArrayList<CRUDPayload>();

	private class Receiver extends ReceiverAdapter{
		/**
		 * Allows an application to write a state through a provided OutputStream.
		 * After the state has been written the OutputStream doesn't need to be
		 * closed as stream closing is automatically done when a calling thread
		 * returns from this callback.
		 * 
		 * @param output
		 *            the OutputStream
		 * @throws Exception
		 *             if the streaming fails, any exceptions should be thrown so
		 *             that the state requester can re-throw them and let the caller
		 *             know what happened
		 * @see java.io.OutputStream#close()
		 */
		@Override
		public void getState(OutputStream output) { // SEND
			try {
				cacheData = new HashMap<String, AbstractEntity>(
						abstractDAO.obtainCache());
			} catch (IOException e1) {
				throw new IllegalStateException(e1);
			}
			synchronized (cacheData) {
				try {
					Util.objectToStream(cacheData, new DataOutputStream(output));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}

			LOGGER.info("getState: SENT " + cacheData.size() + " items");
		}

		/**
		 * Allows an application to read a state through a provided InputStream.
		 * After the state has been read the InputStream doesn't need to be closed
		 * as stream closing is automatically done when a calling thread returns
		 * from this callback.
		 * 
		 * @param input
		 *            the InputStream
		 * @throws Exception
		 *             if the streaming fails, any exceptions should be thrown so
		 *             that the state requester can catch them and thus know what
		 *             happened
		 * @see java.io.InputStream#close()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void setState(InputStream input) { // receive
			try {
				receivedcacheData = (HashMap<String, AbstractEntity>) Util
						.objectFromStream(new DataInputStream(input));
				LOGGER.info("setState: RECEIVED " + receivedcacheData.size()
						+ " items");
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Called when a message is received.
		 * 
		 * @param msg
		 */
		@Override
		public void receive(Message msg) {
			if (!(msg.getObject() instanceof CRUDPayload)
					&& !(msg.getObject() instanceof TreeMap)) {
				throw new IllegalArgumentException(
						"Something wrong has been received");
			}
			if (receiveUpdates) {
				if(msg.getObject() instanceof CRUDPayload){
					CRUDPayload crudPayload = (CRUDPayload) msg.getObject();
					try {
						switch (crudPayload.getCrudOperation()) {
						case CREATE:
							lastReceivedUpdates.add(crudPayload);
							abstractDAO.create(crudPayload.getEntity());
							LOGGER.info("[SYNCH] created: " + crudPayload.getEntity().toString());
							break;
						case READ:
							break;
						case UPDATE:
							lastReceivedUpdates.add(crudPayload);
							abstractDAO.update(crudPayload.getEntity());
							LOGGER.info("[SYNCH] updated: " + crudPayload.getEntity().toString());
							break;
						case DELETE:
							lastReceivedUpdates.add(crudPayload);
							abstractDAO.delete(crudPayload.getEntity().getId());
							LOGGER.info("[SYNCH] deleted: " + crudPayload.getEntity().toString());
							break;
						default:
							break;
						}
					} catch (IOException e) {
						throw new IllegalStateException(
								"Could not complete CRUD operation", e);
					}
				}else if(msg.getObject() instanceof TreeMap){
					@SuppressWarnings("unchecked")
					TreeMap<String, AbstractEntity> map = 
							(TreeMap<String, AbstractEntity>) msg.getObject();
					try {
						Map<String, AbstractEntity> newMapToPut 
							= new HashMap<String, AbstractEntity>(abstractDAO.obtainCache());
						newMapToPut.putAll(map);
						abstractDAO.clear();
						abstractDAO.putAll(newMapToPut);
						LOGGER.info("MAP was put: " + newMapToPut);
					} catch (IOException e) {
						throw new IllegalStateException(
								"Could not complete putAll", e);
					}
				}
			}
		}

	}
	
	/**
	 * Private constructor, used in realization of singleton pattern.
	 */
	private SynchMaster() {
		System.setProperty("java.net.preferIPv4Stack", "true");		
		abstractDAO = new AbstractDAO<AbstractEntity>(AbstractEntity.class) {
		};
		try {
//			syso("Start constructing..."); // delete
			channel = new JChannel();
			channel.setDiscardOwnMessages(true);
			channel.setReceiver(new Receiver());
			// connect("testCluster"); // delete
			// obtainState(); // delete
			// putAllReceived(); // delete
			LOGGER.info("CONSTRUCTED"); // delete
		} catch (Exception e) {
			throw new IllegalStateException("Something wrong with channel", e);
		}
	}

	/**
	 * Call this method to get instance of <b>SynchMaster</b> class. Remember
	 * that <b>SynchMaster</b> is a singleton.
	 * 
	 * @return instance of <b>SynchMaster</b>
	 */
	public static SynchMaster getInstance() {
		if (instance == null) {
			instance = new SynchMaster();
		}
		return instance;
	}

	/**
	 * Connects the channel to a group. The client is now able to receive group
	 * messages, views and to send messages to (all or single) group members.
	 * This is a null operation if already connected.
	 * 
	 * All channels with the same name form a group, that means all messages
	 * sent to the group will be received by all channels connected to the same
	 * cluster name.
	 * 
	 * @param cluster_name
	 */
	public void connect(String cluster_name) {
		try {
			channel.connect(cluster_name);
		} catch (Exception e) {
			throw new IllegalStateException("Could not connect to "
					+ cluster_name, e);
		}
	}

	/**
	 * Disconnects the channel if it is connected. If the channel is closed or
	 * disconnected, this operation is ignored. The channel can then be
	 * connected to the same or a different cluster again.
	 */
	public void disconnect() {
		channel.disconnect();
	}

	/**
	 * Destroys the channel and its associated resources (e.g., the protocol
	 * stack). After a channel has been closed, invoking methods on it throws
	 * the ChannelClosed exception (or results in a null operation). It is a
	 * null operation if the channel is already closed.
	 * 
	 * If the channel is connected to a group, disconnect() will be called
	 * first.
	 */
	public void close() {
		channel.close();
	}

	/**
	 * Sends a message. The message contains
	 * <ol>
	 * <li>a destination address (Address). A <code>null</code> address sends
	 * the message to all group members.
	 * <li>a source address. Can be left empty as it will be assigned
	 * automatically
	 * <li>a byte buffer. The message contents.
	 * <li>several additional fields. They can be used by application programs
	 * (or patterns). E.g. a message ID, flags etc
	 * </ol>
	 * 
	 * @param msg
	 *            The message to be sent. Destination and buffer should be set.
	 *            A null destination means to send to all group members.
	 * @throws Exception
	 *             if
	 * @exception IllegalStateException
	 *                thrown if the channel is disconnected or closed
	 */
	private void send(Message msg) {
		try {
			channel.send(msg);
//			syso("Message send: " + ((CRUDPayload) msg.getObject()).toString());
		} catch (Exception e) {
			LOGGER.warn("Message was not set!");
			throw new IllegalStateException(e);
		}
	}

	public void send(CRUDPayload crudPayload) {
		boolean removed = lastReceivedUpdates.remove(crudPayload);
		if (channel.isConnected() && !removed)
			send(new Message(null, null, crudPayload));
	}
	
	public void setReceiveUpdates(boolean receiveUpdates) {
		this.receiveUpdates = receiveUpdates;
	}

	public boolean isReceiveUpdates() {
		return receiveUpdates;
	}


	private void obtainState() {
		if (channel.getView().getMembers().size() > 1) {
			try {
				channel.getState(null, 40000);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else {
			LOGGER.info("You are the only one in the cluster and cannot obtain state");
		}
	}

	public void setChannelName(String channelName) {
		if (channel.isConnected()) {
			throw new IllegalStateException(
					"Channel is connected, you cannot set name " + channelName);
		} else{
			channel.setName(channelName);
		}
	}

	public String getChannelName() {
		return channel.getName();
	}

	public String getChannelName(Address member){
		return channel.getName(member);
	}
	
	public String getClusterName(){
		return channel.getClusterName();
	}
	
	public boolean isConnected(){
		return channel.isConnected();
	}
	
	public boolean isChanged(){
		for(String key: merge().keySet()){
			if(key.substring(0, 1).equals("1")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <b>null</b> if not connected
	 * 	<br><b>true</b> if the only one member of the cluster
	 * 	<br><b>false</b> if 2+ members of cluster exist
	 * @return membership status
	 */
	public Boolean isSingle(){
		return isConnected()
			? (channel.getView().getMembers().size() == 1)
			: null;
	}
	
	public List<Address> getAddressList(){
		return channel.getView().getMembers();
	}
	
//	private HashMap<String, AbstractEntity> getCacheData() {
//		return cacheData;
//	}
	
	public void obtainCacheData() {
		try {
			cacheData = new HashMap<String, AbstractEntity>(abstractDAO.obtainCache());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
//	public HashMap<String, AbstractEntity> getReceivedcacheData() {
//		return receivedcacheData;
//	}
	
//	public void push(){			//	REWRITE!!
//		obtainCacheData();
//		obtainState();
//		@SuppressWarnings("unchecked")
//		Map<String, AbstractEntity> result = new TreeMap<String, AbstractEntity>(compare(
//				new HashSet<AbstractEntity>(cacheData.values()),
//				new HashSet<AbstractEntity>(receivedcacheData.values())
//				));
//		LOGGER.info("MAP1: " + result);
//		for(Iterator<String> i = result.keySet().iterator(); i.hasNext();){
//			String s = i.next();
//			if(!s.substring(0, 1).equals("1")){
//				i.remove();
//			}
//		}
//		LOGGER.info("MAP2: " + result);
//		Map<String, AbstractEntity> payload = new TreeMap<String, AbstractEntity>();
//		for(String key: result.keySet()){
//			payload.put(result.get(key).getId(), result.get(key));
//		}
//		LOGGER.info("MAP3: " + payload);
//		Message msg = new Message(null, null, payload);
//		try {
//			channel.send(msg);
//		} catch (Exception e) {
//			throw new IllegalStateException("Could not send in push", e);
//		}
//	}
	/**
	 * Push to cluster all changed key-entities and new entities
	 */
	public void push(){
		obtainCacheData();
		Map<String, AbstractEntity> payload = new TreeMap<String, AbstractEntity>(
				cacheData);
		Message msg = new Message(null, null, payload);
		try {
			channel.send(msg);
		} catch (Exception e) {
			throw new IllegalStateException("Could not send in push", e);
		}
	}
	
	/**
	 * Replaces all data in current member for obtained state.
	 */
	public void pull() {
		obtainState();
        try {
            abstractDAO.clear();
            abstractDAO.putAll(receivedcacheData);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }

	}
	
	public Map<String, AbstractEntity> merge(){
		obtainState();
		obtainCacheData();
		@SuppressWarnings("unchecked")
		Map<String, AbstractEntity> map = merge(
        				new HashSet<AbstractEntity>(cacheData.values()),
        				new HashSet<AbstractEntity>(receivedcacheData.values()));
		return map;
	}
	
	private <T> Map<String, T> merge(Set<T>... args){		//expensive algorithm
		if(args.length == 0){
			throw new IllegalArgumentException("No args were found.");
		}
		final List<Set<T>> list = new ArrayList<Set<T>>(Arrays.asList(args));
		Set<T> common = new HashSet<T>(list.get(0));
		for(Iterator<Set<T>> i = list.iterator(); i.hasNext(); ){
			common.retainAll(i.next());
		}
		final List<Set<T>> uniqueList = new ArrayList<Set<T>>();
		for(Set<T> c: list){
			Set<T> temp = new HashSet<T>(c);
			temp.removeAll(common);
			uniqueList.add(temp);
		}
		final Map<String, T> result = new TreeMap<String, T>(new MapComparator());
		int i = 1;
		int j = 0;
		for(T t: common){
			result.put(String.valueOf(j) + "_" + String.valueOf(i++), t);
		}
		j++;
		for(Set<T> uniq: uniqueList){
			for(T t: uniq){
				result.put(String.valueOf(j) + "_" + String.valueOf(i++), t);
			}
			j++;
		}
		return result;
	}
	
	private class MapComparator implements Comparator<String>{

		@Override
		public int compare(String s1, String s2) {
			Integer c1 = Integer.valueOf(s1.substring(0, 1));
			Integer c2 = Integer.valueOf(s2.substring(0, 1));
			Integer i1 = Integer.valueOf(s1.substring(2));
			Integer i2 = Integer.valueOf(s2.substring(2));
			return c1.compareTo(c2) == 0 ? i1.compareTo(i2) : c1.compareTo(c2);
		}
	}

}
