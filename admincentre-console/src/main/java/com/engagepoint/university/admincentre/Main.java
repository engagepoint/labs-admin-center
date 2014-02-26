package com.engagepoint.university.admincentre;

import com.engagepoint.university.admincentre.exception.WrongInputArgException;
import com.engagepoint.university.admincentre.preferences.NodePreferences;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Logger;


public final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final ConsoleController CONSOLE_CONTROLLER = new ConsoleController();

    private Main() {
    }

    public static void main(String... args) {
        Logger globalLogger = Logger.getLogger("");
        Handler[] handlers = globalLogger.getHandlers();
        for(Handler handler : handlers) {
            globalLogger.removeHandler(handler);
        }
        SLF4JBridgeHandler.install();
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
                LOGGER.warning("Illegal arguments");
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.warning("Illegal arguments");
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
            LOGGER.warning("Exception while reading input " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (br != null) {
                    LOGGER.info("Thank you for using EngagePoint Admin Center...");
                    br.close();
                }
            } catch (IOException ioe) {
                LOGGER.warning("Error while closing stream: " + ioe);
            }
        }
    }

    private static void analyzeLine(String line) {
        if (line != null) {
            String[] args = line.split("\\s+");
            ConsoleInputString cis = new ConsoleInputString(args);
            try {
                checkCommand(cis);
            } catch (WrongInputArgException e) {
                // TODO Auto-generated catch block
                LOGGER.warning("analyzeLine: message = " + e.getMessage());
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
                case SYNCH:
                    CONSOLE_CONTROLLER.synch(cis);
                    break;
                case REFRESH:
                    CONSOLE_CONTROLLER.refresh();
                    break;
            case EXPORT:
                CONSOLE_CONTROLLER.export(cis);
                break;
                default:
                    CONSOLE_CONTROLLER.showHelp();
            }
        } catch (IllegalArgumentException e) {
            throw new WrongInputArgException(e);
        }
    }
}
