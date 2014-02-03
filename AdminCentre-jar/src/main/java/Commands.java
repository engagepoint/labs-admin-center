public enum Commands {

    VIEW("-view", "Show Node tree"),
    EXIT("-exit", "Stop application"),
    HELP("-help", "Show list of all commands"),
    VERSION("-version", "Display version information"),
    CREATE("-create", "Allows you create new Node or Key"),
    REMOVE("-remove", "--------------"),
    EDIT("-edit", "-------------"),
    DEBUG("-debug", "Produce execute debug output"),
    CHOOSE("-choose", "Choose node with the given name");

    private String name;
    private String description;

    Commands(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


}
