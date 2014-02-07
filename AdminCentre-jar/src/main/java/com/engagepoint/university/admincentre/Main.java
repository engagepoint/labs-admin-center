package com.engagepoint.university.admincentre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.engagepoint.university.admincentre.dao.NodeDAO;
import com.engagepoint.university.admincentre.entity.Node;
import com.engagepoint.university.admincentre.exception.WrongInputArgException;


public final class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final ConsoleController CONSOLE_CONTROLLER = new ConsoleController();
    private static final Node root = new NodeDAO().getRoot();

    private Main() {
    }

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        if (checkArgs(args)) {
            CONSOLE_CONTROLLER.displayNodes(root);
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

    /**
     * Get connection for console input stream
     */
    private static void connectToInputStream() {
        InputStream is = null;
        BufferedReader br = null;

        try {
            is = System.in;
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;

            while ((line = br.readLine()) != null) {
                if (Commands.EXIT.getName().equals(line)) {
                    break;
                }
                try {
                    analyzeIncomingLine(line);
                } catch (WrongInputArgException e) {
                    System.out.println(e.getMessage());
                }
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

    private static void analyzeIncomingLine(String line) throws WrongInputArgException {
        String[] arguments = line.split(" ");
        checkCommand(arguments);
    }

    private static void checkCommand(String[] arguments) throws WrongInputArgException {
        try {
            switch (Commands.valueOf(arguments[0].toUpperCase().replaceFirst("-", ""))) {
            case VIEW:
                CONSOLE_CONTROLLER.displayNodes(CONSOLE_CONTROLLER.getCurrentNode());
                break;
            case HELP:
                CONSOLE_CONTROLLER.showHelp();
                break;
            case VERSION:
                // CONSOLE_CONTROLLER.showVersion();
                break;
            case CREATE:
                if ("-node".equals(arguments[1]) && (arguments.length == 3)) {
                    CONSOLE_CONTROLLER.createNode(arguments[2]);
                } else if ("-key".equals(arguments[1]) && (arguments.length == 5)) {
                        CONSOLE_CONTROLLER.createKey(arguments[2], arguments[3], arguments[4]);
                } else {
                    throw new WrongInputArgException();
                    }
                break;
            case REMOVE:
                // TODO add remove functional
                break;
            case EDIT:
                // TODO add edit functional
                break;
            case SELECT:
                if (arguments.length == 2) {
                    // CONSOLE_CONTROLLER.selectNode(arguments[1]);
                }
                break;
            }
        } catch (IllegalArgumentException e) {
            throw new WrongInputArgException();
        }
    }
}