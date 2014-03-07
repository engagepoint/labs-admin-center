package com.engagepoint.university.admincentre.config;

import com.engagepoint.university.admincentre.util.ConfLoader;

import javax.ejb.Stateful;

@Stateful
public class ConfLoadBeanImpl implements ConfLoadBean {

    ConfLoader confLoader;

    public ConfLoadBeanImpl() {
        this.confLoader = new ConfLoader();
    }

    public String getChannel() {
        return confLoader.getChannelName();
    }

    public void setChannel(String channel) {
        confLoader.setChannelName(channel);
    }

    public String getMode() {
        return confLoader.getMode();
    }

    public void setMode(String mode) {
        confLoader.setMode(mode);
    }

    public String getCluster() {
        return confLoader.getClusterName();
    }

    public void setCluster(String cluster) {
        confLoader.setClusterName(cluster);
    }

    public String getBasePath() {
        return confLoader.getBasePath();
    }

    public void setBasePath(String basePath) {
        confLoader.setBasePath(basePath);
    }


}
