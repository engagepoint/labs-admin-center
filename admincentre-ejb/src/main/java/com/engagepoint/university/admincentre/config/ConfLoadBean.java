package com.engagepoint.university.admincentre.config;

import javax.ejb.Local;

@Local
public interface ConfLoadBean {

    public String getChannel() ;

    public void setChannel(String channel);

    public String getMode();

    public void setMode(String mode);

    public String getCluster();

    public void setCluster(String cluster) ;

    public String getBasePath();

    public void setBasePath(String basePath);
}
