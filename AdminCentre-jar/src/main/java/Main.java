import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static StringBuilder stringBuilder = new StringBuilder("---");

    private Main(){}

    public static void main(String[] args) {
        if (checkArgs(args)) {
            displayNodes(getParentNode());
            connectToInputStream();
        }
    }

    private static void connectToInputStream() {
        InputStream is = null;
        BufferedReader br = null;

        try {
            is = System.in;
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;

            while ((line = br.readLine()) != null) {

                if (CommandConstants.EXIT.equalsIgnoreCase(line)){
                    break;
                }

                LOGGER.info("Execution command: " + line);
            }

        } catch (IOException ioe) {
            LOGGER.warn("Exception while reading input " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (br != null) {
                    LOGGER.info("Thank you for using EngagePoint Admin Centre...");
                    br.close();
                }
            } catch (IOException ioe) {
                LOGGER.warn("Error while closing stream: " + ioe);
            }

        }

    }

    private static boolean checkArgs(String ... args) {
        try {
            if (CommandConstants.VIEW.equals(args[0]) && args.length == 1) {
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

    private static void displayNodes(Node node) {
        LOGGER.info(stringBuilder + "Node name = " + node.getName());
        displayKeys(node);
        if (node.getChildNodes() != null) {
            stringBuilder.insert(0, "   ");
            LOGGER.info(stringBuilder.substring(0, stringBuilder.length() - 3) + "|");
            LOGGER.info(stringBuilder.substring(0, stringBuilder.length() - 3) + "|");
            Iterator iterator = node.getChildNodes().iterator();
            while (iterator.hasNext()) {
                Node n = (Node) iterator.next();
                displayNodes(n);
            }
            if (!iterator.hasNext()) {
                stringBuilder.delete(0, 3);
            }
        }
    }

    private static void displayKeys(Node node) {
        List <Key>keyList = node.getKeys();
        if (keyList != null) {
            for (Key key :keyList) {
                LOGGER.info(stringBuilder.substring(0, stringBuilder.length() - 3) + "   Key = " + key.getKey() + ";" +
                        " Type = " + key.getType() + "; Value = " + key.getValue());

            }
        }
    }

    private static Node getParentNode() {
        Node nodeParent = new Node();

        Node nodeChild1 = new Node();
        Node nodeChild2 = new Node();
        Node nodeChild3 = new Node();

        Node nodeChild11 = new Node();
        Node nodeChild22 = new Node();
        Node nodeChild33 = new Node();

        nodeParent.setName("Parent");

        nodeChild1.setName("Child1");
        nodeChild2.setName("Child2");
        nodeChild3.setName("Child3");

        nodeChild11.setName("Child11");
        nodeChild22.setName("Child22");
        nodeChild33.setName("Child33");

        List<Node> list = new ArrayList<Node>();
        list.add(nodeChild1);
        list.add(nodeChild2);
        list.add(nodeChild3);

        List<Node> list1 = new ArrayList<Node>();
        list1.add(nodeChild11);
        list1.add(nodeChild22);
        list1.add(nodeChild33);

        List<Key> keyList1 = new ArrayList<Key>();
        keyList1.add(new Key("1", "11", "111"));
        keyList1.add(new Key("2", "22", "222"));
        keyList1.add(new Key("3", "33", "333"));
        keyList1.add(new Key("4", "44", "444"));

        nodeParent.setChildNodes(list);
        nodeChild1.setChildNodes(list1);

        nodeChild2.setKeys(keyList1);
        nodeChild33.setKeys(keyList1);


        return nodeParent;
    }
}

