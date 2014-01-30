import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    final static Logger logger = Logger.getLogger(Main.class.getName());
    private static StringBuilder stringBuilder = new StringBuilder("---");

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

                if (line.equalsIgnoreCase(CommandConstants.EXIT)){
                    break;
                }

                System.out.println("Execution command: " + line);
            }

        } catch (IOException ioe) {
            System.out.println("Exception while reading input " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (br != null) {
                    System.out.println("Thank you for using EngagePoint Admin Centre...");
                    br.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }

        }

    }

    private static boolean checkArgs(String[] args) {
        try {
            if (args[0].equals(CommandConstants.VIEW) && args.length == 1) {
                logger.info("Welcome to EngagePoint Admin Centre...");
            } else {
                logger.warning("Illegal arguments");
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warning("Illegal arguments");
            return false;
        }
        return true;
    }

    private static void displayNodes(Node node) {
        System.out.println(stringBuilder + "Node name = " + node.getName());
        displayKeys(node);
        if (node.getChildNodes() != null) {
            stringBuilder.insert(0, "   ");
            System.out.println(stringBuilder.substring(0, stringBuilder.length() - 3) + "|");
            System.out.println(stringBuilder.substring(0, stringBuilder.length() - 3) + "|");
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
        if (node.getKeys() != null) {
            for (Key key : node.getKeys()) {
                System.out.println(stringBuilder.substring(0, stringBuilder.length() - 3) + "   Key = " + key.getKey() + ";" +
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

