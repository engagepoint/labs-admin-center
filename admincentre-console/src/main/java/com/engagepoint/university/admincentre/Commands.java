package com.engagepoint.university.admincentre;

public enum Commands {

    VIEW("-view", "Show Node tree"),

    EXIT("-exit", "Stop application"),
    HELP("-help", "Show list of all commands"),
    CREATE(
            "-create",
            "Allows you create new Node or Key"),
    REMOVE("-remove", "--------------"),
    EDIT("-edit", "-------------"),
    SELECT("-select",
            "Choose node with the given name"),
    DEBUG(
            "-debug",
            "Produce execute debug output"),
    SYNCH("-synch",
			"Synchronization commands:"
					+ "\n\t -connect \"cluster_name\""
					+ "\n\t -merge \t*compare with state"
					+ "\n\t -disconnect"
					+ "\n\t -pull \t\t*pull state"
					+ "\n\t -push \t\t*send my state to all cluster members"
					+ "\n\t -mode [auto|hand] \t*get or set mode - auto or hand-held"
					+ "\n\t -receiveupdates [true|false] *get or set receive updates status"
					+ "\n\t -name [new name] \t*get or set channel name"
					+ "\n\t -status \t*print all info about channel"),
    REFRESH("-refresh", "*use after obtaining new message or state"),
    EXPORT("-export",
            "exports current base into zip located in entered path");


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
