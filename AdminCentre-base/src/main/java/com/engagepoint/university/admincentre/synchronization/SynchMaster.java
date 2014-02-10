package com.engagepoint.university.admincentre.synchronization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infinispan.Cache;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.dao.NodeDAO;
import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.Node;

/**
 * Used for synchronization. It's possible to create new cluster, connect to
 * existing cluster, start and stop synchronization.
 * 
 * Singleton pattern was used. Call {@code SynchMaster.getInstance()} to get the
 * instance of this class
 */
public class SynchMaster extends ReceiverAdapter {

	private static Logger logger = LoggerFactory.getLogger(SynchMaster.class);
	
	private static volatile SynchMaster instance;

	private JChannel channel;
	private final List<Address> members = new ArrayList<Address>();
	private NodeDAO nodeDAO;
	private KeyDAO keyDAO;
	private final Map<String, Node> cacheNodeData = new HashMap<String, Node>();
	private final Map<String, Key> cacheKeyData = new HashMap<String, Key>();

	/**
	 * Private constructor, used in realization of singleton pattern.
	 * 
	 * @throws IllegalStateException
	 *             if //TODO
	 */
	private SynchMaster() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		nodeDAO = new NodeDAO();
		keyDAO = new KeyDAO();
		try {
			logger.info("Start constructing...");
			channel = new JChannel();
			channel.setReceiver(this);
			channel.connect("defaultCluster");
			if (channel.getView().getMembers().size() > 1) {
				channel.getState(null, 10000);
			}
			logger.info("CONSTRUCTED");
		} catch (IOException e) {
			throw new IllegalStateException("tempOut - exception!");
		} catch (Exception e) {
			throw new IllegalStateException("Something wrong with channel");
		}
	}

	/**
	 * Call this method to get instance of <b>SynchMaster</b> class. Remember
	 * that <b>SynchMaster</b> is a singleton.
	 * 
	 * @return instance of <b>SynchMaster</b>
	 */
	public static SynchMaster getInstance() {
		SynchMaster localInstance = instance;
		if (localInstance == null) {
			synchronized (SynchMaster.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new SynchMaster();
				}
			}
		}
		return localInstance;
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
	public void getState(OutputStream output) throws Exception { // SEND
		loadAllDataFromLocalCache();
		Wrapper wrapper = new Wrapper(cacheNodeData, cacheKeyData);
		synchronized (wrapper) {
			Util.objectToStream(wrapper, new DataOutputStream(output));
		}

		logger.info("getState: SENT " + wrapper.getCacheKeyData().size()
				+ " keys and " + wrapper.getCacheNodeData().size() + " nodes");
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
	@Override
	public void setState(InputStream input) throws Exception { // receive
		Wrapper wrapper = (Wrapper) Util.objectFromStream(new DataInputStream(
				input));
		synchronized (wrapper) {
			nodeDAO.clear();
			nodeDAO.putAll(wrapper.getCacheNodeData());
			keyDAO.putAll(wrapper.getCacheKeyData());
		}
		logger.info("setState: RECEIVED "
				+ wrapper.getCacheKeyData().size() + " keys and "
				+ wrapper.getCacheNodeData().size() + " nodes");
	}

	/**
	 * Called when a message is received.
	 * 
	 * @param msg
	 */
	@Override
	public void receive(Message msg) {
		
		//TODO
		logger.info("Message received: " 
				+ ((MessagePayload) msg.getObject()).toString());
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
	 * 				if 
	 * @exception IllegalStateException
	 *                thrown if the channel is disconnected or closed
	 */
	public void send(Message msg) throws Exception {
		channel.send(msg);
		//TODO hope it gonna work well; delete it
		logger.info("Message send: " 
				+ ((MessagePayload) msg.getObject()).toString());
	}

	/**
	 * Called when a change in membership has occurred. No long running actions,
	 * sending of messages or anything that could block should be done in this
	 * callback. If some long running action needs to be performed, it should be
	 * done in a separate thread.
	 * 
	 * Note that on reception of the first view (a new member just joined), the
	 * channel will not yet be in the connected state. This only happens when
	 * {@link Channel#connect(String)} returns.
	 */
	@Override
	public void viewAccepted(View view) {
		members.clear();
		members.addAll(view.getMembers());
		// TODO delete it
		logger.info("VIEW received: " + view.getMembers().toString());
	}

	private void loadAllDataFromLocalCache() {
		Node rootNode = nodeDAO.getRoot();
		cacheNodeData.put(rootNode.getId(), rootNode);
		readAllData(rootNode);
	}

	private void readAllData(Node node) {
		try {
			for (String id : node.getKeyIdList()) {
				Key key = keyDAO.read(id);
				cacheKeyData.put(key.getId(), key);
			}
			for (String id : node.getChildNodeIdList()) {
				Node treeNode = nodeDAO.read(id);
				cacheNodeData.put(treeNode.getId(), treeNode);
				readAllData(treeNode);
			}
		} catch (IOException ex) {
			throw new IllegalStateException("======================"
					+ "\nData from infinispan could not be read"
					+ "\n==========================");
		}
	}
}
