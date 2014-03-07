package com.engagepoint.university.admincentre;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jgroups.Address;

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.KeyType;
import com.engagepoint.university.admincentre.exception.WrongInputArgException;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.Pair;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;
import com.engagepoint.university.admincentre.synchronization.SynchMaster.MergeStatus;

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
            	LOGGER.info("Mode: " + SynchMaster.getInstance().getMode().name());
            	break;
            case STATUS:
                synchSTATUS();
                break;
            case NAME:
                LOGGER.info("Channel name: " + SynchMaster.getInstance().getChannelName());
                break;
            case LOAD:
            	SynchMaster.getInstance().useSavedConfig();
            	break;
            case SAVE:
            	SynchMaster.getInstance().saveConfig();
            	LOGGER.info("Configurations were saved.");
            	break;
            default: //TODO
        }
    }

    /**
     * Prints caution info if trying to call methods
     * which require connected channel
     * @return <br><b>true</b> if channel is connected
     * 		<br><b>false</br> otherwise
     */
    private boolean isConnected(){
    	if(SynchMaster.getInstance().isConnected())
    		return true;
    	LOGGER.info("Channel is disconnected. Command could not be used.");
    	return false;
    }
    
    /**
     * Prints caution info if trying to call methods
     * acceptable only for non-coordinator member.
     * @return <br><b>true</b> if member is coordinator
     * 		<br><b>false</br> otherwise
     */
    private boolean isCoordinator(){
    	if(SynchMaster.getInstance().info().isCoordinator()){
    		LOGGER.info("This member is coordinator");
    		return true;
    	}
    	return false;
    }
    
    /**
     * Prints all info about channel and cluster.
     */
    void synchSTATUS() {
        LOGGER.info("-----------Synch status-----------"
                + "\nChannel name........." + SynchMaster.getInstance().getChannelName()
                + "\nMode................." + SynchMaster.getInstance().getMode().name()
                + "\nConnected............" + SynchMaster.getInstance().isConnected());
        if (SynchMaster.getInstance().isConnected()) {
            LOGGER.info("Cluster name........." + SynchMaster.getInstance().getClusterName()
            		+ "\nCoordinator.........." + SynchMaster.getInstance().info().getCoordinator().toString());
            if(!SynchMaster.getInstance().info().isCoordinator())
            	LOGGER.info("State synchronized..." + !(SynchMaster.getInstance().isMemberChanged()
            				|| SynchMaster.getInstance().isMemberChanged()));
            String addresses = "Addresses(" + SynchMaster.getInstance().info().getAddressList().size() + "): ";
            for (Iterator<Address> i = SynchMaster.getInstance().info().getAddressList().iterator(); i.hasNext(); ) {
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
    
    //switch-case commands
    
    private void disconnect(){
    	SynchMaster.getInstance().disconnect();
        if(!SynchMaster.getInstance().isConnected())
        	LOGGER.info("Disconnected.");
    }
    
    private void merge(){
    	if(!isConnected()) return;
    	if(isCoordinator()) return;
    	List<Pair<MergeStatus, AbstractEntity>> mergeList = SynchMaster.getInstance().merge();
    	for(Pair<MergeStatus, AbstractEntity> pair: mergeList){
    		LOGGER.info(pair.toString());
    	}
    }
    
    private void pull(){
    	if(!isConnected()) return;
    	if(isCoordinator()) return;
    	if(SynchMaster.getInstance().pull()){
    		LOGGER.info("Pulled.");
    		refresh();
    	}else
    		LOGGER.info("Nothing to pull, you have all cluster's data.");
    }
    
    private void push(){
    	if(!isConnected()) return;
    	if(SynchMaster.getInstance().info().isSingle()){
    		LOGGER.info("Only one member in cluster.");
    		return;
    	}
    	if(SynchMaster.getInstance().push())
    		LOGGER.info("Pushed.");
    	else
    		LOGGER.info("Nothing to push, you do not have local changes.");
    }
    
    private void reset(){
    	if(!isConnected()) return;
    	if(isCoordinator()) return;
    	if(SynchMaster.getInstance().reset()){
    		LOGGER.info("Reseted local changes.");
    		refresh();
    	}else
    		LOGGER.info("Nothing to reset, you do not have local changes.");
    }
    
    private void revert(){
    	if(!isConnected()) return;
    	if(isCoordinator()) return;
    	if(SynchMaster.getInstance().revert()){
    		LOGGER.info("Cluster has been reverted.");
    	}else
    		LOGGER.info("Nothing to revert, you have all cluster data.");
    }
    
    private void autosynch(){
    	if(!isConnected()) return;
    	if(isCoordinator()) return;
    	if(SynchMaster.getInstance().getMode() == SynchMaster.Mode.MANUAL){
    		SynchMaster.getInstance().pull();
    		SynchMaster.getInstance().push();
    		SynchMaster.getInstance().setMode(SynchMaster.Mode.AUTO);
    	}else{
    		LOGGER.info("Already in AURO mode");
    	}
    }
    
    private void connect(ConsoleInputString cis){
    	SynchMaster.getInstance().connect(cis.getThirdArg());
        if(SynchMaster.getInstance().getMode() == SynchMaster.Mode.AUTO
        	&& !SynchMaster.getInstance().info().isSingle()){
        	SynchMaster.getInstance().pull();
        }
    }
    
    private void mode(ConsoleInputString cis){
    	if(!cis.getThirdArg().equals("auto") && !cis.getThirdArg().equals("manual")){
    		LOGGER.info("Wrong argument. Only \"auto\" or \"manual\" could be passed");
    		return;
    	}
    	if(SynchMaster.getInstance().isConnected()){
    		if(SynchMaster.getInstance().info().isCoordinator()
    				&& cis.getThirdArg().equals("manual")){
    			LOGGER.info("NOT RECOMMENDED. This member is coordinator.");
    		}else if(SynchMaster.getInstance().getMode() == SynchMaster.Mode.MANUAL
    				&& cis.getThirdArg().equals("auto")
    				&& (SynchMaster.getInstance().isMemberChanged()
    						||
    						SynchMaster.getInstance().isClusterChanged())){
    			LOGGER.info("Current member is not synchronized with cluster."
    					+ " Use commands 'push', 'pull', 'reset', 'revert' to"
    					+ " synch with cluster.");
    			return;
    		}
    	}
    	SynchMaster.getInstance().setMode(SynchMaster.Mode.valueOf(cis.getThirdArg().toUpperCase(Locale.US)));
    	LOGGER.info("New mode has been set: " + SynchMaster.getInstance().getMode().name());
    }
    
    private void name(ConsoleInputString cis){
    	if (!SynchMaster.getInstance().isConnected()) {
            SynchMaster.getInstance().setChannelName(cis.getThirdArg());
            LOGGER.info("You have set channel name: " + SynchMaster.getInstance().getChannelName());
        } else {
            LOGGER.info("Impossible to set channel name when channel is connected");
        }
    }
}
