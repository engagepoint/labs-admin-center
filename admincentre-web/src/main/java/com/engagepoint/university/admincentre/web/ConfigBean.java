package com.engagepoint.university.admincentre.web;

import com.engagepoint.university.admincentre.config.ConfLoadBean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

@ManagedBean(name = "configBean")
@RequestScoped
public class ConfigBean implements Serializable {

    @EJB
    ConfLoadBean confLoadBean;


    public ConfigBean() {

    }

    public String getChannel() {
        return confLoadBean.getChannel();
    }

    public void setChannel(String channel) {
        confLoadBean.setChannel(channel);
    }

    public String getMode() {
        return confLoadBean.getMode();
    }

    public void setMode(String mode) {
        confLoadBean.setMode(mode);
    }

    public String getCluster() {
        return confLoadBean.getCluster();
    }

    public void setCluster(String cluster) {
        confLoadBean.setCluster(cluster);
    }

    public String getBasePath() {
        return confLoadBean.getBasePath();
    }

    public void setBasePath(String basePath) {
        confLoadBean.setBasePath(basePath);
    }
}
