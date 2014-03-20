package com.engagepoint.university.admincentre.web;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aleksey.korotysh on 17.03.14.
 */
@Named
public class ValidaorEdit {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidaorEdit.class.getName());

    public void validateAlpha(FacesContext facesContext, UIComponent uiComponent, Object value) {
        String selectedValue;
        UIComponent parent = uiComponent.getParent();
        if (null != parent) {
            UIComponent sone = parent.findComponent("selectTypeMenu");

            if (null != sone) {
                LOGGER.debug("----------SELECTED-------------------" + parent.getId() + "----------------------------------------------");
                SelectOneMenu selectOneMenu = (SelectOneMenu) sone;
                selectedValue = (String) selectOneMenu.getSubmittedValue();
                LOGGER.debug("++++++++++++++++++++++++++SELECTED VALUE++++++++++++++++++++++++++++++++++" + selectedValue);

                if ("Integer".equals(selectedValue)) {
                    if (!StringUtils.isNumeric((String) value)) {
                        HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                        FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Integer Number");
                        throw new ValidatorException(facesMessage);
                    }
                } else if (("Double".equals(selectedValue) || "Float".equals(selectedValue)) && (!ValidatorAdd.isDouble((String) value))) {
                    HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                    FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Floating-Point Number");
                    throw new ValidatorException(facesMessage);
                }
            }
        }
    }
}
