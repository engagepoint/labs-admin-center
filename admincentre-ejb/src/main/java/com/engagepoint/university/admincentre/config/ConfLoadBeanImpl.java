package com.engagepoint.university.admincentre.config;

import javax.ejb.Stateless;

import com.engagepoint.university.admincentre.util.ConfLoader;

@Stateless
public class ConfLoadBeanImpl implements ConfLoadBean {

    ConfLoader confLoader;

    public ConfLoadBeanImpl() {
        this.confLoader = ConfLoader.getInstance();
    }

    @Override
    public String getChannel() {
        return confLoader.getChannelName();
    }

    @Override
    public void setChannel(String channel) {
        confLoader.setChannelName(channel);
    }

    @Override
    public String getMode() {
        return confLoader.getMode();
    }

    @Override
    public void setMode(String mode) {
        confLoader.setMode(mode);
    }

    @Override
    public String getCluster() {
        return confLoader.getClusterName();
    }

    @Override
    public void setCluster(String cluster) {
        confLoader.setClusterName(cluster);
    }

    @Override
    public String getBasePath() {
        return confLoader.getBasePath();
    }

    @Override
    public void setBasePath(String basePath) {
        confLoader.setBasePath(basePath);
    }


}
