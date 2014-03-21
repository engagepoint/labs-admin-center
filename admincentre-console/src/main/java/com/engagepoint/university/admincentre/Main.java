package com.engagepoint.university.admincentre;

import com.engagepoint.university.admincentre.exception.WrongInputArgException;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    private static final ConsoleController CONSOLE_CONTROLLER = new ConsoleController();

    private Main() {
    }

    public static void main(String... args) {
        SLF4JBridgeHandler.install();
        if (checkArgs(args)) {
            CONSOLE_CONTROLLER.displayNodes(new NodePreferences(null, ""));
            connectToInputStream();
        }
    }

    private static boolean checkArgs(String... args) {

        if (args.length == 1 && Commands.VIEW.getName().equals(args[0])) {
            LOGGER.info("Welcome to EngagePoint Admin Centre...");
        }else if(args.length == 3 && Commands.SYNCH.getName().equals(args[1])
        		&& AdditionalCommands.LOAD.getCommand().equals(args[2])){
        	SynchMaster.getInstance().useSavedConfig();
        	if(!SynchMaster.getInstance().isSingle()){
        		SynchMaster.getInstance().pull();
        		SynchMaster.getInstance().push();
        	}
        	CONSOLE_CONTROLLER.synchSTATUS();
        }else {
            return false;
        }
        return true;
    }

    private static void connectToInputStream() {
        InputStream is = null;
        BufferedReader br = null;

        try {
            is = System.in;
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;

            while ((line = br.readLine()) != null) {
                if (Commands.EXIT.getName().equals(line)) {
                    SynchMaster.getInstance().close();        //close channel before exit

                    break;
                }
                analyzeLine(line);
            }
        } catch (IOException ioe) {
            LOGGER.error("Exception while reading input ", ioe);
        } finally {
            try {
                if (br != null) {
                    LOGGER.info("Thank you for using EngagePoint Admin Center...");
                    br.close();
                }
            } catch (IOException ioe) {
                LOGGER.warn("Error while closing stream: ", ioe);
            }
        }
    }

    private static void analyzeLine(String line) {
        if (CONSOLE_CONTROLLER.showMessageIfRemoved()) {
            CONSOLE_CONTROLLER.displayNodes(CONSOLE_CONTROLLER.getCurrentPreferences());
            return;
        }
        if (line != null) {
            String[] args = line.split("\\s+");
            ConsoleInputString cis = new ConsoleInputString(args);
            try {
                checkCommand(cis);
            } catch (WrongInputArgException e) {
                LOGGER.info("Can't analyze line...\n{}", e.getMessage());
            }
        }
    }

    private static void checkCommand(ConsoleInputString cis) throws WrongInputArgException {
        try {
            switch (Commands.valueOf(cis.getFirstArg().toUpperCase(Locale.US).replaceFirst("-", ""))) {
                case VIEW:
                    CONSOLE_CONTROLLER.displayNodes(CONSOLE_CONTROLLER.getCurrentPreferences());
                    break;
                case HELP:
                    CONSOLE_CONTROLLER.showHelp();
                    break;
                case CREATE:
                    CONSOLE_CONTROLLER.checkCreateCommand(cis);
                    break;
                case SELECT:
                    CONSOLE_CONTROLLER.selectNode(cis);
                    break;
                case REMOVE:
                    CONSOLE_CONTROLLER.remove(cis);
                    break;
                case SYNCH:
                    CONSOLE_CONTROLLER.synch(cis);
                    break;
                case EXPORT:
                    CONSOLE_CONTROLLER.export(cis);
                    break;
                default:
                    CONSOLE_CONTROLLER.showHelp();
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new WrongInputArgException("You enter wrong argument. Try to use -help for get information about "
                    + "application commands");
        }
    }
}
