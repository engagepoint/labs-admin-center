package com.engagepoint.university.admincentre;

public enum AdditionalCommands {
    NODE("-node"),
    KEY("-key"),
    CONNECT("-connect"),
    DISCONNECT("-disconnect"),
//    OBTAIN("-obtain"),
    PULL("-pull"),
    MERGE("-merge"),
    PUSH("-push"),
    RECEIVEUPDATES("-receiveupdates"),
    STATUS("-status"),
    NAME("-name");

    private String command;

    AdditionalCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
