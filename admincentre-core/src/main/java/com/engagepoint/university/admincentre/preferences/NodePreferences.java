package com.engagepoint.university.admincentre.preferences;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.dao.NodeDAO;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.entity.Node;

public class NodePreferences extends Preferences {
    /**
     * 
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(NodePreferences.class);
    private KeyDAO keyDAO = KeyDAO.getInstance();
    private NodeDAO nodeDAO = NodeDAO.getInstance();
    private Node currentNode;
    private static final String REMOVED_NODE = "Node has been removed: ";
    private static final String NOTREGISTRD_LISTENER = "Listener not registered in node: ";

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    /**
     * Our name relative to parent.
     */
    private String name;

    /**
     * Our absolute path name.
     */
    private String absolutePath;

    /**
     * Our parent node.
     */
    private final NodePreferences parent;

    public NodePreferences getParent() {
        return parent;
    }

    /**
     * Our root node.
     */
    private final NodePreferences root; // Relative to this node

    /**
     * This field should be <tt>true</tt> if this node did not exist in the
     * backing store prior to the creation of this object. The field is
     * initialized to false, but may be set to true by a subclass constructor
     * (and should not be modified thereafter). This field indicates whether a
     * node change event should be fired when creation is complete.
     */
    private boolean newNode = false;

    /**
     * All known unremoved children of this node. (This "cache" is consulted
     * prior to calling childSpi() or getChild().
     */
    private Map<String, NodePreferences> kidCache = new HashMap<String, NodePreferences>();

    /**
     * This field is used to keep track of whether or not this node has been
     * removed. Once it's set to true, it will never be reset to false.
     */
    private boolean removed = false;

    /**
     * Registered preference change listeners.
     */
    private PreferenceChangeListener[] prefListeners = new PreferenceChangeListener[0];

    /**
     * Registered node change listeners.
     */
    private NodeChangeListener[] nodeListeners = new NodeChangeListener[0];

    /**
     * An object whose monitor is used to lock this node. This object is used in
     * preference to the node itself to reduce the likelihood of intentional or
     * unintentional denial of service due to a locked node. To avoid deadlock,
     * a node is <i>never</i> locked by a thread that holds a lock on a
     * descendant of that node.
     */
    private final Object lock = new Object();

    /**
     * Creates a preference node with the specified parent and the specified
     * name relative to its parent.
     * 
     * @param parent
     *            the parent of this preference node, or null if this is the
     *            root.
     * @param name
     *            the name of this preference node, relative to its parent, or
     *            <tt>""</tt> if this is the root.
     * @throws IOException
     * @throws IllegalArgumentException
     *             if <tt>name</tt> contains a slash (<tt>'/'</tt>), or
     *             <tt>parent</tt> is <tt>null</tt> and name isn't <tt>""</tt>.
     */
    public NodePreferences(NodePreferences parent, String name) {

        if (parent == null) {
            if (!"".equals(name))
                throw new IllegalArgumentException("Root name '" + name + "' must be \"\"");
            this.absolutePath = "/";
            root = this;
        } else {
            if (name.indexOf('/') != -1)
                throw new IllegalArgumentException("Name '" + name + "' contains '/'");
            if ("".equals(name))
                throw new IllegalArgumentException("Illegal name: empty string" + name);

            root = parent.root;
            absolutePath = (parent.equals(root) ? "/" + name : parent.absolutePath() + "/" + name);
        }
        this.name = name;
        this.parent = parent;
        try {
            currentNode = nodeDAO.read(this.absolutePath);

            if (currentNode == null) {
                currentNode = new Node(parent.absolutePath, name);
                nodeDAO.create(currentNode);
                parent.currentNode.addChildNodeId(this.currentNode.getId());
                nodeDAO.update(parent.currentNode);
                newNode = true;
            }

        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }

    }

    /**
     * Implements the <tt>put</tt> method as per the specification in
     * {@link Preferences#put(String,String)}.
     * 
     * <p>
     * This implementation checks that the key and value are legal, obtains this
     * preference node's lock, checks that the node has not been removed,
     * invokes {@link #putSpi(String,String)}, and if there are any preference
     * change listeners, enqueues a notification event for processing by the
     * event dispatch thread.
     * 
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws IOException
     * @throws NullPointerException
     *             if key or value is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if <tt>key.length()</tt> exceeds <tt>MAX_KEY_LENGTH</tt> or
     *             if <tt>value.length</tt> exceeds <tt>MAX_VALUE_LENGTH</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void put(String key, KeyType type, String value) throws IOException {
        if (key == null || value == null)
            throw new IllegalArgumentException(
                    "key and value can't be null, trying to put key on preferences" + name);

        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);
            Key persistKey = new Key(absolutePath, key, type, value);
            putSpi(persistKey);
            enqueuePreferenceChangeEvent(key, value);
        }
    }

    /**
     * Implements the <tt>get</tt> method as per the specification in
     * {@link Preferences#get(String,String)}.
     * 
     * <p>
     * This implementation first checks to see if <tt>key</tt> is <tt>null</tt>
     * throwing a <tt>NullPointerException</tt> if this is the case. Then it
     * obtains this preference node's lock, checks that the node has not been
     * removed, invokes {@link #getSpi(String)}, and returns the result, unless
     * the <tt>getSpi</tt> invocation returns <tt>null</tt> or throws an
     * exception, in which case this invocation returns <tt>def</tt>.
     * 
     * @param key
     *            key whose associated value is to be returned.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt>.
     * @return the value associated with <tt>key</tt>, or <tt>def</tt> if no
     *         value is associated with <tt>key</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if key is <tt>null</tt>. (A <tt>null</tt> default <i>is</i>
     *             permitted.)
     */
    public String get(String key, String def) {
        if (key == null)
            throw new IllegalArgumentException(
                    "key can't be null, trying to get key on preferences" + name);
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);

