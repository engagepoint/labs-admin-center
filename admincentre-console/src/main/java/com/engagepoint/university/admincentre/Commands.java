package com.engagepoint.university.admincentre;

public enum Commands {

    VIEW("-view", "Show Node tree"),

    EXIT("-exit", "Stop application"),
    HELP("-help", "Show list of all commands"),
    VERSION("-version", "Display version information"),
    CREATE("-create",
            "Allows you create new Node or Key"),
    REMOVE("-remove", "--------------"),
    EDIT("-edit", "-------------"),
    SELECT("-select",
            "Choose node with the given name"),
    DEBUG(
            "-debug",
            "Produce execute debug output"),
    SYNCH("-synch", "Synchronization commands:"
            + "\n\t\t -connect %cluster_name%"
            + "\n\t\t -disconnect"
            + "\n\t\t -obtain (obtain state)"
            + "\n\t\t -putreceived (put received state)"
            + "\n\t\t -receiveupdates (true, false)");


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
