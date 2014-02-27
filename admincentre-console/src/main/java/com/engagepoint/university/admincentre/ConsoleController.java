package com.engagepoint.university.admincentre;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jgroups.Address;

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.exception.WrongInputArgException;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;


public class ConsoleController {

    private static final Logger LOGGER = Logger.getLogger(ConsoleController.class.getName());
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
            LOGGER.warning("displayNodes: message" + e.getMessage());
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
            LOGGER.warning("displayKeys: message" + e.getMessage());
        }

    }

    public boolean selectNode(ConsoleInputString cis) {
        if (cis.getLength() == 2) {
            this.currentPreferences = currentPreferences.node(cis.getSecondArg());
            displayNodes(currentPreferences);
            return true;
        }
        return false;

    }

    public void createNode(String nodeName) {
        if (nameValidation(nodeName)) {
            String newPath = (("/").equals(currentPreferences.absolutePath()) ? "/" + nodeName
                    : currentPreferences.absolutePath() + "/" + nodeName);
            currentPreferences.node(newPath);
            currentPreferences.node(currentPreferences.absolutePath());
        }
        displayNodes(this.currentPreferences);
    }

    public void createKey(String keyName, String keyType, String keyValue) {
        if (nameValidation(keyName) && keyTypeValidation(keyType)) {
            currentPreferences.put(keyName, keyValue);
        }
        displayNodes(currentPreferences);

    }

    public void export(ConsoleInputString cis) {
        String path = cis.getSecondArg();
        try {
            new NodePreferences(null, "").exportNode(path);
        } catch (BackingStoreException e) {
            LOGGER.info("Can`t export using path" + path);
        } catch (IOException e) {
            LOGGER.info("Can`t export using path" + path);
        }
    }

    public boolean nameValidation(String name) {
        boolean value = name.matches("^\\w+$");
        if (!value) {
            LOGGER.info("You enter not valid name... Only a-zA-Z_0-9");
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
        try {
            KeyType.valueOf(keyType);
        } catch (IllegalArgumentException e) {
            KeyType[] keyTypeList = KeyType.values();
            LOGGER.info("You enter wrong key type. Use one of the next types :");
            for (KeyType keyTypeTemp : keyTypeList) {
                LOGGER.info("  " + keyTypeTemp.toString());
            }
            return false;
        }
        return true;

    }

    public void refresh() {
        currentPreferences = new NodePreferences(null, "");
    }


    public void synch(ConsoleInputString cis) {
        int length =cis.getLength();
        if (length == 2) {
            synchArgLengthOneElement(cis);
        } else if (length == 3) {
            synchArgLengthTwoElements(cis);
        }
    }

    public void synchArgLengthOneElement(ConsoleInputString cis) {
        switch (getEnumElement(cis)) {
            case CONNECT:
                LOGGER.info("Please, type the name of the cluster you want to connect");
                break;
            case DISCONNECT:
                SynchMaster.getInstance().disconnect();
                LOGGER.info("Disconnected.");
                break;
            case OBTAIN:
                SynchMaster.getInstance().obtainState();
                break;
            case PUTRECEIVED:
                SynchMaster.getInstance().putAllReceived();
                refresh();
                break;
            case COMPARE:
            	SynchMaster.getInstance().obtainState();
            	if(SynchMaster.getInstance().getReceivedcacheData() == null){
            		break;
            	}
            	SynchMaster.getInstance().obtainCacheData();
            	Map<String, AbstractEntity> map = SynchMaster
            		.getInstance().compare(
            				new HashSet<AbstractEntity>(SynchMaster.getInstance()
            						.getCacheData().values()),
            				new HashSet<AbstractEntity>(SynchMaster.getInstance()
            						.getReceivedcacheData().values()));
            	for(String key: map.keySet()){
            		LOGGER.info(key + "\t" + map.get(key).toString());
            	}
            	break;
            case PUSH:
            	SynchMaster.getInstance().push();
            	break;
            case RECEIVEUPDATES:
                LOGGER.info("Receive updates status: " + SynchMaster.getInstance().isReceiveUpdates());
                break;
            case STATUS:
                synchSTATUS();
                break;
            case NAME:
                LOGGER.info("Channel name: " + SynchMaster.getInstance().getChannelName());
                break;
            default: //TODO
        }
    }

    private void synchSTATUS() {
        LOGGER.info("-----------Synch status-----------"
                + "\nChannel name........." + SynchMaster.getInstance().getChannelName()
                + "\nConnected............" + SynchMaster.getInstance().isConnected());
        if (SynchMaster.getInstance().isConnected()) {
            LOGGER.info("Receive updates......" + SynchMaster.getInstance().isReceiveUpdates()
            		+ "\nCluster name........." + SynchMaster.getInstance().getClusterName());
            String addresses = "Addresses: ";
            for (Iterator<Address> i = SynchMaster.getInstance().getAddressList().iterator(); i.hasNext(); ) {
            	addresses = addresses.concat( SynchMaster.getInstance().getChannelName(i.next()) );
                if (i.hasNext()) {
                	addresses = addresses.concat(", ");
                } else {
                	addresses = addresses.concat(".");
                }
            }
            LOGGER.info(addresses);
        }
    }

    public void synchArgLengthTwoElements(ConsoleInputString cis) {
        switch (getEnumElement(cis)) {
            case CONNECT:
                SynchMaster.getInstance().connect(cis.getThirdArg());
                break;
            case RECEIVEUPDATES:
                boolean value = Boolean.parseBoolean(cis.getThirdArg());
                SynchMaster.getInstance().setReceiveUpdates(value);
                break;
            case NAME:
                if (!SynchMaster.getInstance().isConnected()) {
                    SynchMaster.getInstance().setChannelName(cis.getThirdArg());
                    LOGGER.info("You have set channel name: " + SynchMaster.getInstance().getChannelName());
                } else {
                    LOGGER.info("Impossible to set channel name when channel is connected");
                }
                break;
            default: //TODO
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
}
