package com.engagepoint.university.admincentre;

public enum AdditionalCommands {
    NODE("-node"),
    KEY("-key"),
    CONNECT("-connect"),
    DISCONNECT("-disconnect"),
    MERGE("-merge"),
    PULL("-pull"),
    PUSH("-push"),
    RESET("-reset"),
    REVERT("-revert"),
    MODE("-mode"),
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
