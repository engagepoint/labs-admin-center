package com.engagepoint.university.admincentre.web;

import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

/**
 * Created by aleksey.korotysh on 13.03.14.
 */
@ManagedBean
public class ValidatorAdd {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorAdd.class.getName());



    public void validateAlpha(FacesContext facesContext, UIComponent
            uiComponent, Object value) throws ValidatorException {
        String selectedValue;
        UIComponent parent = uiComponent.getParent();
        if (null != parent) {
            UIComponent sone = parent.findComponent("selectTypeMenu2");
            if (null != sone) {
                System.out.println("----------SELECTED-------------------" + parent.getId() + "----------------------------------------------");
                SelectOneMenu selectOneMenu = (SelectOneMenu) sone;
                selectedValue = (String) selectOneMenu.getSubmittedValue();
                System.out.println("++++++++++++++++++++++++++SELECTED VALUE++++++++++++++++++++++++++++++++++" + selectedValue);

                if ("Integer".equals(selectedValue)) {
                    if (!StringUtils.isNumeric((String) value)) {
                        HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                        FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Integer Number");
                        throw new ValidatorException(facesMessage);
                    }
                } else if (("Double".equals(selectedValue) || "Float".equals(selectedValue)) && (!isDouble((String) value))) {

                        HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                        FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Floating-Point Number");
                        throw new ValidatorException(facesMessage);
                    }
                }

            }
        }


    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            LOGGER.error("", e);
            return false;
        }
        return true;
    }
}