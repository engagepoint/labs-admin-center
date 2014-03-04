package com.engagepoint.university.admincentre.web;

import java.io.Serializable;
import java.util.Locale;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * @author artem.lysenko
 */
@Named("localeChanger")
@SessionScoped
public class LocaleChanger implements Serializable {

    private static final long serialVersionUID = 1234L;

    public String spanishSet() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot().setLocale(new Locale("es"));
        return null;
    }

    public String englishSet() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot().setLocale(Locale.ENGLISH);
        return null;
    }
}
