package com.engagepoint.university.admincentre;

import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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

    public void showVersion() {
        System.out.println("Current application version is " + 1.0);
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

    public boolean selectNode(String... args) {
        if (args.length == 2) {
            this.currentPreferences = currentPreferences.node(args[1]);
            displayNodes(currentPreferences);
            return true;
        }
        return false;

    }

    public void createNode(String nodeName) {
        if (nameValidation(nodeName)) {
            String newPath = (currentPreferences.absolutePath().equals("/") ? "/" + nodeName
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
        boolean value = name.matches("^[A-Z][a-z0-9]+([A-Z][a-z0-9]+|[A-Z]$)*$");
        if (!value) {
            System.out.println("You enter not valid name...");
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


    public void synch(String... args) {
        if (args.length == 1) {
            synchArgLengthOneElement(args);
        } else if (args.length == 2) {
            synchArgLengthTwoElements(args);
        }
    }

    public void synchArgLengthOneElement(String... args) {
        switch (getEnumElement(args)) {
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
            default: //TODO
        }
    }

    public void synchArgLengthTwoElements(String... args) {
        switch (getEnumElement(args)) {
            case CONNECT:
                SynchMaster.getInstance().connect(args[1]);
                break;
            case RECEIVEUPDATES:
                boolean value = Boolean.parseBoolean(args[1]);
                SynchMaster.getInstance().setReceiveUpdates(value);
                break;
            default: //TODO
        }
    }

    public AdditionalCommands getEnumElement(String... args) {
        return AdditionalCommands.valueOf(args[0].toUpperCase().replaceFirst("-", ""));
    }



    public void checkCreateCommand(String[] arguments) throws WrongInputArgException {
        if (AdditionalCommands.NODE.getCommand().equals(arguments[1]) && (arguments.length == 3)) {
            createNode(arguments[2]);
        } else if (AdditionalCommands.KEY.getCommand().equals(arguments[1]) && (arguments.length == 5)) {
            createKey(arguments[2], arguments[3], arguments[4]);
        } else {
            throw new WrongInputArgException();
        }
    }
}
