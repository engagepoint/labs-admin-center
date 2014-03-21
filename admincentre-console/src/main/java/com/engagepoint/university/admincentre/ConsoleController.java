package com.engagepoint.university.admincentre;

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.exception.WrongInputArgException;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.Pair;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;
import com.engagepoint.university.admincentre.synchronization.SynchMaster.MergeStatus;

import org.jgroups.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ConsoleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleController.class.getName());
    private static final StringBuilder ALIGN_STRING = new StringBuilder("---");
    private Preferences currentPreferences = new NodePreferences(null, "");

    public Preferences getCurrentPreferences() {
        return currentPreferences;
    }

    public void setCurrentPreferences(Preferences currentPreferences) {
        this.currentPreferences = currentPreferences;
    }

    public void showHelp() {
        LOGGER.info("Options ...");
        for (Commands commands : Commands.values()) {
            String name = commands.getName();
            StringBuilder stringBuilder = buildAlignmentString(name.length());
            LOGGER.info("  " + name + stringBuilder + commands.getDescription());
        }
        LOGGER.info("");
    }

    private StringBuilder buildAlignmentString(int length) {
        int fixLength = 30;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fixLength - length; i++) {
            stringBuilder = stringBuilder.append(" ");
        }
        return stringBuilder;
    }

    public void displayNodes(Preferences preference) {
        LOGGER.info(ALIGN_STRING + " name = " + preference.name());
        displayKeys(preference);
        try {
            if (preference.childrenNames().length != 0) {
                ALIGN_STRING.insert(0, "   ");
                LOGGER.info(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
                LOGGER.info(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
                String[] preferenceChildrenNames = preference.childrenNames();
                for (int i = 0; i < preferenceChildrenNames.length; i++) {
                    displayNodes(preference.node(preferenceChildrenNames[i]));
                }
                ALIGN_STRING.delete(0, 3);
            }
        } catch (BackingStoreException e) {
            LOGGER.info("Failed to display nodes. Probably there some problems"
                    + "with the database", e);
        }
    }

    private void displayKeys(Preferences preferance) {
        String[] keys;
        try {
            keys = preferance.keys();
            if (keys.length != 0) {
                for (int i = 0; i < keys.length; i++) {

                    LOGGER.info(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3)
                            + " Key = " + keys[i] + ";" + "Value = "
                            + preferance.get(keys[i], "value wasn`t found"));
                }
                LOGGER.info("");
            }
        } catch (BackingStoreException e) {
            LOGGER.info("Failed to display keys. Probably there some problems"
                    + "with the database", e);
        }
    }

    public void selectNode(ConsoleInputString cis) throws WrongInputArgException {
        if (cis.getLength() == 2) {
            String path = cis.getSecondArg();
            try {
                if (currentPreferences.nodeExists(path)) {
                    this.currentPreferences = currentPreferences.node(path);
                } else {
                    LOGGER.warn("Node with such name does not exist");
                }
            } catch (BackingStoreException e) {
                LOGGER.info("Failed to select node. Probably there some problems"
                        + "with the database", e);
            }
            displayNodes(currentPreferences);
        } else {
            throw new WrongInputArgException();
        }
    }

    public void createNode(String nodeName) {
        if (nameValidation(nodeName)) {
            String newPath = (("/").equals(currentPreferences.absolutePath()) ? "/" + nodeName
                    : currentPreferences.absolutePath() + "/" + nodeName);
            currentPreferences.node(newPath);
        }
        displayNodes(this.currentPreferences);
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        if (nameValidation(keyName) && keyTypeValidation(keyType)) {
            currentPreferences.put(keyName, keyValue);
        }
        displayNodes(currentPreferences);
    }

    public void remove(ConsoleInputString cis) throws WrongInputArgException {
        String entity = null;
        if (cis.getSecondArg().equals("-node")) {
            if ("/".equals(currentPreferences.absolutePath())) {
                LOGGER.info("Could not remove root node.");
                return;
            }
            try {
                Preferences parentPreferences = currentPreferences.parent();
                currentPreferences.removeNode();
                currentPreferences = parentPreferences;
            } catch (BackingStoreException e) {
                throw new IllegalStateException("nodeRemove: failure in the backing store", e);
            }
        } else if (cis.getSecondArg().equals("-key")) {
            entity = cis.getThirdArg();
            NodePreferences nodePreferences = new NodePreferences(null, "");
            Key key;
            try {
                key = nodePreferences.getKey(entity);
                if (null != key) {
                    nodePreferences.node(key.getParentNodeId()).remove(entity);
                } else {
                    LOGGER.info("Selected key does not exist.");
                }
            } catch (IOException e) {
                LOGGER.info("Cannot read from storage ", e);
            }
        } else {
            throw new WrongInputArgException();
        }
    }

    public void export(ConsoleInputString cis) {
        String path = cis.getSecondArg();
        try {
            new NodePreferences(null, "").exportNode(path);
        } catch (BackingStoreException e) {
            LOGGER.info("Can`t export using path {}", path, e);
        }
    }

    public boolean nameValidation(String name) {
        boolean value = name.matches("^\\w+$");
        if (!value) {
            LOGGER.info("You have entered the invalid name. Only symbols a-zA-Z_0-9 are allowed");
        }
        return value;
    }

    /**
     * Allows to verify entered key type from console
     *
     * @param keyType String param which comes from console
     * @return true if key type exist in enum KeyType
     */
    public boolean keyTypeValidation(String keyType) {
        KeyType[] keyTypeList = KeyType.values();
        for (KeyType keyTypeTemp : keyTypeList) {
            if (keyType.equals(keyTypeTemp.name())) {
                return true;
            }
        }
        String types = "";
        for (KeyType keyTypeTemp : keyTypeList) {
            types = types + " " + keyTypeTemp.name();
        }
        LOGGER.info("Wrong type of the key, please try one of the following:" + types);
        return false;
    }

    private void refresh() {
        if (((NodePreferences) currentPreferences).currentNodeExists()) {
            currentPreferences = new NodePreferences(null, "").node(currentPreferences.absolutePath());
        } else {
            currentPreferences = new NodePreferences(null, "");
        }
    }

    public boolean showMessageIfRemoved() {
        if (((NodePreferences) currentPreferences).currentNodeExists()) {
            currentPreferences = new NodePreferences(null, "").node(currentPreferences.absolutePath());
            return false;
        } else {
            LOGGER.info("Current node was removed, refreshing...");
            currentPreferences = new NodePreferences(null, "");
            return true;
        }
    }

    public void synch(ConsoleInputString cis) {
        int length = cis.getLength();
        if (length == 2) {
            synchArgLengthTwoElement(cis);
        } else if (length == 3) {
            synchArgLengthThreeElements(cis);
        }
    }

    public void synchArgLengthTwoElement(ConsoleInputString cis) {
        switch (getEnumElement(cis)) {
            case CONNECT:
                LOGGER.info("Please, type the name of the cluster you want to connect");
                break;
            case DISCONNECT:
                disconnect();
                break;
            case MERGE:
                merge();
                break;
            case PULL:
                pull();
                break;
            case PUSH:
                push();
                break;
            case RESET:
                reset();
                break;
            case REVERT:
                revert();
                break;
            case AUTOSYNCH:
                autosynch();
                break;
            case MODE:
                LOGGER.info("Mode: {}", SynchMaster.getInstance().getMode().name());
                break;
            case STATUS:
                synchSTATUS();
                break;
            case NAME:
                LOGGER.info("Channel name: {}", SynchMaster.getInstance().getChannelName());
                break;
            case LOAD:
                SynchMaster.getInstance().useSavedConfig();
                break;
            case SAVE:
                SynchMaster.getInstance().saveConfig();
                LOGGER.info("Configurations were saved.");
                break;
            default:
        }
    }

    /**
     * Prints caution info if trying to call methods which require connected
     * channel
     *
     * @return <br><b>true</b> if channel is connected
     * <br><b>false</br> otherwise
     */
    private boolean isConnected() {
        if (SynchMaster.getInstance().isConnected()) {
            return true;
        }
        LOGGER.info("/n Channel is disconnected. Command could not be used. /n");
        return false;
    }

    /**
     * Prints caution info if trying to call methods acceptable only for
     * non-coordinator member.
     *
     * @return <br><b>true</b> if member is coordinator
     * <br><b>false</br> otherwise
     */
    private boolean isCoordinator() {
        if (SynchMaster.getInstance().isCoordinator()) {
            LOGGER.debug("This member is coordinator");
            return true;
        }
        return false;
    }

    /**
     * Prints all info about channel and cluster.
     */
    void synchSTATUS() {
        LOGGER.info("-----------Synch status-----------"
                + "\nChannel name.........{}\nMode.................{}\nConnected............{}",
                SynchMaster.getInstance().getChannelName(),
                SynchMaster.getInstance().getMode().name(),
                SynchMaster.getInstance().isConnected());
        if (SynchMaster.getInstance().isConnected()) {
            LOGGER.info("Cluster name.........{}\nCoordinator..........{}",
                    SynchMaster.getInstance().getClusterName(),
                    SynchMaster.getInstance().getCoordinator().toString());
            if (!SynchMaster.getInstance().isCoordinator()) {
                LOGGER.info("State synchronized...{}", !(SynchMaster.getInstance().isMemberChanged()
                        || SynchMaster.getInstance().isClusterChanged()));
            }
            String addresses = "Addresses(" + SynchMaster.getInstance().getAddressList().size() + "): ";
            for (Iterator<Address> i = SynchMaster.getInstance().getAddressList().iterator(); i.hasNext();) {
                addresses = addresses.concat(SynchMaster.getInstance().getChannelName(i.next()));
                if (i.hasNext()) {
                    addresses = addresses.concat(", ");
                } else {
                    addresses = addresses.concat(".");
                }
            }
            LOGGER.info(addresses);
        }
    }

    public void synchArgLengthThreeElements(ConsoleInputString cis) {
        switch (getEnumElement(cis)) {
            case CONNECT:
                connect(cis);
                break;
            case MODE:
                mode(cis);
                break;
            case NAME:
                name(cis);
                break;
            default:
        }
    }

    public AdditionalCommands getEnumElement(ConsoleInputString cis) {
        return AdditionalCommands.valueOf(cis.getSecondArg().toUpperCase(Locale.US).replaceFirst("-", ""));
    }

    public void checkCreateCommand(ConsoleInputString cis) throws WrongInputArgException {
        int length = cis.getLength();
        if (AdditionalCommands.NODE.getCommand().equals(cis.getSecondArg()) && (length == 3)) {
            createNode(cis.getThirdArg());
        } else if (AdditionalCommands.KEY.getCommand().equals(cis.getSecondArg()) && (length == 5)) {
            createKey(cis.getThirdArg(), cis.getFourthArg(), cis.getFifthArg());
        } else {
            throw new WrongInputArgException();
        }
    }

    private void disconnect() {
        SynchMaster.getInstance().disconnect();
        if (!SynchMaster.getInstance().isConnected()) {
            LOGGER.info("Disconnected.");
        }
    }

    private void merge() {
        if (!isConnected()) {
            return;
        }
        if (isCoordinator()) {
            return;
        }
        List<Pair<MergeStatus, AbstractEntity>> mergeList = SynchMaster.getInstance().merge();
        for (Pair<MergeStatus, AbstractEntity> pair : mergeList) {
            LOGGER.info(pair.toString());
        }
    }

    private void pull() {
        if (!isConnected()) {
            return;
        }
        if (isCoordinator()) {
            return;
        }
        if (SynchMaster.getInstance().pull()) {
            LOGGER.info("Pulled.");
            refresh();
        } else {
            LOGGER.info("Nothing to pull, you have all cluster's data.");
        }
    }

    private void push() {
        if (!isConnected()) {
            return;
        }
        if (SynchMaster.getInstance().isSingle()) {
            LOGGER.info("Only one member in cluster.");
            return;
        }
        if (SynchMaster.getInstance().push()) {
            LOGGER.info("Pushed.");
        } else {
            LOGGER.info("Nothing to push, you do not have local changes.");
        }
    }

    private void reset() {
        if (!isConnected()) {
            return;
        }
        if (isCoordinator()) {
            return;
        }
        if (SynchMaster.getInstance().reset()) {
            LOGGER.info("Reseted local changes.");
            refresh();
        } else {
            LOGGER.info("Nothing to reset, you do not have local changes.");
        }
    }

    private void revert() {
        if (!isConnected()) {
            return;
        }
        if (isCoordinator()) {
            return;
        }
        if (SynchMaster.getInstance().revert()) {
            LOGGER.info("Cluster has been reverted.");
        } else {
            LOGGER.info("Nothing to revert, you have all cluster data.");
        }
    }

    private void autosynch() {
        if (!isConnected()) {
            return;
        }
        if (isCoordinator()) {
            return;
        }
        if (SynchMaster.getInstance().getMode() == SynchMaster.Mode.MANUAL) {
            SynchMaster.getInstance().pull();
            SynchMaster.getInstance().push();
            SynchMaster.getInstance().setMode(SynchMaster.Mode.AUTO);
        } else {
            LOGGER.info("Already in AUTO mode");
        }
    }

    private void connect(ConsoleInputString cis) {
        if (SynchMaster.getInstance().isConnected()) {
            LOGGER.info("You are already connected to cluster{}", SynchMaster.getInstance().getClusterName());
            return;
        }
        SynchMaster.getInstance().connect(cis.getThirdArg());
        if (SynchMaster.getInstance().getMode() == SynchMaster.Mode.AUTO
                && !SynchMaster.getInstance().isSingle()) {
            SynchMaster.getInstance().pull();
            SynchMaster.getInstance().push();
        }
    }

    private void mode(ConsoleInputString cis) {
        if (!cis.getThirdArg().equals("auto") && !cis.getThirdArg().equals("manual")) {
            LOGGER.info("Wrong argument. Only \"auto\" or \"manual\" could be passed");
            return;
        }
        if (SynchMaster.getInstance().isConnected()) {
            if (SynchMaster.getInstance().isCoordinator()
                    && cis.getThirdArg().equals("manual")) {
                LOGGER.info("This member is coordinator.");
                return;
            } else if (SynchMaster.getInstance().getMode() == SynchMaster.Mode.MANUAL
                    && cis.getThirdArg().equals("auto")
                    && (SynchMaster.getInstance().isMemberChanged()
                    || SynchMaster.getInstance().isClusterChanged())) {
                LOGGER.info("Current member is not synchronized with cluster."
                        + " Use commands 'push', 'pull', 'reset', 'revert' to"
                        + " synch with cluster.");
                return;
            }
        }
        SynchMaster.getInstance().setMode(SynchMaster.Mode.valueOf(cis.getThirdArg().toUpperCase(Locale.US)));
        LOGGER.info("New mode has been set: " + SynchMaster.getInstance().getMode().name());
    }

    private void name(ConsoleInputString cis) {
        if (!SynchMaster.getInstance().isConnected()) {
            SynchMaster.getInstance().setChannelName(cis.getThirdArg());
            LOGGER.info("You have set channel name: name{} ", SynchMaster.getInstance().getChannelName());
        } else {
            LOGGER.info("Impossible to set channel name when channel is connected");
        }
    }
}
