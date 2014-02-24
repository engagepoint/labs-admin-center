package com.engagepoint.university.admincentre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.util.logging.Handler;
//import java.util.logging.LogManager;
//import java.util.logging.Logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engagepoint.university.admincentre.exception.WrongInputArgException;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final ConsoleController CONSOLE_CONTROLLER = new ConsoleController();

    private Main() {
    }

    public static void main(String[] args) {
//    	LogManager.getLogManager().reset();
//    	for(Handler iHandler: LOGGER.getParent().getHandlers()){
//    		LOGGER.getParent().removeHandler(iHandler);
//    	}
        if (checkArgs(args)) {
            CONSOLE_CONTROLLER.displayNodes(new NodePreferences(null, ""));
            connectToInputStream();
        }
    }

    private static boolean checkArgs(String... args) {
        try {
            if (Commands.VIEW.getName().equals(args[0]) && args.length == 1) {
                LOGGER.info("Welcome to EngagePoint Admin Centre...");
            } else {
                LOGGER.warn("Illegal arguments");
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warn("Illegal arguments");
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
            LOGGER.warn("Exception while reading input " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (br != null) {
                    LOGGER.info("Thank you for using EngagePoint Admin Center...");
                    br.close();
                }
            } catch (IOException ioe) {
                LOGGER.warn("Error while closing stream: " + ioe);
            }
        }
    }

    private static void analyzeLine(String line) {
        String[] arguments = line.split("\\s+");
        try {
            checkCommand(arguments);
        } catch (WrongInputArgException e) {
            // TODO Auto-generated catch block
            LOGGER.warn("analyzeLine: message = " + e.getMessage());
        }
    }

    private static void checkCommand(String... arguments) throws WrongInputArgException {
        try {
            switch (Commands.valueOf(arguments[0].toUpperCase().replaceFirst("-", ""))) {
                case VIEW:
                    CONSOLE_CONTROLLER.displayNodes(CONSOLE_CONTROLLER.getCurrentPreferences());
                    break;
                case HELP:
                    CONSOLE_CONTROLLER.showHelp();
                    break;
                case CREATE:
                    CONSOLE_CONTROLLER.checkCreateCommand(arguments);
                    break;
                case REMOVE:
                    // TODO add remove functional
                    break;
                case EDIT:
                    // TODO add edit functional
                    break;
                case SELECT:
                    CONSOLE_CONTROLLER.selectNode(arguments);
                    break;
                case SYNCH:
                    CONSOLE_CONTROLLER.synch(arguments);
                    break;
                case REFRESH:
                    CONSOLE_CONTROLLER.refresh();
                    break;
                default:
                    CONSOLE_CONTROLLER.showHelp();
            }
        } catch (IllegalArgumentException e) {
            throw new WrongInputArgException(e);
        }
    }
}
