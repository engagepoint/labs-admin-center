package com.engagepoint.university.admincentre.synchronization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.IsSame;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE2;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.UDP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST2;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.dao.AbstractDAO;
import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.Node;
import com.engagepoint.university.admincentre.exception.SynchronizationException;
import com.engagepoint.university.admincentre.util.ConfLoader;
import com.engagepoint.university.admincentre.util.Constants;

/**
 * Used for synchronization. It's possible to create new cluster, connect to
 * existing cluster, start and stop synchronization.
 * 
 * Singleton pattern was used. Call {@code SynchMaster.getInstance()} to get the
 * instance of this class
 * 
 * @author Roman Garkavenko
 */
public class SynchMaster {

	private static final Logger LOGGER = LoggerFactory.getLogger(SynchMaster.class);
	
	private static volatile SynchMaster instance;

	private JChannel channel;
	private AbstractDAO<AbstractEntity> abstractDAO;
	private Map<String, AbstractEntity> cacheData;
	private Map<String, AbstractEntity> receivedState;
	private final List<CRUDPayload> lastReceivedUpdates = new ArrayList<CRUDPayload>();
	private Mode mode = Mode.AUTO;
	
	public enum Mode{
		AUTO,
        MANUAL
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
			obtainCacheData();
			synchronized (cacheData) {
				try {
					Util.objectToStream(cacheData, new DataOutputStream(output));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}

//			LOGGER.info("getState: SENT " + cacheData.size() + " items");
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
//				LOGGER.info("setState: RECEIVED " + receivedState.size()
//						+ " items");
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
			checkParameter(msg);
			if(mode == Mode.AUTO){
				if (msg.getObject() instanceof CRUDPayload) {
					CRUDPayload crudPayload = (CRUDPayload) msg.getObject();
					processCRUDPayload(crudPayload);
				} else if (msg.getObject() instanceof List) {
					@SuppressWarnings("unchecked")
					List<CRUDPayload> sequence = (List<CRUDPayload>) msg
							.getObject();
					processList(sequence);
				}
			}
		}
		
		private void checkParameter(Message msg){
			if (!(msg.getObject() instanceof CRUDPayload)
					&& !(msg.getObject() instanceof List)) {
				throw new IllegalArgumentException(
						"Something wrong has been received");
			}
		}
		
		private void processCRUDPayload(CRUDPayload crudPayload){
			try {
				switch (crudPayload.getCrudOperation()) {
				case CREATE:
					lastReceivedUpdates.add(crudPayload);
					abstractDAO.create(crudPayload.getEntity());
					logCRUD(CRUDOperation.CREATE, crudPayload);
					break;
				case READ:
					break;
				case UPDATE:
					lastReceivedUpdates.add(crudPayload);
					abstractDAO.update(crudPayload.getEntity());
					logCRUD(CRUDOperation.UPDATE, crudPayload);
					break;
				case DELETE:
					lastReceivedUpdates.add(crudPayload);
					abstractDAO.delete(crudPayload.getEntity().getId());
					logCRUD(CRUDOperation.DELETE, crudPayload);
					break;
				default:
					break;
				}
			} catch (IOException e) {
				throw new IllegalStateException(
						Constants.COULD_NOT_COMPLETE_CRUD_OPERATION, e);
			}
		}

		private void logCRUD(CRUDOperation crudOperation, CRUDPayload crudPayload){
			LOGGER.info("[SYNCH] " + crudOperation.name() + " :"
					+ crudPayload.getEntity().toString());
		}
		
