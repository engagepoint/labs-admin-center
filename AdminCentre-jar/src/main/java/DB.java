import java.util.ArrayList;
import java.util.List;

public class DB {
    //Mock data
    public static Node getParentNode() {
        Node nodeParent = new Node();

        Node nodeChild1 = new Node();
        Node nodeChild2 = new Node();
        Node nodeChild3 = new Node();

        nodeChild1.setParentNode(nodeParent);
        nodeChild2.setParentNode(nodeParent);
        nodeChild3.setParentNode(nodeParent);


        Node nodeChild11 = new Node();
        Node nodeChild22 = new Node();
        Node nodeChild33 = new Node();

        nodeChild11.setParentNode(nodeChild1);
        nodeChild22.setParentNode(nodeChild1);
        nodeChild33.setParentNode(nodeChild1);

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
