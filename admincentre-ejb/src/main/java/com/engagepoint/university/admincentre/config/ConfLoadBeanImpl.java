package com.engagepoint.university.admincentre.config;

import com.engagepoint.university.admincentre.util.ConfLoader;

import javax.ejb.Stateless;

@Stateless
public class ConfLoadBeanImpl implements ConfLoadBean {

    ConfLoader confLoader;

    public ConfLoadBeanImpl() {
        this.confLoader = new ConfLoader();
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
