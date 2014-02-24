package com.engagepoint.university.admincentre;

public enum Commands {

    VIEW("-view", "Show Node tree"),
   
    EXIT("-exit", "Stop application"),
    HELP("-help", "Show list of all commands"),
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
 		+ "\n\t -connect %cluster_name%"
 		+ "\n\t -disconnect"
 		+ "\n\t -obtain         *obtain state"
 		+ "\n\t -putreceived    *put received state"
 		+ "\n\t -receiveupdates [true|false]     *set or get receive updates status"   
 		+ "\n\t -name [new name]    *get or set channel name"
 		+ "\n\t -status		*print all info about channel"),
 REFRESH("-refresh", "*use after obtaining new message or state");


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
