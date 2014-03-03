package com.engagepoint.university.admincentre.synchronization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.Node;

/**
 * Used for synchronization. It's possible to create new cluster, connect to
 * existing cluster, start and stop synchronization.
 * 
 * Singleton pattern was used. Call {@code SynchMaster.getInstance()} to get the
 * instance of this class
 * 
 * @author roman.garkavenko
 */
public final class SynchMaster {

	private static final Logger LOGGER = LoggerFactory.getLogger(SynchMaster.class);
	
	
	private static volatile SynchMaster instance;

	private boolean receiveUpdates = true;
	private final JChannel channel;
	private final AbstractDAO<AbstractEntity> abstractDAO;
	private HashMap<String, AbstractEntity> cacheData;
	private HashMap<String, AbstractEntity> receivedState;
	private final List<CRUDPayload> lastReceivedUpdates = new ArrayList<CRUDPayload>();
	public Mode mode = Mode.AUTO;
	
	public enum Mode{
		AUTO,
		HAND
	}
	
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
				receivedState = (HashMap<String, AbstractEntity>) Util
						.objectFromStream(new DataInputStream(input));
				LOGGER.info("setState: RECEIVED " + receivedState.size()
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
					&& !(msg.getObject() instanceof TreeMap)
					&& !(msg.getObject() instanceof List)) {
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
				}else if(msg.getObject() instanceof TreeMap){	//TODO delete
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
				}else if(msg.getObject() instanceof List){
					@SuppressWarnings("unchecked")
					List<CRUDPayload> sequence = (List<CRUDPayload>)msg.getObject();
					lastReceivedUpdates.addAll(sequence);
					for(CRUDPayload crudPayload: sequence){
						CRUDOperation crudOperation = crudPayload.getCrudOperation();
						AbstractEntity entity = crudPayload.getEntity();
						try {
							switch (crudOperation) {
							case CREATE:
								abstractDAO.create(entity);
								break;
							case READ:
								break;
							case UPDATE:
								abstractDAO.update(entity);
								break;
							case DELETE:
								abstractDAO.delete(entity.getId());
								break;
							default:
								break;
							}
						} catch (IOException e) {
							throw new IllegalStateException(
									"Could not complete CRUD operation", e);
						}
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
			channel = new JChannel();
			channel.setDiscardOwnMessages(true);
			channel.setReceiver(new Receiver());
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
	
	/**
	 * Set receive updates status. If <b>false</b>,
	 * channel could be connected and messages be received,
	 * but data would not be stored into infinispan cache.
	 * @param receiveUpdates
	 */
	public void setReceiveUpdates(boolean receiveUpdates) {
		this.receiveUpdates = receiveUpdates;
	}

	public boolean isReceiveUpdates() {
		return receiveUpdates;
	}

	public boolean isMemberChanged(){
		return isChanged(MergeStatus.MEMBER);
	}
	
	public boolean isClusterChanged(){
		return isChanged(MergeStatus.CLUSTER);
	}

	private boolean isChanged(MergeStatus mergeStatus){
		if(mergeStatus == MergeStatus.COMMON)
			throw new IllegalArgumentException("Argument to isChange could not be " + mergeStatus);
		for(Pair<MergeStatus, AbstractEntity> p: merge()){
			if(p.getLeft() == mergeStatus)
				return true;
		}
		return false;
	}

	private boolean obtainState() {
		if (channel.getView().getMembers().size() > 1) {
			try {
				channel.getState(null, 40000);
				return true;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else
			return false;
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
	
	/**
	 * Use this method to get additional info.
	 * <br>Info</br> is an inner class containing optional info methods.
	 * @return
	 */
	public Info info(){
		if(!channel.isConnected())
			throw new IllegalStateException("Channel is not connected.");
		if(infoInstance == null){
			infoInstance = new Info();
		}
		return infoInstance;
	}
	
	private static volatile Info infoInstance;
	
	public class Info{
		
		private Info(){
		}
		
		/**
		 * 	<b>true</b> if the only one member of the cluster
		 * 	<br><b>false</b> if 2+ members of cluster exist
		 * @return membership status
		 */
		public boolean isSingle(){
			return (channel.getView().getMembers().size() == 1);
		}
		
		public boolean isCoordinator(){
			return channel.getAddress().compareTo(getCoordinator()) == 0;
		}
		
		public Address getCoordinator(){
			return channel.getView().getMembers().get(0);
		}
		
		public List<Address> getAddressList(){
			return channel.getView().getMembers();
		}
	}
	
	public void obtainCacheData() {
		try {
			cacheData = new HashMap<String, AbstractEntity>(abstractDAO.obtainCache());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Push to cluster all changed key-entities and new entities
	 */
	public void push(){
		if(!isConnected()){
			throw new IllegalStateException("Channel is not connected.");
		}
		obtainCacheData();
    	List<CRUDPayload> pushSequence = SynchMaster.getInstance()
    			.sequance(SynchMaster.MergeStatus.MEMBER, CRUDOperation.CREATE);
		Message msg = new Message(null, null, pushSequence);
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
		if(!isConnected()){
			throw new IllegalStateException("Channel is not connected.");
		}
		if(info().isCoordinator()){
			throw new IllegalStateException("Coordinator could not pull.");
		}
    	List<CRUDPayload> pullSequance = SynchMaster.getInstance()
    			.sequance(SynchMaster.MergeStatus.CLUSTER, CRUDOperation.CREATE);
    	lastReceivedUpdates.addAll(pullSequance);
    	for(CRUDPayload crudPayload: pullSequance){
    		CRUDOperation crudOperation = crudPayload.getCrudOperation();
    		AbstractEntity entity = crudPayload.getEntity();
    		try {
	    		switch (crudOperation) {
				case CREATE:
					abstractDAO.create(entity);
					break;
				case UPDATE:
					abstractDAO.update(entity);
					break;
				default:
					break;
				}
    		} catch (IOException e) {
    			throw new IllegalStateException("Could not pull", e);
    		}
    	}
	}
	
	private List<CRUDPayload> sequance(MergeStatus mergeStatus, CRUDOperation crudOperation){
		if(mergeStatus != MergeStatus.CLUSTER
				&& mergeStatus != MergeStatus.MEMBER){
			throw new IllegalArgumentException("Wrong argument " + mergeStatus + ". Only CLUSTER"
					+ " and MEMBER could be used.");
		}
		if(crudOperation != CRUDOperation.CREATE 
				&& crudOperation != CRUDOperation.DELETE){
			throw new IllegalArgumentException("Wrong argument " + crudOperation + ". Only CREATE"
					+ " and DELETE could be used.");
		}
		List<CRUDPayload> sequance = new ArrayList<CRUDPayload>();
		List<Pair<MergeStatus, AbstractEntity>> mergeList = merge();
		for(Pair<MergeStatus, AbstractEntity> pair: mergeList){
			if(pair.getLeft() == mergeStatus){
				sequance.add(new CRUDPayload(crudOperation, pair.getRight()));	//add create
				Node parentNode = getParentNodeFromMerge(mergeList, pair.getRight(), mergeStatus);
				addOrDelete(crudOperation, parentNode, pair.getRight());
				sequance.add(new CRUDPayload(CRUDOperation.UPDATE, parentNode));	//add update
			}//finish
		}
		return sequance;
	}
	
	private Node getParentNodeFromMerge(List<Pair<MergeStatus, AbstractEntity>> mergeList,
			AbstractEntity entity, MergeStatus mergeStatus){
		String parentId = entity.getParentNodeId();
		Node parentNode = null;
		for(Pair<MergeStatus, AbstractEntity> innerPair: mergeList){
			if((innerPair.getLeft() == MergeStatus.COMMON
					|| innerPair.getLeft() == mergeStatus
					)
					&& innerPair.getRight().getId().equals(parentId)){
				parentNode = (Node) innerPair.getRight();
				break;
			}
		}
		return parentNode;
	}
	
	private void addOrDelete(CRUDOperation crudOperation, Node parentNode, AbstractEntity entity){
		if(crudOperation == CRUDOperation.CREATE){
			if(entity instanceof Node)
				parentNode.addChildNodeId(entity.getId());
			if(entity instanceof Key)
				parentNode.addKeyId(entity.getName());
		}else if(crudOperation == CRUDOperation.DELETE){
			if(entity instanceof Node)
				parentNode.getChildNodeIdList().remove(entity.getId());
			if(entity instanceof Key)
				parentNode.getKeyIdList().remove(entity.getName());
		}
	}
	
	/**
	 * 
	 * @return sorted list with pair of MergeStatus and entity both from cluster and member
	 */
	public List<Pair<MergeStatus, AbstractEntity>> merge() {
		if(info().isSingle()){
			throw new IllegalStateException("It has to be 2+ members in a cluster.");
		}
		if(info().isCoordinator()){
			throw new IllegalStateException("Current member is coordinator. It's cache data"
					+ " is equal to cluster state.");
		}
		obtainState();
		obtainCacheData();
		Map<String, AbstractEntity> common 
			= new HashMap<String, AbstractEntity>(cacheData);
		common.values().retainAll(receivedState.values());	//common data
		Map<String, AbstractEntity> member 
			= new HashMap<String, AbstractEntity>(cacheData);
		member.values().removeAll(receivedState.values());	//member unique data
		Map<String, AbstractEntity> cluster
			= new HashMap<String, AbstractEntity>(receivedState);
		cluster.values().removeAll(cacheData.values());		//cluster data
		
		List<Pair<MergeStatus, AbstractEntity>> result 
			= new ArrayList<Pair<MergeStatus, AbstractEntity>>();
		for(String key: common.keySet()){
			result.add(new Pair<MergeStatus, AbstractEntity>(MergeStatus.COMMON, common.get(key)));
		}
		for(String key: member.keySet()){
			result.add(new Pair<MergeStatus, AbstractEntity>(MergeStatus.MEMBER, member.get(key)));
		}
		for(String key: cluster.keySet()){
			result.add(new Pair<MergeStatus, AbstractEntity>(MergeStatus.CLUSTER, cluster.get(key)));
		}
		Collections.sort(result, new PairComparator());
		return result;
	}
	
	public enum MergeStatus{
		COMMON,
		MEMBER,
		CLUSTER;
	}
	
	/**
	 * Used for sorting in merge operation.
	 *<br>Sorts first by MergeStatus, then by entity.
	 */
	private class PairComparator implements Comparator<Pair<MergeStatus, AbstractEntity>>{

		@Override
		public int compare(Pair<MergeStatus, AbstractEntity> p1,
				Pair<MergeStatus, AbstractEntity> p2) {
			int i = p1.getLeft().compareTo(p2.getLeft());
			return i != 0 ? i : p1.getRight().toString().compareTo(p2.getRight().toString());
		}
		
	}

}
