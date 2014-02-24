package com.engagepoint.university.admincentre;

public enum AdditionalCommands {
    NODE("-node"),
    KEY("-key"),
    CONNECT("-connect"),
    DISCONECT("-disconnect"),
    OBTAIN("-obtain"),
    PUTRECEIVED("-putreceived"),
    RECEIVEUPDATES("-receiveupdates");

    private String command;

    AdditionalCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
