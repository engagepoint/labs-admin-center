package com.engagepoint.university.admincentre;

import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jgroups.Address;

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
        System.out.println("Options ...");
        for (Commands commands : Commands.values()) {
            String name = commands.getName();
            StringBuilder stringBuilder = buildAlignmentString(name.length());
            System.out.println("  " + name + stringBuilder + commands.getDescription());
        }
        System.out.println();
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
        System.out.println(ALIGN_STRING + " name = " + preference.name());
        displayKeys(preference);
        try {
            if (preference.childrenNames().length != 0) {
                ALIGN_STRING.insert(0, "   ");
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
                System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3) + "|");
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

                    System.out.println(ALIGN_STRING.substring(0, ALIGN_STRING.length() - 3)
                            + " Key = " + keys[i] + ";" + "Value = "
                            + preferance.get(keys[i], "value wasn`t found"));

                }
                System.out.println();
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

    public boolean nameValidation(String name) {
        boolean value = name.matches("^\\w+$");
        if (!value) {
            System.out.println("You enter not valid name... Only a-zA-Z_0-9");
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
            System.out.println("You enter wrong key type. Use one of the next types :");
            for (KeyType keyTypeTemp : keyTypeList) {
                System.out.println("  " + keyTypeTemp.toString());
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
        if (length == 1) {
            synchArgLengthOneElement(cis);
        } else if (length == 2) {
            synchArgLengthTwoElements(cis);
        }
    }

    public void synchArgLengthOneElement(ConsoleInputString cis) {
        switch (getEnumElement(cis)) {
            case CONNECT:
                System.out.println("Please, type the name of the cluster you want to connect");
                break;
            case DISCONECT:
                SynchMaster.getInstance().disconnect();
                System.out.println("Disconnected.");
                break;
            case OBTAIN:
                SynchMaster.getInstance().obtainState();
                break;
            case PUTRECEIVED:
                SynchMaster.getInstance().putAllReceived();
                break;
            case RECEIVEUPDATES:
                System.out.println("Receive updates status: " + SynchMaster.getInstance().isReceiveUpdates());
                break;
            case STATUS:
                synchSTATUS();
                break;
            case NAME:
                System.out.println("Channel name: " + SynchMaster.getInstance().getChannelName());
                break;
            default: //TODO
        }
    }

    private void synchSTATUS() {
        System.out.println("-----------Synch status-----------"
                + "\nChannel name......" + SynchMaster.getInstance().getChannelName()
                + "\nConnected........." + SynchMaster.getInstance().isConnected());
        if (SynchMaster.getInstance().isConnected()) {
            System.out.print("Cluster name......" + SynchMaster.getInstance().getClusterName()
                    + "\nAddresses:		");
            for (Iterator<Address> i = SynchMaster.getInstance().getAddressList().iterator(); i.hasNext(); ) {
                String name = SynchMaster.getInstance().getChannelName(i.next());
                if (i.hasNext()) {
                    System.out.print(name + ", ");
                } else {
                    System.out.println(name + ".");
                }
            }
        }
    }

    public void synchArgLengthTwoElements(ConsoleInputString cis) {
        switch (getEnumElement(cis)) {
            case CONNECT:
                SynchMaster.getInstance().connect(cis.getSecondArg());
                break;
            case RECEIVEUPDATES:
                boolean value = Boolean.parseBoolean(cis.getSecondArg());
                SynchMaster.getInstance().setReceiveUpdates(value);
                break;
            case NAME:
                if (!SynchMaster.getInstance().isConnected()) {
                    SynchMaster.getInstance().setChannelName(cis.getSecondArg());
                    System.out.println("You have set channel name: " + SynchMaster.getInstance().getChannelName());
                } else {
                    System.out.println("Impossible to set channel name when channel is connected");
                }
                break;
            default: //TODO
        }
    }

    public AdditionalCommands getEnumElement(ConsoleInputString cis) {
        return AdditionalCommands.valueOf(cis.getFifthArg().toUpperCase(Locale.US).replaceFirst("-", ""));
    }


    public void checkCreateCommand(ConsoleInputString cis) throws WrongInputArgException {
        int length = cis.getLength();
        if (AdditionalCommands.NODE.getCommand().equals(cis.getSecondArg()) && (length == 3)) {
            createNode(cis.getThirdArg());
        } else if (AdditionalCommands.KEY.getCommand().equals(cis.getSecondArg()) && (length == 5)) {
            createKey(cis.getThirdArg(), cis.getFourthArg(), cis.getFirstArg());
        } else {
            throw new WrongInputArgException();
        }
    }
}
