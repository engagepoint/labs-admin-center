package com.engagepoint.university.admincentre.synch;

import javax.ejb.Stateless;

import com.engagepoint.university.admincentre.exception.SynchronizationException;
import com.engagepoint.university.admincentre.synchronization.SynchMaster;
import com.engagepoint.university.admincentre.util.Constants;

/**
 * Session Bean implementation class SynchBean
 *
 * @author Roman Garkavenko
 */
@Stateless
public class SynchBean implements Synch {

    /**
     * Default constructor.
     */
    public SynchBean() {
    }

    @Override
    public boolean isConnected() {
        return SynchMaster.getInstance().isConnected();
    }

    @Override
    public String getChannelName() {
        return SynchMaster.getInstance().getChannelName();
    }

    @Override
    public void setChannelName(String name) {
        SynchMaster.getInstance().setChannelName(name);
    }

    @Override
    public String getMode() {
        return SynchMaster.getInstance().getMode().name();
    }

    @Override
    public void setMode(String modeLine) {
        if (SynchMaster.Mode.AUTO.name().equals(modeLine)
                || SynchMaster.Mode.MANUAL.name().equals(modeLine)) {
            SynchMaster.getInstance().setMode(SynchMaster.Mode.valueOf(modeLine));
        } else {
            throw new IllegalArgumentException(modeLine + Constants.IS_ILLEGAL_ARGUMENT_ENUM);
        }
    }

    @Override
    public String getClusterName() {
        return SynchMaster.getInstance().getClusterName();
    }

    @Override
    public void connect(String clusterName) throws IllegalStateException {
        if (isConnected()) {
            throw new IllegalStateException(Constants.CHANNEL_IS_CONNECTED);
        }
        SynchMaster.getInstance().connect(clusterName);
    }

    @Override
    public boolean autoConnect(String clusterName) throws SynchronizationException {
        connect(clusterName);
        return SynchMaster.getInstance().autoSynch();
    }

    @Override
    public void disconnect() {
        SynchMaster.getInstance().disconnect();
    }
}