		private void processList(List<CRUDPayload> sequence){
			lastReceivedUpdates.addAll(sequence);
			for (CRUDPayload crudPayload : sequence) {
				CRUDOperation crudOperation = crudPayload
						.getCrudOperation();
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
							Constants.COULD_NOT_COMPLETE_CRUD_OPERATION, e);
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
			channel = new JChannel(false);

			ProtocolStack stack = new ProtocolStack();
			channel.setProtocolStack(stack);
			stack.addProtocol(new UDP()
			 				.setValue("bind_addr", InetAddress.getLocalHost())
			 				.setValue("bind_port", 0))
			.addProtocol(new PING())
			.addProtocol(new MERGE2())
			.addProtocol(new FD_SOCK())
			.addProtocol(new FD_ALL()
							.setValue("timeout", 12000)
							.setValue("interval", 3000))
			.addProtocol(new VERIFY_SUSPECT())
			.addProtocol(new BARRIER())
			.addProtocol(new NAKACK())
			.addProtocol(new UNICAST2())
			.addProtocol(new STABLE())
			.addProtocol(new GMS())
			.addProtocol(new UFC())
			.addProtocol(new MFC())
			.addProtocol(new FRAG2())
			.addProtocol(new STATE_TRANSFER());
			stack.init();
			
			channel.setDiscardOwnMessages(true);
			channel.setReceiver(new Receiver());
		} catch (Exception e) {
			throw new RuntimeException("Something wrong with channel", e);
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
	public void connect(String clusterName) {
		try {
			channel.connect(clusterName);
		} catch (Exception e) {
			throw new IllegalStateException("Could not connect to "
					+ clusterName, e);
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
			throw new IllegalStateException("Message was not sent", e);
		}
	}

	public void send(CRUDPayload crudPayload) {
		if(mode == Mode.MANUAL){
			return;
		}
		boolean removed = lastReceivedUpdates.remove(crudPayload);
		if (channel.isConnected() && !removed){
			send(new Message(null, null, crudPayload));
		}
	}

	public boolean isMemberChanged(){
		return isChanged(MergeStatus.MEMBER);
	}
	
	public boolean isClusterChanged(){
		return isChanged(MergeStatus.CLUSTER);
	}

	private boolean isChanged(MergeStatus mergeStatus){
		if(mergeStatus == MergeStatus.COMMON){
			throw new IllegalArgumentException("Argument to isChange could not be " + mergeStatus);
		}
		for(Pair<MergeStatus, AbstractEntity> p: merge()){
			if(p.getLeft() == mergeStatus){
				return true;
			}
		}
		return false;
	}

	boolean obtainState() {
		if (channel.getView().getMembers().size() > 1) {
			try {
				channel.getState(null, 40000);
				return true;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else {
			return false;
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
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		if(isConnected() && (isMemberChanged() || isClusterChanged())){
			throw new IllegalStateException("Member is not synchronized."
					+ " Swithing to auto mode is possible only for synched member");
		}
		this.mode = mode;
	}
	
	public boolean isConnected(){
		return channel.isConnected();
	}
	
	/**
	 * 	<b>true</b> if the only one member of the cluster
	 * 	<br><b>false</b> if 2+ members of cluster exist
	 * @return membership status
	 */
	public boolean isSingle(){
		throwIfDisconnected();
		return channel.getView().getMembers().size() == 1;
	}
	
	public boolean isCoordinator(){
		throwIfDisconnected();
		return channel.getAddress().compareTo(getCoordinator()) == 0;
	}
	
	public Address getCoordinator(){
		throwIfDisconnected();
		return channel.getView().getMembers().get(0);
	}
	
	public List<Address> getAddressList(){
		throwIfDisconnected();
		return channel.getView().getMembers();
	}
	
	
	public void obtainCacheData() {
		try {
			abstractDAO = new AbstractDAO<AbstractEntity>(AbstractEntity.class) {
			};
			cacheData = new HashMap<String, AbstractEntity>(abstractDAO.obtainCache());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void throwIfCoordinator(){
		if(isCoordinator()){
			throw new IllegalStateException("Current member is coordinator.");
		}
	}
	
	private void throwIfDisconnected(){
		if(!isConnected()){
			throw new IllegalStateException("Channel is not connected.");
		}
	}
	
	/**
	 * Push to cluster all changed key-entities and new entities.
	 * @return <b>true</b> if something was pushed, <b>false</b> otherwise.
	 */
	public boolean push(){
		throwIfDisconnected();
    	List<CRUDPayload> pushSequence = sequance(SynchMaster.MergeStatus.MEMBER, CRUDOperation.CREATE);
    	if(pushSequence.isEmpty()){
    		return false;
    	}
		Message msg = new Message(null, null, pushSequence);
		send(msg);
		return true;
	}
	
	/**
	 * Obtain state from cluster and put into the local storage.
	 * @return <b>true</b> if something was pulled, <b>false</b> otherwise.
	 */
	public boolean pull() {
		throwIfCoordinator();
    	List<CRUDPayload> pullSequance = sequance(SynchMaster.MergeStatus.CLUSTER, CRUDOperation.CREATE);
    	if(pullSequance.isEmpty()){
    		return false;
    	}
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
    	return true;
	}
	
	/**
	 * Reset changes made in local member's storage
	 * @return <b>true</b> if something was reseted, <b>false</b> otherwise.
	 */
	public boolean reset(){
		throwIfCoordinator();
		List<CRUDPayload> sequance = sequance(SynchMaster.MergeStatus.MEMBER, CRUDOperation.DELETE);
		if(sequance.isEmpty()){
			return false;
		}
		lastReceivedUpdates.addAll(sequance);
    	for(CRUDPayload crudPayload: sequance){
    		CRUDOperation crudOperation = crudPayload.getCrudOperation();
    		AbstractEntity entity = crudPayload.getEntity();
    		try {
	    		switch (crudOperation) {
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
    			throw new IllegalStateException("Could not reset", e);
    		}
    	}
    	return true;
	}
	
	/**
	 * Revert changes made in cluster to the state of current member
	 * @return <b>true</b> if something was reverted, <b>false</b> otherwise.
	 */
	public boolean revert(){
		throwIfCoordinator();
		List<CRUDPayload> sequance = sequance(SynchMaster.MergeStatus.CLUSTER, CRUDOperation.DELETE);
		if(sequance.isEmpty()){
			return false;
		}
		Message msg = new Message(null, null, sequance);
		send(msg);
		return true;
	}
	
	/**
	 * 
	 * @return <b>true</b> if operation done well, <b>false</b> if only one member of cluster
	 * @throws SynchronizationException if synchronization fails
	 */
	public boolean autoSynch() throws SynchronizationException{
		if(isSingle()){
			return false;
		}else{
			pull();
			push();
			mode = Mode.AUTO;
			if(isMemberChanged() || isClusterChanged()){
				throw new SynchronizationException("This member is not synched.");
			}
			return true;
		}
	}
	
	/**
	 * Creates a sequence of operations which than could be used in push, pull, reset, revert commands.
	 * @param mergeStatus
	 * @param crudOperation
	 * @return a sequence of (create & update) or (delete & update) operations
	 */
	private List<CRUDPayload> sequance(MergeStatus mergeStatus, CRUDOperation crudOperation){
		checkSequenceInputParameters(mergeStatus, crudOperation);
		List<CRUDPayload> sequance = new ArrayList<CRUDPayload>();
		List<Pair<MergeStatus, AbstractEntity>> mergeList = merge();
		if(crudOperation == CRUDOperation.DELETE){
			Collections.reverse(mergeList);
		}
		for(Pair<MergeStatus, AbstractEntity> pair: mergeList){
			if(pair.getLeft() == mergeStatus){
				if(pair.getRight() instanceof Key){
					Key key = (Key)pair.getRight();
					if(crudOperation != CRUDOperation.DELETE
							&& updateKeyIfExist(key, mergeList, mergeStatus, sequance)){
						continue;
					}
				}
				sequance.add(new CRUDPayload(crudOperation, pair.getRight()));	//add create
				Node parentNode = getParentNodeFromMerge(mergeList, pair.getRight(), mergeStatus);
				createOrDelete(crudOperation, parentNode, pair.getRight());
				sequance.add(new CRUDPayload(CRUDOperation.UPDATE, parentNode));	//add update
			}
		}
		
		return sequance;
	}
	
	private void checkSequenceInputParameters(MergeStatus mergeStatus, CRUDOperation crudOperation){
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
	
	private void createOrDelete(CRUDOperation crudOperation, Node parentNode, AbstractEntity entity){
		if(crudOperation == CRUDOperation.CREATE){
			if(entity instanceof Node){
				parentNode.addChildNodeId(entity.getId());
			}
			if(entity instanceof Key){
				parentNode.addKeyId(entity.getName());
			}
		}else if(crudOperation == CRUDOperation.DELETE){
			if(entity instanceof Node){
				parentNode.getChildNodeIdList().remove(entity.getId());
			}
			if(entity instanceof Key){
				parentNode.getKeyIdList().remove(entity.getName());
			}
		}
	}
	
	private boolean updateKeyIfExist(Key key, List<Pair<MergeStatus, AbstractEntity>> mergeList,
			MergeStatus mergeStatus, List<CRUDPayload> sequance){
		for (Pair<MergeStatus, AbstractEntity> pair : mergeList) {
			if(pair.getRight().getId().equals(key.getId())) {
				if (mergeStatus == MergeStatus.CLUSTER
						&& pair.getLeft() == MergeStatus.MEMBER){
					sequance.add(new CRUDPayload(CRUDOperation.UPDATE, key));
					return true;
				}else if(mergeStatus == MergeStatus.MEMBER
						&& pair.getLeft() == MergeStatus.CLUSTER){
					sequance.add(new CRUDPayload(CRUDOperation.UPDATE, key));
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return sorted list with pair of MergeStatus and entity both from cluster and member
	 */
	public List<Pair<MergeStatus, AbstractEntity>> merge() {
		if(isSingle()){
			throw new IllegalStateException("It has to be 2+ members in a cluster.");
		}
		if(isCoordinator()){
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

	/**
	 * Save configurations (channel name, mode, cluster name if set) to file
	 */
	public void saveConfig(){
        ConfLoader confLoader = ConfLoader.getInstance();
		if(getChannelName() != null){
			confLoader.setChannelName(getChannelName());
		}
		confLoader.setMode(mode.name());
		if(isConnected()){
			confLoader.setClusterName(getClusterName());
		}
	}
	
	/**
	 * Disconnects if connected, loads configurations (channel name, mode, 
	 * cluster name) from file.
	 * Connects to the cluster if cluster name was read from file.
	 */
	public void useSavedConfig(){
		disconnect();
        ConfLoader confLoader = ConfLoader.getInstance();
		String channelName = confLoader.getChannelName();
		if(!"".equals(channelName)){
			setChannelName(channelName);
		}
		String modeLine = confLoader.getMode();
		if(!"".equals(modeLine) && (SynchMaster.Mode.AUTO.name().equals(modeLine) 
				|| SynchMaster.Mode.MANUAL.name().equals(modeLine))){
			setMode(Mode.valueOf(modeLine));
		}
		String clusterName = confLoader.getClusterName();
		if(!"".equals(clusterName)){
			connect(clusterName);
			if(mode == Mode.AUTO  && !isSingle()){
				pull();
				push();
			}
		}
	}
}
