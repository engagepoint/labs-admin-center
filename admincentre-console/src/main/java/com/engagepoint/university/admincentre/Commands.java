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
					+ "\n\t -merge \t\tcompare with state"
					+ "\n\t -pull \t\t\tpull state"
					+ "\n\t -push \t\t\tsend my state to all cluster members"
					+ "\n\t -reset \t\treset member's changes"
					+ "\n\t -revert \t\treset cluster changes"
					+ "\n\t -autosynch \t\tpull, push and set auto mode"
					+ "\n\t -mode [auto|manual] \tget or set mode - auto or manual"
					+ "\n\t -name [new name] \tget or set channel name"
					+ "\n\t -status \t\tprint all info about channel"
					+ "\n\t -load \t\t\tuses configurations from file"
					+ "\n\t -save \t\t\tsave configurations to file"),
    REFRESH("-refresh", "use after obtaining new message or state"),
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
