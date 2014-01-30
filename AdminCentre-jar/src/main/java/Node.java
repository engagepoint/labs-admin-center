import java.util.List;

public class Node {
    private String name;

    private List<Key> keys;

    private Node parentNode;


    private List<Node> childNodes;

    public String getName() {
        return name;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public void setChildNodes(List<Node> childNodes) {
        this.childNodes = childNodes;
    }
}