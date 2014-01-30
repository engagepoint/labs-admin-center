
public class Key {
    Key(String key, String type, String value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    private static final long serialVersionUID = 1L;
    private String key;
    private String type;
    private String value;
    private Node node;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}