package com.engagepoint.university.admincentre.util;

/**
 * Class used to store constants. It could not be instantiated - exception will
 * be thrown.
 *
 * @author Roman Garkavenko
 *
 */
public final class Constants {

    public final static String COULD_NOT_COMPLETE_CRUD_OPERATION = "Could not complete CRUD operation";
    public final static String IS_ILLEGAL_ARGUMENT_ENUM = " is illegal argument."
            + " Only 'AUTO' and 'MANUAL' could be passed";
    public final static String CHANNEL_IS_DISCONNECTED = "Channel is disconnected";
    public final static String CHANNEL_IS_CONNECTED = "Channel is already connected";

    private Constants() {
        throw new UnsupportedOperationException("This class should never be instantiated.");
    }
}
