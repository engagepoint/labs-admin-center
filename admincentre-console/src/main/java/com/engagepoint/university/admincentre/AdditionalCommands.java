package com.engagepoint.university.admincentre;

public enum AdditionalCommands {
    NODE("-node"),
    KEY("-key"),
    CONNECT("-connect"),
    DISCONNECT("-disconnect"),
    OBTAIN("-obtain"),
    PUTRECEIVED("-putreceived"),
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
