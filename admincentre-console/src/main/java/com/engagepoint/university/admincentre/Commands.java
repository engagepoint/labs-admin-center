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
					+ "\n\t -disconnect"
					+ "\n\t -merge \t\t*compare with state"
					+ "\n\t -pull \t\t*pull state"
					+ "\n\t -push \t\t*send my state to all cluster members"
					+ "\n\t -reset \t\t*reset member's changes"
					+ "\n\t -revert \t\t*reset cluster changes"
					+ "\n\t -autosynch \t\t*pull, push and set auto mode"
					+ "\n\t -mode [auto|hand] \t*get or set mode - auto or hand-held"
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