            Key result = null;
            try {
                String keyId = ("/".equals(absolutePath) ? "/" + key : absolutePath + "/" + key);
                result = getSpi(keyId);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
            return ((result == null) ? def : result.getValue());
        }
    }

    /**
     * Implements the <tt>remove(String)</tt> method as per the specification in
     * {@link Preferences#remove(String)}.
     * 
     * <p>
     * This implementation obtains this preference node's lock, checks that the
     * node has not been removed, invokes {@link #removeSpi(String)} and if
     * there are any preference change listeners, enqueues a notification event
     * for processing by the event dispatch thread.
     * 
     * @param key
     *            key whose mapping is to be removed from the preference node.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void remove(String key) {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);

            try {
                removeSpi(key);
            } catch (IOException e) {
                throw new IllegalStateException("Key was removed" + key, e);
            }
            enqueuePreferenceChangeEvent(key, null);
        }
    }

    /**
     * Implements the <tt>clear</tt> method as per the specification in
     * {@link Preferences#clear()}.
     * 
     * <p>
     * This implementation obtains this preference node's lock, invokes
     * {@link #keys()} to obtain an array of keys, and iterates over the array
     * invoking {@link #remove(String)} on each key.
     * 
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void clear() throws BackingStoreException {
        synchronized (lock) {
            String[] keys = keys();
            for (int i = 0; i < keys.length; i++) {
                remove(keys[i]);
            }
        }
    }

    /**
     * Implements the <tt>putInt</tt> method as per the specification in
     * {@link Preferences#putInt(String,int)}.
     * 
     * <p>
     * This implementation translates <tt>value</tt> to a string with
     * {@link Integer#toString(int)} and invokes {@link #put(String,String)} on
     * the result.
     * 
     * @param key
     *            key with which the string form of value is to be associated.
     * @param value
     *            value whose string form is to be associated with key.
     * @throws NullPointerException
     *             if key is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if <tt>key.length()</tt> exceeds <tt>MAX_KEY_LENGTH</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void putInt(String key, int value) {
        try {
            put(key, KeyType.Integer, Integer.toString(value));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Implements the <tt>getInt</tt> method as per the specification in
     * {@link Preferences#getInt(String,int)}.
     * 
     * <p>
     * This implementation invokes {@link #get(String,String) <tt>get(key,
     * null)</tt>}. If the return value is non-null, the implementation attempts
     * to translate it to an <tt>int</tt> with {@link Integer#parseInt(String)}.
     * If the attempt succeeds, the return value is returned by this method.
     * Otherwise, <tt>def</tt> is returned.
     * 
     * @param key
     *            key whose associated value is to be returned as an int.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt> or the
     *            associated value cannot be interpreted as an int.
     * @return the int value represented by the string associated with
     *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as an
     *         int.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if <tt>key</tt> is <tt>null</tt>.
     */
    public int getInt(String key, int def) {
        int result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Ignoring exception causes specified default to be returned
        }

        return result;
    }

    /**
     * Implements the <tt>putLong</tt> method as per the specification in
     * {@link Preferences#putLong(String,long)}.
     * 
     * <p>
     * This implementation translates <tt>value</tt> to a string with
     * {@link Long#toString(long)} and invokes {@link #put(String,String)} on
     * the result.
     * 
     * @param key
     *            key with which the string form of value is to be associated.
     * @param value
     *            value whose string form is to be associated with key.
     * @throws NullPointerException
     *             if key is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if <tt>key.length()</tt> exceeds <tt>MAX_KEY_LENGTH</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void putLong(String key, long value) {
        try {
            put(key, KeyType.Long, Long.toString(value));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Implements the <tt>getLong</tt> method as per the specification in
     * {@link Preferences#getLong(String,long)}.
     * 
     * <p>
     * This implementation invokes {@link #get(String,String) <tt>get(key,
     * null)</tt>}. If the return value is non-null, the implementation attempts
     * to translate it to a <tt>long</tt> with {@link Long#parseLong(String)}.
     * If the attempt succeeds, the return value is returned by this method.
     * Otherwise, <tt>def</tt> is returned.
     * 
     * @param key
     *            key whose associated value is to be returned as a long.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt> or the
     *            associated value cannot be interpreted as a long.
     * @return the long value represented by the string associated with
     *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as a
     *         long.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if <tt>key</tt> is <tt>null</tt>.
     */
    public long getLong(String key, long def) {
        long result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Long.parseLong(value);
        } catch (NumberFormatException e) {
            LOGGER.warn(e.getMessage());
        }

        return result;
    }

    /**
     * Implements the <tt>putBoolean</tt> method as per the specification in
     * {@link Preferences#putBoolean(String,boolean)}.
     * 
     * <p>
     * This implementation translates <tt>value</tt> to a string with
     * {@link String#valueOf(boolean)} and invokes {@link #put(String,String)}
     * on the result.
     * 
     * @param key
     *            key with which the string form of value is to be associated.
     * @param value
     *            value whose string form is to be associated with key.
     * @throws NullPointerException
     *             if key is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if <tt>key.length()</tt> exceeds <tt>MAX_KEY_LENGTH</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void putBoolean(String key, boolean value) {
        try {
            put(key, KeyType.Boolean, String.valueOf(value));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Implements the <tt>getBoolean</tt> method as per the specification in
     * {@link Preferences#getBoolean(String,boolean)}.
     * 
     * <p>
     * This implementation invokes {@link #get(String,String) <tt>get(key,
     * null)</tt>}. If the return value is non-null, it is compared with
     * <tt>"true"</tt> using {@link String#equalsIgnoreCase(String)}. If the
     * comparison returns <tt>true</tt>, this invocation returns <tt>true</tt>.
     * Otherwise, the original return value is compared with <tt>"false"</tt>,
     * again using {@link String#equalsIgnoreCase(String)}. If the comparison
     * returns <tt>true</tt>, this invocation returns <tt>false</tt>. Otherwise,
     * this invocation returns <tt>def</tt>.
     * 
     * @param key
     *            key whose associated value is to be returned as a boolean.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt> or the
     *            associated value cannot be interpreted as a boolean.
     * @return the boolean value represented by the string associated with
     *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as a
     *         boolean.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if <tt>key</tt> is <tt>null</tt>.
     */
    public boolean getBoolean(String key, boolean def) {
        boolean result = def;
        String value = get(key, null);
        if (value != null) {
            if ("true".equalsIgnoreCase(value)) {
                result = true;
            } else if ("false".equalsIgnoreCase(value)) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Implements the <tt>putFloat</tt> method as per the specification in
     * {@link Preferences#putFloat(String,float)}.
     * 
     * <p>
     * This implementation translates <tt>value</tt> to a string with
     * {@link Float#toString(float)} and invokes {@link #put(String,String)} on
     * the result.
     * 
     * @param key
     *            key with which the string form of value is to be associated.
     * @param value
     *            value whose string form is to be associated with key.
     * @throws NullPointerException
     *             if key is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if <tt>key.length()</tt> exceeds <tt>MAX_KEY_LENGTH</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void putFloat(String key, float value) {
        try {
            put(key, KeyType.Float, Float.toString(value));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Implements the <tt>getFloat</tt> method as per the specification in
     * {@link Preferences#getFloat(String,float)}.
     * 
     * <p>
     * This implementation invokes {@link #get(String,String) <tt>get(key,
     * null)</tt>}. If the return value is non-null, the implementation attempts
     * to translate it to an <tt>float</tt> with
     * {@link Float#parseFloat(String)}. If the attempt succeeds, the return
     * value is returned by this method. Otherwise, <tt>def</tt> is returned.
     * 
     * @param key
     *            key whose associated value is to be returned as a float.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt> or the
     *            associated value cannot be interpreted as a float.
     * @return the float value represented by the string associated with
     *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as a
     *         float.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if <tt>key</tt> is <tt>null</tt>.
     */
    public float getFloat(String key, float def) {
        float result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            LOGGER.warn(e.getMessage());
        }

        return result;
    }

    /**
     * Implements the <tt>putDouble</tt> method as per the specification in
     * {@link Preferences#putDouble(String,double)}.
     * 
     * <p>
     * This implementation translates <tt>value</tt> to a string with
     * {@link Double#toString(double)} and invokes {@link #put(String,String)}
     * on the result.
     * 
     * @param key
     *            key with which the string form of value is to be associated.
     * @param value
     *            value whose string form is to be associated with key.
     * @throws NullPointerException
     *             if key is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if <tt>key.length()</tt> exceeds <tt>MAX_KEY_LENGTH</tt>.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void putDouble(String key, double value) {
        try {
            put(key, KeyType.Double, Double.toString(value));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    /**
     * Implements the <tt>getDouble</tt> method as per the specification in
     * {@link Preferences#getDouble(String,double)}.
     * 
     * <p>
     * This implementation invokes {@link #get(String,String) <tt>get(key,
     * null)</tt>}. If the return value is non-null, the implementation attempts
     * to translate it to an <tt>double</tt> with
     * {@link Double#parseDouble(String)}. If the attempt succeeds, the return
     * value is returned by this method. Otherwise, <tt>def</tt> is returned.
     * 
     * @param key
     *            key whose associated value is to be returned as a double.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt> or the
     *            associated value cannot be interpreted as a double.
     * @return the double value represented by the string associated with
     *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as a
     *         double.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if <tt>key</tt> is <tt>null</tt>.
     */
    public double getDouble(String key, double def) {
        double result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.warn(e.getMessage());
        }

        return result;
    }

    /**
     * Implements the <tt>putByteArray</tt> method as per the specification in
     * {@link Preferences#putByteArray(String,byte[])}.
     * 
     * @param key
     *            key with which the string form of value is to be associated.
     * @param value
     *            value whose string form is to be associated with key.
     * @throws NullPointerException
     *             if key or value is <tt>null</tt>.
     * @throws IllegalArgumentException
     *             if key.length() exceeds MAX_KEY_LENGTH or if value.length
     *             exceeds MAX_VALUE_LENGTH*3/4.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public void putByteArray(String key, byte[] value) {
        try {
            put(key, KeyType.ByteArray, byteArrayToBase64(value));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    private static String byteArrayToBase64(byte... a) {
        int aLen = a.length;
        int numFullGroups = aLen / 3;
        int numBytesInPartialGroup = aLen - 3 * numFullGroups;
        int resultLen = 4 * ((aLen + 2) / 3);
        StringBuilder result = new StringBuilder(resultLen);
        char[] intToAlpha = INT_TO_BASE64;

        // Translate all full groups from byte array elements to Base64
        int inCursor = 0;
        for (int i = 0; i < numFullGroups; i++) {
            int byte0 = a[inCursor++] & 0xff;
            int byte1 = a[inCursor++] & 0xff;
            int byte2 = a[inCursor++] & 0xff;
            result.append(intToAlpha[byte0 >> 2]);
            result.append(intToAlpha[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
            result.append(intToAlpha[(byte1 << 2) & 0x3f | (byte2 >> 6)]);
            result.append(intToAlpha[byte2 & 0x3f]);
        }

        // Translate partial group if present
        if (numBytesInPartialGroup != 0) {
            int byte0 = a[inCursor++] & 0xff;
            result.append(intToAlpha[byte0 >> 2]);
            if (numBytesInPartialGroup == 1) {
                result.append(intToAlpha[(byte0 << 4) & 0x3f]);
                result.append("==");
            } else {
                // assert numBytesInPartialGroup == 2;
                int byte1 = a[inCursor++] & 0xff;
                result.append(intToAlpha[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
                result.append(intToAlpha[(byte1 << 2) & 0x3f]);
                result.append('=');
            }
        }
        // assert inCursor == a.length;
        // assert result.length() == resultLen;
        return result.toString();
    }

    /**
     * This array is a lookup table that translates 6-bit positive integer index
     * values into their "Base64 Alphabet" equivalents as specified in Table 1
     * of RFC 2045.
     */
    private static final char INT_TO_BASE64[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '+', '/' };

    /**
     * Implements the <tt>getByteArray</tt> method as per the specification in
     * {@link Preferences#getByteArray(String,byte[])}.
     * 
     * @param key
     *            key whose associated value is to be returned as a byte array.
     * @param def
     *            the value to be returned in the event that this preference
     *            node has no value associated with <tt>key</tt> or the
     *            associated value cannot be interpreted as a byte array.
     * @return the byte array value represented by the string associated with
     *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as a
     *         byte array.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @throws NullPointerException
     *             if <tt>key</tt> is <tt>null</tt>. (A <tt>null</tt> value for
     *             <tt>def</tt> <i>is</i> permitted.)
     */
    public byte[] getByteArray(String key, byte[] def) {
        byte[] result = def;
        String value = get(key, null);
        try {
            if (value != null)
                result = base64ToByteArray(value);
        } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage());
        }

        return result;
    }

    private static byte[] base64ToByteArray(String s) {
        byte[] alphaToInt = BASE64_TO_INT;
        int sLen = s.length();
        int numGroups = sLen / 4;
        if (4 * numGroups != sLen)
            throw new IllegalArgumentException(
                    "String length must be a multiple of four, its length" + sLen);
        int missingBytesInLastGroup = 0;
        int numFullGroups = numGroups;
        if (sLen != 0) {
            if (s.charAt(sLen - 1) == '=') {
                missingBytesInLastGroup++;
                numFullGroups--;
            }
            if (s.charAt(sLen - 2) == '=')
                missingBytesInLastGroup++;
        }
        byte[] result = new byte[3 * numGroups - missingBytesInLastGroup];

        // Translate all full groups from base64 to byte array elements
        int inCursor = 0, outCursor = 0;
        for (int i = 0; i < numFullGroups; i++) {
            int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch3 = base64toInt(s.charAt(inCursor++), alphaToInt);
            result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));
            result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
            result[outCursor++] = (byte) ((ch2 << 6) | ch3);
        }

        // Translate partial group, if present
        if (missingBytesInLastGroup != 0) {
            int ch0 = base64toInt(s.charAt(inCursor++), alphaToInt);
            int ch1 = base64toInt(s.charAt(inCursor++), alphaToInt);
            result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));

            if (missingBytesInLastGroup == 1) {
                int ch2 = base64toInt(s.charAt(inCursor++), alphaToInt);
                result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
            }
        }
        // assert inCursor == s.length()-missingBytesInLastGroup;
        // assert outCursor == result.length;
        return result;
    }

    /**
     * Translates the specified character, which is assumed to be in the
     * "Base 64 Alphabet" into its equivalent 6-bit positive integer.
     * 
     * @throw IllegalArgumentException or ArrayOutOfBoundsException if c is not
     *        in the Base64 Alphabet.
     */
    private static int base64toInt(char c, byte... alphaToInt) {
        int result = alphaToInt[c];
        if (result < 0)
            throw new IllegalArgumentException("Illegal character " + c);
        return result;
    }

    /**
     * This array is a lookup table that translates unicode characters drawn
     * from the "Base64 Alphabet" (as specified in Table 1 of RFC 2045) into
     * their 6-bit positive integer equivalents. Characters that are not in the
     * Base64 alphabet but fall within the bounds of the array are translated to
     * -1.
     */
    private static final byte BASE64_TO_INT[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };

    /**
     * Implements the <tt>keys</tt> method as per the specification in
     * {@link Preferences#keys()}.
     * 
     * <p>
     * This implementation obtains this preference node's lock, checks that the
     * node has not been removed and invokes {@link #keysSpi()}.
     * 
     * @return an array of the keys that have an associated value in this
     *         preference node.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public String[] keys() throws BackingStoreException {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE);

            return keysSpi();
        }
    }

    /**
     * Implements the <tt>children</tt> method as per the specification in
     * {@link Preferences#childrenNames()}.
     * 
     * <p>
     * This implementation obtains this preference node's lock, checks that the
     * node has not been removed, constructs a <tt>TreeSet</tt> initialized to
     * the names of children already cached (the children in this node's
     * "child-cache"), invokes {@link #childrenNamesSpi()}, and adds all of the
     * returned child-names into the set. The elements of the tree set are
     * dumped into a <tt>String</tt> array using the <tt>toArray</tt> method,
     * and this array is returned.
     * 
     * @return the names of the children of this preference node.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @see #cachedChildren()
     */
    public String[] childrenNames() throws BackingStoreException {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE);

            // Set<String> s = new TreeSet<String>(kidCache.keySet());
            // for (String kid : childrenNamesSpi()) {
            // s.add(kid);
            // }
            return childrenNamesSpi();
        }
    }

    /**
     * Returns all known unremoved children of this node.
     * 
     * @return all known unremoved children of this node.
     */
    protected final NodePreferences[] cachedChildren() {
        return kidCache.values().toArray(EMPTY_ABSTRACT_PREFS_ARRAY);
    }

    private static final NodePreferences[] EMPTY_ABSTRACT_PREFS_ARRAY = new NodePreferences[0];

    /**
     * Implements the <tt>parent</tt> method as per the specification in
     * {@link Preferences#parent()}.
     * 
     * <p>
     * This implementation obtains this preference node's lock, checks that the
     * node has not been removed and returns the parent value that was passed to
     * this node's constructor.
     * 
     * @return the parent of this preference node.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public Preferences parent() {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE);

            return parent;
        }
    }

    /**
     * Implements the <tt>node</tt> method as per the specification in
     * {@link Preferences#node(String)}.
     * 
     * <p>
     * This implementation obtains this preference node's lock and checks that
     * the node has not been removed. If <tt>path</tt> is <tt>""</tt>, this node
     * is returned; if <tt>path</tt> is <tt>"/"</tt>, this node's root is
     * returned. If the first character in <tt>path</tt> is not <tt>'/'</tt>,
     * the implementation breaks <tt>path</tt> into tokens and recursively
     * traverses the path from this node to the named node, "consuming" a name
     * and a slash from <tt>path</tt> at each step of the traversal. At each
     * step, the current node is locked and the node's child-cache is checked
     * for the named node. If it is not found, the name is checked to make sure
     * its length does not exceed <tt>MAX_NAME_LENGTH</tt>. Then the
     * {@link #childSpi(String)} method is invoked, and the result stored in
     * this node's child-cache. If the newly created <tt>Preferences</tt>
     * object's {@link #newNode} field is <tt>true</tt> and there are any node
     * change listeners, a notification event is enqueued for processing by the
     * event dispatch thread.
     * 
     * <p>
     * When there are no more tokens, the last value found in the child-cache or
     * returned by <tt>childSpi</tt> is returned by this method. If during the
     * traversal, two <tt>"/"</tt> tokens occur consecutively, or the final
     * token is <tt>"/"</tt> (rather than a name), an appropriate
     * <tt>IllegalArgumentException</tt> is thrown.
     * 
     * <p>
     * If the first character of <tt>path</tt> is <tt>'/'</tt> (indicating an
     * absolute path name) this preference node's lock is dropped prior to
     * breaking <tt>path</tt> into tokens, and this method recursively traverses
     * the path starting from the root (rather than starting from this node).
     * The traversal is otherwise identical to the one described for relative
     * path names. Dropping the lock on this node prior to commencing the
     * traversal at the root node is essential to avoid the possibility of
     * deadlock, as per the {@link #lock locking invariant}.
     * 
     * @param path
     *            the path name of the preference node to return.
     * @return the specified preference node.
     * @throws IllegalArgumentException
     *             if the path name is invalid (i.e., it contains multiple
     *             consecutive slash characters, or ends with a slash character
     *             and is more than one character long).
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     */
    public Preferences node(String path) {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);
            if ("".equals(path))
                return this;

            if ("/".equals(path))
                return root;
            if (path.charAt(0) != '/')
                return node(new StringTokenizer(path, "/", true));
        }

        // Absolute path. Note that we've dropped our lock to avoid deadlock
        return root.node(new StringTokenizer(path.substring(1), "/", true));
    }

    /**
     * tokenizer contains <name> {'/' <name>}
     * 
     * @throws IOException
     *             *
     */
    private Preferences node(StringTokenizer path) {
        String token = path.nextToken();
        if ("/".equals(token)) // Check for consecutive slashes
            throw new IllegalArgumentException("Consecutive slashes in path: " + path);
        synchronized (lock) {
            // NodePreferences child = new NodePreferences(this, token);

            NodePreferences child = kidCache.get(token);
            if (child == null) {

                child = childSpi(token);
                if (child.newNode) {
                    enqueueNodeAddedEvent(child);
                }
                kidCache.put(token, child);
            }
            if (!path.hasMoreTokens())
                return child;
            path.nextToken(); // Consume slash
            if (!path.hasMoreTokens())
                throw new IllegalArgumentException("Path ends with slash: " + path);
            return child.node(path);
        }
    }

    /**
     * Implements the <tt>nodeExists</tt> method as per the specification in
     * {@link Preferences#nodeExists(String)}.
     * 
     * <p>
     * This implementation is very similar to {@link #node(String)}, except that
     * {@link #getChild(String)} is used instead of {@link #childSpi(String)}.
     * 
     * @param path
     *            the path name of the node whose existence is to be checked.
     * @return true if the specified node exists.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @throws IllegalArgumentException
     *             if the path name is invalid (i.e., it contains multiple
     *             consecutive slash characters, or ends with a slash character
     *             and is more than one character long).
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method and <tt>pathname</tt> is not the
     *             empty string (<tt>""</tt>).
     */
    public boolean nodeExists(String path) throws BackingStoreException {
        synchronized (lock) {
            if ("".equals(path))
                return !removed;
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);
            if ("/".equals(path))
                return true;
            if (path.charAt(0) != '/')
                return nodeExists(new StringTokenizer(path, "/", true));
        }

        // Absolute path. Note that we've dropped our lock to avoid deadlock
        return root.nodeExists(new StringTokenizer(path.substring(1), "/", true));
    }

    /**
     * tokenizer contains <name> {'/' <name>}*
     */
    private boolean nodeExists(StringTokenizer path) throws BackingStoreException {
        String token = path.nextToken();
        if ("/".equals(token)) // Check for consecutive slashes
            throw new IllegalArgumentException("Consecutive slashes in path: " + path);
        synchronized (lock) {
            NodePreferences child = kidCache.get(token);
            if (child == null)
                child = getChild(token);
            if (child == null)
                return false;
            if (!path.hasMoreTokens())
                return true;
            path.nextToken(); // Consume slash
            if (!path.hasMoreTokens())
                throw new IllegalArgumentException("Path ends with slash: " + path);
            return child.nodeExists(path);
        }
    }

    /**
     * 
     * Implements the <tt>removeNode()</tt> method as per the specification in
     * {@link Preferences#removeNode()}.
     * 
     * <p>
     * This implementation checks to see that this node is the root; if so, it
     * throws an appropriate exception. Then, it locks this node's parent, and
     * calls a recursive helper method that traverses the subtree rooted at this
     * node. The recursive method locks the node on which it was called, checks
     * that it has not already been removed, and then ensures that all of its
     * children are cached: The {@link #childrenNamesSpi()} method is invoked
     * and each returned child name is checked for containment in the
     * child-cache. If a child is not already cached, the
     * {@link #childSpi(String)} method is invoked to create a
     * <tt>Preferences</tt> instance for it, and this instance is put into the
     * child-cache. Then the helper method calls itself recursively on each node
     * contained in its child-cache. Next, it invokes {@link #removeNodeSpi()},
     * marks itself as removed, and removes itself from its parent's
     * child-cache. Finally, if there are any node change listeners, it enqueues
     * a notification event for processing by the event dispatch thread.
     * 
     * <p>
     * Note that the helper method is always invoked with all ancestors up to
     * the "closest non-removed ancestor" locked.
     * 
     * @throws IllegalStateException
     *             if this node (or an ancestor) has already been removed with
     *             the {@link #removeNode()} method.
     * @throws UnsupportedOperationException
     *             if this method is invoked on the root node.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     */
    public void removeNode() throws BackingStoreException {
        if (this == root)
            throw new UnsupportedOperationException("Can't remove the root!");
        synchronized (parent.lock) {
            try {
                removeNode2();
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
            }
            parent.kidCache.remove(name);
        }
    }

    /*
     * Called with locks on all nodes on path from parent of "removal root" to
     * this (including the former but excluding the latter).
     */
    private void removeNode2() throws BackingStoreException, IOException {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException("Node already removed.");

            // Ensure that all children are cached
            String[] kidNames = childrenNamesSpi();
            for (int i = 0; i < kidNames.length; i++) {
                String kidName = kidNames[i].split("/")[kidNames[i].split("/").length - 1];
                if (!kidCache.containsKey(kidNames[i]))
                    kidCache.put(kidNames[i], childSpi(kidName));
            }
            // Recursively remove all cached children
            for (Iterator<NodePreferences> i = kidCache.values().iterator(); i.hasNext();) {
                try {
                    i.next().removeNode2();
                    i.remove();
                } catch (BackingStoreException e) {
                    LOGGER.warn(e.getMessage());
                }
            }

            // Now we have no descendants - it's time to die!
            removeNodeSpi();
            removed = true;
            parent.enqueueNodeRemovedEvent(this);
        }
    }

    /**
     * Implements the <tt>name</tt> method as per the specification in
     * {@link Preferences#name()}.
     * 
     * <p>
     * This implementation merely returns the name that was passed to this
     * node's constructor.
     * 
     * @return this preference node's name, relative to its parent.
     */
    public String name() {
        return name;
    }

    /**
     * Implements the <tt>absolutePath</tt> method as per the specification in
     * {@link Preferences#absolutePath()}.
     * 
     * <p>
     * This implementation merely returns the absolute path name that was
     * computed at the time that this node was constructed (based on the name
     * that was passed to this node's constructor, and the names that were
     * passed to this node's ancestors' constructors).
     * 
     * @return this preference node's absolute path name.
     */
    public String absolutePath() {
        return absolutePath;
    }

    /**
     * Implements the <tt>isUserNode</tt> method as per the specification in
     * {@link Preferences#isUserNode()}.
     * 
     * <p>
     * This implementation compares this node's root node (which is stored in a
     * private field) with the value returned by {@link Preferences#userRoot()}.
     * If the two object references are identical, this method returns true.
     * 
     * @return <tt>true</tt> if this preference node is in the user preference
     *         tree, <tt>false</tt> if it's in the system preference tree.
     */
    public boolean isUserNode() {
        return root.equals(Preferences.userRoot());

    }

    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        if (pcl == null)
            throw new IllegalArgumentException(
                    "PreferenceChangeListener can`t be null in preferences: " + name);
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);

            // Copy-on-write
            PreferenceChangeListener[] old = prefListeners;
            prefListeners = new PreferenceChangeListener[old.length + 1];
            System.arraycopy(old, 0, prefListeners, 0, old.length);
            prefListeners[old.length] = pcl;
        }
        startEventDispatchThreadIfNecessary();
    }

    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);
            if ((prefListeners == null) || (prefListeners.length == 0))
                throw new IllegalArgumentException(NOTREGISTRD_LISTENER + name);

            // Copy-on-write
            PreferenceChangeListener[] newPl = new PreferenceChangeListener[prefListeners.length - 1];
            int i = 0;
            while (i < newPl.length && prefListeners[i] != pcl) {
                newPl[i] = prefListeners[i++];
            }
            if (i == newPl.length && prefListeners[i] != pcl)
                throw new IllegalArgumentException(NOTREGISTRD_LISTENER + name);
            while (i < newPl.length) {
                newPl[i] = prefListeners[++i];
            }
            prefListeners = newPl;
        }
    }

    public void addNodeChangeListener(NodeChangeListener ncl) {
        if (ncl == null)
            throw new IllegalArgumentException("Change listener is null on preferences " + name);
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);

            // Copy-on-write
            if (nodeListeners == null) {
                nodeListeners = new NodeChangeListener[1];
                nodeListeners[0] = ncl;
            } else {
                NodeChangeListener[] old = nodeListeners;
                nodeListeners = new NodeChangeListener[old.length + 1];
                System.arraycopy(old, 0, nodeListeners, 0, old.length);
                nodeListeners[old.length] = ncl;
            }
        }
        startEventDispatchThreadIfNecessary();
    }

    public void removeNodeChangeListener(NodeChangeListener ncl) {
        synchronized (lock) {
            if (removed)
                throw new IllegalStateException(REMOVED_NODE + name);
            if ((nodeListeners == null) || (nodeListeners.length == 0))
                throw new IllegalArgumentException(NOTREGISTRD_LISTENER + name);

            // Copy-on-write
            int i = 0;
            while (i < nodeListeners.length && nodeListeners[i] != ncl) {
                i++;
            }
            if (i == nodeListeners.length)
                throw new IllegalArgumentException(NOTREGISTRD_LISTENER + name);
            NodeChangeListener[] newNl = new NodeChangeListener[nodeListeners.length - 1];
            if (i != 0)
                System.arraycopy(nodeListeners, 0, newNl, 0, i);
            if (i != newNl.length)
                System.arraycopy(nodeListeners, i + 1, newNl, i, newNl.length - i);
            nodeListeners = newNl;
        }
    }

    // "SPI" METHODS

    /**
     * Put the given key-value association into this preference node. It is
     * guaranteed that <tt>key</tt> and <tt>value</tt> are non-null and of legal
     * length. Also, it is guaranteed that this node has not been removed. (The
     * implementor needn't check for any of these things.)
     * 
     * <p>
     * This method is invoked with the lock on this node held.
     * 
     * @throws IOException
     */
    protected void putSpi(Key key) throws IOException {
        if (keyDAO.read(key.getId()) == null) {
            keyDAO.create(key);
        } else {
            keyDAO.update(key);
        }
        currentNode.addKeyId(key.getName());
        nodeDAO.update(currentNode);
    };

    /**
     * Return the value associated with the specified key at this preference
     * node, or <tt>null</tt> if there is no association for this key, or the
     * association cannot be determined at this time. It is guaranteed that
     * <tt>key</tt> is non-null. Also, it is guaranteed that this node has not
     * been removed. (The implementor needn't check for either of these things.)
     * 
     * <p>
     * Generally speaking, this method should not throw an exception under any
     * circumstances. If, however, if it does throw an exception, the exception
     * will be intercepted and treated as a <tt>null</tt> return value.
     * 
     * <p>
     * This method is invoked with the lock on this node held.
     * 
     * @return the value associated with the specified key at this preference
     *         node, or <tt>null</tt> if there is no association for this key,
     *         or the association cannot be determined at this time.
     * @throws IOException
     */
    protected Key getSpi(String keyId) throws IOException {
        return keyDAO.read(keyId);
    };

    public Key getKey(String key) throws IOException {
        return getSpi("/".equals(absolutePath) ? "/" + key : absolutePath + "/" + key);
    }

    /**
     * Remove the association (if any) for the specified key at this preference
     * node. It is guaranteed that <tt>key</tt> is non-null. Also, it is
     * guaranteed that this node has not been removed. (The implementor needn't
     * check for either of these things.)
     * 
     * <p>
     * This method is invoked with the lock on this node held.
     * 
     * @throws IOException
     */
    protected void removeSpi(String key) throws IOException {

        keyDAO.delete(absolutePath + "/" + key);
        currentNode.getKeyIdList().remove(key);
        nodeDAO.update(this.currentNode);
    };

    /**
     * Removes this preference node, invalidating it and any preferences that it
     * contains. The named child will have no descendants at the time this
     * invocation is made (i.e., the {@link Preferences#removeNode()} method
     * invokes this method repeatedly in a bottom-up fashion, removing each of a
     * node's descendants before removing the node itself).
     * 
     * <p>
     * This method is invoked with the lock held on this node and its parent
     * (and all ancestors that are being removed as a result of a single
     * invocation to {@link Preferences#removeNode()}).
     * 
     * <p>
     * The removal of a node needn't become persistent until the <tt>flush</tt>
     * method is invoked on this node (or an ancestor).
     * 
     * <p>
     * If this node throws a <tt>BackingStoreException</tt>, the exception will
     * propagate out beyond the enclosing {@link #removeNode()} invocation.
     * 
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @throws IOException
     */
    protected void removeNodeSpi() throws BackingStoreException, IOException {
        nodeDAO.delete(absolutePath);
        this.parent.currentNode.getChildNodeIdList().remove(this.absolutePath);
        nodeDAO.update(this.parent.currentNode);
        for (int i = 0; i < keys().length; i++) {
            keyDAO.delete(absolutePath + "/" + keys()[i]);
        }
    };

    /**
     * Returns all of the keys that have an associated value in this preference
     * node. (The returned array will be of size zero if this node has no
     * preferences.) It is guaranteed that this node has not been removed.
     * 
     * <p>
     * This method is invoked with the lock on this node held.
     * 
     * <p>
     * If this node throws a <tt>BackingStoreException</tt>, the exception will
     * propagate out beyond the enclosing {@link #keys()} invocation.
     * 
     * @return an array of the keys that have an associated value in this
     *         preference node.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     */
    protected String[] keysSpi() throws BackingStoreException {
        List<String> keyIdList = currentNode.getKeyIdList();
        return keyIdList.toArray(new String[keyIdList.size()]);
    };

    /**
     * Returns the names of the children of this preference node. (The returned
     * array will be of size zero if this node has no children.) This method
     * need not return the names of any nodes already cached, but may do so
     * without harm.
     * 
     * <p>
     * This method is invoked with the lock on this node held.
     * 
     * <p>
     * If this node throws a <tt>BackingStoreException</tt>, the exception will
     * propagate out beyond the enclosing {@link #childrenNames()} invocation.
     * 
     * @return an array containing the names of the children of this preference
     *         node.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     */
    protected String[] childrenNamesSpi() {
        List<String> childNodeIdList = currentNode.getChildNodeIdList();
        return childNodeIdList.toArray(new String[childNodeIdList.size()]);
    }

    /**
     * Returns the named child if it exists, or <tt>null</tt> if it does not. It
     * is guaranteed that <tt>nodeName</tt> is non-null, non-empty, does not
     * contain the slash character ('/'), and is no longer than
     * {@link #MAX_NAME_LENGTH} characters. Also, it is guaranteed that this
     * node has not been removed. (The implementor needn't check for any of
     * these things if he chooses to override this method.)
     * 
     * <p>
     * Finally, it is guaranteed that the named node has not been returned by a
     * previous invocation of this method or {@link #childSpi} after the last
     * time that it was removed. In other words, a cached value will always be
     * used in preference to invoking this method. (The implementor needn't
     * maintain his own cache of previously returned children if he chooses to
     * override this method.)
     * 
     * <p>
     * This implementation obtains this preference node's lock, invokes
     * {@link #childrenNames()} to get an array of the names of this node's
     * children, and iterates over the array comparing the name of each child
     * with the specified node name. If a child node has the correct name, the
     * {@link #childSpi(String)} method is invoked and the resulting node is
     * returned. If the iteration completes without finding the specified name,
     * <tt>null</tt> is returned.
     * 
     * @param nodeName
     *            name of the child to be searched for.
     * @return the named child if it exists, or null if it does not.
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     */
    protected NodePreferences getChild(String nodeName) throws BackingStoreException {
        synchronized (lock) {
            // assert kidCache.get(nodeName)==null;
            String[] kidNames = childrenNames();
            for (int i = 0; i < kidNames.length; i++) {
                if (kidNames[i].equals(nodeName)) {
                    return childSpi(kidNames[i]);
                }
            }
        }
        return null;
    }

    /**
     * Returns the named child of this preference node, creating it if it does
     * not already exist. It is guaranteed that <tt>name</tt> is non-null,
     * non-empty, does not contain the slash character ('/'), and is no longer
     * than {@link #MAX_NAME_LENGTH} characters. Also, it is guaranteed that
     * this node has not been removed. (The implementor needn't check for any of
     * these things.)
     * 
     * <p>
     * Finally, it is guaranteed that the named node has not been returned by a
     * previous invocation of this method or {@link #getChild(String)} after the
     * last time that it was removed. In other words, a cached value will always
     * be used in preference to invoking this method. Subclasses need not
     * maintain their own cache of previously returned children.
     * 
     * <p>
     * The implementer must ensure that the returned node has not been removed.
     * If a like-named child of this node was previously removed, the
     * implementer must return a newly constructed <tt>PrefTest</tt> node; once
     * removed, an <tt>PrefTest</tt> node cannot be "resuscitated."
     * 
     * <p>
     * If this method causes a node to be created, this node is not guaranteed
     * to be persistent until the <tt>flush</tt> method is invoked on this node
     * or one of its ancestors (or descendants).
     * 
     * <p>
     * This method is invoked with the lock on this node held.
     * 
     * @param name
     *            The name of the child node to return, relative to this
     *            preference node.
     * @return The named child node.
     */
    protected NodePreferences childSpi(String name) {

        return new NodePreferences(this, name);

    };

    /**
     * Returns the absolute path name of this preferences node.
     */
    public String toString() {
        return (this.isUserNode() ? "User" : "System") + " Preference Node: " + this.absolutePath();
    }

    /**
     * Implements the <tt>sync</tt> method as per the specification in
     * {@link Preferences#sync()}.
     * 
     * <p>
     * This implementation calls a recursive helper method that locks this node,
     * invokes syncSpi() on it, unlocks this node, and recursively invokes this
     * method on each "cached child." A cached child is a child of this node
     * that has been created in this VM and not subsequently removed. In effect,
     * this method does a depth first traversal of the "cached subtree" rooted
     * at this node, calling syncSpi() on each node in the subTree while only
     * that node is locked. Note that syncSpi() is invoked top-down.
     * 
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @throws IllegalStateException
     *             if this node (or an ancestor) has been removed with the
     *             {@link #removeNode()} method.
     * @see #flush()
     */
    public void sync() throws BackingStoreException {
        sync2();
    }

    private void sync2() throws BackingStoreException {
        NodePreferences[] cachedKids;

        synchronized (lock) {
            if (removed)
                throw new IllegalStateException("Node has been removed");
            // syncSpi();
            cachedKids = cachedChildren();
        }

        for (int i = 0; i < cachedKids.length; i++) {
            cachedKids[i].sync2();
        }
    }

    /**
     * This method is invoked with this node locked. The contract of this method
     * is to synchronize any cached preferences stored at this node with any
     * stored in the backing store. (It is perfectly possible that this node
     * does not exist on the backing store, either because it has been deleted
     * by another VM, or because it has not yet been created.) Note that this
     * method should <i>not</i> synchronize the preferences in any subnodes of
     * this node. If the backing store naturally syncs an entire subtree at
     * once, the implementer is encouraged to override sync(), rather than
     * merely overriding this method.
     * 
     * <p>
     * If this node throws a <tt>BackingStoreException</tt>, the exception will
     * propagate out beyond the enclosing {@link #sync()} invocation.
     * 
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     */
    // protected abstract void syncSpi() throws BackingStoreException;

    /**
     * Implements the <tt>flush</tt> method as per the specification in
     * {@link Preferences#flush()}.
     * 
     * <p>
     * This implementation calls a recursive helper method that locks this node,
     * invokes flushSpi() on it, unlocks this node, and recursively invokes this
     * method on each "cached child." A cached child is a child of this node
     * that has been created in this VM and not subsequently removed. In effect,
     * this method does a depth first traversal of the "cached subtree" rooted
     * at this node, calling flushSpi() on each node in the subTree while only
     * that node is locked. Note that flushSpi() is invoked top-down.
     * 
     * <p>
     * If this method is invoked on a node that has been removed with the
     * {@link #removeNode()} method, flushSpi() is invoked on this node, but not
     * on others.
     * 
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     * @see #flush()
     */
    public void flush() throws BackingStoreException {
        flush2();
    }

    private void flush2() throws BackingStoreException {
        NodePreferences[] cachedKids;

        synchronized (lock) {
            // flushSpi();
            if (removed)
                return;
            cachedKids = cachedChildren();
        }

        for (int i = 0; i < cachedKids.length; i++) {
            cachedKids[i].flush2();
        }
    }

    /**
     * This method is invoked with this node locked. The contract of this method
     * is to force any cached changes in the contents of this preference node to
     * the backing store, guaranteeing their persistence. (It is perfectly
     * possible that this node does not exist on the backing store, either
     * because it has been deleted by another VM, or because it has not yet been
     * created.) Note that this method should <i>not</i> flush the preferences
     * in any subnodes of this node. If the backing store naturally flushes an
     * entire subtree at once, the implementer is encouraged to override
     * flush(), rather than merely overriding this method.
     * 
     * <p>
     * If this node throws a <tt>BackingStoreException</tt>, the exception will
     * propagate out beyond the enclosing {@link #flush()} invocation.
     * 
     * @throws BackingStoreException
     *             if this operation cannot be completed due to a failure in the
     *             backing store, or inability to communicate with it.
     */
    // protected abstract void flushSpi() throws BackingStoreException;

    /**
     * Returns <tt>true</tt> iff this node (or an ancestor) has been removed
     * with the {@link #removeNode()} method. This method locks this node prior
     * to returning the contents of the private field used to track this state.
     * 
     * @return <tt>true</tt> iff this node (or an ancestor) has been removed
     *         with the {@link #removeNode()} method.
     */
    protected boolean isRemoved() {
        synchronized (lock) {
            return removed;
        }
    }

    /**
     * Queue of pending notification events. When a preference or node change
     * event for which there are one or more listeners occurs, it is placed on
     * this queue and the queue is notified. A background thread waits on this
     * queue and delivers the events. This decouples event delivery from
     * preference activity, greatly simplifying locking and reducing opportunity
     * for deadlock.
     */
    private static final List<EventObject> EVENT_QUEUE = new LinkedList<EventObject>();

    /**
     * These two classes are used to distinguish NodeChangeEvents on eventQueue
     * so the event dispatch thread knows whether to call childAdded or
     * childRemoved.
     */
    private static class NodeAddedEvent extends NodeChangeEvent {

        NodeAddedEvent(Preferences parent, Preferences child) {
            super(parent, child);
        }
    }

    private static class NodeRemovedEvent extends NodeChangeEvent {

        NodeRemovedEvent(Preferences parent, Preferences child) {
            super(parent, child);
        }
    }

    /**
     * A single background thread ("the event notification thread") monitors the
     * event queue and delivers events that are placed on the queue.
     */
    private static class EventDispatchThread extends Thread {
        public void run() {
            while (true) {
                // Wait on eventQueue till an event is present
                EventObject event = null;
                synchronized (EVENT_QUEUE) {
                    try {
                        while (EVENT_QUEUE.isEmpty()) {
                            EVENT_QUEUE.wait();
                        }
                        event = EVENT_QUEUE.remove(0);
                    } catch (InterruptedException e) {
                        // XXX Log "Event dispatch thread interrupted. Exiting"
                        return;
                    }
                }

                // Now we have event & hold no locks; deliver evt to listeners
                NodePreferences src = (NodePreferences) event.getSource();
                if (event instanceof PreferenceChangeEvent) {
                    PreferenceChangeEvent pce = (PreferenceChangeEvent) event;
                    PreferenceChangeListener[] listeners = src.prefListeners();
                    for (int i = 0; i < listeners.length; i++) {
                        listeners[i].preferenceChange(pce);
                    }

                } else {
                    NodeChangeEvent nce = (NodeChangeEvent) event;
                    NodeChangeListener[] listeners = src.nodeListeners();
                    if (nce instanceof NodeAddedEvent) {
                        for (int i = 0; i < listeners.length; i++) {
                            listeners[i].childAdded(nce);
                        }
                    } else {
                        // assert nce instanceof NodeRemovedEvent;
                        for (int i = 0; i < listeners.length; i++) {
                            listeners[i].childRemoved(nce);
                        }
                    }
                }
            }
        }
    }

    private static Thread eventDispatchThread = null;

    /**
     * This method starts the event dispatch thread the first time it is called.
     * The event dispatch thread will be started only if someone registers a
     * listener.
     */
    private static synchronized void startEventDispatchThreadIfNecessary() {
        if (eventDispatchThread == null) {
            // XXX Log "Starting event dispatch thread"
            eventDispatchThread = new EventDispatchThread();
            eventDispatchThread.setDaemon(true);
            eventDispatchThread.start();
        }
    }

    /**
     * Return this node's preference/node change listeners. Even though we're
     * using a copy-on-write lists, we use synchronized accessors to ensure
     * information transmission from the writing thread to the reading thread.
     */
    PreferenceChangeListener[] prefListeners() {
        synchronized (lock) {
            return prefListeners;
        }
    }

    NodeChangeListener[] nodeListeners() {
        synchronized (lock) {
            return nodeListeners;
        }
    }

    /**
     * Enqueue a preference change event for delivery to registered preference
     * change listeners unless there are no registered listeners. Invoked with
     * this.lock held.
     */
    private void enqueuePreferenceChangeEvent(String key, String newValue) {
        if (prefListeners.length != 0) {
            synchronized (EVENT_QUEUE) {
                EVENT_QUEUE.add(new PreferenceChangeEvent(this, key, newValue));
                EVENT_QUEUE.notifyAll();
            }
        }
    }

    /**
     * Enqueue a "node added" event for delivery to registered node change
     * listeners unless there are no registered listeners. Invoked with
     * this.lock held.
     */
    private void enqueueNodeAddedEvent(Preferences child) {
        if (nodeListeners.length != 0) {
            synchronized (EVENT_QUEUE) {
                EVENT_QUEUE.add(new NodeAddedEvent(this, child));
                EVENT_QUEUE.notifyAll();
            }
        }
    }

    /**
     * Enqueue a "node removed" event for delivery to registered node change
     * listeners unless there are no registered listeners. Invoked with
     * this.lock held.
     */
    private void enqueueNodeRemovedEvent(Preferences child) {
        if (nodeListeners.length != 0) {
            synchronized (EVENT_QUEUE) {
                EVENT_QUEUE.add(new NodeRemovedEvent(this, child));
                EVENT_QUEUE.notifyAll();
            }
        }
    }

    @Override
    public void put(String key, String value) {
        try {
            put(key, KeyType.String, value);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }

    }

    public void changeNodeName(String name) {
        String oldId = absolutePath;
        List<String> childList = this.parent.currentNode.getChildNodeIdList();
        childList.remove(oldId);
        absolutePath = (parent.equals(root) ? "/" + name : parent.absolutePath() + "/" + name);
        childList.add(absolutePath);
        changeIdPath(this.currentNode, oldId);
        this.name = name;
        this.currentNode.setName(name);

        try {
            nodeDAO.delete(oldId);
            nodeDAO.create(this.currentNode);
            nodeDAO.update(this.parent.currentNode);


        } catch (IOException e) {
            LOGGER.warn("Exception was occured while node name" + name + "was updating");
        }
    }

    private void changeIdPath(Node parentNode, String oldPath) {
        List<String> childNodeIdList = new LinkedList<String>();
        List<Key> keyList = new LinkedList<Key>();
        if (!parentNode.getKeyIdList().isEmpty()) {
            changeNodeKeyId(parentNode, oldPath);
        }
        for (String childNodeId : parentNode.getChildNodeIdList()) {
            try {
                Node node = nodeDAO.read(childNodeId);
                childNodeIdList.add(childNodeId.replaceFirst(oldPath, absolutePath));
                String oldId = node.getId();
                nodeDAO.delete(oldId);
                changeIdPath(node, oldPath);
                node.setParentNodeId(parentNode.getId().replace(oldPath, absolutePath));
                nodeDAO.create(node);

            } catch (IOException e) {
                LOGGER.warn("Couldn't read/update node with id" + childNodeId);
            }
        }

        try {
            parentNode.setChildNodeIdList(childNodeIdList);
            nodeDAO.update(parentNode);
            for (Key key : keyList) {
                putSpi(key);
            }
        } catch (IOException e) {
            LOGGER.warn("Couldn't update node with id" + parentNode.getId());
        }

    }

    private void changeNodeKeyId(Node node, String oldPath) {

        for (String keyId : node.getKeyIdList()) {
            try {
                String fullOldId = "/".equals(node.getId()) ? "/" + keyId : node.getId() + "/"
                        + keyId;

                Key key = keyDAO.read(fullOldId);
                keyDAO.delete(fullOldId);
                key.setParentNodeId(node.getId().replaceFirst(oldPath, absolutePath));

                keyDAO.create(key);
            } catch (IOException e) {
                LOGGER.warn("Couldn't read/update key with id" + keyId);
            }
        }
    }

    /**
     * Method exports Node with all it SubNodes
     * 
     * @param os
     *            the output stream on which to emit the ZIP document.
     * @throws IOException
     *             if writing to the specified output stream results in an
     *             <tt>IOException</tt>.
     * @throws BackingStoreException
     *             if preference data cannot be read from backing store.
     */
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        exportSubtree(os);
    }

    public void exportNode(String path) throws IOException, BackingStoreException {
        new ZipFiles().exportZipPreferences(this, path);
    }

    /**
     * Method exports Node with all it SubNodes
     * 
     * @param os
     *            the output stream on which to emit the ZIP document.
     * @throws IOException
     *             if writing to the specified output stream results in an
     *             <tt>IOException</tt>.
     * @throws BackingStoreException
     *             if preference data cannot be read from backing store.
     */
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        new ZipFiles().exportZipPreferences(this, os);
    }

    /**
     * Method imports Node with all it SubNodes to current node
     * 
     * @param is
     *            the input stream on which to emit the ZIP document.
     * @throws IOException
     *             if writing to the specified output stream results in an
     *             <tt>IOException</tt>.
     * @throws BackingStoreException
     *             if preference data cannot be read from backing store.
     */
    public void importNode(InputStream is) throws IOException, BackingStoreException {
        new ZipFiles().importZipPreferences(is, absolutePath);
    }
}
